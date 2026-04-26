package com.palodon.server.enumerator

enum class SnowflakeIdType(length: Int) {
    USER(10),
    CHANNEL(10),
    MESSAGE(14)
}
