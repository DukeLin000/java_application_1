package org.example.javademo.repository;

import org.example.javademo.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // ---------------------------
    // 所有權保護（避免跨用戶存取）
    // ---------------------------
    Optional<Item> findByIdAndUser_Id(Long id, Long userId);
    long deleteByIdAndUser_Id(Long id, Long userId);

    // ===========================
    // 非分頁版本（相容舊呼叫）
    // ===========================
    List<Item> findByUser_Id(Long userId);

    // 單一條件
    List<Item> findByUser_IdAndCategoryIgnoreCase(Long userId, String category);
    List<Item> findByUser_IdAndBrandIgnoreCase(Long userId, String brand);
    List<Item> findByUser_IdAndBrandContainingIgnoreCase(Long userId, String brand);

    // 複合條件（category + brand）
    List<Item> findByUser_IdAndCategoryIgnoreCaseAndBrandIgnoreCase(Long userId, String category, String brand);
    List<Item> findByUser_IdAndCategoryIgnoreCaseAndBrandContainingIgnoreCase(Long userId, String category, String brand);

    // ===========================
    // 分頁版本（建議前端清單使用）
    // ===========================
    Page<Item> findByUser_Id(Long userId, Pageable pageable);

    // 單一條件
    Page<Item> findByUser_IdAndCategoryIgnoreCase(Long userId, String category, Pageable pageable);
    Page<Item> findByUser_IdAndBrandIgnoreCase(Long userId, String brand, Pageable pageable);
    Page<Item> findByUser_IdAndBrandContainingIgnoreCase(Long userId, String brand, Pageable pageable);

    // 複合條件（category + brand）
    Page<Item> findByUser_IdAndCategoryIgnoreCaseAndBrandIgnoreCase(Long userId, String category, String brand, Pageable pageable);
    Page<Item> findByUser_IdAndCategoryIgnoreCaseAndBrandContainingIgnoreCase(Long userId, String category, String brand, Pageable pageable);
}
