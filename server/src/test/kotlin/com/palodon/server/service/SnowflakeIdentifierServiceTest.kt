package com.palodon.server.service

import com.palodon.server.enumerator.SnowflakeIdType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.time.Instant
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class SnowflakeIdentifierServiceTest {
    private lateinit var service: SnowflakeIdentifierService

    @BeforeEach
    fun setUp() {
        service = SnowflakeIdentifierService()
    }

    @ParameterizedTest
    @EnumSource(SnowflakeIdType::class)
    fun `generateId should return unique positive IDs`(type: SnowflakeIdType) {
        val id1 = service.generateId(type)
        val id2 = service.generateId(type)
        
        assertTrue(id1 > 0)
        assertTrue(id2 > 0)
        assertNotEquals(id1, id2)
        assertTrue(id2 > id1)
    }

    @ParameterizedTest
    @EnumSource(SnowflakeIdType::class)
    fun `generateId should respect minimum value from enum`(type: SnowflakeIdType) {
        val id = service.generateId(type)
        assertTrue(id >= type.offset)
    }

    @ParameterizedTest
    @EnumSource(SnowflakeIdType::class)
    fun `generateId should handle sequence exhaustion by waiting for next tick`(type: SnowflakeIdType) {
        val sequenceCapacity = 1 shl type.sequenceBits
        val countToGenerate = sequenceCapacity + 10 
        
        val ids = mutableSetOf<Long>()
        repeat(countToGenerate) {
            ids.add(service.generateId(type))
        }
        
        assertEquals(countToGenerate, ids.size, "Should generate all IDs uniquely even after exhausting sequence for ${type.name}")
        
        val idList = ids.toList().sorted()
        for (i in 0 until idList.size - 1) {
            assertTrue(idList[i+1] > idList[i], "IDs should be monotonically increasing")
        }
    }

    @ParameterizedTest
    @EnumSource(SnowflakeIdType::class)
    fun `generateId should generate unique IDs even when called concurrently`(type: SnowflakeIdType) {
        val threadCount = 10
        val idsPerThread = 100
        val executor = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)
        val generatedIds = mutableSetOf<Long>()

        repeat(threadCount) {
            executor.submit {
                try {
                    val localIds = mutableListOf<Long>()
                    repeat(idsPerThread) {
                        localIds.add(service.generateId(type))
                    }
                    synchronized(generatedIds) {
                        generatedIds.addAll(localIds)
                    }
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        assertEquals(threadCount * idsPerThread, generatedIds.size, "Generated IDs should be entirely unique for type ${type.name}")
    }

    @Test
    fun `multiplier should be safe for all types`() {
        SnowflakeIdType.entries.forEach { type ->
            val capacity = 1L shl (type.sequenceBits + type.workerBits)
            assertTrue(type.multiplier >= capacity, 
                "Type ${type.name} multiplier (${type.multiplier}) is too small for capacity ($capacity). Collisions will occur!")
        }
    }

    @Test
    fun `workerId derivation should be consistent and handle overflow`() {
        assertDoesNotThrow {
            service.generateId(SnowflakeIdType.USER)
            service.generateId(SnowflakeIdType.MESSAGE)
        }
    }

    @Test
    fun `generateId should throw exception on large clock rollback`() {
        val statesField = SnowflakeIdentifierService::class.java.getDeclaredField("states")
        statesField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val states = statesField.get(service) as MutableMap<SnowflakeIdType, Any>

        service.generateId(SnowflakeIdType.USER)

        val typeState = states[SnowflakeIdType.USER]!!
        val lastTimeField = typeState.javaClass.getDeclaredField("lastTime")
        lastTimeField.isAccessible = true

        val futureTime = (Instant.now().toEpochMilli() / 1000) + 3600
        lastTimeField.set(typeState, futureTime)

        assertThrows(IllegalStateException::class.java) {
            service.generateId(SnowflakeIdType.USER)
        }
    }

    @ParameterizedTest
    @EnumSource(SnowflakeIdType::class)
    fun `IDs should stay within digit limits for 100 years`(type: SnowflakeIdType) {
        val hundredYearsMs = 100L * 365 * 24 * 60 * 60 * 1000
        val futureTime = Instant.now().toEpochMilli() + hundredYearsMs

        val epochField = SnowflakeIdentifierService::class.java.getDeclaredField("epoch")
        epochField.isAccessible = true
        val epoch = epochField.get(service) as Long

        val timeDivisor = type.timeDivisor.divisor
        val timeOffset = (futureTime / timeDivisor) - (epoch / timeDivisor)

        val maxWorker = (1L shl type.workerBits) - 1
        val maxSeq = (1L shl type.sequenceBits) - 1

        val maxId = (timeOffset * type.multiplier) + (maxWorker shl type.sequenceBits) + maxSeq + type.offset

        val length = maxId.toString().length
        val expectedLength = type.offset.toString().length

        assertEquals(expectedLength, length, "Type ${type.name} ID will grow to $length digits in 100 years (expected $expectedLength)")
        assertTrue(maxId > 0, "ID must remain positive")
        assertTrue(maxId < Long.MAX_VALUE)
    }
}
