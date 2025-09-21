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

    @Autowired private AlunoRepository AlunoRepository;
    @Autowired private DisciplinaRepository DisciplinaRepository;


    @PostMapping
    public Aluno createAluno(@RequestBody Aluno Aluno) {
        return AlunoRepository.save(Aluno);
    }

    @GetMapping
    public ResponseEntity<Object> getAllAlunos(
        @RequestParam(required = false) Long id_disciplina, 
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) Boolean aprovado){

        // return ResponseEntity.ok(aprovado.toString());            
    
            return ResponseEntity.ok(
                AlunoRepository.findAll().stream()
                    .filter( filterAlunoByNome(nome))
                    .filter( filterAlunoByDisciplina(id_disciplina))
                    .filter( filterAlunoByAprovacao(aprovado, id_disciplina))
                    .map(a -> removeRefCircular(a))
                    .toList()
            );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aluno> getAlunoById(@PathVariable Long id) {
        var a = AlunoRepository.findById(id);

        if(a.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var aluno = a.get();

        return ResponseEntity.ok( removeRefCircular(aluno));
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
        return ResponseEntity.ok( removeRefCircular(updatedAluno) );
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
        var optionalAluno = AlunoRepository.findById(id_aluno);
        var optionalDisciplina = DisciplinaRepository.findById(id_disciplina);
        
        if (optionalAluno.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (optionalDisciplina.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var aluno = optionalAluno.get();
        var disciplina = optionalDisciplina.get();
        
        // Valida se o aluno ja esta matriculado
        Boolean matriculado = aluno.getInscricoes().stream()
            .map(i -> i.getDisciplina().getId())
            .anyMatch(d -> d.equals(id_disciplina));
        
        if(matriculado){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Aluno já matriculado nesta disciplian");
        }

        // Cria a inscricao no banco
        var inscricao = new Inscricao();
        inscricao.setAluno(aluno);
        inscricao.setDisciplina(disciplina);

        aluno.getInscricoes().add(inscricao);

        return ResponseEntity.ok( removeRefCircular( AlunoRepository.save(aluno))  );
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
        var optionalAluno = AlunoRepository.findById(id_aluno);
        if (optionalAluno.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var aluno = optionalAluno.get();

        // Valida se o aluno é matriculado na disciplina
        var optionalInscricao = aluno.getInscricoes().stream()
            .filter(i -> i.getDisciplina().getId().equals(id_disciplina))
            .findFirst();
        if (optionalInscricao.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Aluno não matriculado nesta disciplina");
        }

        // Atualiza a nota da inscricao no banco
        Inscricao inscricao = optionalInscricao.get();
        inscricao.setNota(nota);

        return ResponseEntity.ok( removeRefCircular( AlunoRepository.save(aluno))  );
    }

    private Aluno removeRefCircular(Aluno aluno) {

        var inscricoes = aluno.getInscricoes();
        for (var i : inscricoes) {

            // corrige aluno.inscricoes[0].aluno
            var a = new Aluno();
            a.setId(aluno.getId());
            i.setAluno(a);

            // corrige aluno.inscricoes[0].disciplina.inscricoes[0].aluno
            var disciplina = i.getDisciplina();
            disciplina.setInscricoes(new ArrayList<>());
        }
        
        return aluno;
    }
}
