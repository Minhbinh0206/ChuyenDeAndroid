package com.example.socialmediatdcproject.dataModels;

import java.util.List;

public class FilterNotify {
    private int notifyId;
    private List<Integer> listUserGet;

    public List<Integer> getListUserGet() {
        return listUserGet;
    }

    public void setListUserGet(List<Integer> listUserGet) {
        this.listUserGet = listUserGet;
    }

    public int getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(int notifyId) {
        this.notifyId = notifyId;
    }

    public FilterNotify(int notifyId, List<Integer> listUserGet) {
        this.notifyId = notifyId;
        this.listUserGet = listUserGet;
    }

    public FilterNotify() {
    }
}
