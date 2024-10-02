package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.Post;

import java.util.ArrayList;

public class PostDatabase {
    protected ArrayList<Post> postsDtb = new ArrayList<>();

    public ArrayList<Post> dataPost() {
        // Post 1
        Post p1 = new Post();
        p1.setPostId(0);
        p1.setUserId(3);
        p1.setTitle("Chia sẻ kinh nghiệm học tập");
        p1.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        p1.setIsPublic(1);
        p1.setPostImage(null);
        p1.setStatus(1);
        postsDtb.add(p1);

        // Post 2
        Post p2 = new Post();
        p2.setPostId(1);
        p2.setUserId(5);
        p2.setTitle("Tìm người học nhóm môn lập trình");
        p2.setContent("Cần tìm nhóm học tập môn lập trình Java. Ai quan tâm thì liên hệ.");
        p2.setIsPublic(1);
        p2.setStatus(1);
        p2.setPostImage("Nuhll");
        postsDtb.add(p2);

        // Post 3
        Post p3 = new Post();
        p3.setPostId(2);
        p3.setUserId(10);
        p3.setTitle("Thông báo về hội thảo khoa học");
        p3.setContent("Hội thảo khoa học sẽ được tổ chức vào ngày 15/10. Mọi người đăng ký tham gia nhé.");
        p3.setIsPublic(1);
        p3.setStatus(1);
        p3.setPostImage("null");
        postsDtb.add(p3);

        // Post 4
        Post p4 = new Post();
        p4.setPostId(3);
        p4.setUserId(4);
        p4.setTitle("Cần hỗ trợ giải bài tập toán cao cấp");
        p4.setContent("Mình gặp khó khăn với bài tập toán cao cấp. Bạn nào giỏi có thể giúp mình không?");
        p4.setIsPublic(0);
        p4.setStatus(1);
        p4.setPostImage(null);
        postsDtb.add(p4);

        // Post 5
        Post p5 = new Post();
        p5.setPostId(4);
        p5.setUserId(10);
        p5.setTitle("Giới thiệu tài liệu lập trình C++");
        p5.setContent("Mình có tài liệu hay về C++. Ai cần thì mình chia sẻ.");
        p5.setIsPublic(1);
        p5.setStatus(1);
        p5.setPostImage(null);
        postsDtb.add(p5);

        return postsDtb;
    }
}
