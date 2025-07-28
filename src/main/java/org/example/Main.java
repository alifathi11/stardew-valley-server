package org.example;

import org.example.network.ServerApp;
import org.example.network.ServerConfig;
import org.example.utils.ConfigLoader;

public class Main {
    public static void main(String[] args) {

        // load server config
        String configPath = "configs/server.conf";
        ServerConfig config = ConfigLoader.load(configPath);

        // run server
        ServerApp app = new ServerApp(config);
        app.run();
    }
}
