package org.example.javademo.repository;

import org.example.javademo.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    // 取得本人 Profile（1:1）
    Optional<UserProfile> findByUser_Id(Long userId);

    // 依 id 且需屬於本人（更新/讀取用，避免越權）
    Optional<UserProfile> findByIdAndUser_Id(Long id, Long userId);

    // 建立前檢查是否已存在（避免一人多份）
    boolean existsByUser_Id(Long userId);
}
