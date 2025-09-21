package edu.infnet.escalaveis_dr1_at.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return  "<script> window.location.href = \"/home.html\"; </script>" ;
    }
    
    @GetMapping("/login-success")
    public String loginSuccess() {
        return  "LOGIN BEM SUCEDIDO!\n\n<script> window.location.href = \"/home.html\"; </script>" ;
    }

    @GetMapping("/api/")
    public String apiHome() {
        return "Pong";
    }

    @GetMapping("/ping")
    public String ping() {
        return "Pong";
    }

    @GetMapping("/auth-ping")
    public String AuthPing() {
        return "Authenticated Pong";
    }
}