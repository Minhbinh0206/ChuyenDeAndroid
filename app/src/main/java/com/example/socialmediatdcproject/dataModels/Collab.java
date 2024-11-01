package com.example.socialmediatdcproject.dataModels;

public class Collab {
    private int departmentId;
    private int businessId;

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public Collab(int departmentId, int businessId) {
        this.departmentId = departmentId;
        this.businessId = businessId;
    }

    public Collab() {
    }
}
