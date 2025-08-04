package org.example.controller;

import org.example.model.consts.Gender;
import org.example.model.consts.Type;
import org.example.model.message_center.Message;
import org.example.model.user.SecurityQuestion;
import org.example.model.user.User;
import org.example.repository.TokenRepository;
import org.example.repository.UserRepository;
import org.example.utils.Hasher;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthenticationController {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public AuthenticationController() {
        this.userRepository = UserRepository.getInstance();
        this.tokenRepository = TokenRepository.getInstance();
    }

    public Message signup(Message message) {
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
        if (((String) validation.getFromPayload("status")).equalsIgnoreCase("error")) {
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
        payload.put("content", "You have singed up successfully.");
        Message response = new Message(Type.SIGNUP, payload);

        return response;

    }

    public Message login(Message message) {
        if (message.getType() != Type.LOGIN) return Message.invalidArgument();

        String username = (String) message.getFromPayload("username");
        String password = (String) message.getFromPayload("password");

        if (username == null || password == null)
            return Message.error(Type.LOGIN, "Username and password are required.");

        User user;
        if (userRepository.findByUsername(username).isPresent())
            user = userRepository.findByUsername(username).get();
        else return Message.error(Type.LOGIN, "User not found.");

        if (!Hasher.validate(password, user.getPasswordHash()))
            return Message.error(Type.LOGIN, "Invalid password.");

        String token = tokenRepository.generateToken(username);

        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "success");
        payload.put("content", "Logged in successfully.");
        payload.put("username", username);
        payload.put("name", user.getName());
        payload.put("gender", user.getGender().name());
        payload.put("token", token);

        return new Message(Type.LOGIN, payload);
    }

}
