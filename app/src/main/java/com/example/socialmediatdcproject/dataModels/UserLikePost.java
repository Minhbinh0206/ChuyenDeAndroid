package com.example.socialmediatdcproject.dataModels;

public class UserLikePost {
    private int userId;
    private int postId;
    private boolean isLiked;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public UserLikePost(int userId, int postId, boolean isLiked) {
        this.userId = userId;
        this.postId = postId;
        this.isLiked = isLiked;
    }

    public UserLikePost() {
        this.userId = -1;
        this.postId = -1;
        this.isLiked = false;
    }
}
