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

    public void setBusinessId(int bussinessId) {
        this.businessId = bussinessId;
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

    public void setBusinessAdminId(int bussinessAdminId) {
        this.businessAdminId = bussinessAdminId;
    }

    public String getBussinessName() {
        return businessName;
    }

    public void setBussinessName(String bussinessName) {
        this.businessName = bussinessName;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public Business(int bussinessId, String bussinessName, String address, int bussinessAdminId, int groupId) {
        this.businessId = bussinessId;
        this.businessName = bussinessName;
        this.address = address;
        this.businessAdminId = bussinessAdminId;
        this.groupId = groupId;
    }

    public Business() {
        this.businessId = -1;
        this.businessName = "";
        this.address = "";
        this.businessAdminId = -1;
    }
}
