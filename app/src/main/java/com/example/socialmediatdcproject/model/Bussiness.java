package com.example.socialmediatdcproject.model;

public class Bussiness {
    private int bussinessId;
    private String bussinessName;
    private String address;
    private int bussinessAdminId;

    public int getBussinessId() {
        return bussinessId;
    }

    public void setBussinessId(int bussinessId) {
        this.bussinessId = bussinessId;
    }

    public String getBussinessName() {
        return bussinessName;
    }

    public void setBussinessName(String bussinessName) {
        this.bussinessName = bussinessName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getBussinessAdminId() {
        return bussinessAdminId;
    }

    public void setBussinessAdminId(int bussinessAdminId) {
        this.bussinessAdminId = bussinessAdminId;
    }

    public Bussiness(int bussinessId, String bussinessName, String address, int bussinessAdminId) {
        this.bussinessId = bussinessId;
        this.bussinessName = bussinessName;
        this.address = address;
        this.bussinessAdminId = bussinessAdminId;
    }

    public Bussiness() {
        this.bussinessId = -1;
        this.bussinessName = "";
        this.address = "";
        this.bussinessAdminId = -1;
    }
}
