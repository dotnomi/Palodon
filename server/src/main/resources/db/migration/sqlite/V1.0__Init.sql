-- Init script for SQLite

-- Create the user table
CREATE TABLE IF NOT EXISTS palodon_user (
    user_id BIGINT PRIMARY KEY NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    displayname VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted_at DATETIME
);

CREATE INDEX IF NOT EXISTS idx_user_username ON palodon_user(username);
