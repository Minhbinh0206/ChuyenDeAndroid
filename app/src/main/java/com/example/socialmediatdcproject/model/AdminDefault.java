package com.example.socialmediatdcproject.model;

public class AdminDefault extends User{
    private String adminType;
    private int groupId;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getAdminType() {
        return adminType;
    }

    public void setAdminType(String adminType) {
        this.adminType = adminType;
    }

    public AdminDefault(int userId, String email, String password, String fullName, String avatar, String phoneNumber, int roleId, String adminType, int groupId) {
        super(userId, email, password, fullName, avatar, phoneNumber, roleId);
        this.adminType = adminType;
        this.groupId = groupId;
    }

    public AdminDefault(int userId, String email, String password, String fullName, String avatar, String phoneNumber, int roleId) {
        super(userId, email, password, fullName, avatar, phoneNumber, roleId);
    }

    public AdminDefault() {
    }
}
