package org.example.javademo.dto;

import java.util.List;

public class OutfitDto {
    public Long id;

    public Long topId;
    public Long bottomId;
    public Long shoesId;

    public List<Long> accessoryIds;
    public String notes;

    public Long createdAt;
}
