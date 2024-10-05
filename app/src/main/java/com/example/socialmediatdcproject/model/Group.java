package com.example.socialmediatdcproject.model;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private int groupId;
    private String groupName;
    private int adminUserId;
    private List<User> groupMember;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(int adminUserId) {
        this.adminUserId = adminUserId;
    }

    public List<User> getGroupMember() {
        return groupMember;
    }

    public void setGroupMember(List<User> groupMembers) {
        if (this.groupMember == null) {
            this.groupMember = new ArrayList<>();
        }
        this.groupMember.addAll(groupMembers);  // Thêm tất cả các thành viên vào danh sách
    }



    public Group(int groupId, String groupName, int adminUserId, List<User> groupMember) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.adminUserId = adminUserId;
        this.groupMember = groupMember;
    }

    public Group() {
        this.groupId = -1;
        this.groupName = "";
        this.adminUserId = -1;
        this.groupMember = new ArrayList<>();
    }
}
