package org.example.controller;

import java.util.ArrayList;
import java.util.Arrays;

public class ValidationUtils {

    public static boolean isUsernameValid(String username) {
        return username.matches("^[a-zA-Z0-9-]+$");
    }
    public static boolean isEmailValid(String email) {
        return email.matches("^(?!.*\\.\\.)[a-zA-Z0-9](?:[a-zA-Z0-9._-]*[a-zA-Z0-9])?@(?=[a-zA-Z0-9])[a-zA-Z0-9.-]*[a-zA-Z0-9]\\.[a-zA-Z]{2,}$");
    }
    public static boolean isPasswordValid(String password) {
        return password.matches("^[a-zA-Z0-9?><,\"';:/\\\\|\\]\\[}{+=)(*&^%$#!]*$");
    }
    public static boolean isNicknameValid(String nickname) {
        return nickname.matches("^[a-zA-Z0-9 -_]+$");
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
}
