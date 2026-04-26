package com.palodon.server.constant

object Environment {
    object Database {
        object Postgres {
            const val HOST = "PALODON_DB_POSTGRES_HOST"
            const val PORT = "PALODON_DB_POSTGRES_PORT"
            const val DATABASE = "PALODON_DB_POSTGRES_DATABASE"
            const val USERNAME = "PALODON_DB_POSTGRES_USERNAME"
            const val PASSWORD = "PALODON_DB_POSTGRES_PASSWORD"
        }

        object SQLite {
            const val PATH = "PALODON_DB_SQLITE_PATH"
        }

        const val TYPE = "PALODON_DB_TYPE"
    }
}