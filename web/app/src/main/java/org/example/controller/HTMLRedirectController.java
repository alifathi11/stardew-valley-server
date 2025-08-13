package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HTMLRedirectController {

    @GetMapping("/reset-password")
    public String redirectToResetForm(@RequestParam("token") String token) {
        return "redirect:/reset-password.html?token=" + token;
    }
}
