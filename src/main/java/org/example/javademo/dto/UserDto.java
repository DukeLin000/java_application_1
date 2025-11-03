package org.example.javademo.dto;

public class UserDto {
    private Long id;
    private String email;
    private String displayName;

    public UserDto() {}  // 給 Jackson 反序列化用（必備）

    public UserDto(Long id, String email, String displayName) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}
