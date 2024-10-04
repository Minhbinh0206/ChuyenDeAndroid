package com.example.socialmediatdcproject.model;

public class Business {

    private int id;
    private String businessName;

    // Constructor
    public Business(int id, String businessName) {
        this.id = id;
        this.businessName = businessName;

    }
    public Business() {
        this.id = -1;
        this.businessName = "";

    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
}

