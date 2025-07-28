package org.example.controller;

import org.example.model.User;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;

public class SocketController {
    private static final Map<String, Socket> sockets = new ConcurrentHashMap<>();


    public static void addSocket(String username, Socket socket) {
        sockets.put(username, socket);
    }

    public static boolean isOnline(String username) {
        return sockets.containsKey(username);
    }

    public static Socket getSocket(String username) {
        return sockets.get(username);
    }

    public static boolean isSocketAlive(String username) {
        Socket socket = getSocket(username);
        return socket != null && !socket.isClosed() && socket.isConnected();
    }

    public static void removeSocket(String username) {
        Socket socket = sockets.remove(username);
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
