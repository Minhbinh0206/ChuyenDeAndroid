package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.Comment;
import com.example.socialmediatdcproject.model.User;

import java.util.ArrayList;
import java.util.Date;

public class CommentDatabase {

    private ArrayList<Comment> comments = new ArrayList<>();
    ArrayList<Comment> commentsOfPost = new ArrayList<>();

    public ArrayList<Comment> getCommentsByPostId(int id){
        for (Comment c: dataComments()) {
            if (c.getPostId() == id){
                commentsOfPost.add(c);
            }
        }
        return commentsOfPost;
    }
    // Phương thức cung cấp dữ liệu giả cho Comment
    public ArrayList<Comment> dataComments() {
        // 1. Comment đầu tiên
        Comment c1 = new Comment();
        c1.setId(0);
        c1.setPostId(3);
        c1.setUserId(0);
        c1.setContent("Hay quá bạn ơi!");
        c1.setCommentLike(10);
        c1.setCommentCreateAt("30s ago");

        // 2. Comment thứ hai
        Comment c2 = new Comment();
        c2.setId(1);
        c2.setPostId(7);
        c2.setUserId(User.ID_ADMIN_PHONGDAOTAO);
        c2.setContent("Đăng ký vào học ngay!");
        c2.setCommentLike(16);
        c2.setCommentCreateAt("32s ago");

        // 3. Comment thứ ba
        Comment c3 = new Comment();
        c3.setId(2);
        c3.setPostId(7);
        c3.setUserId(27);
        c3.setContent("Hoan hô , Bis Bis");
        c3.setCommentLike(2);
        c3.setCommentCreateAt("1p ago");


        // Thêm tất cả các comment vào danh sách
        comments.add(c1);
        comments.add(c2);
        comments.add(c3);

        return comments;
    }
}
