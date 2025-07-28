package org.example.repository;

import javax.sql.DataSource;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

public class TokenRepository {

    private final DataSource dataSource;
    private final SecureRandom secureRandom = new SecureRandom();
    private final long EXPIRY_SECONDS = 3600;

    public TokenRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String generateToken(String userId) {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        Instant expiry = Instant.now().plusSeconds(EXPIRY_SECONDS);

        String insert = "INSERT INTO auth_tokens (token, expires_at, user_id) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setString(1, token);
            stmt.setString(2, expiry.toString());
            stmt.setString(3, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return token;
    }

    public Optional<String> getUserId(String token) {
        String query = "SELECT user_id, expires_at FROM auth_tokens WHERE token = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Instant expiry = Instant.parse(rs.getString("expires_at"));
                if (Instant.now().isAfter(expiry)) {
                    invalidateToken(token);
                    return Optional.empty();
                }
                return Optional.of(rs.getString("user_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void invalidateToken(String token) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM auth_tokens WHERE token = ?")) {
            stmt.setString(1, token);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
