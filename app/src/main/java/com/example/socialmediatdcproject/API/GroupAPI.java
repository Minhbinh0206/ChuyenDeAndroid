package com.example.socialmediatdcproject.API;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.model.Group;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupAPI {

    private DatabaseReference groupRef;

    // Constructor để kết nối với Firebase "groups" node
    public GroupAPI() {
        groupRef = FirebaseDatabase.getInstance().getReference("Groups");
    }

    // Thêm nhóm mới vào Firebase
    public void addGroup(Group group) {
        int groupId = group.getGroupId();
        groupRef.child(String.valueOf(groupId)).setValue(group)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("GroupAPI", "Group added successfully.");
                    } else {
                        Log.e("GroupAPI", "Failed to add group.", task.getException());
                    }
                });
    }

    // Lấy danh sách tất cả các nhóm từ Firebase
    public void getAllGroups(final GroupCallback callback) {
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Group> groupList = new ArrayList<>();
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    Group group = groupSnapshot.getValue(Group.class);
                    if (group != null) {
                        groupList.add(group);
                    }
                }
                callback.onGroupsReceived(groupList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("GroupAPI", "Error getting all groups.", error.toException());
            }
        });
    }

    // Lấy một nhóm dựa trên groupId
    public void getGroupById(int groupId, final GroupCallback callback) {
        groupRef.child(String.valueOf(groupId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);
                callback.onGroupReceived(group);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("GroupAPI", "Error getting group by ID.", error.toException());
            }
        });
    }

    // Lấy một nhóm dựa trên tên
    public void getGroupByName(String name, final GroupCallback callback) {
        groupRef.orderByChild("groupName").startAt(name).endAt(name + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Group> groups = new ArrayList<>();
                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    Group group = groupSnapshot.getValue(Group.class);
                    if (group != null) {
                        groups.add(group);
                    }
                }
                if (!groups.isEmpty()) {
                    callback.onGroupsReceived(groups); // Gọi callback với danh sách nhóm tìm thấy
                } else {
                    callback.onGroupReceived(null); // Không tìm thấy nhóm
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi
                callback.onGroupReceived(null);
            }
        });
    }

    //


    // Cập nhật thông tin một nhóm
    public void updateGroup(int groupId, Group updatedGroup) {
        groupRef.child(String.valueOf(groupId)).setValue(updatedGroup)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("GroupAPI", "Group updated successfully.");
                    } else {
                        Log.e("GroupAPI", "Failed to update group.", task.getException());
                    }
                });
    }

    // Xóa nhóm dựa trên groupId
    public void deleteGroup(int groupId) {
        groupRef.child(String.valueOf(groupId)).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("GroupAPI", "Group deleted successfully.");
                    } else {
                        Log.e("GroupAPI", "Failed to delete group.", task.getException());
                    }
                });
    }

    // Định nghĩa interface GroupCallback
    public interface GroupCallback {
        void onGroupReceived(Group group);
        void onGroupsReceived(List<Group> groups);
    }
}
