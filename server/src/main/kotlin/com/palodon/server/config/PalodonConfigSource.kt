package com.palodon.server.config

import com.palodon.server.constant.Property
import com.palodon.server.enumerator.DatabaseType
import com.palodon.server.service.ConfigService
import org.eclipse.microprofile.config.spi.ConfigSource
import org.slf4j.LoggerFactory

class PalodonConfigSource : ConfigSource {
    private val logger = LoggerFactory.getLogger(PalodonConfigSource::class.java)
    private val properties = mutableMapOf<String, String>()

    init {
        applyToProperties(ConfigService.config)
    }

    override fun getProperties(): Map<String, String> = properties

    override fun getPropertyNames(): Set<String> = properties.keys

    override fun getValue(propertyName: String): String? = properties[propertyName]

    override fun getName(): String = "PalodonConfigSource"

    override fun getOrdinal(): Int = 275

    private fun applyToProperties(config: PalodonConfig) {
        when (config.database.type) {
            DatabaseType.SQLITE -> {
                properties[Property.Quarkus.Datasource.DB_KIND] = DatabaseType.SQLITE.name.lowercase()
                properties[Property.Quarkus.Datasource.Jdbc.URL] = "jdbc:sqlite:${config.database.sqlite.path}"
                this.logger.info("Using SQLite database")
            }
            DatabaseType.POSTGRESQL -> {
                properties[Property.Quarkus.Datasource.DB_KIND] = DatabaseType.POSTGRESQL.name.lowercase()
                properties[Property.Quarkus.Datasource.Jdbc.URL] = "jdbc:postgresql://${config.database.postgres.host}:${config.database.postgres.port}/${config.database.postgres.databaseName}"
                properties[Property.Quarkus.Datasource.USERNAME] = config.database.postgres.username
                properties[Property.Quarkus.Datasource.PASSWORD] = config.database.postgres.password
                this.logger.info("Using PostgreSQL database")
            }
        }
    }
}
