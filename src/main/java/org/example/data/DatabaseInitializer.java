package org.example.data;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    private final DataSource dataSource;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void run(String... args) throws Exception {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {

            // Create users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id TEXT PRIMARY KEY NOT NULL,
                    username TEXT NOT NULL UNIQUE,
                    name TEXT,
                    email TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    gender TEXT CHECK(gender IN ('MALE', 'FEMALE')),
                    security_question TEXT,
                    is_in_any_game BOOL NOT NULL DEFAULT FALSE,
                    score INTEGER DEFAULT 0,
                    avatar_path TEXT,
                    token TEXT
                );
            """);


            // Create auth-token table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS auth_tokens (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    token TEXT NOT NULL,
                    expires_at TEXT NOT NULL,
                    user_id TEXT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                );
            """);

            // Create persistent token for stay-logged-in
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS persistent_tokens (
                    token TEXT PRIMARY KEY,
                    expires_at TEXT NOT NULL,
                    user_id TEXT NOT NULL
                );
            """);

            // Create invitation-token table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS lobby_invite_tokens (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    token TEXT NOT NULL,
                    expires_at TEXT NOT NULL,
                    invited_user_id TEXT NOT NULL,
                    lobby_id TEXT NOT NULL
                );
            """);

            System.out.println("✅ SQLite tables initialized.");
        }
    }
}
