package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Major;
import com.example.socialmediatdcproject.model.User;

import java.util.ArrayList;

public class DepartmentDatabase {
    ArrayList<Department> departmentsDtb = new ArrayList<>();

    public Department getDepartmentByName(String name, ArrayList<Department> departmentArrayList) {
        for (Department department : departmentArrayList) {
            if (department.getDepartmentName() == name) {
                return department;
            }
        }
        return null;
    }

    public ArrayList<Department> dataDepartments(){
        // Khoa Công nghệ thông tin
        Department d1 = new Department();
        d1.setDepartmentId(User.ID_ADMIN_DEPARTMENT_CNTT);
        d1.setDepartmentName("Công nghệ thông tin");
        d1.setMajorId(6,7,8);
        departmentsDtb.add(d1);

        // Khoa Công nghệ cơ khí - ô tô
        Department d2 = new Department();
        d2.setDepartmentId(User.ID_ADMIN_DEPARTMENT_CKOT);
        d2.setDepartmentName("Công nghệ cơ khí - ô tô");
        d2.setMajorId(0);
        departmentsDtb.add(d2);

        // Khoa Cơ khí chế tạo máy
        Department d3 = new Department();
        d3.setDepartmentId(User.ID_ADMIN_DEPARTMENT_CKCTM);
        d3.setDepartmentName("Cơ khí chế tạo máy");
        d3.setMajorId(1,2);
        departmentsDtb.add(d3);

        // Khoa Công nghệ tự động
        Department d4 = new Department();
        d4.setDepartmentId(User.ID_ADMIN_DEPARTMENT_CNTD);
        d4.setDepartmentName("Công nghệ tự động");
        d4.setMajorId(3,4,5);
        departmentsDtb.add(d4);

        // Khoa Điện - Điện tử
        Department d5 = new Department();
        d5.setDepartmentId(User.ID_ADMIN_DEPARTMENT_DDT);
        d5.setDepartmentName("Điện - Điện tử");
        d5.setMajorId(9,10,11,12,13,14);
        departmentsDtb.add(d5);

        // Khoa Tiếng Hàn
        Department d6 = new Department();
        d6.setDepartmentId(User.ID_ADMIN_DEPARTMENT_KOREA);
        d6.setDepartmentName("Tiếng Hàn");
        d6.setMajorId(15);
        departmentsDtb.add(d6);

        // Khoa Du lịch
        Department d7 = new Department();
        d7.setDepartmentId(User.ID_ADMIN_DEPARTMENT_DULICH);
        d7.setDepartmentName("Du lịch");
        d7.setMajorId(16,17,18);
        departmentsDtb.add(d7);

        // Khoa Quản trị kinh doanh
        Department d8 = new Department();
        d8.setDepartmentId(User.ID_ADMIN_DEPARTMENT_QTKD);
        d8.setDepartmentName("Quản trị kinh doanh");
        d8.setMajorId(19,20,21,22,23);
        departmentsDtb.add(d8);

        // Khoa Tiếng Anh
        Department d9 = new Department();
        d9.setDepartmentId(User.ID_ADMIN_DEPARTMENT_ENGLISH);
        d9.setDepartmentName("Tiếng Anh");
        d9.setMajorId(24);
        departmentsDtb.add(d9);

        // Khoa Tài chính - Kế toán
        Department d10 = new Department();
        d10.setDepartmentId(User.ID_ADMIN_DEPARTMENT_TCKT);
        d10.setDepartmentName("Tài chính - Kế toán");
        d10.setMajorId(25,26,27);
        departmentsDtb.add(d10);

        // Khoa Tiếng Nhật
        Department d11 = new Department();
        d11.setDepartmentId(User.ID_ADMIN_DEPARTMENT_JAPANESE);
        d11.setDepartmentName("Tiếng Nhật");
        d11.setMajorId(28);
        departmentsDtb.add(d11);

        // Khoa Tiếng Trung
        Department d12 = new Department();
        d12.setDepartmentId(User.ID_ADMIN_DEPARTMENT_CHINESE);
        d12.setDepartmentName("Tiếng Trung");
        d12.setMajorId(29);
        departmentsDtb.add(d12);


        return departmentsDtb;
    }

}
