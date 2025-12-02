
package com.example.project;

public class User {
    // Fields are private to control access via getters and setters
    private int idNum;
    private String userId; // This should be the unique ID from Firebase Auth
    private String name;
    private int rating;

    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
        //TODO: idNum will be set separately after retrieving it from the Firebase counter
    }

    public User() {
        // Default constructor is required for Firebase to read data
    }

    // --- Getters and Setters for all fields ---

    public int getIdNum() {
        return idNum;
    }

    public void setIdNum(int idNum) {
        this.idNum = idNum;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
