package com.example.socialmediatdcproject.model;

public class Group {
    private int groupId;
    private String groupName;
    private int adminUserId;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        groupName = groupName;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(int adminUserId) {
        this.adminUserId = adminUserId;
    }

    public Group(int groupId, String groupName, int adminUserId) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.adminUserId = adminUserId;
    }

    public Group() {
        this.groupId = -1;
        this.groupName = "";
        this.adminUserId = -1;
    }
}
