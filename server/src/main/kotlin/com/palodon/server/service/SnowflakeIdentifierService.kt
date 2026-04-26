package com.palodon.server.service

import com.palodon.server.enumerator.SnowflakeIdType
import jakarta.enterprise.context.ApplicationScoped
import java.time.Instant

@ApplicationScoped
class SnowflakeIdentifierService {
    private var sequence = 0L
    private var lastTimestamp = -1L
    
    @Synchronized
    fun generateId(type: SnowflakeIdType): Long {
        val timestamp = Instant.now().toEpochMilli()
        
        if (timestamp < lastTimestamp) {
            throw IllegalStateException("Clock moved backwards")
        }

        sequence = if (timestamp == lastTimestamp) {
            (sequence + 1)
        } else {
            0L
        }
        
        lastTimestamp = timestamp
        
        val epoch = 1767229200L // 2026-01-01
        
        return when (type) {
            SnowflakeIdType.MESSAGE -> {
                // 14 digits
                val time = (timestamp - epoch)
                val seq = sequence % 1000
                var id = time * 1000 + seq
                val min = 10_000_000_000_000L
                if (id < min) id += min 
                id
            }
            SnowflakeIdType.USER, SnowflakeIdType.CHANNEL -> {
                // 10 digits
                val time = (timestamp - epoch) / 1000
                val seq = sequence % 100
                var id = time * 100 + seq
                val min = 1_000_000_000L
                if (id < min) id += min 
                id
            }
        }
    }
}