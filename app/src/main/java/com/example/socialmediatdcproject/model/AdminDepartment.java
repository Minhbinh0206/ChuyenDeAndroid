package com.example.socialmediatdcproject.model;

public class AdminDepartment extends User{
    private int departmentId;

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public AdminDepartment(int userId, String email, String password, String fullName, String avatar, String phoneNumber, int roleId, int departmentId) {
        super(userId, email, password, fullName, avatar, phoneNumber, roleId);
        this.departmentId = departmentId;
    }

    public AdminDepartment(int userId, String email, String password, String fullName, String avatar, String phoneNumber, int roleId) {
        super(userId, email, password, fullName, avatar, phoneNumber, roleId);
        this.departmentId = -1;
    }

    public AdminDepartment() {
    }
}
