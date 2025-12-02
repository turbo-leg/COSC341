package com.example.project;

public class Listing {
    String address;
    String id;
    String title;
    String requesterName;
    String helperName;
    String category;
    String desc;
    boolean isComplete;
    String startDateTime;
    String helper; // Added to match potential DB field 'helper'

    public Listing() {
        // Default constructor required for Firebase
    }

    public Listing(String title, String requesterName, String category, String desc, String startDateTime, String address) {
        this.title = title;
        this.requesterName = requesterName;
        this.helperName = null;
        this.category = category;
        this.desc = desc;
        this.startDateTime = startDateTime;
        this.address = address;
        this.isComplete = false;
        this.id = null; // This will be set by Firebase when the object is saved
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getHelperName() {
        return helperName;
    }

    public void setHelperName(String helperName) {
        this.helperName = helperName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHelper() {
        return helper;
    }

    public void setHelper(String helper) {
        this.helper = helper;
    }
}
