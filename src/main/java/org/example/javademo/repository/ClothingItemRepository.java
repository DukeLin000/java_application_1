package org.example.javademo.repository;

import org.example.javademo.domain.ClothingItem;
import org.example.javademo.domain.ClothingItem.Category;
import org.springframework.data.domain.Page;         // ✅ 新增引用
import org.springframework.data.domain.Pageable;     // ✅ 新增引用
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClothingItemRepository extends JpaRepository<ClothingItem, Long> {

    // 原有的方法 (回傳 List)
    List<ClothingItem> findByUser_Id(Long userId);

    // ✅ 新增：支援分頁的查詢方法 (回傳 Page)
    // 這是 ItemService.list(..., Pageable pageable) 所需要的
    Page<ClothingItem> findByUser_Id(Long userId, Pageable pageable);

    List<ClothingItem> findByUser_IdAndCategory(Long userId, Category category);
    List<ClothingItem> findByUser_IdAndFavoriteTrue(Long userId);
    Optional<ClothingItem> findByIdAndUser_Id(Long id, Long userId);
}