package com.palodon.server.persistence

import com.palodon.server.enumerator.DatabaseType
import com.palodon.server.service.ConfigService
import org.hibernate.boot.model.naming.Identifier
import org.hibernate.boot.model.naming.PhysicalNamingStrategy
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment

class PalodonPhysicalNamingStrategy : PhysicalNamingStrategy {
    override fun toPhysicalCatalogName(name: Identifier?, jdbcEnvironment: JdbcEnvironment?): Identifier? = name

    override fun toPhysicalSchemaName(name: Identifier?, jdbcEnvironment: JdbcEnvironment?): Identifier? {
        val type = ConfigService.config.database.type
        if (type == DatabaseType.SQLITE) {
            // SQLite does not support schemas natively. Remove the schema name to prevent query errors.
            return null
        }
        return name
    }

    override fun toPhysicalTableName(name: Identifier?, jdbcEnvironment: JdbcEnvironment?): Identifier? {
        val type = ConfigService.config.database.type
        if (type == DatabaseType.SQLITE && name != null) {
            // SQLite does not support schemas natively. Add a prefix to the table name to prevent query errors.
            return Identifier("palodon_${name.text}", name.isQuoted)
        }
        return name
    }

    override fun toPhysicalSequenceName(name: Identifier?, jdbcEnvironment: JdbcEnvironment?): Identifier? = name

    override fun toPhysicalColumnName(name: Identifier?, jdbcEnvironment: JdbcEnvironment?): Identifier? = name
}
