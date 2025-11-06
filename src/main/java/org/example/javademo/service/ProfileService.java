package org.example.javademo.service;

import org.example.javademo.domain.User;
import org.example.javademo.domain.UserProfile;
import org.example.javademo.dto.UserProfileDto;
import org.example.javademo.repository.UserProfileRepository;
import org.example.javademo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

@Service
public class ProfileService {

    private final UserRepository users;
    private final UserProfileRepository profiles;

    public ProfileService(UserRepository users, UserProfileRepository profiles) {
        this.users = users;
        this.profiles = profiles;
    }

    /** 取得「本人」的 Profile（由 JWT 取 email） */
    public Optional<UserProfileDto> getMine(String email) {
        User u = mustUser(email);
        return profiles.findByUser_Id(u.getId()).map(this::toDto);
    }

    /** 依 id 回讀，但需屬於本人（避免越權） */
    public Optional<UserProfileDto> getByIdForOwner(String email, long id) {
        User u = mustUser(email);
        return profiles.findByIdAndUser_Id(id, u.getId()).map(this::toDto);
    }

    /**
     * 建立或更新本人 Profile（upsert）。
     * 只覆蓋請求中「非 null」欄位，避免把未填欄位洗成 null/false。
     * 回傳 { id } 方便前端記錄。
     */
    public Map<String, Object> saveForUser(String email, UserProfileDto req) {
        User u = mustUser(email);

        UserProfile p = profiles.findByUser_Id(u.getId())
                .orElseGet(() -> {
                    UserProfile np = new UserProfile();
                    np.setUser(u);
                    return np;
                });

        copyNonNull(req, p);
        touchUpdatedAt(p);

        UserProfile saved = profiles.save(p);
        return Map.of("id", saved.getId());
    }

    /** 更新指定 id 的 Profile（僅本人可改；部分更新：只覆蓋非 null 欄位） */
    public UserProfileDto update(String email, long id, UserProfileDto req) {
        User u = mustUser(email);
        UserProfile p = profiles.findByIdAndUser_Id(id, u.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found or not yours"));

        copyNonNull(req, p);
        touchUpdatedAt(p);

        return toDto(profiles.save(p));
    }

    /** 依 id 回讀（僅供舊 API 使用；不做所有權檢查） */
    public Optional<UserProfileDto> get(Long id) {
        return profiles.findById(id).map(this::toDto);
    }

    /** 依 email 回讀（僅供舊 API 使用；不建議新功能繼續用） */
    public Optional<UserProfileDto> getByUserEmail(String email) {
        return users.findByEmail(email.toLowerCase())
                .flatMap(u -> profiles.findByUser_Id(u.getId()))
                .map(this::toDto);
    }

    // =================== helpers ===================

    private User mustUser(String email) {
        if (email == null || email.isBlank())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No principal");
        return users.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    /** 只覆蓋非 null 欄位；List/Map 也只有傳入非 null 才覆蓋 */
    private void copyNonNull(UserProfileDto src, UserProfile dst) {
        if (src.getHeight() != null) dst.setHeight(src.getHeight());
        if (src.getWeight() != null) dst.setWeight(src.getWeight());
        if (src.getShoulderWidth() != null) dst.setShoulderWidth(src.getShoulderWidth());
        if (src.getWaistline() != null) dst.setWaistline(src.getWaistline());
        if (notBlank(src.getFitPreference())) dst.setFitPreference(src.getFitPreference());
        if (src.getColorBlacklist() != null) dst.setColorBlacklist(src.getColorBlacklist());
        if (src.getHasMotorcycle() != null) dst.setHasMotorcycle(src.getHasMotorcycle());
        if (notBlank(src.getCommuteMethod())) dst.setCommuteMethod(src.getCommuteMethod());
        if (src.getStyleWeights() != null) dst.setStyleWeights(src.getStyleWeights());
    }

    private boolean notBlank(String s) { return s != null && !s.isBlank(); }

    /** 若 Entity 有 updatedAt(Long) 欄位，更新時間戳；沒有就忽略 */
    private void touchUpdatedAt(UserProfile p) {
        try {
            var m = UserProfile.class.getMethod("setUpdatedAt", Long.class);
            m.invoke(p, System.currentTimeMillis());
        } catch (Exception ignore) {}
    }

    private UserProfileDto toDto(UserProfile p) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(p.getId());
        dto.setHeight(p.getHeight());
        dto.setWeight(p.getWeight());
        dto.setShoulderWidth(p.getShoulderWidth());
        dto.setWaistline(p.getWaistline());
        dto.setFitPreference(p.getFitPreference());
        dto.setColorBlacklist(p.getColorBlacklist());
        dto.setHasMotorcycle(p.getHasMotorcycle());
        dto.setCommuteMethod(p.getCommuteMethod());
        dto.setStyleWeights(p.getStyleWeights());
        return dto;
    }
}
