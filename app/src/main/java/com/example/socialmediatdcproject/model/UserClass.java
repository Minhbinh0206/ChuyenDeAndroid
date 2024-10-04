package com.example.socialmediatdcproject.model;

public class UserClass {

    private int userId;
    private int classId;

    // Constructor
    public UserClass(int userId, int classId) {
        this.userId = userId;
        this.classId = classId;
    }

    public UserClass() {
        this.userId = -1;
        this.classId = -1;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }
}
