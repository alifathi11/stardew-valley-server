package org.example.controller;

import org.example.model.consts.Gender;
import org.example.model.message_center.Message;
import org.example.model.consts.Type;
import org.example.model.user.User;
import org.example.repository.UserRepository;
import org.example.utils.Hasher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProfileController {
    public Message changeUsername(Message message) {
        String username = (String) message.getFromPayload("username");
        String newUsername = (String) message.getFromPayload("new_username");

        if (newUsername.equalsIgnoreCase(username)) {
            return Message.error(Type.CHANGE_USERNAME, "please enter a new username.");
        }

        Optional<User> userOpt = UserRepository.getInstance().findByUsername(username);
        if (userOpt.isEmpty()) {
            return Message.error(Type.CHANGE_USERNAME, "user doesn't exist.");
        }

        if (UserRepository.getInstance().usernameExists(newUsername)) {
            return Message.error(Type.CHANGE_USERNAME, "username is already used.");
        }

        if (!Validator.validateUsername(newUsername)) {
            return Message.error(Type.CHANGE_USERNAME, "username is not valid.");
        }

        User user = userOpt.get();
        user.setUsername(newUsername);

        boolean result = UserRepository.getInstance().updateUser(user);
        if (!result) {
            return Message.error(Type.CHANGE_USERNAME, "failed to change username.");
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "success");
        payload.put("new_username", newUsername);

        return new Message(Type.CHANGE_USERNAME, payload);
    }

    public Message changePassword(Message message) {
        String username = (String) message.getFromPayload("username");
        String password = (String) message.getFromPayload("password");
        String newPassword = (String) message.getFromPayload("new_password");

        if (password.equals(newPassword)) {
            return Message.error(Type.CHANGE_PASSWORD, "please enter a new password.");
        }

        Optional<User> userOpt = UserRepository.getInstance().findByUsername(username);
        if (userOpt.isEmpty()) {
            return Message.error(Type.CHANGE_PASSWORD, "user doesn't exist.");
        }

        User user = userOpt.get();

        if (!Hasher.validate(password, user.getPasswordHash())) {
            return Message.error(Type.CHANGE_PASSWORD, "password is not correct.");
        }

        if (!Validator.validatePassword(newPassword)) {
            return Message.error(Type.CHANGE_PASSWORD, "new password is not valid.");
        }

        if (Validator.isPasswordWeak(newPassword)) {
            return Message.error(Type.CHANGE_PASSWORD, Validator.checkWeakness(newPassword));
        }

        user.setPasswordHash(Hasher.hash(newPassword));
        boolean result = UserRepository.getInstance().updateUser(user);
        if (!result) {
            return Message.error(Type.CHANGE_USERNAME, "failed to change password.");
        }


        return Message.success(Type.CHANGE_PASSWORD, "password has been changed successfully");
    }

    public Message changeEmail(Message message) {
        String username = (String) message.getFromPayload("username");
        String newEmail = (String) message.getFromPayload("new_email");

        Optional<User> userOpt = UserRepository.getInstance().findByUsername(username);
        if (userOpt.isEmpty()) {
            return Message.error(Type.CHANGE_EMAIL, "user doesn't exist.");
        }

        User user = userOpt.get();
        String email = user.getEmail();
        if (email.equalsIgnoreCase(newEmail)) {
            return Message.error(Type.CHANGE_EMAIL, "please enter a new email address.");
        }

        if (!Validator.validateEmail(newEmail)) {
            return Message.error(Type.CHANGE_EMAIL, "email address is not valid.");
        }

        if (UserRepository.getInstance().emailExists(newEmail)) {
            return Message.error(Type.CHANGE_EMAIL, "email address is already used.");
        }

        user.setEmail(newEmail);
        boolean result = UserRepository.getInstance().updateUser(user);
        if (!result) {
            return Message.error(Type.CHANGE_USERNAME, "failed to change email address.");
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "success");
        payload.put("new_email", newEmail);

        return new Message(Type.CHANGE_EMAIL, payload);
    }

    public Message changeNickname(Message message) {
        String username = (String) message.getFromPayload("username");
        String newNickname = (String) message.getFromPayload("new_nickname");

        Optional<User> userOpt = UserRepository.getInstance().findByUsername(username);
        if (userOpt.isEmpty()) {
            return Message.error(Type.CHANGE_NICKNAME, "user doesn't exist.");
        }

        User user = userOpt.get();
        String nickname = user.getName();
        if (nickname.equals(newNickname)) {
            return Message.error(Type.CHANGE_NICKNAME, "please enter a new nickname.");
        }

        if (!Validator.validateNickname(newNickname)) {
            return Message.error(Type.CHANGE_NICKNAME, "nickname is not valid.");
        }

        user.setName(newNickname);
        boolean result = UserRepository.getInstance().updateUser(user);
        if (!result) {
            return Message.error(Type.CHANGE_USERNAME, "failed to change nickname.");
        }


        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "success");
        payload.put("new_nickname", newNickname);

        return new Message(Type.CHANGE_NICKNAME, payload);
    }

    public Message changeGender(Message message) {
        String username = (String) message.getFromPayload("username");
        String genderStr = (String) message.getFromPayload("gender");

        Optional<User> userOpt = UserRepository.getInstance().findByUsername(username);
        if (userOpt.isEmpty()) {
            return Message.error(Type.CHANGE_GENDER, "user doesn't exist.");
        }

        if (!Validator.validateGender(genderStr)) {
            return Message.error(Type.CHANGE_GENDER, "gender is not valid.");
        }

        Gender gender = Gender.fromString(genderStr);
        if (gender == null) {
            return Message.error(Type.CHANGE_GENDER, "failed to change gender.");
        }

        User user = userOpt.get();
        user.setGender(gender);
        UserRepository.getInstance().updateUser(user);

        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "success");
        payload.put("new_gender", gender.name());

        return new Message(Type.CHANGE_GENDER, payload);
    }

    public Message changeAvatar(Message message) {
        String username = (String) message.getFromPayload("username");
        String avatarPath = (String) message.getFromPayload("avatar_path");

        Optional<User> userOpt = UserRepository.getInstance().findByUsername(username);
        if (userOpt.isEmpty()) {
            return Message.error(Type.CHANGE_AVATAR, "user doesn't exist.");
        }

        User user = userOpt.get();
        user.setAvatarPath(avatarPath);
        UserRepository.getInstance().updateUser(user);

        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "success");
        payload.put("new_avatar_path", avatarPath);

        return new Message(Type.CHANGE_AVATAR, payload);

    }
}
