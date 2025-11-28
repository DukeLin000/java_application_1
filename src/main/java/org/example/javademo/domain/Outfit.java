package org.example.javademo.domain;

import jakarta.persistence.*;
import java.util.ArrayList; // ✅ 新增
import java.util.HashSet;   // ✅ 新增
import java.util.List;
import java.util.Set;       // ✅ 新增

@Entity
@Table(name="outfits")
public class Outfit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    private String name;
    private Integer score;

    @ManyToMany
    @JoinTable(name="outfit_items",
            joinColumns=@JoinColumn(name="outfit_id"),
            inverseJoinColumns=@JoinColumn(name="item_id"))
    private List<ClothingItem> items;

    // ⬇️⬇️⬇️ 新增：社群功能需要的欄位 ⬇️⬇️⬇️

    // 1. 按讚的使用者列表
    @ManyToMany
    @JoinTable(
            name = "outfit_likes",
            joinColumns = @JoinColumn(name = "outfit_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likedByUsers = new HashSet<>();

    // 2. 留言列表
    @OneToMany(mappedBy = "outfit", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<Comment> comments = new ArrayList<>();

    public Outfit() {}

    // --- Helper Methods (Service 會呼叫這些) ---

    public Set<User> getLikedByUsers() {
        return likedByUsers;
    }

    public void addLike(User user) {
        this.likedByUsers.add(user);
    }

    public void removeLike(User user) {
        this.likedByUsers.remove(user);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    // --- 基本 Getters / Setters ---

    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}

    public User getUser(){return user;}
    public void setUser(User user){this.user=user;}

    public String getName(){return name;}
    public void setName(String v){this.name=v;}

    public Integer getScore(){return score;}
    public void setScore(Integer v){this.score=v;}

    public List<ClothingItem> getItems(){return items;}
    public void setItems(List<ClothingItem> v){this.items=v;}
}