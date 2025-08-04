package org.example.controller;

import org.example.model.Message;
import org.example.model.Type;
import org.ietf.jgss.GSSName;

public class Validator {

    public static boolean validateUsername(String username) {
        return true;
    }

    public static boolean validatePassword(String password) {
        return true;
    }

    public static boolean validateEmail(String email) {
        return true;
    }

    public static boolean validateNickname(String nickname) {
        return true;
    }

    public static boolean validateGender(String gender) {
        return true;
    }

    public static Message validateSignup(String username, String email, String password) {
        return Message.success(Type.SIGNUP, "signed up");
    }

    public static Message validateLogin(String username, String email, String password) {
        return Message.success(Type.LOGIN, "login");
    }

}
