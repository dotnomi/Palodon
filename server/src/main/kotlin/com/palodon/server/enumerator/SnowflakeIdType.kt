package com.palodon.server.enumerator

/**
 * Configuration for Snowflake IDs.
 *
 * ### What the parameters are:
 * 1. [sequenceBits]: Bits for the "Sequence". How many IDs per time unit? (e.g., 10 bits = 1024 IDs).
 * 2. [workerBits]: Bits for the "Worker". How many different servers can run at once? (e.g., 6 bits = 64 servers).
 * 3. [timeDivisor]: The "Time Resolution". Use [TimeDivisor.MILLISECOND] for messages or [TimeDivisor.SECOND] for others.
 * 4. [multiplier]: The "Safety Gap". **MUST** be larger than 2 ^ ([sequenceBits] + [workerBits]).
 *    If you set this too low, IDs will collide!
 * 5. [offset]: The "Starting Number". Use this to fix the digit length (e.g., 14 or 18 digits).
 */
enum class SnowflakeIdType(
    val sequenceBits: Int,
    val workerBits: Int,
    val timeDivisor: TimeDivisor,
    val multiplier: Long,
    val offset: Long
) {
    DEFAULT(7, 1, TimeDivisor.SECOND, 500L, 10_000_000_000_000L),
    USER(8, 2, TimeDivisor.SECOND, 2000L, 10_000_000_000_000L),
    CHANNEL(8, 2, TimeDivisor.SECOND, 2000L, 10_000_000_000_000L),
    SESSION(8, 2, TimeDivisor.SECOND, 2000L, 10_000_000_000_000L),
    MESSAGE(10, 6, TimeDivisor.MILLISECOND, 70000L, 100_000_000_000_000_000L)
}
