package org.example.javademo.service;

import org.example.javademo.dto.UserProfileDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;                 // ← 新增
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProfileService {

    private final AtomicLong seq = new AtomicLong(1);
    private final Map<Long, UserProfileDto> store = new ConcurrentHashMap<>();

    /** 建立/儲存 */
    public Map<String, Object> save(UserProfileDto dto) {
        long id = seq.getAndIncrement();
        store.put(id, dto);

        return Map.of(
                "id", "P-" + id,
                "status", "saved",
                "createdAt", Instant.now().toString(),
                "echo", dto
        );
    }

    /** 回讀（給 Controller 的 GET /api/profile/{id} 使用） */
    public Optional<UserProfileDto> get(long id) {
        return Optional.ofNullable(store.get(id));
    }
}

