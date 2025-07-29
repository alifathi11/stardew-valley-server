package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.model.User;
import org.hibernate.annotations.processing.SQL;
import org.springframework.stereotype.Repository;


import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


public class UserRepository {

    private static UserRepository instance;

    private final DataSource dataSource;

    private UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void init(DataSource dataSource) {
        if (instance != null) return;

        instance = new UserRepository(dataSource);
    }

    public static UserRepository getInstance() {
        return instance;
    }

    public boolean usernameExists(String username) {
        String query = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            return stmt.executeQuery().next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean emailExists(String email) {
        String query = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            return stmt.executeQuery().next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void save(User user) {
        String insert = """
            INSERT INTO users (id, username, name, email, password_hash, gender, security_question, is_in_any_game, score)
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPasswordHash());
            stmt.setString(6, user.getGender().name());

            String securityQuestion = user.getSecurityQuestion().getQuestion() + ":" + user.getSecurityQuestion().getAnswer();
            stmt.setString(7, securityQuestion);
            stmt.setBoolean(8, user.isInAnyGame());
            stmt.setInt(9, user.getScore());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<User> findByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(User.fromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
