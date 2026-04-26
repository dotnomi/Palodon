package com.palodon.server.enumerator

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
enum class DatabaseType {
    POSTGRESQL,
    SQLITE,
}