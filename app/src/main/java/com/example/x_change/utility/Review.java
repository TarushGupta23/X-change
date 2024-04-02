package com.example.x_change.utility;

public class Review {
    public String reviewId, creatorId, content;
    public int stars;

    public Review(String reviewId, String creatorId, String content, int stars) {
        this.reviewId = reviewId;
        this.creatorId = creatorId;
        this.content = content;
        this.stars = stars;
    }
}
