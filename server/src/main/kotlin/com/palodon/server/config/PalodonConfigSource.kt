package com.palodon.server.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.palodon.server.constant.EnvironmentVariable
import com.palodon.server.constant.Properties
import com.palodon.server.enumerator.DatabaseType
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.eclipse.microprofile.config.spi.ConfigSource
import org.slf4j.LoggerFactory
import java.io.File

class PalodonConfigSource : ConfigSource {
    private val logger = LoggerFactory.getLogger(PalodonConfigSource::class.java)
    private val mapper = ObjectMapper()
    private val configFile = File("palodon.config.json")
    private val properties = mutableMapOf<String, String>()

    init {
        loadConfig()
    }

    override fun getProperties(): Map<String, String> = properties

    override fun getPropertyNames(): Set<String> = properties.keys

    override fun getValue(propertyName: String): String? = properties[propertyName]

    override fun getName(): String = "PalodonConfigSource"

    override fun getOrdinal(): Int = 275

    private fun loadConfig() {
        val config = if (!this.configFile.exists()) {
            this.logger.warn("No ${this.configFile.name} found. Creating standard configuration...")
            PalodonConfig()
        } else {
            this.logger.info("Loading configuration file ${this.configFile.absolutePath}...")
            this.mapper.readValue(this.configFile, PalodonConfig::class.java)
        }

        val configChanged = applyEnvironmentVariables(config)

        if (configChanged || !this.configFile.exists()) {
            this.mapper.writerWithDefaultPrettyPrinter().writeValue(this.configFile, config)
            if (configChanged) {
                this.logger.info("Configuration file ${this.configFile.name} updated with environment variables.")
            }
        }

        validateConfig(config)
        applyToProperties(config)
    }

    private fun applyEnvironmentVariables(config: PalodonConfig): Boolean {
        var changed = false
        System.getenv(EnvironmentVariable.DB_TYPE)?.let {
            try {
                val type = DatabaseType.valueOf(it.uppercase())
                if (config.database.type != type) {
                    config.database.type = type
                    changed = true
                }
            } catch (_: IllegalArgumentException) {
                this.logger.warn("Invalid ${EnvironmentVariable.DB_TYPE} environment variable: {}", it)
            }
        }
        System.getenv(EnvironmentVariable.DB_POSTGRES_HOST)?.let {
            if (config.database.postgres.host != it) {
                config.database.postgres.host = it
                changed = true
            }
        }
        System.getenv(EnvironmentVariable.DB_POSTGRES_PORT)?.let {
            try {
                val port = it.toInt()
                if (config.database.postgres.port != port) {
                    config.database.postgres.port = port
                    changed = true
                }
            } catch (_: NumberFormatException) {
                this.logger.warn("Invalid ${EnvironmentVariable.DB_POSTGRES_PORT} environment variable: {}", it)
            }
        }
        System.getenv(EnvironmentVariable.DB_POSTGRES_DATABASE)?.let {
            if (config.database.postgres.databaseName != it) {
                config.database.postgres.databaseName = it
                changed = true
            }
        }
        System.getenv(EnvironmentVariable.DB_POSTGRES_USERNAME)?.let {
            if (config.database.postgres.username != it) {
                config.database.postgres.username = it
                changed = true
            }
        }
        System.getenv(EnvironmentVariable.DB_POSTGRES_PASSWORD)?.let {
            if (config.database.postgres.password != it) {
                config.database.postgres.password = it
                changed = true
            }
        }
        System.getenv(EnvironmentVariable.DB_SQLITE_PATH)?.let {
            if (config.database.sqlite.path != it) {
                config.database.sqlite.path = it
                changed = true
            }
        }
        return changed
    }

    private fun validateConfig(config: PalodonConfig) {
        val validator: Validator = Validation.byDefaultProvider()
            .configure()
            .traversableResolver(object : jakarta.validation.TraversableResolver {
                override fun isReachable(
                    traversableObject: Any?,
                    traversableProperty: jakarta.validation.Path.Node?,
                    rootBeanType: Class<*>?,
                    pathToTraversableObject: jakarta.validation.Path?,
                    elementType: java.lang.annotation.ElementType?
                ): Boolean = true

                override fun isCascadable(
                    traversableObject: Any?,
                    traversableProperty: jakarta.validation.Path.Node?,
                    rootBeanType: Class<*>?,
                    pathToTraversableObject: jakarta.validation.Path?,
                    elementType: java.lang.annotation.ElementType?
                ): Boolean = true
            })
            .buildValidatorFactory()
            .validator
        val violations = validator.validate(config)
        
        if (violations.isNotEmpty()) {
            this.logger.error("Configuration validation failed:")
            val messages = violations.map { "- ${it.propertyPath}: ${it.message}" }
            messages.forEach { this.logger.error(it) }
            throw RuntimeException("Configuration validation failed:\n" + messages.joinToString("\n"))
        }
    }

    private fun applyToProperties(config: PalodonConfig) {
        when (config.database.type) {
            DatabaseType.SQLITE -> {
                properties[Properties.Quarkus.Datasource.DB_KIND] = DatabaseType.SQLITE.name.lowercase()
                properties[Properties.Quarkus.Datasource.JDBC.URL] = "jdbc:sqlite:${config.database.sqlite.path}"
                this.logger.info("Using SQLite database")
            }
            DatabaseType.POSTGRESQL -> {
                properties[Properties.Quarkus.Datasource.DB_KIND] = DatabaseType.POSTGRESQL.name.lowercase()
                properties[Properties.Quarkus.Datasource.JDBC.URL] = "jdbc:postgresql://${config.database.postgres.host}:${config.database.postgres.port}/${config.database.postgres.databaseName}"
                properties[Properties.Quarkus.Datasource.USERNAME] = config.database.postgres.username
                properties[Properties.Quarkus.Datasource.PASSWORD] = config.database.postgres.password
                this.logger.info("Using PostgreSQL database")
            }
        }
    }
}
