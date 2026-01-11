package com.example.vault.dto;

public class SecretsResponse {
    private String databaseUsername;
    private String databasePassword;
    private String apiKey;
    private String environment;
    private String profile;

    public SecretsResponse(String databaseUsername, String databasePassword, String apiKey, 
                         String environment, String profile) {
        this.databaseUsername = databaseUsername;
        this.databasePassword = databasePassword;
        this.apiKey = apiKey;
        this.environment = environment;
        this.profile = profile;
    }

    // Getters
    public String getDatabaseUsername() { return databaseUsername; }
    public void setDatabaseUsername(String databaseUsername) { this.databaseUsername = databaseUsername; }
    
    public String getDatabasePassword() { return databasePassword; }
    public void setDatabasePassword(String databasePassword) { this.databasePassword = databasePassword; }
    
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    
    public String getProfile() { return profile; }
    public void setProfile(String profile) { this.profile = profile; }
}