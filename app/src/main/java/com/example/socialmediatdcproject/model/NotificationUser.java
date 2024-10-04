package com.example.socialmediatdcproject.model;

public class NotificationUser {

    private int notificationId;
    private int userId;
    private int classId;
    private int departmentId;
    private int businessId;
    private int groupId;

    public NotificationUser(int notificationId, int userId, int classId, int departmentId, int businessId, int groupId) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.classId = classId;
        this.departmentId = departmentId;
        this.businessId = businessId;
        this.groupId = groupId;
    }

    // Constructor
    public NotificationUser(int notificationId, int userId) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.classId = -1;
        this.departmentId = -1;
        this.businessId = -1;
        this.groupId = -1;
    }

    public NotificationUser() {
        this.notificationId = -1;
        this.userId = -1;
        this.classId = -1;
        this.departmentId = -1;
        this.businessId = -1;
        this.groupId = -1;
    }

    // Getters and Setters
    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
