package org.example.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {
    private final ServerConfig config;

    public ServerApp(ServerConfig config) {
        this.config = config;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(config.getPort())) {

            System.out.println("Server run on port " + config.getPort());

            while (true) {
                // accept user connection
                Socket socket = serverSocket.accept();

                System.out.println("New connection from " + socket.getInetAddress());

                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Failed to run server. Closing the program...");
        }
    }
}
