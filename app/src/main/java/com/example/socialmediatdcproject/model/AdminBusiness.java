package com.example.socialmediatdcproject.model;

public class AdminBusiness extends User{
    private int businessId;

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public AdminBusiness(int userId, String email, String password, String fullName, String avatar, String phoneNumber, int roleId, int businessId) {
        super(userId, email, password, fullName, avatar, phoneNumber, roleId);
        this.businessId = businessId;
    }

    public AdminBusiness(int userId, String email, String password, String fullName, String avatar, String phoneNumber, int roleId) {
        super(userId, email, password, fullName, avatar, phoneNumber, roleId);
        this.businessId = -1;
    }

    public AdminBusiness() {
    }
}
