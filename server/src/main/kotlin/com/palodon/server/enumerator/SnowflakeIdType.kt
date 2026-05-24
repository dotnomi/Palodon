package com.palodon.server.enumerator

enum class SnowflakeIdType(
    val length: Int,
    val workerBits: Int,
    val multiplier: Long,
    val offset: Long
) {
    USER(8, 2, 2000L, 10_000_000_000_000L),
    CHANNEL(8, 2, 2000L, 10_000_000_000_000L),
    MESSAGE(10, 6, 50000L, 100_000_000_000_000_000L)
}
