package org.example.model;

public class LobbyMember {
    private String username;
    private String name;
    private Gender gender;

    public LobbyMember(String username,
                       String name,
                       Gender gender) {
        this.username = username;
        this.name = name;
        this.gender = gender;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
