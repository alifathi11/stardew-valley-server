package org.example.model;

import jakarta.persistence.Lob;

import java.util.Set;
import java.util.UUID;

public class Lobby {
    private final String id;
    private String hostUsername;
    private final Set<String> members;
    private LobbyState state;
    private boolean isPrivate;
    private boolean isVisible;
    private String password;


    public Lobby(String id,
                 String hostUsername,
                 Set<String> members,
                 LobbyState state,
                 boolean isPrivate,
                 boolean isVisible,
                 String password) {

        this.id = id;
        this.hostUsername = hostUsername;
        this.members = members;
        this.state = state;
        this.isPrivate = isPrivate;
        this.isVisible = isVisible;
        this.password = password;

        this.members.add(hostUsername);
    }

    public String getId() {
        return id;
    }

    public LobbyState getState() {
        return state;
    }

    public void setState(LobbyState state) {
        this.state = state;
    }

    public Set<String> getMembers() {
        return members;
    }

    public void addMember(String member) {
        this.members.add(member);
    }

    public void removeMember(String member) {
        this.members.remove(member);
    }

    public String getHostUsername() {
        return hostUsername;
    }

    public void setHostUsername(String hostUsername) {
        this.hostUsername = hostUsername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }
}
