package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.Comment;

import java.util.ArrayList;
import java.util.Date;

public class CommentDatabase {

    private ArrayList<Comment> comments = new ArrayList<>();

    // Phương thức cung cấp dữ liệu giả cho Comment
    public ArrayList<Comment> dataComments() {
        // 1. Comment đầu tiên
        Comment c1 = new Comment();
        c1.setId(0);
        c1.setPostId(1);
        c1.setUserId(0);
        c1.setContent("Hay quá bạn ơi!");

        // 2. Comment thứ hai
        Comment c2 = new Comment();
        c2.setId(1);
        c2.setPostId(1);
        c2.setUserId(1);
        c2.setContent("Đăng ký vào học ngay!");

        // 3. Comment thứ ba
        Comment c3 = new Comment();
        c3.setId(2);
        c3.setPostId(1);
        c3.setUserId(3);
        c3.setContent("Hoan hô , Bis Bis");


        // Thêm tất cả các comment vào danh sách
        comments.add(c1);
        comments.add(c2);
        comments.add(c3);

        return comments;
    }
}
