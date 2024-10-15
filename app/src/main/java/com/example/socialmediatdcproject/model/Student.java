package com.example.socialmediatdcproject.model;

import android.net.Uri;

public class Student extends User {
    private String studentNumber;
    private String birthday;
    private int departmentId;
    private int majorId;
    private String studentClass;
    private String description;
    // Thêm trường cập nhật Avatar
    private String avatar;

    @Override
    public String getAvatar() {
        return avatar;
    }

    @Override
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    //------------------------------------------

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getMajorId() {
        return majorId;
    }

    public void setMajorId(int majorId) {
        this.majorId = majorId;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Student(int userId, String email, String password, String fullName, String avatar, String phoneNumber, int roleId, String studentNumber, String birthday, int departmentId, int majorId, String studentClass, String description) {
        super(userId, email, password, fullName, avatar, phoneNumber, roleId);
        this.studentNumber = studentNumber;
        this.birthday = birthday;
        this.departmentId = departmentId;
        this.majorId = majorId;
        this.studentClass = studentClass;
        this.description = description;
    }


    public Student(int userId, String email, String password, String fullName, String avatar, String phoneNumber, int roleId) {
        super(userId, email, password, fullName, avatar, phoneNumber, roleId);
        this.studentNumber = "";
        this.birthday = "";
        this.departmentId = -1;
        this.majorId = -1;
        this.studentClass = "";
        this.description = "";
    }

    public Student() {

    }
}
