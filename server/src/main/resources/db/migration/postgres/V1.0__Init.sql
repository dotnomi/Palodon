-- Init script for PostgreSQL

-- Create the palodon schema
CREATE SCHEMA IF NOT EXISTS palodon;

-- Create the user table
CREATE TABLE IF NOT EXISTS palodon."user" (
    user_id BIGINT PRIMARY KEY NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    displayname VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_user_username ON user(username);