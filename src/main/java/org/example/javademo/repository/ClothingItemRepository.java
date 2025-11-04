package org.example.javademo.repository;

import org.example.javademo.domain.ClothingItem;
import org.example.javademo.domain.ClothingItem.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClothingItemRepository extends JpaRepository<ClothingItem, Long> {
    List<ClothingItem> findByUser_Id(Long userId);
    List<ClothingItem> findByUser_IdAndCategory(Long userId, Category category);
    List<ClothingItem> findByUser_IdAndFavoriteTrue(Long userId);
    Optional<ClothingItem> findByIdAndUser_Id(Long id, Long userId);
}
