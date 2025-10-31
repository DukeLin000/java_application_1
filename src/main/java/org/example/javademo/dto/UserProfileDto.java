package org.example.javademo.dto;

import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;

public class UserProfileDto {

    @Min(50) @Max(260)
    public int height;           // cm

    @Min(20) @Max(300)
    public int weight;           // kg

    @Min(20) @Max(100)
    public int shoulderWidth;    // cm

    @Min(30) @Max(200)
    public int waistline;        // cm

    @NotBlank
    public String fitPreference; // slim | regular | loose

    @NotNull
    public List<@NotBlank String> colorBlacklist;

    public boolean hasMotorcycle;

    @NotBlank
    public String commuteMethod; // walk | bike | motorcycle | car | public

    @NotNull
    public Map<@NotBlank String, @Min(0) @Max(100) Integer> styleWeights;
}
