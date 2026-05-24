package com.palodon.server.enumerator

enum class TimeDivisor(
    val divisor: Long
) {
    MILLISECOND(1L),
    SECOND(1000L),
    MINUTE(60000L),
    HOUR(3600000L),
    DAY(86400000L),
}