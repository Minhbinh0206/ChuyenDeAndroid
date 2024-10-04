package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.DepartmentMajor;

import java.util.ArrayList;

public class DepartmentMajorDatabase {

    private ArrayList<DepartmentMajor> departmentMajors = new ArrayList<>();


    public ArrayList<DepartmentMajor> dataDepartmentMajors() {
        // thêm ngành vào khoa
        DepartmentMajor dm1 = new DepartmentMajor();
        dm1.setDepartmentId(1);
        dm1.setMajorId(1);

        DepartmentMajor dm2 = new DepartmentMajor();
        dm2.setDepartmentId(2);
        dm2.setMajorId(1);

        DepartmentMajor dm3 = new DepartmentMajor();
        dm3.setDepartmentId(1);
        dm3.setMajorId(103);

        // Thêm tất cả các DepartmentMajor vào danh sách
        departmentMajors.add(dm1);
        departmentMajors.add(dm2);
        departmentMajors.add(dm3);

        return departmentMajors;
    }
}
