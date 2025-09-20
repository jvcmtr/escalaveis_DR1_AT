package edu.infnet.escalaveis_dr1_at.controllers;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.infnet.escalaveis_dr1_at.models.Aluno;
import edu.infnet.escalaveis_dr1_at.models.Disciplina;
import edu.infnet.escalaveis_dr1_at.models.Inscricao;
import edu.infnet.escalaveis_dr1_at.repositories.AlunoRepository;
import edu.infnet.escalaveis_dr1_at.repositories.DisciplinaRepository;
import static edu.infnet.escalaveis_dr1_at.services.FilterService.filterAlunoByAprovacao;
import static edu.infnet.escalaveis_dr1_at.services.FilterService.filterAlunoByDisciplina;
import static edu.infnet.escalaveis_dr1_at.services.FilterService.filterAlunoByNome;


@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    @GetMapping("/ping")
    public String register() {
        return "Pong = AlunosController";
    }

    @Autowired private AlunoRepository AlunoRepository;
    @Autowired private DisciplinaRepository DisciplinaRepository;


    @PostMapping
    public Aluno createAluno(@RequestBody Aluno Aluno) {
        return AlunoRepository.save(Aluno);
    }

    @GetMapping
    public List<Aluno> getAllAlunos(
        @RequestParam(required = false) long disciplina, 
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) Boolean aprovado){
    
            return AlunoRepository.findAll().stream()
                .filter( filterAlunoByNome(nome))
                .filter( filterAlunoByDisciplina(disciplina))
                .filter( filterAlunoByAprovacao(aprovado))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aluno> getAlunoById(@PathVariable Long id) {
        Optional<Aluno> Aluno = AlunoRepository.findById(id);
        return Aluno.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aluno> updateAluno(@PathVariable Long id, @RequestBody Aluno AlunoDetails) {
        Optional<Aluno> optionalAluno = AlunoRepository.findById(id);

        if (optionalAluno.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Aluno Aluno = optionalAluno.get();
        Aluno.setCpf(AlunoDetails.getCpf());
        Aluno.setNome(AlunoDetails.getNome());
        Aluno.setEmail(AlunoDetails.getEmail());
        Aluno.setTelefone(AlunoDetails.getTelefone());
        Aluno.setEndereco(AlunoDetails.getEndereco());

        Aluno updatedAluno = AlunoRepository.save(Aluno);
        return ResponseEntity.ok(updatedAluno);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAluno(@PathVariable Long id) {
        if (!AlunoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        AlunoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id_aluno}/matricular")
    public ResponseEntity<Object> martricularAluno(@PathVariable Long id_aluno, @RequestParam Long id_disciplina) {

        // Valida se as entidades existem
        Optional<Aluno> optionalAluno = AlunoRepository.findById(id_aluno);
        Optional<Disciplina> optionalDisciplina = DisciplinaRepository.findById(id_disciplina);
        
        if (optionalAluno.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (optionalDisciplina.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Aluno aluno = optionalAluno.get();
        Disciplina disciplina = optionalDisciplina.get();
        
        // Valida se o aluno ja esta matriculado
        Boolean matriculado = aluno.getInscricoes().stream()
            .map(i -> i.getDisciplina().getId())
            .anyMatch(d -> d.equals(id_disciplina));
        
        if(matriculado){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Aluno já matriculado nesta disciplian");
        }

        // Cria a inscricao no banco
        Inscricao inscricao = new Inscricao();
        inscricao.setAluno(aluno);
        inscricao.setDisciplina(disciplina);

        aluno.getInscricoes().add(inscricao);
        disciplina.getInscricoes().add(inscricao);

        AlunoRepository.save(aluno);
        return ResponseEntity.ok().build();
    }

    
    @PostMapping("/{id_aluno}/avaliar")
    public ResponseEntity<Object> avaliarAluno(
        @PathVariable Long id_aluno,
        @RequestParam Long id_disciplina,
        @RequestParam float nota) {

        // Valida se a nota é valida
        if(nota < 0 || nota > 10){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Somente notas de 0 à 10");
        }

        // Valida se o aluno existe
        Optional<Aluno> optionalAluno = AlunoRepository.findById(id_aluno);
        if (optionalAluno.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Aluno aluno = optionalAluno.get();

        // Valida se o aluno é matriculado na disciplina
        Optional<Inscricao> optionalInscricao = aluno.getInscricoes().stream()
            .filter(i -> i.getDisciplina().getId().equals(id_disciplina))
            .findFirst();
        if (optionalInscricao.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Aluno não matriculado nesta disciplina");
        }

        // Atualiza a nota da inscricao no banco
        Inscricao inscricao = optionalInscricao.get();
        inscricao.setNota(nota);

        AlunoRepository.save(aluno);

        return ResponseEntity.ok().build();
    }
}
