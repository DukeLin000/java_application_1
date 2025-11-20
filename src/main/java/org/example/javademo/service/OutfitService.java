package org.example.javademo.service;

import org.example.javademo.domain.ClothingItem;
import org.example.javademo.domain.Outfit;
import org.example.javademo.domain.User;
import org.example.javademo.dto.OutfitDto;
import org.example.javademo.repository.ClothingItemRepository;
import org.example.javademo.repository.OutfitRepository;
import org.example.javademo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OutfitService {

    private final OutfitRepository outfitRepository;
    private final ClothingItemRepository clothingItemRepository;
    private final UserRepository userRepository;

    // 建構子注入 Repository
    public OutfitService(OutfitRepository outfitRepository,
                         ClothingItemRepository clothingItemRepository,
                         UserRepository userRepository) {
        this.outfitRepository = outfitRepository;
        this.clothingItemRepository = clothingItemRepository;
        this.userRepository = userRepository;
    }

    // 建立穿搭 (需綁定 User)
    public OutfitDto create(String email, OutfitDto req) {
        User user = mustUser(email);

        Outfit outfit = new Outfit();
        outfit.setUser(user);
        outfit.setName(req.notes); // 暫時將筆記存為名稱，視需求調整

        // 解析傳入的 ID 並從資料庫撈取 ClothingItem 實體
        List<ClothingItem> items = new ArrayList<>();

        if (req.topId != null) items.add(mustGetItem(req.topId, user.getId()));
        if (req.bottomId != null) items.add(mustGetItem(req.bottomId, user.getId()));
        if (req.shoesId != null) items.add(mustGetItem(req.shoesId, user.getId()));

        if (req.accessoryIds != null) {
            for (Long accId : req.accessoryIds) {
                items.add(mustGetItem(accId, user.getId()));
            }
        }

        // 注意：這需要你的 Outfit Entity 的 items 欄位是 List<ClothingItem>
        // 如果目前是 List<Item>，請同步修改 Outfit.java
        outfit.setItems(items);

        Outfit saved = outfitRepository.save(outfit);
        return toDto(saved);
    }

    // 取得列表 (僅限本人)
    public List<OutfitDto> list(String email) {
        User user = mustUser(email);
        return outfitRepository.findByUser_Id(user.getId())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 取得單一穿搭 (需檢查擁有權)
    public OutfitDto get(String email, long id) {
        User user = mustUser(email);
        Outfit outfit = outfitRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Outfit not found"));

        if (!outfit.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return toDto(outfit);
    }

    // --- Helpers ---

    private User mustUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private ClothingItem mustGetItem(Long itemId, Long userId) {
        return clothingItemRepository.findByIdAndUser_Id(itemId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item not found or not owned: " + itemId));
    }

    // 將 Entity 轉回 DTO (包含簡單的分類邏輯)
    private OutfitDto toDto(Outfit entity) {
        OutfitDto dto = new OutfitDto();
        dto.id = entity.getId();
        dto.notes = entity.getName();
        // dto.createdAt = entity.getCreatedAt(); // 若 Entity 有此欄位

        dto.accessoryIds = new ArrayList<>();

        // 將 List<ClothingItem> 拆解回 top/bottom/shoes/accessories
        if (entity.getItems() != null) {
            for (ClothingItem item : entity.getItems()) {
                switch (item.getCategory()) {
                    case TOP -> dto.topId = item.getId();
                    case BOTTOM -> dto.bottomId = item.getId();
                    case SHOES -> dto.shoesId = item.getId();
                    case ACCESSORY -> dto.accessoryIds.add(item.getId());
                    // 其他類型如 OUTERWEAR 可視需求決定放哪，或 DTO 增加欄位
                    default -> {
                        // 預設處理：如果 DTO 沒有對應欄位，暫時放入配件或忽略
                        if (item.getCategory() == ClothingItem.Category.OUTERWEAR) {
                            // 視業務邏輯決定，這裡暫不處理或加到 accessory
                        }
                    }
                }
            }
        }

        return dto;
    }
}