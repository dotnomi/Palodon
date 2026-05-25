-- Init script for SQLite

-- Create the user table
CREATE TABLE IF NOT EXISTS palodon_user (
    "user_id" BIGINT PRIMARY KEY NOT NULL UNIQUE,
    "username" VARCHAR(255) NOT NULL UNIQUE,
    "displayname" VARCHAR(255) NOT NULL,
    "password_hash" VARCHAR(255) NOT NULL,
    "created_at" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" DATETIME DEFAULT CURRENT_TIMESTAMP,
    "deleted_at" DATETIME
);

CREATE INDEX IF NOT EXISTS idx_user_username ON palodon_user(username);

-- Create the session table
CREATE TABLE IF NOT EXISTS palodon_session (
    "session_id" BIGINT PRIMARY KEY NOT NULL UNIQUE,
    "user_id" BIGINT NOT NULL,
    "refresh_token_hash" VARCHAR(255) NOT NULL UNIQUE,
    "expires_at" DATETIME NOT NULL,
    "created_at" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_used_at" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "user_agent" VARCHAR(255),
    "ip_address" VARCHAR(255),
    "is_revoked" INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY(user_id) REFERENCES palodon_user(user_id) ON DELETE CASCADE
);
