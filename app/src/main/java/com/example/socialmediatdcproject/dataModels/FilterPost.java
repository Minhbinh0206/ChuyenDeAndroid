package com.example.socialmediatdcproject.dataModels;

import java.util.List;

public class FilterPost {
    private int postId;
    private List<Integer> listUserGet;

    public List<Integer> getListUserGet() {
        return listUserGet;
    }

    public void setListUserGet(List<Integer> listUserGet) {
        this.listUserGet = listUserGet;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }


    public FilterPost(int postId, List<Integer> listUserGet) {
        this.postId = postId;
        this.listUserGet = listUserGet;
    }

    public FilterPost() {
    }
}
