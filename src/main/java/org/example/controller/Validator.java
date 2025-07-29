package org.example.controller;

import org.example.model.Message;

public class Validator {

    public static Message validateSignup(String username, String email, String password) {
        return Message.success();
    }

    public static Message validateLogin(String username, String email, String password) {
        return Message.success();
    }

}
