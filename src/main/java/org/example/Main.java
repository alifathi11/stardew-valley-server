package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.data.DatabaseInitializer;
import org.example.network.GameServer;
import org.example.network.ServerConfig;
import org.example.repository.TokenRepository;
import org.example.repository.UserRepository;
import org.example.utils.ConfigLoader;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {

        // Init DB
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:src/main/java/org/example/data/users.db");
        HikariDataSource ds = new HikariDataSource(config);

        DatabaseInitializer dbInit = new DatabaseInitializer(ds);
        dbInit.run();

        // Initialize repositories
        UserRepository.init(ds);
        TokenRepository.init(ds);

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
