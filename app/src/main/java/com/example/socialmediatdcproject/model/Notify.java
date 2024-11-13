package com.example.socialmediatdcproject.model;

import java.util.ArrayList;
import java.util.List;

public class    Notify {
    private int notifyId;
    private int userSendId;
    private String notifyTitle;
    private String notifyContent;
    private List<Integer> isReaded;
    private boolean isFilter;
    private String createAt;

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

    public boolean isFilter() {
        return isFilter;
    }

    public void setFilter(boolean filter) {
        isFilter = filter;
    }

    public List<Integer> getIsReaded() {
        return isReaded;
    }

    public void setIsReaded(List<Integer> isReaded) {
        this.isReaded = isReaded;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public Notify(int notifyId, int userSendId, String notifyTitle, String notifyContent, boolean isFilter, List<Integer> isRead, String createAt) {
        this.notifyId = notifyId;
        this.userSendId = userSendId;
        this.notifyTitle = notifyTitle;
        this.notifyContent = notifyContent;
        this.isReaded = isRead;
        this.createAt = createAt;
        this.isFilter = isFilter;
    }

    public Notify() {
        this.notifyId = -1;
        this.userSendId = -1;
        this.notifyTitle = "";
        this.notifyContent = "";
    }
}
