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

    /** 依「目前登入者 email」建立/更新個人量身資料，回傳 created id */
    public Map<String, Object> saveForUser(String email, UserProfileDto req) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No principal");
        }
        User u = users.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        UserProfile p = profiles.findByUser_Id(u.getId()).orElse(new UserProfile());
        p.setUser(u);
        p.setHeight(req.getHeight());
        p.setWeight(req.getWeight());
        p.setShoulderWidth(req.getShoulderWidth());
        p.setWaistline(req.getWaistline());
        p.setFitPreference(req.getFitPreference());
        p.setColorBlacklist(req.getColorBlacklist());
        p.setHasMotorcycle(Boolean.TRUE.equals(req.getHasMotorcycle())); // <- hasMotorcycle
        p.setCommuteMethod(req.getCommuteMethod());
        p.setStyleWeights(req.getStyleWeights());

        UserProfile saved = profiles.save(p);
        return Map.of("id", saved.getId());
    }

    public Optional<UserProfileDto> get(Long id) {
        return profiles.findById(id).map(this::toDto);
    }

    public Optional<UserProfileDto> getByUserEmail(String email) {
        return users.findByEmail(email.toLowerCase())
                .flatMap(u -> profiles.findByUser_Id(u.getId()))
                .map(this::toDto);
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
        dto.setHasMotorcycle(p.getHasMotorcycle());      // <- hasMotorcycle
        dto.setCommuteMethod(p.getCommuteMethod());
        dto.setStyleWeights(p.getStyleWeights());
        return dto;
    }
}
