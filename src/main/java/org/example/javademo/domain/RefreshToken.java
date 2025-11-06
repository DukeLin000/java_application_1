package org.example.javademo.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name="idx_rt_token", columnList="token", unique = true),
        @Index(name="idx_rt_user", columnList="user_id")
})
public class RefreshToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Column(nullable=false, unique=true, length=512)
    private String token;

    @Column(nullable=false)
    private Long expiresAt;

    @Column(nullable=false)
    private boolean revoked = false;

    @Column(nullable=false, updatable=false)
    private Long createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = System.currentTimeMillis();
    }

    // getters/setters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getToken() { return token; }
    public Long getExpiresAt() { return expiresAt; }
    public boolean isRevoked() { return revoked; }
    public Long getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setToken(String token) { this.token = token; }
    public void setExpiresAt(Long expiresAt) { this.expiresAt = expiresAt; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
}
