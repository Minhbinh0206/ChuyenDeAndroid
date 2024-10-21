package com.example.socialmediatdcproject.dataModels;

public class Friends {
    private int myUserId;
    private int yourUserId;
    private int status;

    public int getMyUserId() {
        return myUserId;
    }

    public void setMyUserId(int myUserId) {
        this.myUserId = myUserId;
    }

    public int getYourUserId() {
        return yourUserId;
    }

    public void setYourUserId(int yourUserId) {
        this.yourUserId = yourUserId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Friends(int myUserId, int yourUserId, int status) {
        this.myUserId = myUserId;
        this.yourUserId = yourUserId;
        this.status = status;
    }

    public Friends() {
        this.myUserId = -1;
        this.yourUserId = -1;
        this.status = -1;
    }
}
