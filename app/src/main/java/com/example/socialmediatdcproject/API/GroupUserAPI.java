package com.example.socialmediatdcproject.API;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupUserAPI {

    private DatabaseReference groupUserRef;

    // Constructor để kết nối với Firebase "GroupUser" node
    public GroupUserAPI() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        groupUserRef = firebaseDatabase.getReference("GroupUser");
    }

    // Thêm GroupUser mới vào Firebase
    public void addGroupUser(GroupUser groupUser) {
        String groupUserId = groupUserRef.push().getKey(); // Tạo ID mới
        if (groupUserId != null) {
            groupUserRef.child(groupUserId).setValue(groupUser);
        }
    }

    // Lấy danh sách tất cả các GroupUser từ Firebase
    public void getAllGroupUsers(ValueEventListener listener) {
        groupUserRef.addValueEventListener(listener);
    }

    // Lấy GroupUser dựa trên groupId
    public void getGroupUserByIdGroup(int groupId, ValueEventListener listener) {
        groupUserRef.orderByChild("groupId").equalTo(groupId).addListenerForSingleValueEvent(listener);
    }

    // Lấy GroupUser dựa trên userId
    public void getGroupUserByIdUser(int userId, ValueEventListener listener) {
        groupUserRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(listener);
    }

    // Cập nhật thông tin một GroupUser
    public void updateGroupUser(String groupUserId, GroupUser updatedGroupUser) {
        groupUserRef.child(groupUserId).setValue(updatedGroupUser);
    }

    // Xóa GroupUser dựa trên groupUserId
    public void deleteGroupUser(String groupUserId) {
        groupUserRef.child(groupUserId).removeValue();
    }
}
