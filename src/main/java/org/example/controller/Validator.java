package org.example.controller;

import org.example.model.consts.Gender;
import org.example.model.message_center.Message;
import org.example.model.consts.Type;
import org.example.model.user.SecurityQuestion;
import org.example.repository.UserRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Validator {

    public static boolean validateUsername(String username) {
        return username.matches("^[a-zA-Z0-9-]+$");
    }

    public static boolean validatePassword(String password) {
        return password.matches("^[a-zA-Z0-9?><,\"';:/\\\\|\\]\\[}{+=)(*&^%$#!]*$");
    }

    public static boolean validateEmail(String email) {
        return email.matches("^(?!.*\\.\\.)[a-zA-Z0-9](?:[a-zA-Z0-9._-]*[a-zA-Z0-9])?@(?=[a-zA-Z0-9])[a-zA-Z0-9.-]*[a-zA-Z0-9]\\.[a-zA-Z]{2,}$");
    }

    public static boolean isPasswordWeak(String password) {
        if (password.length() < 8) return true;
        ArrayList<Character> specialChars = new ArrayList<>(Arrays.asList('?', '>', '<', ',', '"', '\'', ';', ':', '/',
                '|', ']', '[', '}', '{', '+', '=', ')', '(', '*', '&', '^', '%', '$', '#', '!'));
        boolean containsSpecialChars = false;
        for (Character ch : specialChars) {
            if (password.indexOf(ch) != -1) {
                containsSpecialChars = true;
                break;
            }
        }
        if (!containsSpecialChars) return true;

        boolean hasLowerCase =  false;
        boolean hasUppercase = false;
        for (Character ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) hasUppercase = true;
            if (Character.isLowerCase(ch)) hasLowerCase = true;
        }
        if (!hasUppercase || !hasLowerCase) return true;

        return false;
    }

    public static String checkWeakness(String password) {
        if (password.length() < 8) return "password is too short.";
        ArrayList<Character> specialChars = new ArrayList<>(Arrays.asList('?', '>', '<', ',', '"', '\'', ';', ':', '/',
                '|', ']', '[', '}', '{', '+', '=', ')', '(', '*', '&', '^', '%', '$', '#', '!'));
        boolean containsSpecialChars = false;
        for (Character ch : specialChars) {
            if (password.indexOf(ch) != -1) {
                containsSpecialChars = true;
                break;
            }
        }
        if (!containsSpecialChars) return "password must contain at least one special character.";

        boolean hasLowerCase =  false;
        boolean hasUppercase = false;
        for (Character ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) hasUppercase = true;
            if (Character.isLowerCase(ch)) hasLowerCase = true;
        }
        if (!hasUppercase || !hasLowerCase) return "password must contain at least one uppercase and lowercase characters.";

        return "";
    }

    public static boolean validateNickname(String nickname) {
        return true;
    }

    public static boolean validateGender(String gender) {
        return true;
    }

    public static Message validateSignup(String username,
                                  String name,
                                  String email,
                                  String password,
                                  Gender gender,
                                  SecurityQuestion securityQuestion) {

        if (username.isEmpty()) {
            return Message.error(Type.SIGNUP, "please enter your username.");
        }

        if (name.isEmpty()) {
            return Message.error(Type.SIGNUP, "please enter your name.");
        }

        if (email.isEmpty()) {
            return Message.error(Type.SIGNUP, "please enter your email.");
        }

        if (password.isEmpty()) {
            return Message.error(Type.SIGNUP, "please enter your password.");
        }

        if (securityQuestion.getAnswer().isEmpty()) {
            return Message.error(Type.SIGNUP, "please enter your answer.");
        }

        if (UserRepository.getInstance().usernameExists(username)) {
            return Message.error(Type.SIGNUP, "username is already taken.");

        }

        if (UserRepository.getInstance().emailExists(email)) {
            return Message.error(Type.SIGNUP, "email address is already used.");

        }

        if (!validateUsername(username)) {
            return Message.error(Type.SIGNUP, "username is not valid.");
        }

        if (!validateEmail(email)) {
            return Message.error(Type.SIGNUP, "email address is not valid.");
        }

        if (!validatePassword(password)) {
            return Message.error(Type.SIGNUP, "password is not valid.");
        }

        if (!validateGender(name)) {
            return Message.error(Type.SIGNUP, "nickname is not valid.");
        }

        if (isPasswordWeak(password)) {
            String message = checkWeakness(password);
            return Message.error(Type.SIGNUP, message);
        }

        return Message.success(Type.SIGNUP, "data is valid.");
    }



}
