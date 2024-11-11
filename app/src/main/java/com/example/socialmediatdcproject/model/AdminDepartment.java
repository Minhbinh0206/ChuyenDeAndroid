package com.example.socialmediatdcproject.model;

public class AdminDepartment extends User{
    private int departmentId;
    //thêm trương cập nhật background
    private String background;


    public AdminDepartment(int userId, String email, String password, String fullName, String avatar, String phoneNumber, int roleId, String background) {
        super(userId, email, password, fullName, avatar, phoneNumber, roleId);
        this.background = background;
    }

    public AdminDepartment(String background) {
        this.background = background;
    }

    public AdminDepartment(String fullName, String email, String password, String background) {
        super(fullName, email, password);
        this.background = background;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
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
