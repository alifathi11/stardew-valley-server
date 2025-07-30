package org.example.controller;

import org.example.model.Message;
import org.example.model.Type;

public class Validator {

    public static Message validateSignup(String username, String email, String password) {
        return Message.success(Type.SIGNUP, "signed up");
    }

    public static Message validateLogin(String username, String email, String password) {
        return Message.success(Type.LOGIN, "login");
    }

}
