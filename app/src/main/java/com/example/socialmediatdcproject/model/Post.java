package com.example.socialmediatdcproject.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post implements Serializable {
    private int postId;
    private int userId;
    private int postLike;
    private List<Comment> postComment;
    private String title;
    private String postImage;
    private String content;
    private int status;
    private int isPublic;
    private String createdAt;

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

    public List<Comment> getPostComment() {
        return postComment;
    }

    public void setPostComment(List<Comment> postComment) {
        this.postComment = postComment;
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

    public Post(int postId, int userId, int postLike, List<Comment> postComment, String title, String postImage, String content, int status, int isPublic, String createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.postLike = postLike;
        this.postComment = postComment;
        this.title = title;
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
        this.postComment = new ArrayList<>();
        this.title = "";
        this.postImage = "";
        this.content = "";
        this.status = -1;
        this.isPublic = -1;
        this.createdAt = "";
    }
}
