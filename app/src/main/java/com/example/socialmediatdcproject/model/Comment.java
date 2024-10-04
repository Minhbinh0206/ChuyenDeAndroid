package com.example.socialmediatdcproject.model;

public class Comment {
    private int id;
    private int postId;
    private int userId;
    private String content;


    // Constructor
    public Comment(int id, int postId, int userId, String content) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.content = content;

    }

    public Comment() {
        this.id = -1;
        this.postId = -1;
        this.userId = -1;
        this.content = "";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
