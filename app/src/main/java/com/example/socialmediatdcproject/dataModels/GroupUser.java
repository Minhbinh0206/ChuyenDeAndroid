package com.example.socialmediatdcproject.dataModels;

public class GroupUser {
    private int groupId;
    private int userId;

    public GroupUser(int groupId, int userId) {
        this.groupId = groupId;
        this.userId = userId;
    }

    public GroupUser() {
        this.groupId = -1;
        this.userId = -1;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
