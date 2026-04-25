package com.palodon.server.config

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class PostgreSQLConfig(
    @NotBlank(message = "Host must not be blank!")
    @JsonProperty("host")
    var host: String = "localhost",

    @Min(1, message = "Port must be greater than 0")
    @Max(65535, message = "Port must be less than 65535")
    @JsonProperty("port")
    var port: Int = 5432,

    @NotBlank(message = "Database name must not be blank!")
    @JsonProperty("database")
    var databaseName: String = "palodon",

    @NotBlank(message = "Username must not be blank!")
    @JsonProperty("username")
    var username: String = "username",

    @NotBlank(message = "Password must not be blank!")
    @JsonProperty("password")
    var password: String = "password",
)
