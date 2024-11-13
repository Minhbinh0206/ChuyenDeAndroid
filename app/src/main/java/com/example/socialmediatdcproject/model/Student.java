package com.example.socialmediatdcproject.model;

public class Student extends User {
    private String studentNumber;
    private String birthday;
    private int departmentId;
    private int majorId;
    private int classId;
    private String description;

    // Thêm trường cập nhật Avatar
    private String avatar;
    private String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    //thêm trương cập nhật background
    private String background;


    //

    public Student(int userId, String email, String password, String fullName, String avatar, String phoneNumber, int roleId, String background) {
        super(userId, email, password, fullName, avatar, phoneNumber, roleId);
        this.background = background;
    }


    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }



    public String getAvatar() {
        return avatar;
    }

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

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Student(int userId, String email, String password, String fullName, String avatar, String phoneNumber, int roleId, String studentNumber, String birthday, int departmentId, int majorId, int studentClass, String description) {
        super(userId, email, password, fullName, avatar, phoneNumber, roleId);
        this.studentNumber = studentNumber;
        this.birthday = birthday;
        this.departmentId = departmentId;
        this.majorId = majorId;
        this.classId = studentClass;
        this.description = description;
        this.background = background;
    }
    public Student(int userId, String email, String password, String fullName, String avatar, String phoneNumber, int roleId, String studentNumber, String birthday, int departmentId, int majorId, int studentClass, String description , String gender) {
        super(userId, email, password, fullName, avatar, phoneNumber, roleId);
        this.studentNumber = studentNumber;
        this.birthday = birthday;
        this.departmentId = departmentId;
        this.majorId = majorId;
        this.classId = studentClass;
        this.description = description;
        this.gender = gender;
    }
    public Student() {

    }

    public Student(String avatar){
        this.avatar = avatar;
    }

    public Student(String fullName, String email, String password, String background) {
        super(fullName, email, password);
        this.background = background;
    }

}
