package org.example.javademo.dto;

import java.util.List;

public class OutfitDto {
    public Long id;

    public Long topId;
    public Long bottomId;
    public Long shoesId;

    public List<Long> accessoryIds;
    public String notes;

    public Long createdAt;

    // ⬇️⬇️⬇️ 新增：社群功能需要的欄位 ⬇️⬇️⬇️
    public Integer likeCount = 0;     // 按讚數
    public Boolean likedByMe = false; // 當前用戶是否已按讚
    public String userDisplayName;    // 發布者名稱
}