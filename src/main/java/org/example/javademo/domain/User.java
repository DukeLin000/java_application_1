package org.example.javademo.domain;

import jakarta.persistence.*;

@Entity @Table(name = "users", indexes = {
        @Index(name = "ux_users_email", columnList = "email", unique = true)
})
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique = true, length = 190)
    private String email;

    @Column(nullable=false, length = 200)
    private String passwordHash;

    @Column(nullable=false, length = 100)
    private String displayName;

    @Column(nullable=false, length = 50)
    private String roles; // "USER,ADMIN" 簡化存字串

    public User() {}
    public User(String email, String passwordHash, String displayName, String roles) {
        this.email = email; this.passwordHash = passwordHash; this.displayName = displayName; this.roles = roles;
    }
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public String getRoles() { return roles; }
    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setRoles(String roles) { this.roles = roles; }
}
