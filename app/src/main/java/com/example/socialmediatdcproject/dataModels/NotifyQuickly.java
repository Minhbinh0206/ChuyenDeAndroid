package com.example.socialmediatdcproject.dataModels;

public class NotifyQuickly {
    private int notifyId;
    private int userSendId;
    private int userGetId;
    private String content;

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

    public int getUserGetId() {
        return userGetId;
    }

    public void setUserGetId(int userGetId) {
        this.userGetId = userGetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NotifyQuickly(int notifyId, int userSendId, int userGetId, String content) {
        this.notifyId = notifyId;
        this.userSendId = userSendId;
        this.userGetId = userGetId;
        this.content = content;
    }

    public NotifyQuickly() {
    }
}
