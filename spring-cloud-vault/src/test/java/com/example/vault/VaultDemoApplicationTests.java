package com.example.vault;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VaultDemoApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        // Test basique pour vérifier que l'application démarre
    }

    @Test
    void shouldReturnHealthStatus() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/health", String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Vault Demo Application is running!");
    }

    @Test
    void shouldReturnSecrets() {
        ResponseEntity<VaultDemoApplication.SecretsResponse> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/secrets", VaultDemoApplication.SecretsResponse.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        VaultDemoApplication.SecretsResponse secrets = response.getBody();
        assertThat(secrets).isNotNull();
        
        // Vérifie que les valeurs sont présentes (fallback ou depuis Vault)
        assertThat(secrets.getDatabaseUsername()).isNotNull();
        assertThat(secrets.getDatabasePassword()).isNotNull();
        assertThat(secrets.getApiKey()).isNotNull();
        assertThat(secrets.getEnvironment()).isNotNull();
    }
}