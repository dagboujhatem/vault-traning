package com.example.vault.service.impl;

import com.example.vault.dto.SecretsResponse;
import com.example.vault.dto.ProfileResponse;
import com.example.vault.service.VaultService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class VaultServiceImpl implements VaultService {

    private final Environment environment;

    // Valeurs chargées depuis Vault
    @Value("${database.username:not-set}")
    private String dbUsername;

    @Value("${database.password:not-set}")
    private String dbPassword;

    @Value("${api.key:not-set}")
    private String apiKey;

    @Value("${app.environment:unknown}")
    private String appEnvironment;

    @Value("${app.profile:unknown}")
    private String appProfile;

    public VaultServiceImpl(Environment environment) {
        this.environment = environment;
    }

    @Override
    public SecretsResponse getSecrets() {
        return new SecretsResponse(dbUsername, dbPassword, apiKey, appEnvironment, appProfile);
    }

    @Override
    public ProfileResponse getProfile() {
        return new ProfileResponse(
            environment.getActiveProfiles(),
            environment.getDefaultProfiles(),
            appProfile,
            appEnvironment
        );
    }

    @Override
    public String getHealthStatus() {
        return "Vault Demo Application is running with profile: " + 
               String.join(",", environment.getActiveProfiles());
    }
}