package org.example.javademo.domain;

import jakarta.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private Integer height;          // cm
    private Integer weight;          // kg
    private Integer shoulderWidth;   // cm
    private Integer waistline;       // cm
    private String  fitPreference;   // slim | regular | loose

    @ElementCollection
    @CollectionTable(name = "user_color_blacklist",
            joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "color")
    private List<String> colorBlacklist;

    @Column(name = "has_motorcycle")
    private Boolean hasMotorcycle;   // 可為 null，未填

    private String commuteMethod;    // walk | bike | motorcycle | car | public

    @ElementCollection
    @CollectionTable(name = "user_style_weights",
            joinColumns = @JoinColumn(name = "profile_id"))
    @MapKeyColumn(name = "style_key")
    @Column(name = "weight_val")
    private Map<String, Integer> styleWeights;

    public UserProfile() {}

    // --- getters & setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }

    public Integer getWeight() { return weight; }
    public void setWeight(Integer weight) { this.weight = weight; }

    public Integer getShoulderWidth() { return shoulderWidth; }
    public void setShoulderWidth(Integer shoulderWidth) { this.shoulderWidth = shoulderWidth; }

    public Integer getWaistline() { return waistline; }
    public void setWaistline(Integer waistline) { this.waistline = waistline; }

    public String getFitPreference() { return fitPreference; }
    public void setFitPreference(String fitPreference) { this.fitPreference = fitPreference; }

    public List<String> getColorBlacklist() { return colorBlacklist; }
    public void setColorBlacklist(List<String> colorBlacklist) { this.colorBlacklist = colorBlacklist; }

    public Boolean getHasMotorcycle() { return hasMotorcycle; }
    public void setHasMotorcycle(Boolean hasMotorcycle) { this.hasMotorcycle = hasMotorcycle; }

    public String getCommuteMethod() { return commuteMethod; }
    public void setCommuteMethod(String commuteMethod) { this.commuteMethod = commuteMethod; }

    public Map<String, Integer> getStyleWeights() { return styleWeights; }
    public void setStyleWeights(Map<String, Integer> styleWeights) { this.styleWeights = styleWeights; }
}
