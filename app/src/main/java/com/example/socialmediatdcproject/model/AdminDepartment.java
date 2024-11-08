package com.example.socialmediatdcproject.model;

public class AdminDepartment extends User{
    private int departmentId;
    //thêm trương cập nhật backgroup
    private String backgroup;


    public AdminDepartment(int userId, String email, String password, String fullName, String avatar, String phoneNumber, int roleId, String backgroup) {
        super(userId, email, password, fullName, avatar, phoneNumber, roleId);
        this.backgroup = backgroup;
    }

    public AdminDepartment(String backgroup) {
        this.backgroup = backgroup;
    }

    public AdminDepartment(String fullName, String email, String password, String backgroup) {
        super(fullName, email, password);
        this.backgroup = backgroup;
    }

    public String getBackgroup() {
        return backgroup;
    }

    public void setBackgroup(String backgroup) {
        this.backgroup = backgroup;
    }



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
