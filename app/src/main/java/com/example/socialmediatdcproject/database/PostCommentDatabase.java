package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.PostComment;

import java.util.ArrayList;

public class PostCommentDatabase {

    private ArrayList<PostComment> postComments = new ArrayList<>();

    // Phương thức cung cấp dữ liệu giả cho PostComment
    public ArrayList<PostComment> dataPostComments() {
        PostComment pc1 = new PostComment();
        pc1.setPostId(1);
        pc1.setCommentId(1);
        pc1.setUserCommentedId(1);

        PostComment pc2 = new PostComment();
        pc2.setPostId(1);
        pc2.setCommentId(2);
        pc2.setUserCommentedId(2);

//        PostComment pc3 = new PostComment();
//        pc3.setPostId(1);
//        pc3.setCommentId(3);
//        pc3.setUserCommentedId(3);

        // Thêm tất cả các PostComment vào danh sách
        postComments.add(pc1);
        postComments.add(pc2);
//        postComments.add(pc3);

        return postComments;
    }
}
