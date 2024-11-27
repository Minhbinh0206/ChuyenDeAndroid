package com.example.socialmediatdcproject.model;

public class User {
    public static final int ID_ADMIN_BUSINES = 1;
    private int userId;
    private String email;
    private String password;
    private String fullName;
    private String avatar;
    private String phoneNumber;
    private int roleId;
    // Key đăng nhập
    public static final int USER_KEY = -999;
    // Các id đặc biệt
    public static final int ID_ADMIN_PHONGDAOTAO = 0;
    public static final int ID_ADMIN_DOANTHANHNIEN = 1;
    // ----- KHOA ----- //
    public static final int ID_ADMIN_DEPARTMENT_CNTT = 2;           // Khoa Công nghệ thông tin
    public static final int ID_ADMIN_DEPARTMENT_CKOT = 3;           // Khoa Công nghệ cơ khí - ô tô
    public static final int ID_ADMIN_DEPARTMENT_CKCTM = 4;          // Khoa Cơ khí chế tạo máy
    public static final int ID_ADMIN_DEPARTMENT_CNTD = 5;           // Khoa Công nghệ tự động
    public static final int ID_ADMIN_DEPARTMENT_DDT = 6;            // Khoa Điện - Điện tử
    public static final int ID_ADMIN_DEPARTMENT_KOREA = 7;          // Khoa Tiếng Hàn
    public static final int ID_ADMIN_DEPARTMENT_DULICH = 8;         // Khoa Du lịch
    public static final int ID_ADMIN_DEPARTMENT_QTKD = 9;           // Khoa Quản trị kinh doanh
    public static final int ID_ADMIN_DEPARTMENT_ENGLISH = 10;       // Khoa Tiếng Anh
    public static final int ID_ADMIN_DEPARTMENT_TCKT = 11;          // Khoa Tài chính - Kế toán
    public static final int ID_ADMIN_DEPARTMENT_JAPANESE = 12;      // Khoa Tiếng Nhật
    public static final int ID_ADMIN_DEPARTMENT_CHINESE = 13;       // Khoa Tiếng Trung
    // ----- Doanh Nghiep ----- //
    public static final int ID_ADMIN_BUSSINESS_FPT = 14;            // FPT
    public static final int ID_ADMIN_BUSSINESS_MBBANK = 15;         // MB Bank
    public static final int ID_ADMIN_BUSSINESS_VINFAST = 16;        // Vinfast
    public static final int ID_ADMIN_BUSSINESS_APPLE = 17;          // Apple
    public static final int ID_ADMIN_BUSSINESS_GRAB = 18;           // Grab
    public static final int ID_ADMIN_BUSSINESS_EVN = 19;            // EVN
    public static final int ID_ADMIN_BUSSINESS_TITAN = 20;          // TITAN ENGLISH

    // CÁC ROLE GIẢ DỮ LIỆU
    public static final int ROLE_STUDENT = 0;                       // Sinh viên
    public static final int ROLE_LECTURER = 1;                      // Giảng viên - Nhân viên
    public static final int ROLE_ADMIN_DEPARTMENT = 2;              // Admin Khoa
    public static final int ROLE_ADMIN_BUSSINESS= 3;                // Admin Doanh nghiệp
    public static final int ROLE_DOANTHANHNIEN = 4;                 // Đoàn thanh niên
    public static final int ROLE_PHONGDAOTAO = 5;                   // Phòng đào tạo


    // Các Admin Type
    public static final String TYPE_ADMIN_TRAINING = "Training";
    public static final String TYPE_ADMIN_YOUTH = "Youth";
    public static final String TYPE_SUPPER_ADMIN = "Super";

    // Getter - Setter
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public User(int userId, String email, String password, String fullName, String avatar, String phoneNumber, int roleId) {
        this.email = email;
        this.userId = userId;
        this.password = password;
        this.fullName = fullName;
        this.avatar = avatar;
        this.roleId = roleId;
        this.phoneNumber = phoneNumber;
    }

    public User() {
        this.email = "";
        this.userId = -1;
        this.password = "";
        this.fullName = "";
        this.avatar = "";
        this.roleId = -1;
        this.phoneNumber = "";
    }

    public User(String fullName, String email, String password) {
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.avatar = "";
        this.roleId = -1;
        this.phoneNumber = "";
    }
}
