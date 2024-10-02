package com.example.socialmediatdcproject.model;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private int departmentId;
    private String departmentName;
    private List<Integer> majorId;

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

    public List<Integer> getMajorId() {
        return majorId;
    }

    public void setMajorId(int... majorIds) {
        for (int id : majorIds) {
            this.majorId.add(id);
        }
    }

    // Constructor mặc định
    public Department() {
        this.departmentId = -1;
        this.departmentName = "";
        this.majorId = new ArrayList<>(); // Khởi tạo danh sách trong constructor
    }

    // Constructor với các tham số
    public Department(int departmentId, String departmentName, List<Integer> majorId) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.majorId = majorId != null ? majorId : new ArrayList<>(); // Khởi tạo danh sách, nếu không có thì tạo mới
    }
}
