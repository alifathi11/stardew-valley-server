package org.example.model.lobby_models;

import org.example.model.consts.Gender;
import org.example.model.consts.LobbyState;
import org.example.model.user.User;
import org.example.model.game_models.Game;
import org.example.network.GameSession;
import org.example.repository.UserRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Lobby {
    private final String id;
    private String name;
    private String hostUsername;
    private final Set<String> members;
    private final Map<String, Integer> userToMapNumber;
    private final Map<String, String> playerNames;
    private final Map<String, Gender> playerGenders;
    private LobbyState state;
    private boolean isPrivate;
    private boolean isVisible;
    private String passwordHash;
    private GameSession session;
    private Game game;


    public Lobby(String id,
                 String name,
                 String hostUsername,
                 boolean isPrivate,
                 boolean isVisible,
                 String passwordHash) {

        this.id = id;
        this.name = name;
        this.hostUsername = hostUsername;
        this.isVisible = isVisible;
        this.isPrivate = isPrivate;
        this.passwordHash = passwordHash;

        this.members = ConcurrentHashMap.newKeySet();
        this.userToMapNumber = new ConcurrentHashMap<>();
        this.playerNames = new ConcurrentHashMap<>();
        this.playerGenders = new ConcurrentHashMap<>();
        this.state = LobbyState.WAITING;

    }


    public Lobby(String id,
                 String hostUsername,
                 Set<String> members,
                 Map<String, Integer> userToMapNumber,
                 Map<String, String> playerNames,
                 Map<String, Gender> playerGenders,
                 LobbyState state,
                 boolean isPrivate,
                 boolean isVisible,
                 String password) {

        this.id = id;
        this.hostUsername = hostUsername;
        this.members = members;
        this.userToMapNumber = userToMapNumber;
        this.playerNames = playerNames;
        this.playerGenders = playerGenders;
        this.state = state;
        this.isPrivate = isPrivate;
        this.isVisible = isVisible;
        this.passwordHash = password;

        this.members.add(hostUsername);
    }

    public void setSession(GameSession session) {
        this.session = session;
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

        Optional<User> userOpt = UserRepository.getInstance().findByUsername(member);
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();

        this.playerNames.put(member, user.getName());
        this.playerGenders.put(member, user.getGender());
    }

    public void removeMember(String member) {

        this.members.remove(member);

        this.playerNames.remove(member);
        this.playerGenders.remove(member);
    }

    public String getHostUsername() {
        return hostUsername;
    }

    public void setHostUsername(String hostUsername) {
        this.hostUsername = hostUsername;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public Map<String, Integer> getUserToMapNumber() {
        return userToMapNumber;
    }

    public GameSession getSession() {
        return session;
    }

    public Map<String, Gender> getPlayerGenders() {
        return playerGenders;
    }

    public Map<String, String> getPlayerNames() {
        return playerNames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
