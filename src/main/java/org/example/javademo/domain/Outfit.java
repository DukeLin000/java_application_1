package org.example.javademo.domain;

import jakarta.persistence.*;
import java.util.List;

@Entity @Table(name="outfits")
public class Outfit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) @JoinColumn(name="user_id", nullable=false)
    private User user;

    private String name;
    private Integer score;

    @ManyToMany
    @JoinTable(name="outfit_items",
            joinColumns=@JoinColumn(name="outfit_id"),
            inverseJoinColumns=@JoinColumn(name="item_id"))
    private List<Item> items;

    public Outfit() {}
    // getters/setters...
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public User getUser(){return user;} public void setUser(User user){this.user=user;}
    public String getName(){return name;} public void setName(String v){this.name=v;}
    public Integer getScore(){return score;} public void setScore(Integer v){this.score=v;}
    public List<Item> getItems(){return items;} public void setItems(List<Item> v){this.items=v;}
}
