package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.Role;

import java.util.ArrayList;

public class RoleDatabase {

    private ArrayList<Role> roles = new ArrayList<>();

    // Phương thức cung cấp dữ liệu giả cho Role
    public ArrayList<Role> dataRoles() {
        // 1. Admin Role
        Role r1 = new Role();
        r1.setId(1);
        r1.setRoleName("Admin");

        // 2. User Role
        Role r2 = new Role();
        r2.setId(2);
        r2.setRoleName("User");

        // 3. Guest Role
        Role r3 = new Role();
        r3.setId(3);
        r3.setRoleName("Guest");

        // 3. Guest Role
        Role r4 = new Role();
        r4.setId(4);
        r4.setRoleName("Admin Phòng Đào Tạo");

        // 3. Admin Doàn Thanh niên Role
        Role r5 = new Role();
        r5.setId(5);
        r5.setRoleName("Admin Doàn Thanh niên");

        // 3. Lecturer Role
        Role r6 = new Role();
        r6.setId(6);
        r6.setRoleName("Lecturer");

        // Thêm tất cả các role vào danh sách
        roles.add(r1);
        roles.add(r2);
        roles.add(r3);
        roles.add(r4);
        roles.add(r5);
        roles.add(r6);


        return roles;
    }
}
