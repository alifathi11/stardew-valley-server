package org.example.model.user;

import org.example.model.consts.Gender;

import java.sql.ResultSet;
import java.sql.SQLException;


public class User {

    private String id;
    private String username;
    private String name;
    private String email;
    private String passwordHash;
    private Gender gender;
    private SecurityQuestion securityQuestion;
    private boolean isInAnyGame;
    private int score;

    public User(String id,
                String username,
                String name,
                String email,
                String passwordHash,
                Gender gender,
                SecurityQuestion securityQuestion,
                boolean isInAnyGame,
                int score) {

        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.gender = gender;
        this.securityQuestion = securityQuestion;
        this.isInAnyGame = isInAnyGame;
        this.score = score;
    }

    public static User fromResultSet(ResultSet rs) {

        try {
            return new User(
                rs.getString("id"),
                rs.getString("username"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                Gender.fromString(rs.getString("gender")),
                SecurityQuestion.fromString(rs.getString("security_question")),
                rs.getBoolean("is_in_any_game"),
                rs.getInt("score")
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public SecurityQuestion getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(SecurityQuestion securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isInAnyGame() {
        return isInAnyGame;
    }

    public void setInAnyGame(boolean inAnyGame) {
        isInAnyGame = inAnyGame;
    }
}
