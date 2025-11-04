package org.example.javademo.controller;

import org.example.javademo.dto.FileInfo;
import org.example.javademo.service.FileStorageService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FilesController {

    private final FileStorageService storage;

    public FilesController(FileStorageService storage) {
        this.storage = storage;
    }

    // 上傳單一圖片，回傳可公開讀取的 URL 與基本資訊
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public FileInfo upload(@RequestPart("file") MultipartFile file, Authentication auth) {
        String email = (auth != null) ? (String) auth.getPrincipal() : null;
        return storage.saveUserImage(email, file);
    }
}
