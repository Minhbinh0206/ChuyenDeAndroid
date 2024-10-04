package com.example.socialmediatdcproject.model;

public class PostUser {

    private int userId;
    private int postId;

    private int businessId;
    private int departmentId;
    private int groupId;
    private int classId;

    // gửi toàn bộ
    public PostUser(int userId, int postId, int businessId, int departmentId, int groupId, int classId) {
        this.userId = userId;
        this.postId = postId;
        this.businessId = businessId;
        this.departmentId = departmentId;
        this.groupId = groupId;
        this.classId = classId;
    }
    public PostUser(int userId , int postId) {
        this.userId = userId;
        this.postId = postId;
        this.businessId = -1;
        this.departmentId = -1;
        this.groupId = -1;
        this.classId = -1;
    }
    public PostUser() {
        this.userId = -1;
        this.postId = -1;
        this.businessId = -1;
        this.departmentId = -1;
        this.groupId = -1;
        this.classId = -1;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }
}
