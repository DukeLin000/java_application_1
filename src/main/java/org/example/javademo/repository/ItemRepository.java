package org.example.javademo.repository;

import org.example.javademo.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item,Long> {
    List<Item> findByUser_Id(Long userId);
    List<Item> findByUser_IdAndCategoryIgnoreCase(Long userId, String category);
    List<Item> findByUser_IdAndBrandIgnoreCase(Long userId, String brand);
    List<Item> findByUser_IdAndCategoryIgnoreCaseAndBrandIgnoreCase(Long userId, String category, String brand);
}
