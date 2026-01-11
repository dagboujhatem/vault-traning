package com.example.vault.service;

import com.example.vault.dto.SecretsResponse;
import com.example.vault.dto.ProfileResponse;

public interface VaultService {
    SecretsResponse getSecrets();
    ProfileResponse getProfile();
    String getHealthStatus();
}