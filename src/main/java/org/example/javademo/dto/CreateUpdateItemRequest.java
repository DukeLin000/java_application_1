package org.example.javademo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.javademo.domain.ClothingItem.Category;
import java.time.LocalDate;
import java.util.List;

public class CreateUpdateItemRequest {
    @NotNull
    private Category category;

    @Size(max = 50)  private String color;
    @Size(max = 100) private String brand;
    @Size(max = 50)  private String size;
    @Size(max = 20)  private String fit;
    @Size(max = 20)  private String season;

    private List<String> tags;
    private Boolean favorite;
    private String imageUrl;
    private LocalDate purchaseDate;
    private Integer price;

    public CreateUpdateItemRequest() {}

    // getters/setters
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
}
