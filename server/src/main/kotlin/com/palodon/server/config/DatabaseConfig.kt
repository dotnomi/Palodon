package com.palodon.server.config

import com.fasterxml.jackson.annotation.JsonProperty
import com.palodon.server.enumerator.DatabaseType
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
@Suppress("KotlinNullnessAnnotation")
data class DatabaseConfig (
    @NotNull(message = "Database type must not be null!")
    @JsonProperty("type")
    var type: DatabaseType = DatabaseType.SQLITE,

    @field:Valid
    @NotNull(message = "PostgreSQL config must not be null!")
    @JsonProperty("postgresql")
    var postgres: PostgreSQLConfig = PostgreSQLConfig(),

    @field:Valid
    @NotNull(message = "SQLite config must not be null!")
    @JsonProperty("sqlite")
    var sqlite: SQLiteConfig = SQLiteConfig(),
)