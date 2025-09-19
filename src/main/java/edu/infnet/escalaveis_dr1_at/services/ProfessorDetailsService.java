package edu.infnet.escalaveis_dr1_at.services;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import edu.infnet.escalaveis_dr1_at.repositories.ProfessorRepository;

@Service
public class ProfessorDetailsService implements UserDetailsService {

    private final ProfessorRepository repository;

    public ProfessorDetailsService(ProfessorRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var prof = repository
            .findByEmail(email)
            .orElseThrow( () -> new UsernameNotFoundException("Nenhum professor encontrado com este email") );
        
        var authorities = prof.getRolesAsList().stream()
            .map(SimpleGrantedAuthority::new)
            .toList();

        return new User(prof.getEmail(), prof.getSenha(), authorities);
    }
}
