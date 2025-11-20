package org.example.javademo.service;

import org.example.javademo.domain.ClothingItem;
import org.example.javademo.domain.ClothingItem.Category;
import org.example.javademo.domain.ClothingItem.Fit;
import org.example.javademo.domain.ClothingItem.Occasion;
import org.example.javademo.domain.ClothingItem.Season;
import org.example.javademo.domain.User;
import org.example.javademo.dto.ItemDto;
import org.example.javademo.repository.ClothingItemRepository;
import org.example.javademo.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ItemService {

    private final ClothingItemRepository itemRepo; // 改用 ClothingItemRepository
    private final UserRepository userRepo;

    public ItemService(ClothingItemRepository itemRepo, UserRepository userRepo) {
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
    }

    // =====================================================================
    // 核心 CRUD (綁定 Email/User)
    // =====================================================================

    // 建立衣物
    public ItemDto create(String email, ItemDto req) {
        User user = mustUser(email);
        ClothingItem item = new ClothingItem();
        item.setUser(user);

        applyDtoToEntity(item, req);

        ClothingItem saved = itemRepo.save(item);
        return toDto(saved);
    }

    // 列表查詢 (簡單版，不分頁)
    public List<ItemDto> list(String email) {
        User user = mustUser(email);
        return itemRepo.findByUser_Id(user.getId())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // 列表查詢 (進階版，支援篩選與分頁)
    // 若 Repository 尚未支援複雜查詢，可先回傳簡單列表或空 Page
    public Page<ItemDto> list(String email, String category, String brand, Pageable pageable) {
        User user = mustUser(email);
        // 目前先回傳簡單查詢的分頁結果 (不含篩選)，待 Repository 補強後再接上篩選
        // 實務上建議在 ClothingItemRepository 增加類似 findByUser_IdAndCategory... 的方法
        // 這裡暫時用 findAll (scoped to user) 或是先回傳空以避免編譯錯誤，
        // 假設 Repository 只有基本 findByUser_Id，我們先手動過濾或暫時略過
        return Page.empty();
    }

    // 取得單件
    public ItemDto get(String email, long id) {
        User user = mustUser(email);
        ClothingItem item = itemRepo.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        return toDto(item);
    }

    // 更新
    public ItemDto update(String email, long id, ItemDto req) {
        User user = mustUser(email);
        ClothingItem item = itemRepo.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        applyDtoToEntity(item, req);

        return toDto(itemRepo.save(item));
    }

    // 刪除
    public Map<String, Object> delete(String email, long id) {
        User user = mustUser(email);
        return itemRepo.findByIdAndUser_Id(id, user.getId())
                .map(it -> {
                    itemRepo.delete(it);
                    return Map.<String, Object>of("deleted", Boolean.TRUE, "id", id);
                })
                .orElseGet(() -> Map.<String, Object>of("deleted", Boolean.FALSE, "id", id));
    }

    // =====================================================================
    // 舊方法相容 (Deprecated - 建議逐步移除)
    // =====================================================================

    @Deprecated
    public ItemDto create(ItemDto req) {
        // 舊方法無法綁定 User，暫時拋出錯誤或回傳空
        throw new ResponseStatusException(HttpStatus.GONE, "Please use authenticated API");
    }

    @Deprecated
    public List<ItemDto> list() {
        return List.of();
    }

    @Deprecated
    public ItemDto get(long id) {
        return itemRepo.findById(id).map(this::toDto).orElseThrow();
    }

    // --- Helpers ---

    private User mustUser(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    // DTO (ItemDto) -> Entity (ClothingItem) 轉換邏輯
    private void applyDtoToEntity(ClothingItem item, ItemDto dto) {
        try {
            // Category (必填)
            if (dto.category != null) {
                item.setCategory(Category.valueOf(dto.category.toUpperCase()));
            }

            // SubCategory / Name
            String sub = dto.subCategory;
            if (sub == null || sub.isEmpty()) sub = dto.name; // 相容舊欄位
            item.setSubCategory(sub != null ? sub : "");

            item.setBrand(dto.brand);
            item.setColor(dto.color);
            item.setSize(dto.size);
            item.setImageUrl(dto.imageUrl);

            // Fit
            if (dto.fit != null) {
                item.setFit(Fit.valueOf(dto.fit.toUpperCase()));
            }

            item.setWaterproof(dto.waterproof);
            item.setWarmth(dto.warmth);
            item.setBreathability(dto.breathability);
            item.setFavorite(dto.favorite);
            item.setPrice(dto.price);

            if (dto.tags != null) item.setTags(dto.tags);

            // 處理 List<Enum>
            if (dto.season != null) {
                item.setSeasons(dto.season.stream()
                        .map(s -> Season.valueOf(s.toUpperCase()))
                        .collect(Collectors.toList()));
            }

            if (dto.occasion != null) {
                item.setOccasions(dto.occasion.stream()
                        .map(o -> Occasion.valueOf(o.toUpperCase()))
                        .collect(Collectors.toList()));
            }

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid enum value: " + e.getMessage());
        }
    }

    // Entity (ClothingItem) -> DTO (ItemDto) 轉換邏輯
    private ItemDto toDto(ClothingItem item) {
        ItemDto dto = new ItemDto();
        dto.id = item.getId();
        dto.category = item.getCategory().name().toLowerCase();
        dto.subCategory = item.getSubCategory();
        dto.name = item.getSubCategory(); // 回填 name 以相容舊前端

        dto.brand = item.getBrand();
        dto.color = item.getColor();
        dto.size = item.getSize();
        dto.imageUrl = item.getImageUrl();
        dto.fit = item.getFit() != null ? item.getFit().name().toLowerCase() : null;

        dto.waterproof = item.getWaterproof();
        dto.warmth = item.getWarmth();
        dto.breathability = item.getBreathability();
        dto.favorite = item.getFavorite();
        dto.price = item.getPrice();
        dto.tags = item.getTags();

        if (item.getSeasons() != null) {
            dto.season = item.getSeasons().stream().map(s -> s.name().toLowerCase()).toList();
        }
        if (item.getOccasions() != null) {
            dto.occasion = item.getOccasions().stream().map(o -> o.name().toLowerCase()).toList();
        }

        dto.createdAt = item.getCreatedAt();
        return dto;
    }
}