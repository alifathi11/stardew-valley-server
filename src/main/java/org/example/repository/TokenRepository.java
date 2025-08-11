package org.example.repository;

import javax.sql.DataSource;
import java.security.SecureRandom;
import java.sql.*;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

public class TokenRepository {

    private static TokenRepository instance;

    private final DataSource dataSource;
    private final SecureRandom secureRandom = new SecureRandom();

    // Short-lived auth token (1 hour)
    private final long EXPIRY_SECONDS = 3600;

    // Long-lived persistent token (90 days)
    private final long PERSISTENT_EXPIRY_SECONDS = 90L * 24 * 60 * 60;

    private TokenRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void init(DataSource dataSource) {
        if (instance != null) return;
        instance = new TokenRepository(dataSource);
    }

    public static TokenRepository getInstance() {
        return instance;
    }

    // ------------------- NORMAL TOKEN -------------------

    public String generateToken(String userId) {
        String token = createRandomToken();
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
        return getUserIdFromTable(token, "auth_tokens", true);
    }

    public void invalidateToken(String token) {
        deleteTokenFromTable(token, "auth_tokens");
    }

    // ------------------- PERSISTENT TOKEN -------------------

    public String generatePersistentToken(String userId) {
        String token = createRandomToken();
        Instant expiry = Instant.now().plusSeconds(PERSISTENT_EXPIRY_SECONDS);

        String insert = "INSERT INTO persistent_tokens (token, expires_at, user_id) VALUES (?, ?, ?)";
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

    public Optional<String> getUserIdFromPersistentToken(String token) {
        return getUserIdFromTable(token, "persistent_tokens", false);
    }

    public void invalidatePersistentToken(String token) {
        deleteTokenFromTable(token, "persistent_tokens");
    }

    // ------------------- HELPER METHODS -------------------

    private String createRandomToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private Optional<String> getUserIdFromTable(String token, String table, boolean deleteIfExpired) {
        String query = "SELECT user_id, expires_at FROM " + table + " WHERE token = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Instant expiry = Instant.parse(rs.getString("expires_at"));
                if (Instant.now().isAfter(expiry)) {
                    if (deleteIfExpired) deleteTokenFromTable(token, table);
                    return Optional.empty();
                }
                return Optional.of(rs.getString("user_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void deleteTokenFromTable(String token, String table) {
        String delete = "DELETE FROM " + table + " WHERE token = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(delete)) {
            stmt.setString(1, token);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
