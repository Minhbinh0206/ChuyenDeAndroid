package com.example.socialmediatdcproject.model;

public class Business {
    private int businessId;
    private String businessName;
    private String address;
    private int businessAdminId;
    private String avatar;
    private int groupId;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getBusinessAdminId() {
        return businessAdminId;
    }

    public void setBusinessAdminId(int businessAdminId) {
        this.businessAdminId = businessAdminId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public Business(int businessId, String businessName, String address, int businessAdminId, int groupId) {
        this.businessId = businessId;
        this.businessName = businessName;
        this.address = address;
        this.businessAdminId = businessAdminId;
        this.groupId = groupId;
    }

    public Business() {

    }
}
