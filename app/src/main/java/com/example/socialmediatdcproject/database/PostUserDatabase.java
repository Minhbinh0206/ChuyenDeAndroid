package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.PostUser;

import java.util.ArrayList;

public class PostUserDatabase {

    private ArrayList<PostUser> postUsers = new ArrayList<>();

    // Phương thức cung cấp dữ liệu giả cho PostUser
    public ArrayList<PostUser> dataPostUsers() {
        PostUser pu1 = new PostUser();
        pu1.setUserId(1);
        pu1.setPostId(101);

        PostUser pu2 = new PostUser();
        pu2.setUserId(2);
        pu2.setPostId(102);

        PostUser pu3 = new PostUser();
        pu3.setUserId(1);
        pu3.setPostId(103);

        // Thêm tất cả các PostUser vào danh sách
        postUsers.add(pu1);
        postUsers.add(pu2);
        postUsers.add(pu3);

        return postUsers;
    }
}
