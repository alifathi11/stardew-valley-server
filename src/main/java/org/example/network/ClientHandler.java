package org.example.network;

import org.example.controller.AuthenticationController;
import org.example.data.DatabaseInitializer;
import org.example.factory.DataSourceFactory;
import org.example.model.Message;
import org.example.model.Type;
import org.example.repository.TokenRepository;
import org.example.repository.UserRepository;
import org.example.utils.MessageParser;

import javax.sql.DataSource;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private final DataSource dataSource;

    private final AuthenticationController authController;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.err.println("Error setting up I/O streams: " + e.getMessage());
        }

        // initialization
        this.dataSource = DataSourceFactory.createDataSource();
        DatabaseInitializer dbInit = new DatabaseInitializer(dataSource);
        try {
            dbInit.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.userRepository = new UserRepository(dataSource);
        this.tokenRepository = new TokenRepository(dataSource);
        this.authController = new AuthenticationController(userRepository);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String jsonMessage = in.readLine();

                if (jsonMessage == null) {
                    System.out.println("Client disconnected.");
                    break;
                }

                Message message;
                try {
                    message = MessageParser.fromJson(jsonMessage);
                } catch (Exception e) {
                    System.err.println("Invalid message received: " + jsonMessage);
                    continue;
                }

                Message response = handleMessage(message);

                try {
                    String jsonResponse = MessageParser.toJson(response);
                    sendResponse(jsonResponse);
                } catch (Exception e) {
                    System.err.println("Error serializing response.");
                }
            }
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private Message handleMessage(Message message) {
        Type type = message.getType();

        switch (type) {
            case SIGNUP:
                return authController.handleSignup(message);
            case LOGIN:
                return authController.handleLogin(message);
            default:
                return new Message(Type.ERROR, "Unsupported request type.");
        }
    }

    private void sendResponse(String response) {
        try {
            out.write(response);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            System.err.println("Error sending response: " + e.getMessage());
        }
    }
}
