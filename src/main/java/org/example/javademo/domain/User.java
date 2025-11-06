package org.example.javademo.domain;

import jakarta.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
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
    private String roles; // 以逗號分隔，例如 "USER,ADMIN"

    @Column(nullable=false, updatable=false)
    private Long createdAt;

    public User() {}

    public User(String email, String passwordHash, String displayName, String roles) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.roles = roles;
    }

    // 預設值：新建用戶給 "USER" 角色與建立時間
    @PrePersist
    public void prePersist() {
        if (this.roles == null || this.roles.isBlank()) {
            this.roles = "USER";
        }
        if (this.createdAt == null) {
            this.createdAt = System.currentTimeMillis();
        }
    }

    /** 便利方法：把 "USER,ADMIN" 轉成 ["USER","ADMIN"] 並去空白 */
    @Transient
    public List<String> roleList() {
        if (roles == null || roles.isBlank()) return List.of();
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .distinct()
                .toList();
    }

    // getters & setters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public String getRoles() { return roles; }
    public Long getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setRoles(String roles) { this.roles = roles; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
}
