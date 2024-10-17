package com.example.socialmediatdcproject.dataModels;

public class UserLikeComment {
    private int userId;
    private int commentId;
    private boolean isLiked;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public UserLikeComment(int userId, int postId, boolean isLiked) {
        this.userId = userId;
        this.commentId = postId;
        this.isLiked = isLiked;
    }

    public UserLikeComment() {
        this.userId = -1;
        this.commentId = -1;
        this.isLiked = false;
    }
}
