package org.example.network;

import jakarta.persistence.Lob;
import org.example.controller.MessageHandler;
import org.example.global.LobbyManager;
import org.example.model.Lobby;
import org.example.model.LobbyInvitation;
import org.example.model.Message;
import org.example.model.Type;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class InvitationProcessor {
    private final ClientHandler clientHandler;
    private final BlockingQueue<LobbyInvitation> globalInvitationQueue;

    public InvitationProcessor(ClientHandler clientHandler, BlockingQueue<LobbyInvitation> globalInvitationQueue) {
        this.clientHandler = clientHandler;
        this.globalInvitationQueue = globalInvitationQueue;
    }

    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    // Take invitation
                    LobbyInvitation invitation = globalInvitationQueue.take();
                    Lobby lobby = LobbyManager.getLobby(invitation.getLobbyId());

                    // Build invitation message
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("from_user", invitation.getFromUser());
                    payload.put("lobby_id", invitation.getLobbyId());
                    payload.put("members", lobby.getMembers());
                    payload.put("invitation_token", invitation.getToken());

                    Message invitaionMessage = new Message(Type.INVITATION, payload);

                    // Send to target user
                    String targetUsername = invitation.getToUser();
                    ClientConnection client = clientHandler.getClientByUsername(targetUsername);
                    if (client != null) {
                        client.send(invitaionMessage);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "RequestProcessor").start();
    }
}
