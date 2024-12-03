package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "menu_info")
public class Menu {
    @Id
    private Integer mid;
    
    private String mname;
    private Integer mprice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rid")
    @JsonIgnore
    private Restaurant restaurant;

    // 기본 생성자
    public Menu() {
    }

    // 모든 필드를 포함한 생성자
    public Menu(Integer mid, String mname, Integer mprice, Restaurant restaurant) {
        this.mid = mid;
        this.mname = mname;
        this.mprice = mprice;
        this.restaurant = restaurant;
    }
}