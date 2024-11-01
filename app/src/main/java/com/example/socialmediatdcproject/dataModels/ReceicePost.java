package com.example.socialmediatdcproject.dataModels;

import com.example.socialmediatdcproject.model.User;

import java.util.List;

public class ReceicePost {
    private int postId;
    private List<Object> listUserGet;

    public List<?> getListUserGet() {
        return listUserGet;
    }

    public void setListUserGet(List<Object> listUserGet) {
        this.listUserGet = listUserGet;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }


    public ReceicePost(int postId, List<Object> listUserGet) {
        this.postId = postId;
        this.listUserGet = listUserGet;
    }

    public ReceicePost() {
    }
}
