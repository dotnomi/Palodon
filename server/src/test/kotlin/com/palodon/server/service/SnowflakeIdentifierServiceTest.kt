package com.palodon.server.service

import com.palodon.server.enumerator.SnowflakeIdType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class SnowflakeIdentifierServiceTest {
    private lateinit var service: SnowflakeIdentifierService

    @BeforeEach
    fun setUp() {
        service = SnowflakeIdentifierService()
    }

    @Test
    fun `generateId should return unique positive IDs for USER type`() {
        val id1 = service.generateId(SnowflakeIdType.USER)
        val id2 = service.generateId(SnowflakeIdType.USER)
        
        assertTrue(id1 > 0)
        assertTrue(id2 > 0)
        assertNotEquals(id1, id2)
        assertTrue(id2 > id1)
    }

    @Test
    fun `generateId should return unique positive IDs for MESSAGE type`() {
        val id1 = service.generateId(SnowflakeIdType.MESSAGE)
        val id2 = service.generateId(SnowflakeIdType.MESSAGE)
        
        assertTrue(id1 > 0)
        assertTrue(id2 > 0)
        assertNotEquals(id1, id2)
        assertTrue(id2 > id1)
    }
    
    @Test
    fun `generateId should return unique positive IDs for CHANNEL type`() {
        val id1 = service.generateId(SnowflakeIdType.CHANNEL)
        val id2 = service.generateId(SnowflakeIdType.CHANNEL)
        
        assertTrue(id1 > 0)
        assertTrue(id2 > 0)
        assertNotEquals(id1, id2)
        assertTrue(id2 > id1)
    }

    @Test
    fun `generateId should respect minimum value from enum`() {
        val userId = service.generateId(SnowflakeIdType.USER)
        val channelId = service.generateId(SnowflakeIdType.CHANNEL)
        val messageId = service.generateId(SnowflakeIdType.MESSAGE)

        assertTrue(userId >= SnowflakeIdType.USER.offset)
        assertTrue(channelId >= SnowflakeIdType.CHANNEL.offset)
        assertTrue(messageId >= SnowflakeIdType.MESSAGE.offset)
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

        val title = "===== ${type.name} ====="
        LoggerFactory.getLogger(this::class.java).info(title)
        for ((index, value) in generatedIds.withIndex()) {
            LoggerFactory.getLogger(this::class.java).info("\"${index}\":\"${value}\"")
        }
        LoggerFactory.getLogger(this::class.java).info("=".repeat(title.length))

        assertEquals(threadCount * idsPerThread, generatedIds.size, "Generated IDs should be entirely unique for type ${type.name}")
    }
}