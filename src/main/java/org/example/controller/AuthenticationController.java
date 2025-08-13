package org.example.controller;

import org.example.data.AvatarLoader;
import org.example.model.consts.Gender;
import org.example.model.consts.Type;
import org.example.model.message_center.Message;
import org.example.model.user.SecurityQuestion;
import org.example.model.user.User;
import org.example.repository.TokenRepository;
import org.example.repository.UserRepository;
import org.example.utils.Hasher;

import java.util.*;

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

        // Validation
        Message validation = Validator.validateSignup(
                username,
                name,
                email,
                password,
                gender,
                securityQuestion
        );

//        if (((String) validation.getFromPayload("status")).equalsIgnoreCase("error")) {
//            return validation;
//        }

        // Choose avatar
        Random random = new Random();
        List<String>avatarPaths = Arrays.asList(AvatarLoader.getAvatars());
        String avatarPath = avatarPaths.get(random.nextInt(avatarPaths.size()));

        // Add to database
        String passwordHash = Hasher.hash(password);
        userRepository.save(new User(UUID.randomUUID().toString(),
                                     username,
                                     name,
                                     email,
                                     passwordHash,
                                     gender,
                                     securityQuestion,
                                     false,
                                     0,
                                     avatarPath));

        // Build response
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
        boolean stayLoggedIn = (Boolean) message.getFromPayload("stay_logged_in");

        if (username == null || password == null)
            return Message.error(Type.LOGIN, "Username and password are required.");

        User user;
        if (userRepository.findByUsername(username).isPresent())
            user = userRepository.findByUsername(username).get();
        else return Message.error(Type.LOGIN, "User not found.");

        if (!Hasher.validate(password, user.getPasswordHash()))
            return Message.error(Type.LOGIN, "Invalid password.");

        String persistentToken = "";
        if (stayLoggedIn) {
            persistentToken = TokenRepository.getInstance().generatePersistentToken(username);
        }

        String token = tokenRepository.generateToken(username);

        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "success");
        payload.put("content", "Logged in successfully.");
        payload.put("username", username);
        payload.put("email", user.getEmail());
        payload.put("name", user.getName());
        payload.put("gender", user.getGender().name());
        payload.put("avatar_path", user.getAvatarPath());
        payload.put("persistent_token", persistentToken);
        payload.put("token", token);

        return new Message(Type.LOGIN, payload);
    }

    public Message autoLogin(Message message) {
        String persistentToken = (String) message.getFromPayload("persistent_token");

        if (persistentToken == null) {
            return Message.error(Type.AUTO_LOGIN, "token not found.");
        }

        Optional<String> usernameOpt = TokenRepository.getInstance().getUserIdFromPersistentToken(persistentToken);

        if (usernameOpt.isPresent()) {

            String username = usernameOpt.get();

            String token = tokenRepository.generateToken(username);
            Optional<User> userOpt = UserRepository.getInstance().findByUsername(username);

            if (userOpt.isEmpty()) {
                return Message.error(Type.AUTO_LOGIN, "user not found.");
            }

            User user = userOpt.get();

            Map<String, Object> payload = new HashMap<>();
            payload.put("status", "success");
            payload.put("content", "Logged in successfully.");
            payload.put("username", username);
            payload.put("email", user.getEmail());
            payload.put("name", user.getName());
            payload.put("gender", user.getGender().name());
            payload.put("avatar_path", user.getAvatarPath());
            payload.put("token", token);

            return new Message(Type.LOGIN, payload);

        } else {
            return Message.error(Type.AUTO_LOGIN, "user not found.");
        }
    }

}
