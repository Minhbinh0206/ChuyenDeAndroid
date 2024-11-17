package com.example.socialmediatdcproject.model;

public class Assist {
    private int userId;
    private boolean isAssist;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isAssist() {
        return isAssist;
    }

    public void setAssist(boolean assist) {
        isAssist = assist;
    }

    public Assist(int userId, boolean isAssist) {
        this.userId = userId;
        this.isAssist = isAssist;
    }

    public Assist() {
    }
}
