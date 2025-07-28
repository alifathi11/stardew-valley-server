package org.example.controller;

import org.example.repository.TokenRepository;
import org.example.model.*;
import org.example.repository.UserRepository;
import org.example.utils.Hasher;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthenticationController {

    private UserRepository userRepository;
    private TokenRepository tokenManager;

    public AuthenticationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Message handleSignup(Message message) {
        if (message.getType() != Type.SIGNUP) return Message.invalidArgument();

        // extract data
        String username = (String) message.getFromPayload("username");
        String name = (String) message.getFromPayload("name");
        Gender gender = Gender.fromString((String) message.getFromPayload("gender"));
        String email = (String) message.getFromPayload("email");
        String password = (String) message.getFromPayload("password");
        String question = (String) message.getFromPayload("security_question");
        String answer = (String) message.getFromPayload("answer");
        SecurityQuestion securityQuestion = new SecurityQuestion(question, answer);

        // validation
        Message validation = Validator.validateSignup(username, email, password);
        if (validation.getType() == Type.ERROR) {
            return validation;
        }

        // add to database
        String passwordHash = Hasher.hash(password);
        userRepository.save(new User(UUID.randomUUID().toString(),
                                     username,
                                     name,
                                     email,
                                     passwordHash,
                                     gender,
                                     securityQuestion,
                                     false,
                                     0));

        // build response
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "success");
        payload.put("message", "You have singed up successfully.");
        Message response = new Message(Type.SUCCESS, payload);

        return response;

    }

    public Message handleLogin(Message message) {
        if (message.getType() != Type.LOGIN) return Message.invalidArgument();

        String username = (String) message.getFromPayload("username");
        String password = (String) message.getFromPayload("password");

        if (username == null || password == null)
            return Message.error("Username and password are required.");

        User user;
        if (userRepository.findByUsername(username).isPresent())
            user = userRepository.findByUsername(username).get();
        else return Message.error("User not found.");

        if (!Hasher.validate(password, user.getPasswordHash()))
            return Message.error("Invalid password.");

        String token = tokenManager.generateToken(username);

        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "success");
        payload.put("message", "Logged in successfully.");
        payload.put("token", token);

        return new Message(Type.SUCCESS, payload);
    }

}
