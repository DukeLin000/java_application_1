package org.example.javademo.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wardrobe_items",
        indexes = @Index(name = "ix_items_user", columnList = "user_id"))
public class ClothingItem {

    // Enum 定義 (與前端對齊)
    public enum Category { TOP, BOTTOM, OUTERWEAR, SHOES, ACCESSORY, DRESS, BAG }
    public enum Season { SPRING, SUMMER, FALL, WINTER }
    public enum Occasion { CASUAL, OFFICE, SPORT, FORMAL }
    public enum Fit { SLIM, REGULAR, LOOSE }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING) @Column(length = 20, nullable = false)
    private Category category;

    @Column(length = 50, nullable = false)
    private String subCategory; // e.g. "針織衫"

    @Column(length = 50)
    private String color;

    @Column(length = 100) private String brand;

    @Column(length = 50)
    private String size;

    private Integer price; // TWD

    @Enumerated(EnumType.STRING)
    private Fit fit;       // slim/regular/loose

    // 注意：這裡原本你可能是單選 String，但為了更靈活通常建議 List<Season>
    // 不過為了配合你原本的 ItemDto 和 Service 邏輯，這裡先保留為 List<Season>
    // 如果你的 DTO 是單選 String season，這裡可能需要調整
    @ElementCollection(targetClass = Season.class)
    @CollectionTable(name = "item_seasons", joinColumns = @JoinColumn(name = "item_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "season")
    private List<Season> seasons = new ArrayList<>();

    @ElementCollection(targetClass = Occasion.class)
    @CollectionTable(name = "item_occasions", joinColumns = @JoinColumn(name = "item_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "occasion")
    private List<Occasion> occasions = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name="item_tags", joinColumns=@JoinColumn(name="item_id"))
    @Column(name="tag")
    private List<String> tags = new ArrayList<>();

    private Boolean waterproof = false;
    private Integer warmth; // 0-5
    private Integer breathability; // 0-5
    private Boolean favorite = false;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    private LocalDate purchaseDate;

    private Long createdAt;
    private Long updatedAt;

    @PrePersist
    public void preInsert() {
        long now = System.currentTimeMillis();
        this.createdAt = now; this.updatedAt = now;
        if (favorite == null) favorite = false;
        if (waterproof == null) waterproof = false;
        if (warmth == null) warmth = 0;
        if (breathability == null) breathability = 0;
    }

    @PreUpdate
    public void preUpdate() { this.updatedAt = System.currentTimeMillis(); }

    // --- Getters / Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public Fit getFit() { return fit; }
    public void setFit(Fit fit) { this.fit = fit; }

    public List<Season> getSeasons() { return seasons; }
    public void setSeasons(List<Season> seasons) { this.seasons = seasons; }

    public List<Occasion> getOccasions() { return occasions; }
    public void setOccasions(List<Occasion> occasions) { this.occasions = occasions; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Boolean getWaterproof() { return waterproof; }
    public void setWaterproof(Boolean waterproof) { this.waterproof = waterproof; }

    public Integer getWarmth() { return warmth; }
    public void setWarmth(Integer warmth) { this.warmth = warmth; }

    public Integer getBreathability() { return breathability; }
    public void setBreathability(Integer breathability) { this.breathability = breathability; }

    public Boolean getFavorite() { return favorite; }
    public void setFavorite(Boolean favorite) { this.favorite = favorite; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}