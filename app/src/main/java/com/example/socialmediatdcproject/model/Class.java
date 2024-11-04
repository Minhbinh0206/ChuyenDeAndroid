package com.example.socialmediatdcproject.model;

public class Class {
    private int id;
    private String className;
    private int departmentId;

    public Class(int id, String className, int majorId) {
        this.id = id;
        this.className = className;
        this.departmentId = majorId;
    }

    public Class() {
        this.id = -1;
        this.className = "";
        this.departmentId = -1;
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

    public int getMajorId() {
        return departmentId;
    }

    public void setMajorId(int majorId) {
        this.departmentId = majorId;
    }
}
