package org.example.javademo.service;

import org.example.javademo.domain.ClothingItem;
import org.example.javademo.domain.Comment; // ✅ 新增：引用 Comment
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

    public OutfitService(OutfitRepository outfitRepository,
                         ClothingItemRepository clothingItemRepository,
                         UserRepository userRepository) {
        this.outfitRepository = outfitRepository;
        this.clothingItemRepository = clothingItemRepository;
        this.userRepository = userRepository;
    }

    // 1. 建立穿搭
    public OutfitDto create(String email, OutfitDto req) {
        User user = mustUser(email);

        Outfit outfit = new Outfit();
        outfit.setUser(user);
        outfit.setName(req.notes);

        List<ClothingItem> items = new ArrayList<>();
        if (req.topId != null) items.add(mustGetItem(req.topId, user.getId()));
        if (req.bottomId != null) items.add(mustGetItem(req.bottomId, user.getId()));
        if (req.shoesId != null) items.add(mustGetItem(req.shoesId, user.getId()));

        if (req.accessoryIds != null) {
            for (Long accId : req.accessoryIds) {
                items.add(mustGetItem(accId, user.getId()));
            }
        }
        outfit.setItems(items);

        Outfit saved = outfitRepository.save(outfit);
        return toDto(saved, user); // ✅ 傳入 user 以判斷 isLiked
    }

    // 2. 取得列表 (這裡目前設定為列出所有人的穿搭，變成社群牆)
    //    如果您只想列出自己的，可以改回 findByUser_Id
    public List<OutfitDto> list(String email) {
        User user = mustUser(email);
        // ✅ 改成 findAll() 讓大家可以看到彼此的穿搭 (社群功能)
        return outfitRepository.findAll()
                .stream()
                .map(entity -> toDto(entity, user))
                .collect(Collectors.toList());
    }

    // 3. 取得單一穿搭
    public OutfitDto get(String email, long id) {
        User user = mustUser(email);
        Outfit outfit = outfitRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Outfit not found"));

        // 註解掉這行：社群功能通常允許查看別人的穿搭
        // if (!outfit.getUser().getId().equals(user.getId())) { ... }

        return toDto(outfit, user);
    }

    // --- 社群功能實作 ---

    // 4. 按讚
    public void likeOutfit(String email, long outfitId) {
        User user = mustUser(email);
        Outfit outfit = outfitRepository.findById(outfitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Outfit not found"));

        outfit.addLike(user);
        outfitRepository.save(outfit);
    }

    // 5. 取消讚
    public void unlikeOutfit(String email, long outfitId) {
        User user = mustUser(email);
        Outfit outfit = outfitRepository.findById(outfitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Outfit not found"));

        outfit.removeLike(user);
        outfitRepository.save(outfit);
    }

    // 6. 取得留言
    public List<Comment> getComments(long outfitId) {
        Outfit outfit = outfitRepository.findById(outfitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Outfit not found"));
        return outfit.getComments();
    }

    // 7. 新增留言
    public Comment addComment(String email, long outfitId, String content) {
        User user = mustUser(email);
        Outfit outfit = outfitRepository.findById(outfitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Outfit not found"));

        Comment comment = new Comment(user, outfit, content);

        // 因為有設定 CascadeType.ALL，加到 list 後存 outfit 即可
        outfit.getComments().add(comment);
        outfitRepository.save(outfit);

        return comment;
    }

    // --- Helpers ---

    private User mustUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private ClothingItem mustGetItem(Long itemId, Long userId) {
        return clothingItemRepository.findByIdAndUser_Id(itemId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item not found or not owned"));
    }

    // ✅ 更新後的 toDto：計算按讚數與是否按讚
    private OutfitDto toDto(Outfit entity, User viewer) {
        OutfitDto dto = new OutfitDto();
        dto.id = entity.getId();
        dto.notes = entity.getName();

        // ✅ 填入前端需要的社群資料
        if (entity.getLikedByUsers() != null) {
            dto.likeCount = entity.getLikedByUsers().size();
            dto.likedByMe = entity.getLikedByUsers().contains(viewer);
        } else {
            dto.likeCount = 0;
            dto.likedByMe = false;
        }

        // ✅ 填入留言數 (可選)
        if (entity.getComments() != null) {
            // dto.commentCount = entity.getComments().size();
        }

        // ✅ 填入發布者名稱 (給前端顯示 "User123")
        if (entity.getUser() != null) {
            dto.userDisplayName = entity.getUser().getDisplayName();
        }

        dto.accessoryIds = new ArrayList<>();

        // 拆解 ClothingItem 到對應欄位
        if (entity.getItems() != null) {
            for (ClothingItem item : entity.getItems()) {
                switch (item.getCategory()) {
                    case TOP -> dto.topId = item.getId();
                    case BOTTOM -> dto.bottomId = item.getId();
                    case SHOES -> dto.shoesId = item.getId();
                    case ACCESSORY -> dto.accessoryIds.add(item.getId());
                    default -> {}
                }
            }
        }
        return dto;
    }
}