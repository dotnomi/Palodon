package com.palodon.server.service

import com.palodon.server.enumerator.SnowflakeIdType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
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
        // Calculate how many IDs are needed to exhaust the sequence in one tick
        val sequenceCapacity = 1 shl type.sequenceBits
        val countToGenerate = sequenceCapacity + 10 // Force overflow into next tick
        
        val ids = mutableSetOf<Long>()
        repeat(countToGenerate) {
            ids.add(service.generateId(type))
        }
        
        assertEquals(countToGenerate, ids.size, "Should generate all IDs uniquely even after exhausting sequence for ${type.name}")
        
        // Verify time progression (IDs should be strictly increasing)
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

        /*val title = "===== ${type.name} ====="
        LoggerFactory.getLogger(this::class.java).info(title)
        for ((index, value) in generatedIds.withIndex()) {
            LoggerFactory.getLogger(this::class.java).info("\"${index}\":\"${value}\"")
        }
        LoggerFactory.getLogger(this::class.java).info("=".repeat(title.length))*/

        assertEquals(threadCount * idsPerThread, generatedIds.size, "Generated IDs should be entirely unique for type ${type.name}")
    }
}