package org.example.network;

import org.example.controller.MessageHandler;
import org.example.model.message_center.Message;

import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

public class RequestProcessor {
    private final ClientHandler clientHandler;
    private final BlockingQueue<Message> globalRequestQueue;

    public RequestProcessor(ClientHandler clientHandler, BlockingQueue<Message> globalRequestQueue) {
        this.clientHandler = clientHandler;
        this.globalRequestQueue = globalRequestQueue;
    }

    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    Message request = globalRequestQueue.take();
                    Message response = MessageHandler.handle(request);

                    SocketChannel source = request.getSource();
                    ClientConnection client = clientHandler.getClientByChannel(source);
                    if (client != null && response != null) {
                        client.send(response);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "RequestProcessor").start();
    }
}
