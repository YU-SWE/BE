package com.example.demo.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "review_info")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reviewid;
    private Integer reviewstar;
    private String content;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "userid", insertable = false, updatable = false)
    private Integer userid;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rid")
    private Restaurant restaurant;
    
    // 기본 생성자
    public Review() {
        this.reviewstar = 0;
        this.content = "";
    }
    
    public Integer getReviewid() {
        return reviewid;
    }
    
    public void setReviewid(Integer reviewid) {
        this.reviewid = reviewid;
    }
    
    public Integer getReviewstar() {
        return reviewstar != null ? reviewstar : 0;
    }
    
    public void setReviewstar(Integer reviewstar) {
        this.reviewstar = reviewstar != null ? reviewstar : 0;
    }
    
    public String getContent() {
        return content != null ? content : "";
    }
    
    public void setContent(String content) {
        this.content = content != null ? content : "";
    }
    
    public void setRid(Integer rid) {
        if (this.restaurant == null) {
            this.restaurant = new Restaurant();
        }
        this.restaurant.setRid(rid);
    }
    
    public Integer getRid() {
        return this.restaurant != null ? this.restaurant.getRid() : null;
    }
    
    public Integer getUserid() {
        return user != null ? user.getId() : userid;
    }
    
    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Restaurant getRestaurant() {
        return this.restaurant;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
}