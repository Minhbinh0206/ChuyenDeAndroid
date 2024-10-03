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

    // Hàm tìm group bằng userId
    public Group getGroupById(int id, ArrayList<Group> groupArrayList) {
        for (Group group : groupArrayList) {
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
        g1.setGroupMember(groupUsersWithoutBussiness);
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
        g3.setGroupMember(groupStudentsAndLecturerOfDepartment);
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

        // Doanh nghiệp FPT
        Group g15 = new Group();
        g15.setGroupId(User.ID_ADMIN_BUSSINESS_FPT);
        g15.setGroupName("Doanh nghiệp FPT");
        g15.setAdminUserId(User.ID_ADMIN_BUSSINESS_FPT);
        groupsDtb.add(g15);

        // Doanh nghiệp MB Bank
        Group g16 = new Group();
        g16.setGroupId(User.ID_ADMIN_BUSSINESS_MBBANK);
        g16.setGroupName("Doanh nghiệp MB Bank");
        g16.setAdminUserId(User.ID_ADMIN_BUSSINESS_MBBANK);
        groupsDtb.add(g16);

        // Doanh nghiệp Vinfast
        Group g17 = new Group();
        g17.setGroupId(User.ID_ADMIN_BUSSINESS_VINFAST);
        g17.setGroupName("Doanh nghiệp Vinfast");
        g17.setAdminUserId(User.ID_ADMIN_BUSSINESS_VINFAST);
        groupsDtb.add(g17);

        // Doanh nghiệp Apple
        Group g18 = new Group();
        g18.setGroupId(User.ID_ADMIN_BUSSINESS_APPLE);
        g18.setGroupName("Doanh nghiệp Apple");
        g18.setAdminUserId(User.ID_ADMIN_BUSSINESS_APPLE);
        groupsDtb.add(g18);

        // Doanh nghiệp Grab
        Group g19 = new Group();
        g19.setGroupId(User.ID_ADMIN_BUSSINESS_GRAB);
        g19.setGroupName("Doanh nghiệp Grab");
        g19.setAdminUserId(User.ID_ADMIN_BUSSINESS_GRAB);
        groupsDtb.add(g19);

        // Doanh nghiệp EVN
        Group g20 = new Group();
        g20.setGroupId(User.ID_ADMIN_BUSSINESS_EVN);
        g20.setGroupName("Doanh nghiệp EVN");
        g20.setAdminUserId(User.ID_ADMIN_BUSSINESS_EVN);
        groupsDtb.add(g20);


        return  groupsDtb;
    }
}
