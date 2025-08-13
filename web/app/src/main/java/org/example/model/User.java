package org.example.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, unique = true)
    private String username;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(columnDefinition = "TEXT CHECK(gender IN ('MALE', 'FEMALE'))")
    private String gender;

    @Column(name = "security_question")
    private String securityQuestion;

    @Column(name = "is_in_anyGame", nullable = false)
    private Boolean isInAnyGame = false;

    private Integer score = 0;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getSecurityQuestion() { return securityQuestion; }
    public void setSecurityQuestion(String securityQuestion) { this.securityQuestion = securityQuestion; }

    public Boolean getIsInAnyGame() { return isInAnyGame; }
    public void setIsInAnyGame(Boolean isInAnyGame) { this.isInAnyGame = isInAnyGame; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    @PrePersist
    public void ensureId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }
}
