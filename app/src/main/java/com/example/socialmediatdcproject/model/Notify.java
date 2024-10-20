package com.example.socialmediatdcproject.model;

import java.util.ArrayList;
import java.util.List;

public class    Notify {
    private int notifyId;
    private int userSendId;
    private String notifyTitle;
    private String notifyContent;
    private int isRead;

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

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public Notify(int notifyId, int userSendId, String notifyTitle, String notifyContent, int isRead) {
        this.notifyId = notifyId;
        this.userSendId = userSendId;
        this.notifyTitle = notifyTitle;
        this.notifyContent = notifyContent;
        this.isRead = isRead;
    }

    public Notify() {
        this.notifyId = -1;
        this.userSendId = -1;
        this.notifyTitle = "";
        this.notifyContent = "";
        this.isRead = 0;
    }
}
