package com.palodon.server.config

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class SQLiteConfig(
    @NotBlank(message = "Path must not be blank!")
    @JsonProperty("path")
    var path: String = "palodon.db",
)
