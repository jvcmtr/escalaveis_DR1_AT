package edu.infnet.escalaveis_dr1_at.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.infnet.escalaveis_dr1_at.models.Disciplina;
import edu.infnet.escalaveis_dr1_at.repositories.DisciplinaRepository;


@RestController
@RequestMapping("/api/disciplinas")
public class DisciplinaController {

    @Autowired private DisciplinaRepository DisciplinaRepository;

    @PostMapping
    public Disciplina createDisciplina(@RequestBody Disciplina Disciplina) {
        return DisciplinaRepository.save(Disciplina);
    }

    @GetMapping
    public List<Disciplina> getAllDisciplinas() {
        return DisciplinaRepository.findAll();
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<Disciplina> getDisciplinaById(@PathVariable Long id) {
        Optional<Disciplina> Disciplina = DisciplinaRepository.findById(id);
        return Disciplina.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Disciplina> updateDisciplina(@PathVariable Long id, @RequestBody Disciplina DisciplinaDetails) {
        Optional<Disciplina> optionalDisciplina = DisciplinaRepository.findById(id);

        if (optionalDisciplina.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Disciplina Disciplina = optionalDisciplina.get();
        Disciplina.setNome(DisciplinaDetails.getNome());
        Disciplina.setCodigo(DisciplinaDetails.getCodigo());

        Disciplina updatedDisciplina = DisciplinaRepository.save(Disciplina);
        return ResponseEntity.ok(updatedDisciplina);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDisciplina(@PathVariable Long id) {
        if (!DisciplinaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        DisciplinaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
