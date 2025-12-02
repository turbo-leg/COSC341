package com.example.project;

public class Review {
    private String reviewId;
    private String reviewerId; // User ID of the person writing the review
    private String revieweeId; // User ID of the person being reviewed
    private String listingId;  // ID of the listing associated with the review
    private float rating;
    private String comment;
    private long timestamp;

    public Review() {
        // Default constructor required for Firebase
    }

    public Review(String reviewerId, String revieweeId, String listingId, float rating, String comment) {
        this.reviewerId = reviewerId;
        this.revieweeId = revieweeId;
        this.listingId = listingId;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = System.currentTimeMillis();
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getRevieweeId() {
        return revieweeId;
    }

    public void setRevieweeId(String revieweeId) {
        this.revieweeId = revieweeId;
    }

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
