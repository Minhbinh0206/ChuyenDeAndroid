package com.example.socialmediatdcproject.model;

import java.util.ArrayList;
import java.util.List;

public class Notify {
    private int notifyId;
    private int userSendId;
    private String notifyTitle;
    private String notifyContent;

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

    public Notify(int notifyId, int userSendId, String notifyTitle, String notifyContent, List<User> userGetId) {
        this.notifyId = notifyId;
        this.userSendId = userSendId;
        this.notifyTitle = notifyTitle;
        this.notifyContent = notifyContent;
    }

    public Notify() {
        this.notifyId = -1;
        this.userSendId = -1;
        this.notifyTitle = "";
        this.notifyContent = "";
    }
}
