package com.example.socialmediatdcproject.database;

import android.util.Log;

import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Lecturer;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GroupDatabase {

    ArrayList<Group> groupsDtb = new ArrayList<>();
    UserDatabase userDatabase = new UserDatabase();

    public Group getGroupById(int id) {
        for (Group group : dataGroups()) {
            if (group.getGroupId() == id) {
                return group;
            }
        }
        return null;
    }

    public ArrayList<Group> dataGroups(){
        // Phòng Đào tạo
        ArrayList<User> groupUsersWithoutBussiness = new ArrayList<>();
        Group g1 = new Group();
        g1.setGroupId(User.ID_ADMIN_PHONGDAOTAO);
        g1.setGroupName("Phòng Đào tạo");
        g1.setAdminUserId(User.ID_ADMIN_PHONGDAOTAO);
        for (User u: userDatabase.dataUser()) {
            if (u.getUserId() != User.ID_ADMIN_BUSSINESS_FPT && u.getUserId() != User.ID_ADMIN_BUSSINESS_APPLE && u.getUserId() != User.ID_ADMIN_BUSSINESS_GRAB && u.getUserId() != User.ID_ADMIN_BUSSINESS_EVN && u.getUserId() != User.ID_ADMIN_BUSSINESS_MBBANK && u.getUserId() != User.ID_ADMIN_BUSSINESS_TITAN && u.getUserId() != User.ID_ADMIN_BUSSINESS_VINFAST) {
                groupUsersWithoutBussiness.add(u);
            }
        }
        groupsDtb.add(g1);

        // Đoàn Thanh niên
        Group g2 = new Group();
        g2.setGroupId(User.ID_ADMIN_DOANTHANHNIEN);
        g2.setGroupName("Đoàn Thanh niên");
        g2.setAdminUserId(User.ID_ADMIN_DOANTHANHNIEN);
        groupsDtb.add(g2);

        // Khoa Công nghệ Thông tin
        List<User> groupStudentsAndLecturerOfDepartment = new ArrayList<>();
        Group g3 = new Group();
        g3.setGroupId(User.ID_ADMIN_DEPARTMENT_CNTT);
        g3.setGroupName("Khoa Công nghệ Thông tin");
        g3.setAdminUserId(User.ID_ADMIN_DEPARTMENT_CNTT);
        // Tìm tất cả sinh viên có mã khoa là khoa CNTT
        for (Student s: userDatabase.dataStudent()) {
            if (s.getDepartmentId() == User.ID_ADMIN_DEPARTMENT_CNTT) {
                groupStudentsAndLecturerOfDepartment.add(s);
            }
        }

        // Tìm tất cả giảng viên có mã khoa là khoa CNTT
        for (Lecturer l: userDatabase.dataLecturer()) {
            if (l.getDepartmentId() == User.ID_ADMIN_DEPARTMENT_CNTT) {
                groupStudentsAndLecturerOfDepartment.add(l);
            }
        }
        groupsDtb.add(g3);

        // Khoa Công nghệ Cơ khí - Ô tô
        Group g4 = new Group();
        g4.setGroupId(User.ID_ADMIN_DEPARTMENT_CKOT);
        g4.setGroupName("Khoa Công nghệ Cơ khí - Ô tô");
        g4.setAdminUserId(User.ID_ADMIN_DEPARTMENT_CKOT);
        groupsDtb.add(g4);

        // Khoa Cơ khí Chế tạo máy
        Group g5 = new Group();
        g5.setGroupId(User.ID_ADMIN_DEPARTMENT_CKCTM);
        g5.setGroupName("Khoa Cơ khí Chế tạo máy");
        g5.setAdminUserId(User.ID_ADMIN_DEPARTMENT_CKCTM);
        groupsDtb.add(g5);

        // Khoa Công nghệ Tự động
        Group g6 = new Group();
        g6.setGroupId(User.ID_ADMIN_DEPARTMENT_CNTD);
        g6.setGroupName("Khoa Công nghệ Tự động");
        g6.setAdminUserId(User.ID_ADMIN_DEPARTMENT_CNTD);
        groupsDtb.add(g6);

        // Khoa Điện - Điện tử
        Group g7 = new Group();
        g7.setGroupId(User.ID_ADMIN_DEPARTMENT_DDT);
        g7.setGroupName("Khoa Điện - Điện tử");
        g7.setAdminUserId(User.ID_ADMIN_DEPARTMENT_DDT);
        groupsDtb.add(g7);

        // Khoa Tiếng Hàn
        Group g8 = new Group();
        g8.setGroupId(User.ID_ADMIN_DEPARTMENT_KOREA);
        g8.setGroupName("Khoa Tiếng Hàn");
        g8.setAdminUserId(User.ID_ADMIN_DEPARTMENT_KOREA);
        groupsDtb.add(g8);

        // Khoa Du lịch
        Group g9 = new Group();
        g9.setGroupId(User.ID_ADMIN_DEPARTMENT_DULICH);
        g9.setGroupName("Khoa Du lịch");
        g9.setAdminUserId(User.ID_ADMIN_DEPARTMENT_DULICH);
        groupsDtb.add(g9);

        // Khoa Quản trị Kinh doanh
        Group g10 = new Group();
        g10.setGroupId(User.ID_ADMIN_DEPARTMENT_QTKD);
        g10.setGroupName("Khoa Quản trị Kinh doanh");
        g10.setAdminUserId(User.ID_ADMIN_DEPARTMENT_QTKD);
        groupsDtb.add(g10);

        // Khoa Tiếng Anh
        Group g11 = new Group();
        g11.setGroupId(User.ID_ADMIN_DEPARTMENT_ENGLISH);
        g11.setGroupName("Khoa Tiếng Anh");
        g11.setAdminUserId(User.ID_ADMIN_DEPARTMENT_ENGLISH);
        groupsDtb.add(g11);

        // Khoa Tài chính - Kế toán
        Group g12 = new Group();
        g12.setGroupId(User.ID_ADMIN_DEPARTMENT_TCKT);
        g12.setGroupName("Khoa Tài chính - Kế toán");
        g12.setAdminUserId(User.ID_ADMIN_DEPARTMENT_TCKT);
        groupsDtb.add(g12);

        // Khoa Tiếng Nhật
        Group g13 = new Group();
        g13.setGroupId(User.ID_ADMIN_DEPARTMENT_JAPANESE);
        g13.setGroupName("Khoa Tiếng Nhật");
        g13.setAdminUserId(User.ID_ADMIN_DEPARTMENT_JAPANESE);
        groupsDtb.add(g13);

        // Khoa Tiếng Trung
        Group g14 = new Group();
        g14.setGroupId(User.ID_ADMIN_DEPARTMENT_CHINESE);
        g14.setGroupName("Khoa Tiếng Trung");
        g14.setAdminUserId(User.ID_ADMIN_DEPARTMENT_CHINESE);
        groupsDtb.add(g14);

        // Nhóm được tạo bởi các User
        Group gr1 = new Group();
        gr1.setGroupId(20);
        gr1.setGroupName("Chiến dịch mùa hè xanh");
        gr1.setAdminUserId(User.ID_ADMIN_DOANTHANHNIEN);
        groupsDtb.add(gr1);

        Group gr2 = new Group();
        gr2.setGroupId(21);
        gr2.setGroupName("Chiến dịch xuân tình nguyện");
        gr2.setAdminUserId(User.ID_ADMIN_DOANTHANHNIEN);
        groupsDtb.add(gr2);

        Group gr3 = new Group();
        gr3.setGroupId(22);
        gr3.setGroupName("Chiến dịch xanh sạch đẹp");
        gr3.setAdminUserId(User.ID_ADMIN_DOANTHANHNIEN);
        groupsDtb.add(gr3);

        return  groupsDtb;
    }
}
