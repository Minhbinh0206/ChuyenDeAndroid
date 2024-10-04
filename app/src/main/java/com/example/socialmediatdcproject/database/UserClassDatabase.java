package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.UserClass;

import java.util.ArrayList;

public class UserClassDatabase {

    private ArrayList<UserClass> userClasses = new ArrayList<>();

    // Phương thức cung cấp dữ liệu giả cho User_Class
    public ArrayList<UserClass> dataUserClasses() {
        UserClass uc1 = new UserClass();
        uc1.setUserId(1);
        uc1.setClassId(1);

        UserClass uc2 = new UserClass();
        uc2.setUserId(2);
        uc2.setClassId(1);

        // Thêm tất cả các UserClass vào danh sách
        userClasses.add(uc1);
        userClasses.add(uc2);

        return userClasses;
    }
}
