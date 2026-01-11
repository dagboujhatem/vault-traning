package com.example.vault.controller;

import com.example.vault.dto.SecretsResponse;
import com.example.vault.dto.ProfileResponse;
import com.example.vault.service.VaultService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VaultController {

    private final VaultService vaultService;

    public VaultController(VaultService vaultService) {
        this.vaultService = vaultService;
    }

    @GetMapping("/secrets")
    public SecretsResponse getSecrets() {
        return vaultService.getSecrets();
    }

    @GetMapping("/profile")
    public ProfileResponse getProfile() {
        return vaultService.getProfile();
    }

    @GetMapping("/health")
    public String health() {
        return vaultService.getHealthStatus();
    }
}