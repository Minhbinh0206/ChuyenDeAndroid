package com.example.socialmediatdcproject.model;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private int departmentId;
    private String departmentName;
    private String departmentInfo;

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

    public String getDepartmentInfo() {
        return departmentInfo;
    }

    public void setDepartmentInfo(String departmentInfo) {
        this.departmentInfo = departmentInfo;
    }

    // Constructor mặc định
    public Department() {
        this.departmentId = -1;
        this.departmentName = "";
        this.departmentInfo = "";
    }

    // Constructor với các tham số
    public Department(int departmentId, String departmentName, String departmentInfo) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentInfo = departmentInfo;
    }
}
