package com.palodon.server.persistence.migration

import com.palodon.server.enumerator.DatabaseType
import com.palodon.server.service.ConfigService
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import javax.sql.DataSource

@ApplicationScoped
class FlywayMigrator(
    @Suppress("CdiInjectionPointsInspection") private val dataSource: DataSource
) {
    private val logger = LoggerFactory.getLogger(FlywayMigrator::class.java)

    fun onStart(@Observes ev: StartupEvent) {
        logger.info("Starting database migration...")
        
        val config = ConfigService.config

        val location = when (config.database.type) {
            DatabaseType.POSTGRESQL -> "classpath:db/migration/postgres"
            DatabaseType.SQLITE -> "classpath:db/migration/sqlite"
        }

        logger.info("Using Flyway migration location: $location")

        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations(location)
            .baselineOnMigrate(true)
            .table("palodon_flyway_history")
            .load()

        val result = flyway.migrate()
        if (result.migrationsExecuted > 0) {
            logger.info("Successfully applied ${result.migrationsExecuted} migrations")
        } else {
            logger.info("Database is up to date, no migrations applied")
        }
    }
}
