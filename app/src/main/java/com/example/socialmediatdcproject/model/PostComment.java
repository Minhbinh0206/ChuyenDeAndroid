package com.example.socialmediatdcproject.model;

public class PostComment {
    private int commentId;
    private int postId;
    private int userCommentedId;

    public PostComment() {
        this.commentId = -1;
        this.postId = -1;
        this.userCommentedId = -1;
    }

    public PostComment(int commentId, int postId, int userCommentedId) {
        this.commentId = commentId;
        this.postId = postId;
        this.userCommentedId = userCommentedId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getUserCommentedId() {
        return userCommentedId;
    }

    public void setUserCommentedId(int userCommentedId) {
        this.userCommentedId = userCommentedId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}
