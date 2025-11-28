package com.example.project;

public class Listing {
    String title;
    String requesterName;
    String helperName;
    String category;
    String desc;
    boolean isComplete;
    String startDateTime;
    String endDateTime;

    public Listing(String title, String requesterName, String helperName, String category, String desc, String startDateTime, String endDateTime) {
        this.title = title;
        this.requesterName = requesterName;
        this.helperName = helperName;
        this.category = category;
        this.desc = desc;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;

        boolean isComplete = false;
    }
}
