package com.palodon.server.service

import com.palodon.server.enumerator.SnowflakeIdType
import jakarta.enterprise.context.ApplicationScoped
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import org.slf4j.LoggerFactory

@ApplicationScoped
class SnowflakeIdentifierService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val workerId: Long get() {
        val workerIdStr = ConfigService.config.workerId
        val digits = workerIdStr.filter { it.isDigit() }
        return if (digits.isNotEmpty()) {
            digits.toLong()
        } else {
            (workerIdStr.hashCode().toLong() and 0xFFFFFFFFL)
        }
    }

    private data class TypeState(
        var lastTime: Long = -1L,
        var sequence: Long = 0L
    )

    private val states = ConcurrentHashMap<SnowflakeIdType, TypeState>()
    private val epoch = 1767229200L // 2026-01-01

    fun generateId(type: SnowflakeIdType): Long {
        val state = states.computeIfAbsent(type) { TypeState() }
        
        synchronized(state) {
            var currentTime = getCurrentTime(type)
            
            if (currentTime < state.lastTime) {
                val drift = state.lastTime - currentTime
                if (drift <= 1000) {
                    logger.warn("Clock drift detected for ${type.name}. Waiting...")
                    currentTime = waitUntilNextTime(type, state.lastTime)
                } else {
                    throw IllegalStateException("Clock moved backwards. Refusing to generate ID.")
                }
            }

            if (currentTime == state.lastTime) {
                state.sequence = (state.sequence + 1) % (1L shl type.length)
                if (state.sequence == 0L) {
                    currentTime = waitUntilNextTime(type, currentTime)
                    state.sequence = 0L
                }
            } else {
                state.sequence = 0L
            }
            
            state.lastTime = currentTime
            
            val effectiveWorkerId = workerId % (1L shl type.workerBits)
            val timeOffset = currentTime - getEpoch(type)
            
            // Composition
            val timePart = timeOffset * type.multiplier
            val workerPart = effectiveWorkerId shl type.length
            
            return timePart + workerPart + state.sequence + type.offset
        }
    }

    private fun getCurrentTime(type: SnowflakeIdType): Long {
        val now = Instant.now().toEpochMilli()
        return if (type == SnowflakeIdType.MESSAGE) now else now / 1000
    }

    private fun getEpoch(type: SnowflakeIdType): Long {
        return if (type == SnowflakeIdType.MESSAGE) epoch else epoch / 1000
    }

    private fun waitUntilNextTime(type: SnowflakeIdType, lastTime: Long): Long {
        var time = getCurrentTime(type)
        while (time <= lastTime) {
            Thread.onSpinWait()
            time = getCurrentTime(type)
        }
        return time
    }
}
