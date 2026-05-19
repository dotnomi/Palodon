package com.palodon.server.service

import com.palodon.server.enumerator.SnowflakeIdType
import jakarta.enterprise.context.ApplicationScoped
import java.time.Instant

/**
 * Service for generating Snowflake-style unique IDs.
 * 
 * Snowflake ID format: [timestamp_bits][datacenter_bits][worker_bits][sequence_bits]
 * This implementation uses a simplified version with type-specific configurations.
 */
@ApplicationScoped
class SnowflakeIdentifierService {
    /** Current sequence counter (increments within the same millisecond) */
    private var sequence = 0L
    
    /** Timestamp of the last generated ID (to detect clock rollbacks) */
    private var lastTimestamp = -1L
    
    @Synchronized
    fun generateId(type: SnowflakeIdType): Long {
        // Get current timestamp in milliseconds since epoch
        val timestamp = Instant.now().toEpochMilli()
        
        // Check for clock rollback (clock moving backwards)
        if (timestamp < lastTimestamp) {
            throw IllegalStateException("Clock moved backwards")
        }

        // Increment sequence if within the same millisecond, otherwise reset to 0
        sequence = if (timestamp == lastTimestamp) {
            (sequence + 1)
        } else {
            0L
        }
        
        // Update last timestamp to current one
        lastTimestamp = timestamp
        
        // Epoch timestamp: January 1, 2026 00:00:00 UTC (Unix timestamp in milliseconds)
        val epoch = 1767229200L // 2026-01-01
        
        // Retrieve type-specific configuration from enum (single source of truth)
        val (multiplier, min) = type.multiplier to type.min
        
        // Calculate time component based on multiplier:
        // - For 14-bit types (messages): use full milliseconds
        // - For 10-bit types (users/channels): divide by 1000 to get seconds
        val time = if (type.multiplier == 1000L) {
            timestamp - epoch
        } else {
            (timestamp - epoch) / 1000
        }
        
        // Calculate sequence component using the length from enum
        val seq = sequence % (1L shl type.length)
        
        // Combine time and sequence components with multiplier
        val id = time * multiplier + seq
        
        // Ensure ID is at least the minimum value for this type (prevents collisions)
        return if (id < min) id + min else id
    }
}