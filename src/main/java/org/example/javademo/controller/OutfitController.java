package org.example.javademo.controller;

import org.example.javademo.domain.Comment; // ✅ 記得引入 Comment 實體 (如果尚未建立，會報錯)
import org.example.javademo.dto.OutfitDto;
import org.example.javademo.service.OutfitService;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.Map; // ✅ 引入 Map 用於接收留言內容

@RestController
@RequestMapping("/api/outfits")
@Validated
public class OutfitController {

    private final OutfitService service;

    public OutfitController(OutfitService service) { this.service = service; }

    @PostMapping
    public OutfitDto create(@Valid @RequestBody OutfitDto req, Authentication auth) {
        String email = (String) auth.getPrincipal();
        return service.create(email, req);
    }

    @GetMapping
    public List<OutfitDto> list(Authentication auth) {
        String email = (String) auth.getPrincipal();
        return service.list(email);
    }

    @GetMapping("/{id}")
    public OutfitDto get(@PathVariable("id") @Min(1) long id, Authentication auth) {
        String email = (String) auth.getPrincipal();
        return service.get(email, id);
    }

    // ⬇️⬇️⬇️ 新增的部分 ⬇️⬇️⬇️

    // 1. 按讚
    @PostMapping("/{id}/like")
    public void like(@PathVariable("id") long id, Authentication auth) {
        String email = (String) auth.getPrincipal();
        service.likeOutfit(email, id);
    }

    // 2. 取消讚
    @DeleteMapping("/{id}/like")
    public void unlike(@PathVariable("id") long id, Authentication auth) {
        String email = (String) auth.getPrincipal();
        service.unlikeOutfit(email, id);
    }

    // 3. 取得留言列表
    @GetMapping("/{id}/comments")
    public List<Comment> getComments(@PathVariable("id") long id) {
        // 為了簡化，直接回傳 Entity，正式專案建議轉成 DTO
        return service.getComments(id);
    }

    // 4. 發表留言
    @PostMapping("/{id}/comments")
    public Comment postComment(@PathVariable("id") long id,
                               @RequestBody Map<String, String> body,
                               Authentication auth) {
        String email = (String) auth.getPrincipal();
        // 前端傳來的 JSON 格式: { "content": "留言內容..." }
        String content = body.get("content");
        return service.addComment(email, id, content);
    }
}