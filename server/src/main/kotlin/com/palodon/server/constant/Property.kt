package com.palodon.server.constant

object Property {
    object Quarkus {
        object Datasource {
            object Jdbc {
                const val URL = "quarkus.datasource.jdbc.url"
            }

            const val DB_KIND = "quarkus.datasource.db-kind"
            const val USERNAME = "quarkus.datasource.username"
            const val PASSWORD = "quarkus.datasource.password"
        }
    }
}