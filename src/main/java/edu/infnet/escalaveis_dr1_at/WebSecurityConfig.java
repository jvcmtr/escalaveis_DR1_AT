package edu.infnet.escalaveis_dr1_at;

import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers(
                    // Autenticação
                    "/api/auth/**",

                    // Documentação
                    "/swagger-ui/index.html", 
                    "/swagger-ui.html", 
                    "v3/api-docs",

                    // Home e Ping
                    "/",
                    "/ping",
                    "/api/"
                ).permitAll() 
                .anyRequest().authenticated()
            .and()
                .formLogin()
            .and()
                .logout();

        return http.build();
    }
}
