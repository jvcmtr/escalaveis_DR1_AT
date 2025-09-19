package edu.infnet.escalaveis_dr1_at.repositories;

import edu.infnet.escalaveis_dr1_at.models.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByEmail(String email);
}
