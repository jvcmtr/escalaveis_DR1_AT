package edu.infnet.escalaveis_dr1_at.services;

import java.util.function.Predicate;

import edu.infnet.escalaveis_dr1_at.models.Aluno;
import edu.infnet.escalaveis_dr1_at.models.Inscricao;

// Classe utilitaria que implementa filtros reutilizaveis para Aluno.
// Encapsula a logica de filtragem, tornando o codigo mais limpo, modular e facil de manter.
public class FilterService {

    public static final Predicate<Aluno> filterAlunoByNome(String nome){
        return nome == null? a -> true : a -> a.getNome().toLowerCase().contains(nome.toLowerCase());
    }

    public static final Predicate<Aluno> filterAlunoByDisciplina(Long disciplina_id){
        return disciplina_id == 0? 
            a -> true : 
            a -> a.getInscricoes().stream()
            .map(Inscricao::getDisciplina)
            .anyMatch(d -> d.getId().equals(disciplina_id));
    }

    public static final Predicate<Aluno> filterAlunoByAprovacao(Boolean aprovado){
        return aprovado == null? 
            a -> true : 
            a -> a.getInscricoes().stream()
            .map(Inscricao::getNota)
            .anyMatch(n -> n != null && (aprovado? n >= 7 : n < 7));
    }
}
