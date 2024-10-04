package com.example.socialmediatdcproject.model;

public class BusinessDepartment {

    private int businessId;
    private int departmentId;

    // Constructor
    public BusinessDepartment(int businessId, int departmentId) {
        this.businessId = businessId;
        this.departmentId = departmentId;
    }

    public BusinessDepartment() {
        this.businessId = -1;
        this.departmentId = -1;
    }

    // Getters and Setters
    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }
}
