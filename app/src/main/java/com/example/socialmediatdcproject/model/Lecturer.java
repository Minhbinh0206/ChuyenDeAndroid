package com.example.socialmediatdcproject.model;

public class Lecturer extends User {
    private String lecturerNumber;
    private String birthday;
    private int departmentId;
    private int majorId;
    private String description;



    //thêm trương cập nhật backgroup
    private String backgroup;

    public Lecturer(int userId, String email, String password, String fullName, String avatar, String phoneNumber, int roleId, String backgroup) {
        super(userId, email, password, fullName, avatar, phoneNumber, roleId);
        this.backgroup = backgroup;
    }

    public Lecturer(String backgroup) {
        this.backgroup = backgroup;
    }

    public Lecturer(String fullName, String email, String password, String backgroup) {
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

    public String getLecturerNumber() {
        return lecturerNumber;
    }

    public void setLecturerNumber(String lecturerNumber) {
        this.lecturerNumber = lecturerNumber;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Lecturer(int userId, String email, String password, String fullName, String avatar, String phoneNumber, int roleId, String lecturerNumber, String birthday, int departmentId, int majorId, String description) {
        super(userId, email, password, fullName, avatar, phoneNumber, roleId);
        this.lecturerNumber = lecturerNumber;
        this.birthday = birthday;
        this.departmentId = departmentId;
        this.majorId = majorId;
        this.description = description;
    }

    public Lecturer(int userId, String email, String password, String fullName, String avatar, String phoneNumber, int roleId) {
        super(userId, email, password, fullName, avatar, phoneNumber, roleId);
        this.lecturerNumber = "";
        this.birthday = "";
        this.departmentId = -1;
        this.majorId = -1;
        this.description = "";
    }

    public Lecturer() {
    }
}
