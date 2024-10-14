package com.example.socialmediatdcproject.model;

public class Business {
    private int businessId;
    private String businessName;
    private String address;
    private int businessAdminId;

    public int getBussinessId() {
        return businessId;
    }

    public void setBussinessId(int bussinessId) {
        this.businessId = bussinessId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getBussinessAdminId() {
        return businessAdminId;
    }

    public void setBussinessAdminId(int bussinessAdminId) {
        this.businessAdminId = bussinessAdminId;
    }

    public String getBussinessName() {
        return businessName;
    }

    public void setBussinessName(String bussinessName) {
        this.businessName = bussinessName;
    }

    public Business(int bussinessId, String bussinessName, String address, int bussinessAdminId) {
        this.businessId = bussinessId;
        this.businessName = bussinessName;
        this.address = address;
        this.businessAdminId = bussinessAdminId;
    }

    public Business() {
        this.businessId = -1;
        this.businessName = "";
        this.address = "";
        this.businessAdminId = -1;
    }
}
