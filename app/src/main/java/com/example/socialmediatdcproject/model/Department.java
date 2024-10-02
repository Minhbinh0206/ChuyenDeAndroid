package com.example.socialmediatdcproject.model;

public class Department {
    private int departmentId;
    private String departmentName;
    private int majorId;

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public int getMajorId() {
        return majorId;
    }

    public void setMajorId(int majorId) {
        this.majorId = majorId;
    }

    public Department(int departmentId, String departmentName, int majorId) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.majorId = majorId;
    }

    public Department() {
        this.departmentId = 0;
        this.departmentName = "";
        this.majorId = 0;
    }
}
