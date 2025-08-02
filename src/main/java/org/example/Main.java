package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.data.DatabaseInitializer;
import org.example.model.Gender;
import org.example.model.SecurityQuestion;
import org.example.model.User;
import org.example.network.GameServer;
import org.example.network.ServerConfig;
import org.example.repository.LobbyInviteTokenRepository;
import org.example.repository.TokenRepository;
import org.example.repository.UserRepository;
import org.example.utils.ConfigLoader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Stack;

public class Main {
    public static void main(String[] args) throws Exception {

        // Init DB
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:src/main/java/org/example/data/users.db");

        config.setMaximumPoolSize(2);

        config.addDataSourceProperty("busy_timeout", 5000);

        HikariDataSource ds = new HikariDataSource(config);

        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA journal_mode=WAL");
        }

        DatabaseInitializer dbInit = new DatabaseInitializer(ds);
        dbInit.run();

        // Initialize repositories
        UserRepository.init(ds);
        TokenRepository.init(ds);
        LobbyInviteTokenRepository.init(ds);

        // Load server config
        // TODO: use config
        String configPath = "src/main/java/org/example/configs/server.conf";
        ServerConfig serverConfig = ConfigLoader.load(configPath);

        // Run server
        try {
            new GameServer().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
