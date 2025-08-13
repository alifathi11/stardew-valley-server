package org.example.service;

import org.example.model.PasswordResetToken;
import org.example.model.User;
import org.example.repository.PasswordResetTokenRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private static final int EXPIRATION_HOURS = 1;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    public void createPasswordResetTokenForEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User with given email not found");
        }

        User user = userOpt.get();

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(EXPIRATION_HOURS);

        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiry);
        tokenRepository.save(resetToken);

        String link = "http://localhost:8080/reset-password?token=" + token;
        emailService.sendEmail(user.getEmail(), "Password Reset", "Click the link to reset your password:\n" + link);
    }

    public User validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid token");
        }

        PasswordResetToken resetToken = tokenOpt.get();
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        return resetToken.getUser();
    }

    public void updatePassword(String token, String newPasswordHash) {
        User user = validatePasswordResetToken(token);
        user.setPasswordHash(newPasswordHash);
        userRepository.save(user);

        tokenRepository.deleteById(
            tokenRepository.findByToken(token).get().getId()
        );
    }
}
