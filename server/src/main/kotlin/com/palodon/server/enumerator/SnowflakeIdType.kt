package com.palodon.server.enumerator

enum class SnowflakeIdType(
    val length: Int,           // Number of bits for the sequence number (e.g., 14 for messages)
    val multiplier: Long,      // Multiplier for the timestamp (1000L for 14-bit types, 100L for 10-bit types)
    val min: Long              // Minimum offset to ensure IDs are positive and unique across types
) {
    USER(10, 100L, 1_000_000_000L),           // User IDs: 10 digits, multiplier 100, min 1B
    CHANNEL(10, 100L, 1_000_000_000L),        // Channel IDs: 10 digits, multiplier 100, min 1B
    MESSAGE(14, 1000L, 10_000_000_000_000L)   // Message IDs: 14 digits, multiplier 1000, min 10T
}
