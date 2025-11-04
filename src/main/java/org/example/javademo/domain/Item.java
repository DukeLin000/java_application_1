package org.example.javademo.domain;

import jakarta.persistence.*;

@Entity @Table(name="items", indexes = {
        @Index(name="ix_items_user", columnList = "user_id"),
        @Index(name="ix_items_cat_brand", columnList="category,brand")
})
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Column(nullable=false) private String name;
    private String category;
    private String brand;
    private String color;
    private String size;
    private String imageUrl;

    public Item() {}
    // getters/setters...
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public User getUser(){return user;} public void setUser(User user){this.user=user;}
    public String getName(){return name;} public void setName(String v){this.name=v;}
    public String getCategory(){return category;} public void setCategory(String v){this.category=v;}
    public String getBrand(){return brand;} public void setBrand(String v){this.brand=v;}
    public String getColor(){return color;} public void setColor(String v){this.color=v;}
    public String getSize(){return size;} public void setSize(String v){this.size=v;}
    public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){this.imageUrl=v;}
}
