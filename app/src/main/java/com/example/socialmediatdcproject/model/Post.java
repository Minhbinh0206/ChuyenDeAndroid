package com.example.socialmediatdcproject.model;

import java.io.Serializable;

public class Post implements Serializable {
    private int postId;
    private int userId;
    private String title;
    private String postImage;
    private String content;
    private int status;
    private int isPublic;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
    }

    public Post(int postId, int userId, String title, String content, String postImage, int status, int isPublic) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.postImage = postImage;
        this.content = content;
        this.status = status;
        this.isPublic = isPublic;
    }

    public Post() {
        this.postId = -1;
        this.userId = -1;
        this.title = "";
        this.content = "";
        this.postImage = "";
        this.status = -1;
        this.isPublic = -1;
    }
}
