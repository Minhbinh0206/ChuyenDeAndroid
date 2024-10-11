package com.example.socialmediatdcproject.model;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private int groupId;
    private String groupName;
    private int adminUserId;
    private String avatar;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(int adminUserId) {
        this.adminUserId = adminUserId;
    }

    // Getter và setter cho imageUrl
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String imageUrl) {
        this.avatar = imageUrl;
    }

    public Group(int groupId, String groupName, int adminUserId, String avatar) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.adminUserId = adminUserId;
        this.avatar = avatar;
    }

    public Group() {
        this.groupId = -1;
        this.groupName = "";
        this.adminUserId = -1;
        this.avatar = "";
    }
}
