package com.example.vault.dto;

public class ProfileResponse {
    private String[] activeProfiles;
    private String[] defaultProfiles;
    private String appProfile;
    private String appEnvironment;

    public ProfileResponse(String[] activeProfiles, String[] defaultProfiles, 
                         String appProfile, String appEnvironment) {
        this.activeProfiles = activeProfiles;
        this.defaultProfiles = defaultProfiles;
        this.appProfile = appProfile;
        this.appEnvironment = appEnvironment;
    }

    // Getters
    public String[] getActiveProfiles() { return activeProfiles; }
    public void setActiveProfiles(String[] activeProfiles) { this.activeProfiles = activeProfiles; }
    
    public String[] getDefaultProfiles() { return defaultProfiles; }
    public void setDefaultProfiles(String[] defaultProfiles) { this.defaultProfiles = defaultProfiles; }
    
    public String getAppProfile() { return appProfile; }
    public void setAppProfile(String appProfile) { this.appProfile = appProfile; }
    
    public String getAppEnvironment() { return appEnvironment; }
    public void setAppEnvironment(String appEnvironment) { this.appEnvironment = appEnvironment; }
}