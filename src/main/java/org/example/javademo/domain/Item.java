package org.example.javademo.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "items", indexes = {
        @Index(name = "ix_items_user", columnList = "user_id")
})
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 先允許為 null；之後再強制綁定使用者
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 100) private String name;
    @Column(length = 40)  private String category;
    @Column(length = 40)  private String color;
    @Column(length = 20)  private String size;
    @Column(length = 80)  private String brand;

    private Long createdAt;
    private Long updatedAt;

    @PrePersist
    public void onCreate() {
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() { this.updatedAt = System.currentTimeMillis(); }

    // --- getters / setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}
