-- Init script for PostgreSQL

-- Create the palodon schema
CREATE SCHEMA IF NOT EXISTS palodon;

-- Create the user table
CREATE TABLE IF NOT EXISTS palodon."user" (
    "user_id" BIGINT PRIMARY KEY NOT NULL UNIQUE,
    "username" VARCHAR(255) NOT NULL UNIQUE,
    "displayname" VARCHAR(255) NOT NULL,
    "password_hash" VARCHAR(255) NOT NULL,
    "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "deleted_at" TIMESTAMP
);

CREATE INDEX idx_user_username ON user(username);

-- Create the session table
CREATE TABLE IF NOT EXISTS palodon."session" (
    "session_id" BIGINT PRIMARY KEY NOT NULL UNIQUE,
    "user_id" BIGINT NOT NULL,
    "refresh_token_hash" VARCHAR(255) NOT NULL UNIQUE,
    "expires_at" TIMESTAMP NOT NULL,
    "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_used_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "user_agent" VARCHAR(255),
    "ip_address" VARCHAR(255),
    "is_revoked" BOOLEAN NOT NULL,
    CONSTRAINT fk_session_user_id
    FOREIGN KEY(user_id)
    REFERENCES palodon."user"(user_id)
    ON DELETE CASCADE
);