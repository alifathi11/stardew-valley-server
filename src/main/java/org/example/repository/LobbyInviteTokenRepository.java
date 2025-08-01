package org.example.repository;

import org.hibernate.annotations.processing.SQL;

import javax.sql.DataSource;
import javax.swing.text.html.Option;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

public class LobbyInviteTokenRepository {
    private static LobbyInviteTokenRepository instance;

    private final DataSource dataSource;
    private final SecureRandom secureRandom = new SecureRandom();
    private final long EXPIRY_SECONDS = 300;

    private LobbyInviteTokenRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void init(DataSource dataSource) {
        if (instance != null) return;
        instance = new LobbyInviteTokenRepository(dataSource);
    }

    public static LobbyInviteTokenRepository getInstance() {
        return instance;
    }

    public String generateToken(String invitedUserId, String lobbyId) {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        Instant expiry = Instant.now().plusSeconds(EXPIRY_SECONDS);
        String sql = "INSERT INTO lobby_invite_tokens (token, expires_at, invited_user_id, lobby_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.setString(2, expiry.toString());
            stmt.setString(3, invitedUserId);
            stmt.setString(4, lobbyId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return token;
    }

    public Optional<String> consumeToken(String token, String userId) {
        String sql = "SELECT invited_user_id, lobby_id, expires_at FROM lobby_invite_tokens WHERE token = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Instant expiry = Instant.parse(rs.getString("expires_at"));
                String lobbyId = rs.getString("lobby_id");
                String invitedUserId = rs.getString("invited_user_id");

                if (!invitedUserId.equalsIgnoreCase(userId)) return Optional.empty();

                if (expiry.isAfter(Instant.now())) {
                    invalidateToken(token);
                    return Optional.of(lobbyId);
                } else {
                    invalidateToken(token);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public void invalidateToken(String token) {
        String sql = "DELETE FROM lobby_invite_tokens WHERE token = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
