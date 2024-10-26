package com.example.socialmediatdcproject.dataModels;

public class Message {
    private int messageId;
    private int myUserId;
    private int yourUserId;
    private String content;
    private String messageType;
    private String createAt;

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public Message(int messageId, int myUserId, int yourUserId, String content, String messageType, String createAt) {
        this.messageId = messageId;
        this.myUserId = myUserId;
        this.yourUserId = yourUserId;
        this.content = content;
        this.messageType = messageType;
        this.createAt = createAt;
    }

    public Message() {
    }
}
