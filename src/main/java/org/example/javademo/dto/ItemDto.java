package org.example.javademo.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class ItemDto {
    public Long id;

    // 舊欄位保留 (若前端送 name，後端可視為 subCategory 或備註)
    public String name;

    @NotBlank
    public String category; // top | bottom | outerwear | shoes | accessory

    public String subCategory; // 新增：例如 "針織衫", "牛仔褲"
    public String color;
    public String brand;

    // 尺寸與版型
    public String size;
    public String fit; // slim | regular | loose

    // 多選屬性 (字串列表，對應 Enum)
    public List<String> season;
    public List<String> occasion;

    // 功能屬性
    public Boolean waterproof;
    public Integer warmth;        // 0-5
    public Integer breathability; // 0-5

    // 其他
    public List<String> tags;
    public Boolean favorite;
    public String imageUrl;
    public Integer price;

    public Long createdAt;

    // 為了方便，有些時候我們會把 name 對應到 subCategory
    public String getSubCategory() {
        return (subCategory != null && !subCategory.isEmpty()) ? subCategory : name;
    }
}