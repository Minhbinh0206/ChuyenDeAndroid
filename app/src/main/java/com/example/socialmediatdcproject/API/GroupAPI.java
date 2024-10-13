package com.example.socialmediatdcproject.API;

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
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // Tên phải đúng từng kí tự cả hoa và thường, nếu không sẽ xuất ra dữ liệu rỗng
        groupRef = firebaseDatabase.getReference("Group");
    }

    // Thêm nhóm mới vào Firebase
    public void addGroup(Group group) {
        int groupId = group.getGroupId();
        groupRef.child(String.valueOf(groupId)).setValue(group);
    }

    // Lấy danh sách tất cả các nhóm từ Firebase
    public void getAllGroups(ValueEventListener listener) {
        groupRef.addValueEventListener(listener);
    }

    // Lấy một nhóm dựa trên groupId
    public void getGroupById(int groupId, ValueEventListener listener) {
        groupRef.child(String.valueOf(groupId)).addListenerForSingleValueEvent(listener);
    }

    // Lấy một nhóm dựa trên tên
    public void getGroupByName(String name, ValueEventListener listener) {
        groupRef.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                        listener.onDataChange(groupSnapshot); // Gửi lại nhóm tìm thấy
                    }
                } else {
                    listener.onDataChange(dataSnapshot); // Không tìm thấy nhóm nào
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onCancelled(databaseError); // Xử lý lỗi
            }
        });
    }


    // Cập nhật thông tin một nhóm
    public void updateGroup(int groupId, Group updatedGroup) {
        groupRef.child(String.valueOf(groupId)).setValue(updatedGroup);
    }

    // Xóa nhóm dựa trên groupId
    public void deleteGroup(int groupId) {
        groupRef.child(String.valueOf(groupId)).removeValue();
    }
}
