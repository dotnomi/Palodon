package com.palodon.server.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.runtime.Quarkus
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

    private fun loadConfig() {
        val config = if (!this.configFile.exists()) {
            createDefaultConfig()
        } else {
            this.logger.info("Loading configuration file ${this.configFile.absolutePath}...")
            this.mapper.readValue(this.configFile, PalodonConfig::class.java)
        }

        validateConfig(config)
        applyToProperties(config)
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

    private fun createDefaultConfig(): PalodonConfig {
        val default = PalodonConfig()
        this.logger.warn("No ${this.configFile.name} found. Creating standard configuration...")
        this.mapper.writerWithDefaultPrettyPrinter().writeValue(this.configFile, default)
        return default
    }

    private fun applyToProperties(config: PalodonConfig) {
        when (config.database.type) {
            DatabaseType.SQLITE -> {
                properties["quarkus.datasource.db-kind"] = "sqlite"
                properties["quarkus.datasource.jdbc.url"] = "jdbc:sqlite:${config.database.sqlite.path}"
                this.logger.info("Using SQLite database")
            }
            DatabaseType.POSTGRESQL -> {
                properties["quarkus.datasource.db-kind"] = "postgresql"
                properties["quarkus.datasource.jdbc.url"] = "jdbc:postgresql://${config.database.postgres.host}:${config.database.postgres.port}/${config.database.postgres.databaseName}"
                properties["quarkus.datasource.username"] = config.database.postgres.username
                properties["quarkus.datasource.password"] = config.database.postgres.password
                this.logger.info("Using PostgreSQL database")
            }
        }
    }

    override fun getProperties(): Map<String, String> = properties

    override fun getPropertyNames(): Set<String> = properties.keys

    override fun getValue(propertyName: String): String? = properties[propertyName]

    override fun getName(): String = "PalodonConfigSource"

    override fun getOrdinal(): Int = 275
}
