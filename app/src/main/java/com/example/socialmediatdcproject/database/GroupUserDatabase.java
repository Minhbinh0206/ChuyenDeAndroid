package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.GroupUser;

import java.util.ArrayList;

public class GroupUserDatabase {

    private ArrayList<GroupUser> groupUsers = new ArrayList<>();

    // Phương thức cung cấp dữ liệu giả cho GroupUser
    public ArrayList<GroupUser> dataGroupUsers() {
        GroupUser gu1 = new GroupUser();
        gu1.setUserId(1);
        gu1.setGroupId(201);

        GroupUser gu2 = new GroupUser();
        gu2.setUserId(2);
        gu2.setGroupId(202);

        GroupUser gu3 = new GroupUser();
        gu3.setUserId(1);
        gu3.setGroupId(203);

        // Thêm tất cả các GroupUser vào danh sách
        groupUsers.add(gu1);
        groupUsers.add(gu2);
        groupUsers.add(gu3);

        return groupUsers;
    }
}
