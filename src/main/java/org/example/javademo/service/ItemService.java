package org.example.javademo.service;

import org.example.javademo.domain.Item;
import org.example.javademo.domain.User;
import org.example.javademo.dto.ItemDto;
import org.example.javademo.repository.ItemRepository;
import org.example.javademo.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository items;
    private final UserRepository users;

    public ItemService(ItemRepository items, UserRepository users) {
        this.items = items;
        this.users = users;
    }

    // =====================================================================
    // 相容：維持你原本簽名（單人 / 未綁帳號），方便舊前端或測試先走得通
    // =====================================================================

    public ItemDto create(ItemDto req) {
        Item e = new Item();
        apply(e, req);
        Item saved = items.save(e);
        return toDto(saved);
    }

    public Collection<ItemDto> list() {
        return items.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public ItemDto get(long id) {
        Item e = items.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item not found: " + id));
        return toDto(e);
    }

    public ItemDto update(long id, ItemDto req) {
        Item cur = items.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item not found: " + id));
        apply(cur, req);
        return toDto(items.save(cur));
    }

    public Map<String, Object> delete(long id) {
        boolean exists = items.existsById(id);
        if (exists) items.deleteById(id);
        return Map.of("deleted", exists, "id", id);
    }

    // =========================================================
    // 建議：正式使用（多使用者版，email 綁定本人資料的 CRUD）
    // =========================================================

    public ItemDto create(String email, ItemDto req) {
        Item e = new Item();
        e.setUser(mustUser(email));
        apply(e, req);
        return toDto(items.save(e));
    }

    /** 簡版本人列表（不分頁，保留相容） */
    public List<ItemDto> list(String email) {
        User u = mustUser(email);
        return items.findByUser_Id(u.getId()).stream().map(this::toDto).toList();
    }

    /** 分頁 + 排序 + 篩選（category、brand）— 推薦前端走這支 */
    public Page<ItemDto> list(String email, String category, String brand, Pageable pageable) {
        User u = mustUser(email);

        boolean hasCat   = category != null && !category.isBlank();
        boolean hasBrand = brand != null && !brand.isBlank();

        Page<Item> page;
        if (hasCat && hasBrand) {
            page = items.findByUser_IdAndCategoryIgnoreCaseAndBrandIgnoreCase(
                    u.getId(), category, brand, pageable);
        } else if (hasCat) {
            page = items.findByUser_IdAndCategoryIgnoreCase(
                    u.getId(), category, pageable);
        } else if (hasBrand) {
            page = items.findByUser_IdAndBrandIgnoreCase(
                    u.getId(), brand, pageable);
        } else {
            page = items.findByUser_Id(u.getId(), pageable);
        }
        return page.map(this::toDto);
    }

    public ItemDto get(String email, long id) {
        User u = mustUser(email);
        Item e = items.findByIdAndUser_Id(id, u.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        return toDto(e);
    }

    public ItemDto update(String email, long id, ItemDto req) {
        User u = mustUser(email);
        Item e = items.findByIdAndUser_Id(id, u.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        apply(e, req);
        return toDto(items.save(e));
    }

    public Map<String, Object> delete(String email, long id) {
        User u = mustUser(email);
        return items.findByIdAndUser_Id(id, u.getId())
                .map(it -> {
                    items.delete(it);
                    return Map.<String, Object>of("deleted", Boolean.TRUE, "id", id);
                })
                .orElseGet(() -> Map.<String, Object>of("deleted", Boolean.FALSE, "id", id));
    }

    // =========================
    // helpers
    // =========================

    private User mustUser(String email) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No principal");
        }
        return users.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    /** 只套用非 null 欄位；createdAt/updatedAt 交由 @PrePersist/@PreUpdate */
    private void apply(Item e, ItemDto r) {
        if (r.name != null)     e.setName(r.name);
        if (r.category != null) e.setCategory(r.category);
        if (r.color != null)    e.setColor(r.color);
        if (r.size != null)     e.setSize(r.size);
        if (r.brand != null)    e.setBrand(r.brand);
    }

    private ItemDto toDto(Item e) {
        ItemDto d = new ItemDto();
        d.id        = e.getId();
        d.name      = e.getName();
        d.category  = e.getCategory();
        d.color     = e.getColor();
        d.size      = e.getSize();
        d.brand     = e.getBrand();
        d.createdAt = e.getCreatedAt();
        return d;
    }
}
