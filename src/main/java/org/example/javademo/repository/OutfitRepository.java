package org.example.javademo.repository;

import org.example.javademo.domain.Outfit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutfitRepository extends JpaRepository<Outfit,Long> {
    List<Outfit> findByUser_Id(Long userId);
}
