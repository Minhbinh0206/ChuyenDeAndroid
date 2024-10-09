package com.example.socialmediatdcproject.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post implements Serializable {
    private int postId;
    private int userId;
    private int postLike;
    private String postImage;
    private String content;
    private int status;
    private int isPublic;
    private String createdAt;
    private int groupId;


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

    public int getPostLike() {
        return postLike;
    }

    public void setPostLike(int postLike) {
        this.postLike = postLike;
    }


    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Post(int postId, int userId, int postLike, int groupId, String postImage, String content, int status, int isPublic, String createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.postLike = postLike;
        this.groupId = groupId;
        this.postImage = postImage;
        this.content = content;
        this.status = status;
        this.isPublic = isPublic;
        this.createdAt = createdAt;
    }

    public Post() {
        this.postId = -1;
        this.userId = -1;
        this.postLike = 0;
        this.groupId = -1;
        this.postImage = "";
        this.content = "";
        this.status = -1;
        this.isPublic = -1;
        this.createdAt = "";
    }
}
