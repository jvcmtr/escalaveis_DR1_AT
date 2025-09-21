package edu.infnet.escalaveis_dr1_at.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import edu.infnet.escalaveis_dr1_at.models.Professor;
import edu.infnet.escalaveis_dr1_at.repositories.ProfessorRepository;


@RestController
@RequestMapping("/api/disciplinas")
public class DisciplinaController {

    @Autowired private DisciplinaRepository DisciplinaRepository;
    @Autowired private ProfessorRepository ProfessorRepository;

    @PostMapping
    public Disciplina createDisciplina(@RequestBody Disciplina Disciplina) {
        return removeRefCircular( DisciplinaRepository.save(Disciplina) );
    }

    @GetMapping
    public List<Disciplina> getAllDisciplinas() {
        var disciplinas =  DisciplinaRepository.findAll();

        return disciplinas
            .stream()
            .map(d -> removeRefCircular(d))
            .toList();
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<Object> getDisciplinaById(@PathVariable Long id) {
        var d = DisciplinaRepository.findById(id);

        if(d.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var disciplina = d.get();

        return ResponseEntity.ok( removeRefCircular(disciplina));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateDisciplina(@PathVariable Long id, @RequestBody Disciplina DisciplinaDetails) {
        var optionalDisciplina = DisciplinaRepository.findById(id);

        if (optionalDisciplina.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var Disciplina = optionalDisciplina.get();
        Disciplina.setNome(DisciplinaDetails.getNome());
        Disciplina.setCodigo(DisciplinaDetails.getCodigo());

        // Atualiza o professor se fornecido
        if (DisciplinaDetails.getProfessor() != null) {
            var optionalProfessor = ProfessorRepository.findById(DisciplinaDetails.getProfessor().getId());

            if( optionalProfessor.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não foi encontrado um professor com o ID fornecido.");
            }

            Disciplina.setProfessor(optionalProfessor.get());
        }

        var updatedDisciplina = DisciplinaRepository.save(Disciplina);
        return ResponseEntity.ok( removeRefCircular(updatedDisciplina) );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDisciplina(@PathVariable Long id) {
        if (!DisciplinaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        DisciplinaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Disciplina removeRefCircular(Disciplina disciplina) {

        // corrige disciplina.professor.disciplinas
        var professor = disciplina.getProfessor();
        if (professor != null) {

            var profDisciplinas = professor.getDisciplinas()
                .stream()
                .map(d -> {
                    var empty = new Disciplina();
                    empty.setId(d.getId());
                    return empty;
                })
                .toList();

            professor.setDisciplinas( profDisciplinas );
        }


        var inscricoes = disciplina.getInscricoes();
        for (var i : inscricoes) {

            // corrige disciplina.inscricoes[0].disciplina
            var d = new Disciplina();
            d.setId(disciplina.getId());
            i.setDisciplina(d);

            // corrige disciplina.inscricoes[0].aluno.inscricoes[0].disciplina
            var aluno = i.getAluno();
            aluno.setInscricoes(new ArrayList<>());
        }
        
        return disciplina;
    }
}
