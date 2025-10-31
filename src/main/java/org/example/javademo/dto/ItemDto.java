package org.example.javademo.dto;

import jakarta.validation.constraints.NotBlank;

public class ItemDto {
    public Long id;

    @NotBlank
    public String name;

    @NotBlank
    public String category; // top | bottom | outerwear | shoes | accessory

    public String color;
    public String size;
    public String brand;

    public Long createdAt;
}
