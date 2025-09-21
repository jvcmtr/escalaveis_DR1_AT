package edu.infnet.escalaveis_dr1_at.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.infnet.escalaveis_dr1_at.models.Professor;
import edu.infnet.escalaveis_dr1_at.repositories.ProfessorRepository;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody Professor professor) {
        
        if (! professor.getEmail().contains("@")) {
            return ResponseEntity.badRequest().body("dados incorretos");
        }

        if (professor.getSenha().length() < 6) {
            return ResponseEntity.badRequest().body("Senha deve ter ao menos 6 caracteres");
        }

        if (professor.getNome().isEmpty()) {
            return ResponseEntity.badRequest().body("Nome não pode ser vazio");
        }
        
        var professorExistente = professorRepository.findByEmail(professor.getEmail());

        if (professorExistente.isPresent()) {
            return ResponseEntity.badRequest().body("Email já existe");
        }
        
        professor.setSenha(encoder.encode(professor.getSenha()));
        professor.setRoles("PROFESSOR");
        professorRepository.save(professor);

        return ResponseEntity.ok("Registro bem sucedido, seja bem vindo " + professor.getNome());
    }

    @GetMapping("/logout")
    public ResponseEntity<Object> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout bem sucedido");
    }
}
