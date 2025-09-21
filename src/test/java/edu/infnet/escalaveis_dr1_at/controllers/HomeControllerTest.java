package edu.infnet.escalaveis_dr1_at.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HomeControllerTest {
    
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String JSESSIONID;
    
    private String endpoint(String path) {
        return "http://localhost:" + port + "/" + path;
    }
    
    private <T> ResponseEntity<T> get(String path, Class<T> clazz) {
        return restTemplate.getForEntity(endpoint(path), clazz);
    }

    @Test
    @DisplayName("Home redireciona para home.html")
    public void testHome() throws Exception {
        ResponseEntity<String> response = get( "/", String.class);
        assert(response.getBody().contains("window.location.href = \"/home.html\";"));
    }

    @Test
    @DisplayName("Ping funciona")
    public void testPing() throws Exception {
        ResponseEntity<String> response = get( "/ping", String.class);
        assert(response.getBody().contains("pong"));
    }

    @Test
    @DisplayName("Auth-Ping nao autenticado da erro")
    public void testAuthPing() throws Exception {
        ResponseEntity<String> response = get( "/auth-ping", String.class);
        assert(response.getBody().contains("Please sign in"));
    }
}
