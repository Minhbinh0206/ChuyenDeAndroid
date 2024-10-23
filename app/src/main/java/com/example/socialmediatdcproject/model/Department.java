package com.example.socialmediatdcproject.model;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private int departmentId;
    private String departmentName;
    private String departmentInfo;
    private int groupId ;

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

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    // Constructor mặc định
    public Department() {
        this.departmentId = -1;
        this.departmentName = "";
        this.departmentInfo = "";
        this.groupId = -1;
    }

    // Constructor với các tham số
    public Department(int departmentId, String departmentName, String departmentInfo, int groupId) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentInfo = departmentInfo;
        this.groupId = groupId;
    }
}
