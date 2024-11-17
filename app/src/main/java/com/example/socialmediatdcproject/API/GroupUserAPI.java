package com.example.socialmediatdcproject.API;

import android.util.Log;
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

    public GroupUserAPI() {
        // Khởi tạo reference đến nút "UsersInGroup" trong Firebase
        groupUserRef = FirebaseDatabase.getInstance().getReference("UsersInGroup");
    }

    public void addGroupUser(GroupUser groupUser) {
        // Sử dụng push() để tạo ra một ID duy nhất cho mỗi GroupUser
        String uniqueId = groupUserRef.push().getKey();

        if (uniqueId != null) {
            groupUserRef.child(uniqueId).setValue(groupUser)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("GroupUserAPI", "GroupUser added successfully with ID: " + uniqueId);
                        } else {
                            Log.e("GroupUserAPI", "Failed to add GroupUser.", task.getException());
                        }
                    });
        } else {
            Log.e("GroupUserAPI", "Failed to generate a unique ID for GroupUser.");
        }
    }

    // Cập nhật thông tin GroupUser
    public void updateGroupUser(String groupUserId, GroupUser updatedGroupUser) {
        groupUserRef.child(groupUserId).setValue(updatedGroupUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("GroupUserAPI", "GroupUser updated successfully.");
                    } else {
                        Log.e("GroupUserAPI", "Failed to update GroupUser.", task.getException());
                    }
                });
    }

    // Lấy GroupUser dựa trên groupId
    public void getGroupUserByIdGroup(int groupId, final GroupUserCallback callback) {
        groupUserRef.orderByChild("groupId").equalTo(groupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<GroupUser> groupUserList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            GroupUser groupUser = dataSnapshot.getValue(GroupUser.class);
                            if (groupUser != null) {
                                groupUserList.add(groupUser);
                            }
                        }
                        callback.onGroupUsersReceived(groupUserList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý lỗi
                        Log.e("GroupUserAPI", "Failed to retrieve GroupUser by groupId.", error.toException());
                    }
                });
    }

    // Lấy GroupUser dựa trên userId
    public void getGroupUserByIdUser(int userId, final GroupUserCallback callback) {
        groupUserRef.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<GroupUser> groupUserList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            GroupUser groupUser = dataSnapshot.getValue(GroupUser.class);
                            if (groupUser != null) {
                                groupUserList.add(groupUser);
                            }
                        }
                        callback.onGroupUsersReceived(groupUserList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý lỗi
                        Log.e("GroupUserAPI", "Failed to retrieve GroupUser by userId.", error.toException());
                    }
                });
    }

    // Xóa GroupUser dựa trên groupUserId
    public void deleteGroupUser(String groupUserId) {
        groupUserRef.child(groupUserId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("GroupUserAPI", "GroupUser deleted successfully.");
                    } else {
                        Log.e("GroupUserAPI", "Failed to delete GroupUser.", task.getException());
                    }
                });
    }

    // Lấy tất cả GroupUser
    public void getAllGroupUsers(final GroupUserCallback callback) {
        groupUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<GroupUser> groupUserList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    GroupUser groupUser = dataSnapshot.getValue(GroupUser.class);
                    if (groupUser != null) {
                        groupUserList.add(groupUser);
                    }
                }
                callback.onGroupUsersReceived(groupUserList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
                Log.e("GroupUserAPI", "Failed to retrieve GroupUsers.", error.toException());
            }
        });
    }

    // Định nghĩa interface GroupUserCallback
    public interface GroupUserCallback {
        void onGroupUsersReceived(List<GroupUser> groupUsers);
    }
}