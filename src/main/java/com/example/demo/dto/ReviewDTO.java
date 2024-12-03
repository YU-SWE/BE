package com.example.demo.dto;

import com.example.demo.entity.Review;

public class ReviewDTO {
    private Integer reviewid;
    private Integer reviewstar;
    private String content;
    private String imageUrl;
    private String username;

    public ReviewDTO(Review review, String username) {
        this.reviewid = review.getReviewid();
        this.reviewstar = review.getReviewstar();
        this.content = review.getContent();
        this.imageUrl = review.getImageUrl();
        this.username = username;
    }

    public Integer getReviewid() {
        return reviewid;
    }

    public void setReviewid(Integer reviewid) {
        this.reviewid = reviewid;
    }

    public Integer getReviewstar() {
        return reviewstar;
    }

    public void setReviewstar(Integer reviewstar) {
        this.reviewstar = reviewstar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl != null ?  imageUrl : null;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
}