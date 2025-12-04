package com.example.project;

public class Review {
    private String helper;
    private String listing;
    private float rating;

    public Review() {
        // Default constructor required for Firebase
    }

    public Review(String helper, String listing, float rating) {
        this.helper = helper;
        this.listing = listing;
        this.rating = rating;
    }

    public String getHelper() {
        return helper;
    }

    public void setHelper(String helper) {
        this.helper = helper;
    }

    public String getListing() {
        return listing;
    }

    public void setListing(String listing) {
        this.listing = listing;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}