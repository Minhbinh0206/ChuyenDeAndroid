package com.example.socialmediatdcproject.model;

public class Class {
    private int id;
    private String className;
    private int departmentId;
    private int majorId;

    public Class(int id, String className, int departmentId) {
        this.id = id;
        this.className = className;
        this.departmentId = departmentId;
    }
    public Class(int id, String className, int departmentId , int majorId) {
        this.id = id;
        this.className = className;
        this.departmentId = departmentId;
        this.majorId = majorId;
    }

    public Class() {
        this.id = -1;
        this.className = "";
        this.departmentId = -1;
        this.majorId = -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

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
