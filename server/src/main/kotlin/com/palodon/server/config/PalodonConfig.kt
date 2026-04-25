package com.palodon.server.config

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

@Suppress("KotlinNullnessAnnotation")
data class PalodonConfig(
    @Valid
    @NotNull(message = "Database must not be null!")
    @JsonProperty("database")
    var database: DatabaseConfig = DatabaseConfig()
)
