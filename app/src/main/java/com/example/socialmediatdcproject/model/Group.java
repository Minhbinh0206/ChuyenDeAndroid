package com.example.socialmediatdcproject.model;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private int groupId;
    private String groupName;
    private int adminUserId;
    private String avatar;
    private boolean isGroupDefault;
    private boolean isPrivate;
    //thêm trương cập nhật background
    private String background;


    public Group(String background) {
        this.background = background;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }


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

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isGroupDefault() {
        return isGroupDefault;
    }

    public void setGroupDefault(boolean groupDefault) {
        isGroupDefault = groupDefault;
    }

    public Group(int groupId, String groupName, int adminUserId, String avatar,boolean isGroupDefault, boolean isPrivate) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.adminUserId = adminUserId;
        this.avatar = avatar;
        this.isPrivate = isPrivate;
        this.isGroupDefault = isGroupDefault;
    }

    public Group() {
        this.groupId = -1;
        this.groupName = "";
        this.adminUserId = -1;
        this.avatar = "";
        this.isPrivate = false;
    }
}
