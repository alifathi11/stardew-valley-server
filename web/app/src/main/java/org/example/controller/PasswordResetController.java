package org.example.controller;

import org.example.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @CrossOrigin(origins = "*")
    @PostMapping("/request-password-reset")
    public String requestReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return "Email is required.";
        }

        try {
            passwordResetService.createPasswordResetTokenForEmail(email);
            return "Password reset link has been sent.";
        } catch (IllegalArgumentException ex) {
            return ex.getMessage();
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if (token == null || newPassword == null) {
            return "Token and newPassword are required.";
        }

        try {
            String hashed = passwordEncoder.encode(newPassword);
            passwordResetService.updatePassword(token, hashed);
            return "Password updated successfully.";
        } catch (IllegalArgumentException ex) {
            return ex.getMessage();
        }
    }
}
