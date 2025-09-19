package edu.infnet.escalaveis_dr1_at.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alunos")
public class AlunosController {

    @GetMapping("/ping")
    public String register() {
        return "Pong = AlunosController";
    }
}