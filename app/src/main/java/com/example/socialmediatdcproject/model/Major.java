package com.example.socialmediatdcproject.model;

public class Major {
    private int majorId;
    private String majorName;
    private int departmentId;

    public int getMajorId() {
        return majorId;
    }

    public void setMajorId(int majorId) {
        this.majorId = majorId;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public Major(int majorId, String majorName, int departmentId) {
        this.majorId = majorId;
        this.majorName = majorName;
        this.departmentId = departmentId;
    }

    public Major() {
        this.majorId = -1;
        this.majorName = "";
        this.departmentId = -1;
    }
}
