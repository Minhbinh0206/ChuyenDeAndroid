package com.example.socialmediatdcproject.model;

import java.util.List;

public class Event {
    private int eventId;
    private int adminEventId;
    private String titleEvent;
    private String contentEvent;
    private String imageEvent;
    private String description;
    private List<Assist> userAssist;
    private List<RollCall> userJoin;
    private int status;
    private String createAt;
    private String beginAt;
    private String finishAt;

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getAdminEventId() {
        return adminEventId;
    }

    public void setAdminEventId(int adminEventId) {
        this.adminEventId = adminEventId;
    }

    public String getTitleEvent() {
        return titleEvent;
    }

    public List<RollCall> getUserJoin() {
        return userJoin;
    }

    public void setUserJoin(List<RollCall> userJoin) {
        this.userJoin = userJoin;
    }

    public void setTitleEvent(String titleEvent) {
        this.titleEvent = titleEvent;
    }

    public String getContentEvent() {
        return contentEvent;
    }

    public void setContentEvent(String contentEvent) {
        this.contentEvent = contentEvent;
    }

    public String getImageEvent() {
        return imageEvent;
    }

    public void setImageEvent(String imageEvent) {
        this.imageEvent = imageEvent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Assist> getUserAssist() {
        return userAssist;
    }

    public void setUserAssist(List<Assist> userAssist) {
        this.userAssist = userAssist;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getBeginAt() {
        return beginAt;
    }

    public void setBeginAt(String beginAt) {
        this.beginAt = beginAt;
    }

    public String getFinishAt() {
        return finishAt;
    }

    public void setFinishAt(String finishAt) {
        this.finishAt = finishAt;
    }

    public Event(int eventId, int adminEventId, String titleEvent, String contentEvent, String imageEvent, String description, List<Assist> userAssist, List<RollCall> userJoin, int status, String createAt, String beginAt, String finishAt) {
        this.eventId = eventId;
        this.adminEventId = adminEventId;
        this.titleEvent = titleEvent;
        this.contentEvent = contentEvent;
        this.imageEvent = imageEvent;
        this.description = description;
        this.userAssist = userAssist;
        this.userJoin = userJoin;
        this.status = status;
        this.createAt = createAt;
        this.beginAt = beginAt;
        this.finishAt = finishAt;
    }

    public Event() {
    }
}
