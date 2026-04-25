package com.palodon.server.config

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
enum class DatabaseType {
    POSTGRESQL,
    SQLITE,
}