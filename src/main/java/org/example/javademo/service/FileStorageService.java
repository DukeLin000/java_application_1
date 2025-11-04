package org.example.javademo.service;

import org.example.javademo.dto.FileInfo;
import org.example.javademo.domain.User;
import org.example.javademo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.Set;

@Service
public class FileStorageService {

    private final UserRepository users;

    @Value("${app.files.root:./uploads}")
    private String rootDir;

    @Value("${app.files.base-url:http://localhost:8080/files}")
    private String baseUrl;

    @Value("${app.files.allowed-types:image/jpeg,image/png,image/webp}")
    private String allowedTypesCsv;

    public FileStorageService(UserRepository users) {
        this.users = users;
    }

    public FileInfo saveUserImage(String email, MultipartFile file) {
        User u = mustUser(email);
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }

        String ct = file.getContentType();
        Set<String> allowed = Set.of(allowedTypesCsv.split(","));
        if (ct == null || !allowed.contains(ct)) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported content type: " + ct);
        }

        try {
            // 讀入 bytes 計算 SHA-256（可用於去重）
            byte[] bytes = file.getBytes();
            String sha256 = sha256Hex(bytes);

            // 決定儲存子路徑：/YYYY/MM/DD/{userId}/
            LocalDate today = LocalDate.now();
            String sub = "%04d/%02d/%02d/%d/".formatted(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), u.getId());
            Path dir = Path.of(rootDir, sub);
            Files.createDirectories(dir);

            // 檔名：時間戳-短UUID.副檔名
            String ext = extFromContentType(ct); // jpg/png/webp
            String filename = System.currentTimeMillis() + "-" + shortUuid() + "." + ext;
            Path path = dir.resolve(filename);

            // 寫入檔案
            Files.write(path, bytes, StandardOpenOption.CREATE_NEW);

            // 若為圖片，取尺寸
            Integer w = null, h = null;
            try (InputStream in = Files.newInputStream(path)) {
                BufferedImage img = ImageIO.read(in);
                if (img != null) { w = img.getWidth(); h = img.getHeight(); }
            } catch (Exception ignore) {}

            FileInfo info = new FileInfo();
            info.setUrl(toPublicUrl(sub + filename));
            info.setFilename(sub + filename);
            info.setContentType(ct);
            info.setSize(file.getSize());
            info.setWidth(w);
            info.setHeight(h);
            info.setSha256(sha256);
            info.setCreatedAt(System.currentTimeMillis());
            return info;

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Store file failed", e);
        }
    }

    private String toPublicUrl(String relative) {
        String base = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
        return base + "/" + relative.replace("\\", "/");
    }

    private String extFromContentType(String ct) {
        return switch (ct) {
            case "image/jpeg" -> "jpg";
            case "image/png"  -> "png";
            case "image/webp" -> "webp";
            default -> "bin";
        };
    }

    private String shortUuid() {
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    private User mustUser(String email) {
        if (email == null || email.isBlank())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No principal");
        return users.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private String sha256Hex(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(data));
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
