package com.example.pro_hive;

import java.io.Serializable;

public class Post implements Serializable {
    private String title;
    private String fieldOfInterest;
    private float budget;
    private String contactInfo;
    private String projectRequirements;

    private String postId;

    public Post() {
        // Default constructor required for Firestore
    }

    public Post(String title, String fieldOfInterest, float budget, String contactInfo, String projectRequirements) {
        this.title = title;
        this.fieldOfInterest = fieldOfInterest;
        this.budget = budget;
        this.contactInfo = contactInfo;
        this.projectRequirements = projectRequirements;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFieldOfInterest() {
        return fieldOfInterest;
    }

    public void setFieldOfInterest(String fieldOfInterest) {
        this.fieldOfInterest = fieldOfInterest;
    }

    public float getBudget() {
        return budget;
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getProjectRequirements() {
        return projectRequirements;
    }

    public void setProjectRequirements(String projectRequirements) {
        this.projectRequirements = projectRequirements;
    }
}
