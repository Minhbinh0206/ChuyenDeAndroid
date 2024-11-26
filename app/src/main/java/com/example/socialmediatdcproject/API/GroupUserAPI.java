package com.example.socialmediatdcproject.API;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupUserAPI {

    private final DatabaseReference groupUserRef;

    public GroupUserAPI() {
        // Khởi tạo reference đến nút "UsersInGroup" trong Firebase
        groupUserRef = FirebaseDatabase.getInstance().getReference("UsersInGroup");
    }

    public void addGroupUser(GroupUser groupUser) {
        // Lấy ID nhóm
        String groupId = String.valueOf(groupUser.getGroupId());

        // Lấy tham chiếu đến nhóm và lấy ID người dùng cuối cùng
        groupUserRef.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Lấy ID người dùng cuối cùng trong nhóm
                long nextId = snapshot.getChildrenCount();  // ID tự động tăng, sử dụng số lượng phần tử hiện tại

                // Tạo ID mới cho người dùng
                String newUserId = String.valueOf(nextId);  // ID mới sẽ là số kế tiếp

                // Thêm người dùng với ID tự tăng
                groupUserRef.child(groupId).child(newUserId).child("userId")
                        .setValue(groupUser.getUserId())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("GroupUserAPI", "GroupUser added successfully with GroupID: " +
                                        groupUser.getGroupId() + " and UserID: " + groupUser.getUserId());
                            } else {
                                Log.e("GroupUserAPI", "Failed to add GroupUser.", task.getException());
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("GroupUserAPI", "Failed to retrieve data.", error.toException());
            }
        });
    }

    // Cập nhật userId của GroupUser
    public void updateGroupUser(int groupId, String groupUserId, String newUserId) {
        groupUserRef.child(String.valueOf(groupId))
                .child(groupUserId)
                .setValue(newUserId) // Cập nhật userId
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("GroupUserAPI", "GroupUser updated successfully.");
                    } else {
                        Log.e("GroupUserAPI", "Failed to update GroupUser.", task.getException());
                    }
                });
    }

    // Xóa GroupUser theo groupId và groupUserId
    public void deleteGroupUser(int groupId, String groupUserId) {
        groupUserRef.child(String.valueOf(groupId))
                .child(groupUserId)
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("GroupUserAPI", "GroupUser deleted successfully.");
                    } else {
                        Log.e("GroupUserAPI", "Failed to delete GroupUser.", task.getException());
                    }
                });
    }

    // Lắng nghe người dùng mới vào nhóm
    public void listenForNewUsersInGroup(int groupId, final StudentCallback callback) {
        groupUserRef.child(String.valueOf(groupId)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Lấy thông tin userId từ phần tử trong snapshot
                Integer newUserId = snapshot.child("userId").getValue(Integer.class);

                // Kiểm tra xem newUserId có hợp lệ không
                if (newUserId != null) {
                    // Gọi callback để trả về newUserId
                    callback.onStudentReceived(newUserId);  // Trả về newUserId qua callback
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Xử lý khi có thay đổi trong node người dùng (ít gặp khi chỉ theo dõi người dùng mới tham gia)
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Xử lý khi người dùng rời nhóm (nếu cần)
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Xử lý khi người dùng thay đổi vị trí trong danh sách (ít gặp trong trường hợp này)
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
                System.err.println("Lỗi khi lắng nghe người dùng mới: " + error.getMessage());
            }
        });
    }

    // Callback interface
    public interface StudentCallback {
        void onStudentReceived(Integer newUserId);  // Trả về newUserId
    }

    // Lấy tất cả người dùng trong nhóm theo groupId
    public void getAllUsersInGroup(int groupId, final GroupUsersCallback callback) {
        groupUserRef.child(String.valueOf(groupId))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Integer> userIds = new ArrayList<>();
                        // Kiểm tra nếu dữ liệu không phải là null và có dữ liệu con
                        if (snapshot.exists()) {
                            for (DataSnapshot groupUserSnapshot : snapshot.getChildren()) {
                                // Mỗi groupUserSnapshot là một đối tượng chứa userId
                                Integer userId = groupUserSnapshot.child("userId").getValue(Integer.class);
                                if (userId != null) {
                                    userIds.add(userId); // Thêm userId vào danh sách
                                } else {
                                    Log.e("GroupUserAPI", "userId is missing in groupUserSnapshot");
                                }
                            }
                        }
                        callback.onUsersReceived(userIds); // Gọi callback với danh sách userIds
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("GroupUserAPI", "Failed to retrieve users in group.", error.toException());
                    }
                });
    }

    public interface GroupUsersCallback {
        void onUsersReceived(List<Integer> userIds); // Callback trả về danh sách userId
    }

}