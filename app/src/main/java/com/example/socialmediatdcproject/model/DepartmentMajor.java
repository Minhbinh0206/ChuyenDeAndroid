package com.example.socialmediatdcproject.model;

public class DepartmentMajor {

    private int departmentId;
    private int majorId;

    // Constructor
    public DepartmentMajor(int departmentId, int majorId) {
        this.departmentId = departmentId;
        this.majorId = majorId;
    }

    public DepartmentMajor() {
        this.departmentId = -1;
        this.majorId = -1;
    }

    // Getters and Setters
    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getMajorId() {
        return majorId;
    }

    public void setMajorId(int majorId) {
        this.majorId = majorId;
    }
}
