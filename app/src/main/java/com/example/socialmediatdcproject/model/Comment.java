package com.example.socialmediatdcproject.model;

public class Comment {
    private int id;
    private int postId;
    private int userId;
    private int groupId;
    private String content;
    private int commentLike;
    private String commentCreateAt;


    // Constructor
    public Comment(int id, int postId, int userId, String content, int commentLike, String commentCreateAt) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.commentLike = commentLike;
        this.commentCreateAt = commentCreateAt;
    }

    public Comment() {
        this.id = -1;
        this.postId = -1;
        this.userId = -1;
        this.content = "";
        this.commentLike = 0;
        this.commentCreateAt = "";
    }



    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCommentLike() {
        return commentLike;
    }

    public void setCommentLike(int commentLike) {
        this.commentLike = commentLike;
    }

    public String getCommentCreateAt() {
        return commentCreateAt;
    }

    public void setCommentCreateAt(String commentCreateAt) {
        this.commentCreateAt = commentCreateAt;
    }
}
