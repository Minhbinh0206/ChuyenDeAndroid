package com.example.socialmediatdcproject.model;

import java.util.Date;

public class Notification {

    private int id;
    private int roleId;
    private String title;
    private String content;
    private int adminUserId;


    // Constructor
    public Notification(int id, int roleId, String title, String content, int adminUserId) {
        this.id = id;
        this.roleId = roleId;
        this.title = title;
        this.content = content;
        this.adminUserId = adminUserId;
    }

    public Notification() {
        this.id = -1;
        this.roleId = -1;
        this.title = "";
        this.content = "";
        this.adminUserId = -1;

    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(int adminUserId) {
        this.adminUserId = adminUserId;
    }


}
