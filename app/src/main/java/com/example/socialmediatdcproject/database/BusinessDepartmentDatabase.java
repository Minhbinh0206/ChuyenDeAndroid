package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.BusinessDepartment;

import java.util.ArrayList;

public class BusinessDepartmentDatabase {

    private ArrayList<BusinessDepartment> businessDepartments = new ArrayList<>();


    public ArrayList<BusinessDepartment> dataBusinessDepartments() {
        BusinessDepartment bd1 = new BusinessDepartment();
        bd1.setBusinessId(1);
        bd1.setDepartmentId(1);

        BusinessDepartment bd2 = new BusinessDepartment();
        bd2.setBusinessId(2);
        bd2.setDepartmentId(102);

        BusinessDepartment bd3 = new BusinessDepartment();
        bd3.setBusinessId(1);
        bd3.setDepartmentId(3);

        // Thêm tất cả các BusinessDepartment vào danh sách
        businessDepartments.add(bd1);
        businessDepartments.add(bd2);
        businessDepartments.add(bd3);

        return businessDepartments;
    }
}
