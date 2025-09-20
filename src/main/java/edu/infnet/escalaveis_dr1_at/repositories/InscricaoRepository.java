package edu.infnet.escalaveis_dr1_at.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.infnet.escalaveis_dr1_at.models.Inscricao;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {
}
