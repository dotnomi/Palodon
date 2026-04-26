package com.palodon.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.palodon.server.config.PalodonConfig
import com.palodon.server.constant.Environment
import com.palodon.server.enumerator.DatabaseType
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.slf4j.LoggerFactory
import java.io.File

object ConfigService {
    private val logger = LoggerFactory.getLogger(ConfigService::class.java)
    private val mapper = ObjectMapper()
    private val configFile = File("palodon.config.json")

    val config: PalodonConfig by lazy {
        loadConfig()
    }

    private fun loadConfig(): PalodonConfig {
        val loadedConfig = if (!configFile.exists()) {
            logger.warn("No ${configFile.name} found. Creating standard configuration...")
            PalodonConfig()
        } else {
            logger.info("Loading configuration file ${configFile.absolutePath}...")
            mapper.readValue(configFile, PalodonConfig::class.java)
        }

        val configChanged = applyEnvironmentVariables(loadedConfig)

        if (configChanged || !configFile.exists()) {
            mapper.writerWithDefaultPrettyPrinter().writeValue(configFile, loadedConfig)
            if (configChanged) {
                logger.info("Configuration file ${configFile.name} updated with environment variables.")
            }
        }

        validateConfig(loadedConfig)
        return loadedConfig
    }

    private fun applyEnvironmentVariables(config: PalodonConfig): Boolean {
        var changed = false
        System.getenv(Environment.Database.TYPE)?.let {
            try {
                val type = DatabaseType.valueOf(it.uppercase())
                if (config.database.type != type) {
                    config.database.type = type
                    changed = true
                }
            } catch (_: IllegalArgumentException) {
                logger.warn("Invalid ${Environment.Database.TYPE} environment variable: {}", it)
            }
        }
        System.getenv(Environment.Database.Postgres.HOST)?.let {
            if (config.database.postgres.host != it) {
                config.database.postgres.host = it
                changed = true
            }
        }
        System.getenv(Environment.Database.Postgres.PORT)?.let {
            try {
                val port = it.toInt()
                if (config.database.postgres.port != port) {
                    config.database.postgres.port = port
                    changed = true
                }
            } catch (_: NumberFormatException) {
                logger.warn("Invalid ${Environment.Database.Postgres.PORT} environment variable: {}", it)
            }
        }
        System.getenv(Environment.Database.Postgres.DATABASE)?.let {
            if (config.database.postgres.databaseName != it) {
                config.database.postgres.databaseName = it
                changed = true
            }
        }
        System.getenv(Environment.Database.Postgres.USERNAME)?.let {
            if (config.database.postgres.username != it) {
                config.database.postgres.username = it
                changed = true
            }
        }
        System.getenv(Environment.Database.Postgres.PASSWORD)?.let {
            if (config.database.postgres.password != it) {
                config.database.postgres.password = it
                changed = true
            }
        }
        System.getenv(Environment.Database.SQLite.PATH)?.let {
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
            logger.error("Configuration validation failed:")
            val messages = violations.map { "- ${it.propertyPath}: ${it.message}" }
            messages.forEach { logger.error(it) }
            throw RuntimeException("Configuration validation failed:\n" + messages.joinToString("\n"))
        }
    }
}
