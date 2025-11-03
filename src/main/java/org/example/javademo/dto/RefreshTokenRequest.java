// src/main/java/org/example/javademo/dto/RefreshTokenRequest.java
package org.example.javademo.dto;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequest {

    @NotBlank
    private String refreshToken;

    public RefreshTokenRequest() { }            // ✅ 給 Jackson 反序列化用

    public String getRefreshToken() {           // ✅ getter
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {  // ✅ setter
        this.refreshToken = refreshToken;
    }
}
