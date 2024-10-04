package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Lecturer;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDatabase {
    protected ArrayList<User> usersDtb = new ArrayList<>();
    protected ArrayList<Student> studentsDtb = new ArrayList<>();
    protected ArrayList<Lecturer> lecturersDtb = new ArrayList<>();
    protected ArrayList<AdminDepartment> adminDepartmentsDtb = new ArrayList<>();

    // Hàm tìm student bằng userId
    public Student getStudentById(int id) {
        for (User user : usersDtb) {
            if (user.getUserId() == id && user instanceof Student) {
                return (Student) user;
            }
        }
        return null;
    }

    // Hàm tìm giảng viên bằng userId
    public Lecturer getLecturerById(int id) {
        for (User user : usersDtb) {
            if (user.getUserId() == id && user instanceof Lecturer) {
                return (Lecturer) user;
            }
        }
        return null;
    }
    // Role giả dữ liệu hiện tại
    // - 0: Sinh viên
    // - 1: Giảng viên - Nhân viên
    // - 2: Admin Khoa
    // - 3: Admin Doanh nghiệp
    // - 4: Đoàn thanh niên
    // - 5: Phòng đào tạo

    public ArrayList<User> dataUser() {
        // Phòng đào tạo
        User u1 = new User();
        u1.setUserId(User.ID_ADMIN_PHONGDAOTAO);
        u1.setEmail("phongdaotao@gmail.com");
        u1.setPassword("999");
        u1.setFullName("Phòng đào tạo");
        u1.setPhoneNumber("0987654321");
        u1.setAvatar("");
        u1.setRoleId(User.ROLE_PHONGDAOTAO);
        usersDtb.add(u1);

        // Đoàn thanh niên
        User u2 = new User();
        u2.setUserId(User.ID_ADMIN_DOANTHANHNIEN);
        u2.setEmail("doanthanhnien@gmail.com");
        u2.setPassword("999");
        u2.setFullName("Đoàn thanh niên");
        u2.setPhoneNumber("0987654322");
        u2.setAvatar("");
        u2.setRoleId(User.ROLE_DOANTHANHNIEN);
        usersDtb.add(u2);

        // Khoa CNTT
        User u3 = new User();
        u3.setUserId(User.ID_ADMIN_DEPARTMENT_CNTT);
        u3.setEmail("cntt@gmail.com");
        u3.setPassword("999");
        u3.setFullName("Khoa Công nghệ thông tin");
        u3.setPhoneNumber("0987654323");
        u3.setAvatar("");
        u3.setRoleId(User.ROLE_ADMIN_DEPARTMENT);
        usersDtb.add(u3);

        // Khoa CK-OT
        User u4 = new User();
        u4.setUserId(User.ID_ADMIN_DEPARTMENT_CKOT);
        u4.setEmail("ckot@gmail.com");
        u4.setPassword("999");
        u4.setFullName("Khoa Công nghệ cơ khí - ô tô");
        u4.setPhoneNumber("0987654324");
        u4.setAvatar("");
        u4.setRoleId(User.ROLE_ADMIN_DEPARTMENT);
        usersDtb.add(u4);

        // Khoa CK-CTM
        User u5 = new User();
        u5.setUserId(User.ID_ADMIN_DEPARTMENT_CKCTM);
        u5.setEmail("ckctm@gmail.com");
        u5.setPassword("999");
        u5.setFullName("Khoa Cơ khí chế tạo máy");
        u5.setPhoneNumber("0987654325");
        u5.setAvatar("");
        u5.setRoleId(User.ROLE_ADMIN_DEPARTMENT);
        usersDtb.add(u5);

        // Khoa CNTĐ
        User u6 = new User();
        u6.setUserId(User.ID_ADMIN_DEPARTMENT_CNTD);
        u6.setEmail("cntd@gmail.com");
        u6.setPassword("999");
        u6.setFullName("Khoa Công nghệ tự động");
        u6.setPhoneNumber("0987654326");
        u6.setAvatar("");
        u6.setRoleId(User.ROLE_ADMIN_DEPARTMENT);
        usersDtb.add(u6);

        // Khoa Đ-ĐT
        User u7 = new User();
        u7.setUserId(User.ID_ADMIN_DEPARTMENT_DDT);
        u7.setEmail("ddt@gmail.com");
        u7.setPassword("999");
        u7.setFullName("Khoa Điện - Điện tử");
        u7.setPhoneNumber("0987654327");
        u7.setAvatar("");
        u7.setRoleId(User.ROLE_ADMIN_DEPARTMENT);
        usersDtb.add(u7);

        // Khoa Tiếng Hàn
        User u8 = new User();
        u8.setUserId(User.ID_ADMIN_DEPARTMENT_KOREA);
        u8.setEmail("tienghan@gmail.com");
        u8.setPassword("999");
        u8.setFullName("Khoa Tiếng Hàn");
        u8.setPhoneNumber("0987654328");
        u8.setAvatar("");
        u8.setRoleId(User.ROLE_ADMIN_DEPARTMENT);
        usersDtb.add(u8);

        // Khoa Du lịch
        User u9 = new User();
        u9.setUserId(User.ID_ADMIN_DEPARTMENT_DULICH);
        u9.setEmail("dulich@gmail.com");
        u9.setPassword("999");
        u9.setFullName("Khoa Du lịch");
        u9.setPhoneNumber("0987654329");
        u9.setAvatar("");
        u9.setRoleId(User.ROLE_ADMIN_DEPARTMENT);
        usersDtb.add(u9);

        // Khoa QTKD
        User u10 = new User();
        u10.setUserId(User.ID_ADMIN_DEPARTMENT_QTKD);
        u10.setEmail("qtkd@gmail.com");
        u10.setPassword("999");
        u10.setFullName("Khoa Quản trị kinh doanh");
        u10.setPhoneNumber("0987654330");
        u10.setAvatar("");
        u10.setRoleId(User.ROLE_ADMIN_DEPARTMENT);
        usersDtb.add(u10);

        // Khoa Tiếng Anh
        User u11 = new User();
        u11.setUserId(User.ID_ADMIN_DEPARTMENT_ENGLISH);
        u11.setEmail("tienganh@gmail.com");
        u11.setPassword("999");
        u11.setFullName("Khoa Tiếng Anh");
        u11.setPhoneNumber("0987654331");
        u11.setAvatar("");
        u11.setRoleId(User.ROLE_ADMIN_DEPARTMENT);
        usersDtb.add(u11);

        // Khoa TCKT
        User u12 = new User();
        u12.setUserId(User.ID_ADMIN_DEPARTMENT_TCKT);
        u12.setEmail("tckt@gmail.com");
        u12.setPassword("999");
        u12.setFullName("Khoa Tài chính - Kế toán");
        u12.setPhoneNumber("0987654332");
        u12.setAvatar("");
        u12.setRoleId(User.ROLE_ADMIN_DEPARTMENT);
        usersDtb.add(u12);

        // Khoa Tiếng Nhật
        User u13 = new User();
        u13.setUserId(User.ID_ADMIN_DEPARTMENT_JAPANESE);
        u13.setEmail("tiengnhat@gmail.com");
        u13.setPassword("999");
        u13.setFullName("Khoa Tiếng Nhật");
        u13.setPhoneNumber("0987654333");
        u13.setAvatar("");
        u13.setRoleId(User.ROLE_ADMIN_DEPARTMENT);
        usersDtb.add(u13);

        // Khoa Tiếng Trung
        User u14 = new User();
        u14.setUserId(User.ID_ADMIN_DEPARTMENT_CHINESE);
        u14.setEmail("tiengtrung@gmail.com");
        u14.setPassword("999");
        u14.setFullName("Khoa Tiếng Trung");
        u14.setPhoneNumber("0987654334");
        u14.setAvatar("");
        u14.setRoleId(User.ROLE_ADMIN_DEPARTMENT);
        usersDtb.add(u14);

        // Doanh nghiệp FPT
        User u15 = new User();
        u15.setUserId(User.ID_ADMIN_BUSSINESS_FPT);
        u15.setEmail("fpt@gmail.com");
        u15.setPassword("999");
        u15.setFullName("FPT");
        u15.setPhoneNumber("0987654335");
        u15.setAvatar("");
        u15.setRoleId(User.ROLE_ADMIN_BUSSINESS);
        usersDtb.add(u15);

        // Doanh nghiệp MB Bank
        User u16 = new User();
        u16.setUserId(User.ID_ADMIN_BUSSINESS_MBBANK);
        u16.setEmail("mbbank@gmail.com");
        u16.setPassword("999");
        u16.setFullName("MB Bank");
        u16.setPhoneNumber("0987654336");
        u16.setAvatar("");
        u16.setRoleId(User.ROLE_ADMIN_BUSSINESS);
        usersDtb.add(u16);

        // Doanh nghiệp Vinfast
        User u17 = new User();
        u17.setUserId(User.ID_ADMIN_BUSSINESS_VINFAST);
        u17.setEmail("vinfast@gmail.com");
        u17.setPassword("999");
        u17.setFullName("Vinfast");
        u17.setPhoneNumber("0987654337");
        u17.setAvatar("");
        u17.setRoleId(User.ROLE_ADMIN_BUSSINESS);
        usersDtb.add(u17);

        // Doanh nghiệp Apple
        User u18 = new User();
        u18.setUserId(User.ID_ADMIN_BUSSINESS_APPLE);
        u18.setEmail("apple@gmail.com");
        u18.setPassword("999");
        u18.setFullName("Apple");
        u18.setPhoneNumber("0987654338");
        u18.setAvatar("");
        u18.setRoleId(User.ROLE_ADMIN_BUSSINESS);
        usersDtb.add(u18);

        // Doanh nghiệp Grab
        User u19 = new User();
        u19.setUserId(User.ID_ADMIN_BUSSINESS_GRAB);
        u19.setEmail("grab@gmail.com");
        u19.setPassword("999");
        u19.setFullName("Grab");
        u19.setPhoneNumber("0987654339");
        u19.setAvatar("");
        u19.setRoleId(User.ROLE_ADMIN_BUSSINESS);
        usersDtb.add(u19);

        // Doanh nghiệp EVN
        User u20 = new User();
        u20.setUserId(User.ID_ADMIN_BUSSINESS_EVN);
        u20.setEmail("evn@gmail.com");
        u20.setPassword("999");
        u20.setFullName("EVN");
        u20.setPhoneNumber("0987654340");
        u20.setAvatar("");
        u20.setRoleId(User.ROLE_ADMIN_BUSSINESS);
        usersDtb.add(u20);

        // Doanh nghiệp TITAN ENGLISH
        User u21 = new User();
        u21.setUserId(User.ID_ADMIN_BUSSINESS_TITAN);
        u21.setEmail("titan@gmail.com");
        u21.setPassword("999");
        u21.setFullName("TITAN");
        u21.setPhoneNumber("0987654340");
        u21.setAvatar("");
        u21.setRoleId(User.ROLE_ADMIN_BUSSINESS);
        usersDtb.add(u21);

        // Student
        User u22 = new User();
        u22.setUserId(21);
        u22.setEmail("nguyenvana@gmail.com");
        u22.setPassword("111");
        u22.setFullName("Nguyễn Văn A");
        u22.setPhoneNumber("0123456789");
        u22.setAvatar("hs");
        u22.setRoleId(User.ROLE_STUDENT);
        usersDtb.add(u22);

        User u23 = new User();
        u23.setUserId(22);
        u23.setEmail("tranthic@gmail.com");
        u23.setPassword("111");
        u23.setFullName("Trần Thị C");
        u23.setPhoneNumber("0123456788");
        u23.setAvatar("hs");
        u23.setRoleId(User.ROLE_STUDENT);
        usersDtb.add(u2);

        User u24 = new User();
        u24.setUserId(23);
        u24.setEmail("phamvand@gmail.com");
        u24.setPassword("111");
        u24.setFullName("Phạm Văn D");
        u24.setPhoneNumber("0123456787");
        u24.setAvatar("hs");
        u24.setRoleId(User.ROLE_STUDENT);
        usersDtb.add(u24);

        User u25 = new User();
        u25.setUserId(24);
        u25.setEmail("nguyenthie@gmail.com");
        u25.setPassword("111");
        u25.setFullName("Nguyễn Thị E");
        u25.setPhoneNumber("0123456786");
        u25.setAvatar("hs");
        u25.setRoleId(User.ROLE_STUDENT);
        usersDtb.add(u25);

        User u26 = new User();
        u26.setUserId(25);
        u26.setEmail("buiquangf@gmail.com");
        u26.setPassword("111");
        u26.setFullName("Bùi Quang F");
        u26.setPhoneNumber("0123456785");
        u26.setAvatar("hs");
        u26.setRoleId(User.ROLE_STUDENT);
        usersDtb.add(u26);

        User u27 = new User();
        u27.setUserId(26);
        u27.setEmail("doanthig@gmail.com");
        u27.setPassword("111");
        u27.setFullName("Đoàn Thị G");
        u27.setPhoneNumber("0123456784");
        u27.setAvatar("hs");
        u27.setRoleId(User.ROLE_STUDENT);
        usersDtb.add(u27);

        User u28 = new User();
        u28.setUserId(27);
        u28.setEmail("lehoangh@gmail.com");
        u28.setPassword("111");
        u28.setFullName("Lê Hoàng H");
        u28.setPhoneNumber("0123456783");
        u28.setAvatar("hs");
        u28.setRoleId(User.ROLE_STUDENT);
        usersDtb.add(u28);

        // Lecturer
        User u29 = new User();
        u29.setUserId(28);
        u29.setEmail("lethib@gmail.com");
        u29.setPassword("111");
        u29.setFullName("Lê Thị B");
        u29.setPhoneNumber("0123456782");
        u29.setAvatar("hs");
        u29.setRoleId(User.ROLE_LECTURER);
        usersDtb.add(u29);

        User u30 = new User();
        u30.setUserId(29);
        u30.setEmail("tranvanc@gmail.com");
        u30.setPassword("111");
        u30.setFullName("Trần Văn C");
        u30.setPhoneNumber("0123456781");
        u30.setAvatar("hs");
        u30.setRoleId(User.ROLE_LECTURER);
        usersDtb.add(u30);

        User u31 = new User();
        u31.setUserId(30);
        u31.setEmail("nguyenquangd@gmail.com");
        u31.setPassword("111");
        u31.setFullName("Nguyễn Quang D");
        u31.setPhoneNumber("0123456780");
        u31.setAvatar("hs");
        u31.setRoleId(User.ROLE_LECTURER);
        usersDtb.add(u31);

        return usersDtb;
    }

    // Sinh viên
    // Hiện tại đang cho sinh viên giống nhau hết
    public ArrayList<Student> dataStudent() {
        for (User u : usersDtb) {
            switch (u.getUserId()) {
                case 21:
                    // Nguyễn Văn A
                    Student s1 = new Student(u.getUserId(), u.getEmail(), u.getPassword(), u.getFullName(), u.getAvatar(), u.getPhoneNumber(), u.getRoleId());
                    s1.setStudentNumber("22211TT0001");
                    s1.setBirthday("22/11/2001");
                    s1.setDescription("Học sinh năm nhất");
                    s1.setDepartmentId(User.ID_ADMIN_DEPARTMENT_CNTT);
                    s1.setMajorId(6);
                    studentsDtb.add(s1);
                    break;
                case 22:
                    // Trần Thị C
                    Student s2 = new Student(u.getUserId(), u.getEmail(), u.getPassword(), u.getFullName(), u.getAvatar(), u.getPhoneNumber(), u.getRoleId());
                    s2.setStudentNumber("22211KD0002");
                    s2.setBirthday("15/10/2001");
                    s2.setDescription("Học sinh năm nhất");
                    s2.setDepartmentId(User.ID_ADMIN_DEPARTMENT_QTKD);
                    s2.setMajorId(6);
                    studentsDtb.add(s2);
                    break;
                case 23:
                    // Phạm Văn D
                    Student s3 = new Student(u.getUserId(), u.getEmail(), u.getPassword(), u.getFullName(), u.getAvatar(), u.getPhoneNumber(), u.getRoleId());
                    s3.setStudentNumber("22211DH0003");
                    s3.setBirthday("10/12/2001");
                    s3.setDescription("Học sinh năm nhất");
                    s3.setDepartmentId(User.ID_ADMIN_DEPARTMENT_CNTT);
                    s3.setMajorId(6);
                    studentsDtb.add(s3);
                    break;
                case 24:
                    // Nguyễn Thị E
                    Student s4 = new Student(u.getUserId(), u.getEmail(), u.getPassword(), u.getFullName(), u.getAvatar(), u.getPhoneNumber(), u.getRoleId());
                    s4.setStudentNumber("22211CK0004");
                    s4.setBirthday("05/08/2001");
                    s4.setDescription("Học sinh năm nhất");
                    s4.setDepartmentId(User.ID_ADMIN_DEPARTMENT_CKOT);
                    s4.setMajorId(6);
                    studentsDtb.add(s4);
                    break;
                case 25:
                    // Bùi Quang F
                    Student s5 = new Student(u.getUserId(), u.getEmail(), u.getPassword(), u.getFullName(), u.getAvatar(), u.getPhoneNumber(), u.getRoleId());
                    s5.setStudentNumber("22211OT0005");
                    s5.setBirthday("12/06/2001");
                    s5.setDescription("Học sinh năm nhất");
                    s5.setDepartmentId(User.ID_ADMIN_DEPARTMENT_CKOT);
                    s5.setMajorId(6);
                    studentsDtb.add(s5);
                    break;
                case 26:
                    // Đoàn Thị G
                    Student s6 = new Student(u.getUserId(), u.getEmail(), u.getPassword(), u.getFullName(), u.getAvatar(), u.getPhoneNumber(), u.getRoleId());
                    s6.setStudentNumber("22211CK0006");
                    s6.setBirthday("20/09/2001");
                    s6.setDescription("Học sinh năm nhất");
                    s6.setDepartmentId(User.ID_ADMIN_DEPARTMENT_CKOT);
                    s6.setMajorId(6);
                    studentsDtb.add(s6);
                    break;
                case 27:
                    // Lê Hoàng H
                    Student s7 = new Student(u.getUserId(), u.getEmail(), u.getPassword(), u.getFullName(), u.getAvatar(), u.getPhoneNumber(), u.getRoleId());
                    s7.setStudentNumber("22211TT0007");
                    s7.setBirthday("30/11/2001");
                    s7.setDescription("Học sinh năm nhất");
                    s7.setDepartmentId(User.ID_ADMIN_DEPARTMENT_CNTT);
                    s7.setMajorId(6);
                    studentsDtb.add(s7);
                    break;
                default:
                    break;
            }
        }

        return studentsDtb;
    }

    // Giảng viên
    // Hiện tại đang cho giảng viên giống nhau hết
    public ArrayList<Lecturer> dataLecturer() {
        for (User u : usersDtb) {
            switch (u.getUserId()) {
                case 28:
                    // Lê Thị B
                    Lecturer l1 = new Lecturer(u.getUserId(), u.getEmail(), u.getPassword(), u.getFullName(), u.getAvatar(), u.getPhoneNumber(), u.getRoleId());
                    l1.setLecturerNumber("33311LT0001");
                    l1.setBirthday("12/05/1980");
                    l1.setDescription("Giảng viên chính");
                    l1.setDepartmentId(User.ID_ADMIN_DEPARTMENT_CNTT);
                    lecturersDtb.add(l1);
                    break;
                case 29:
                    // Trần Văn C
                    Lecturer l2 = new Lecturer(u.getUserId(), u.getEmail(), u.getPassword(), u.getFullName(), u.getAvatar(), u.getPhoneNumber(), u.getRoleId());
                    l2.setLecturerNumber("33311LT0002");
                    l2.setBirthday("08/03/1978");
                    l2.setDescription("Giảng viên thỉnh giảng");
                    l2.setDepartmentId(User.ID_ADMIN_DEPARTMENT_QTKD);
                    lecturersDtb.add(l2);
                    break;
                case 30:
                    // Nguyễn Quang D
                    Lecturer l3 = new Lecturer(u.getUserId(), u.getEmail(), u.getPassword(), u.getFullName(), u.getAvatar(), u.getPhoneNumber(), u.getRoleId());
                    l3.setLecturerNumber("33311LT0003");
                    l3.setBirthday("25/10/1975");
                    l3.setDescription("Giảng viên cơ hữu");
                    l3.setDepartmentId(User.ID_ADMIN_DEPARTMENT_CNTT);
                    lecturersDtb.add(l3);
                    break;
            }
        }
        return lecturersDtb;
    }

    // Admin Department - CHỈ ĐỊNH
    public ArrayList<AdminDepartment> dataAdminDepartment() {
        ArrayList<AdminDepartment> adminDepartmentsDtb = new ArrayList<>();

        for (User u : usersDtb) {
            if (u.getRoleId() == User.ROLE_ADMIN_DEPARTMENT) {
                AdminDepartment adminDepartment = new AdminDepartment(u.getUserId(), u.getEmail(), u.getPassword(), u.getFullName(), u.getAvatar(), u.getPhoneNumber(), u.getRoleId());

                // Gán departmentId tương ứng
                switch (u.getUserId()) {
                    case User.ID_ADMIN_DEPARTMENT_CNTT:
                        adminDepartment.setDepartmentId(User.ID_ADMIN_DEPARTMENT_CNTT);
                        break;
                    case User.ID_ADMIN_DEPARTMENT_CKOT:
                        adminDepartment.setDepartmentId(User.ID_ADMIN_DEPARTMENT_CKOT);
                        break;
                    case User.ID_ADMIN_DEPARTMENT_CKCTM:
                        adminDepartment.setDepartmentId(User.ID_ADMIN_DEPARTMENT_CKCTM);
                        break;
                    case User.ID_ADMIN_DEPARTMENT_CNTD:
                        adminDepartment.setDepartmentId(User.ID_ADMIN_DEPARTMENT_CNTD);
                        break;
                    case User.ID_ADMIN_DEPARTMENT_DDT:
                        adminDepartment.setDepartmentId(User.ID_ADMIN_DEPARTMENT_DDT);
                        break;
                    case User.ID_ADMIN_DEPARTMENT_KOREA:
                        adminDepartment.setDepartmentId(User.ID_ADMIN_DEPARTMENT_KOREA);
                        break;
                    case User.ID_ADMIN_DEPARTMENT_DULICH:
                        adminDepartment.setDepartmentId(User.ID_ADMIN_DEPARTMENT_DULICH);
                        break;
                    case User.ID_ADMIN_DEPARTMENT_QTKD:
                        adminDepartment.setDepartmentId(User.ID_ADMIN_DEPARTMENT_QTKD);
                        break;
                    case User.ID_ADMIN_DEPARTMENT_ENGLISH:
                        adminDepartment.setDepartmentId(User.ID_ADMIN_DEPARTMENT_ENGLISH);
                        break;
                    case User.ID_ADMIN_DEPARTMENT_TCKT:
                        adminDepartment.setDepartmentId(User.ID_ADMIN_DEPARTMENT_TCKT);
                        break;
                    case User.ID_ADMIN_DEPARTMENT_JAPANESE:
                        adminDepartment.setDepartmentId(User.ID_ADMIN_DEPARTMENT_JAPANESE);
                        break;
                    case User.ID_ADMIN_DEPARTMENT_CHINESE:
                        adminDepartment.setDepartmentId(User.ID_ADMIN_DEPARTMENT_CHINESE);
                        break;
                }

                adminDepartmentsDtb.add(adminDepartment); // Thêm admin department vào danh sách
            }
        }

        return adminDepartmentsDtb;
    }

}
