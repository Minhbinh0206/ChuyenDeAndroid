package com.example.socialmediatdcproject.model;

import java.util.ArrayList;
import java.util.List;

public class    Notify {
    private int notifyId;
    private int userSendId;
    private String notifyTitle;
    private String notifyContent;
    private List<User> userGetId;

    public int getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(int notifyId) {
        this.notifyId = notifyId;
    }

    public int getUserSendId() {
        return userSendId;
    }

    public void setUserSendId(int userSendId) {
        this.userSendId = userSendId;
    }

    public String getNotifyTitle() {
        return notifyTitle;
    }

    public void setNotifyTitle(String notifyTitle) {
        this.notifyTitle = notifyTitle;
    }

    public String getNotifyContent() {
        return notifyContent;
    }

    public void setNotifyContent(String notifyContent) {
        this.notifyContent = notifyContent;
    }

    public List<User> getUserGetId() {
        return userGetId;
    }

    public void setUserGetId(List<User> userGetIds) {
        if (this.userGetId == null) {
            this.userGetId = new ArrayList<>();
        }
        this.userGetId.addAll(userGetIds);  // Thêm tất cả các thành viên vào danh sách
    }

    public Notify(int notifyId, int userSendId, String notifyTitle, String notifyContent, List<User> userGetId) {
        this.notifyId = notifyId;
        this.userSendId = userSendId;
        this.notifyTitle = notifyTitle;
        this.notifyContent = notifyContent;
        this.userGetId = userGetId;
    }

    public Notify() {
        this.notifyId = -1;
        this.userSendId = -1;
        this.notifyTitle = "";
        this.notifyContent = "";
        this.userGetId = new ArrayList<>();
    }
}
