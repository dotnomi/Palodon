package com.palodon.server.service

import com.palodon.server.enumerator.SnowflakeIdType
import com.palodon.server.enumerator.TimeDivisor
import jakarta.enterprise.context.ApplicationScoped
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import org.slf4j.LoggerFactory

/**
 * Service for generating unique, roughly time-ordered identifiers.
 *
 * This implementation guarantees strict decimal digit lengths based on the configuration
 * in [SnowflakeIdType].
 *
 * ### ID Composition:
 * ```
 * ID = (TimeOffset * Multiplier) + (WorkerId << SequenceBits) + Sequence + Offset
 * ```
 *
 * ### Features:
 * - **Worker ID**: Supports horizontal scaling via numeric derivation from a string config.
 * - **Time Granularity**: Controlled by [TimeDivisor] for each ID type.
 * - **Clock Safety**: Automatically waits for small clock drifts and prevents backward jumps.
 */
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
                state.sequence = (state.sequence + 1) % (1L shl type.sequenceBits)
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
            val workerPart = effectiveWorkerId shl type.sequenceBits
            
            return timePart + workerPart + state.sequence + type.offset
        }
    }

    private fun getCurrentTime(type: SnowflakeIdType): Long {
        val now = Instant.now().toEpochMilli()
        return now / type.timeDivisor.divisor
    }

    private fun getEpoch(type: SnowflakeIdType): Long {
        return epoch / type.timeDivisor.divisor
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
