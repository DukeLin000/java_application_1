package org.example.javademo.model;

import java.util.List;

public class User {
    private Long id;
    private String email;
    private String passwordHash;
    private String displayName;
    private List<String> roles;

    public User() {}

    public User(Long id, String email, String passwordHash, String displayName, List<String> roles) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.roles = roles;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public List<String> getRoles() { return roles; }

    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}
