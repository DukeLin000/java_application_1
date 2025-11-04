package org.example.javademo.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wardrobe_items",
        indexes = @Index(name = "ix_items_user", columnList = "user_id"))
public class ClothingItem {

    public enum Category { TOP, BOTTOM, OUTERWEAR, SHOES, ACCESSORY, DRESS, BAG }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING) @Column(length = 20, nullable = false)
    private Category category;

    @Column(length = 50)  private String color;
    @Column(length = 100) private String brand;
    @Column(length = 50)  private String size;
    @Column(length = 20)  private String fit;       // slim/regular/loose
    @Column(length = 20)  private String season;   // SS/AW/All

    @ElementCollection
    @CollectionTable(name="item_tags", joinColumns=@JoinColumn(name="item_id"))
    @Column(name="tag")
    private List<String> tags = new ArrayList<>();

    private Boolean favorite = false;
    private String imageUrl;
    private LocalDate purchaseDate;
    private Integer price; // TWD

    private Long createdAt;
    private Long updatedAt;

    @PrePersist
    public void preInsert() {
        long now = System.currentTimeMillis();
        this.createdAt = now; this.updatedAt = now;
        if (favorite == null) favorite = false;
    }

    @PreUpdate
    public void preUpdate() { this.updatedAt = System.currentTimeMillis(); }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getFit() { return fit; }
    public void setFit(String fit) { this.fit = fit; }

    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Boolean getFavorite() { return favorite; }
    public void setFavorite(Boolean favorite) { this.favorite = favorite; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}
