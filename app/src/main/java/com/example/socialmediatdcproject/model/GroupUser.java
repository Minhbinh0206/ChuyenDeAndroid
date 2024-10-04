package com.example.socialmediatdcproject.model;

public class GroupUser {

    private int userId;
    private int groupId;

    // Constructor
    public GroupUser(int userId, int groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    public GroupUser() {
        this.userId = -1;
        this.groupId = -1;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
