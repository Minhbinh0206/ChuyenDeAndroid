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
    public void deleteGroupUser(int groupId, int targetUserId) {
        // Truy cập đến nhóm cần xóa
        groupUserRef.child(String.valueOf(groupId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean userFound = false;

                    // Duyệt qua các người dùng trong nhóm
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        Long userId = userSnapshot.child("userId").getValue(Long.class);

                        // Kiểm tra nếu userId trùng với targetUserId
                        if (userId != null && userId == targetUserId) {
                            Log.d("deleteGroupUser", "Deleting user " + userId + " from group " + groupId);

                            // Xóa người dùng
                            userSnapshot.getRef().removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("deleteGroupUser", "User " + targetUserId + " deleted successfully.");
                                } else {
                                    Log.e("deleteGroupUser", "Failed to delete user " + targetUserId, task.getException());
                                }
                            });
                            userFound = true;
                            break; // Dừng vòng lặp sau khi tìm thấy và xóa
                        }
                    }

                    if (!userFound) {
                        Log.e("deleteGroupUser", "User " + targetUserId + " not found in group " + groupId);
                    }
                } else {
                    Log.e("deleteGroupUser", "Group " + groupId + " does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("deleteGroupUser", "Failed to retrieve group data.", error.toException());
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
    // Lấy tất cả GroupUser
    public void getAllGroupUsers(final GroupUsersCallback callback) {
        groupUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Integer> groupUserList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Integer groupUser = dataSnapshot.getValue(Integer.class);
                    if (groupUser != null) {
                        groupUserList.add(groupUser);
                    }
                }
                callback.onUsersReceived(groupUserList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
                Log.e("GroupUserAPI", "Failed to retrieve GroupUsers.", error.toException());
            }
        });
    }

    public void getAllGroupsByUserId(int userId, final GroupsCallback callback) {
        groupUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Integer> groupIds = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                        String groupId = groupSnapshot.getKey(); // Lấy groupId
                        if (groupSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : groupSnapshot.getChildren()) {
                                Integer currentUserId = userSnapshot.child("userId").getValue(Integer.class);
                                if (currentUserId != null && currentUserId == userId) {
                                    groupIds.add(Integer.parseInt(groupId)); // Thêm groupId vào danh sách
                                    break; // Dừng duyệt khi tìm thấy userId trong nhóm
                                }
                            }
                        }
                    }
                }
                callback.onGroupsReceived(groupIds); // Trả về danh sách groupIds qua callback
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("GroupUserAPI", "Failed to retrieve groups by userId.", error.toException());
            }
        });
    }
    // Lấy người dùng theo userId
    public void getUserByUserId(int userId, final UserCallback callback) {
        groupUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Duyệt qua tất cả các nhóm
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    // Duyệt qua tất cả người dùng trong mỗi nhóm
                    for (DataSnapshot userSnapshot : groupSnapshot.getChildren()) {
                        Integer currentUserId = userSnapshot.child("userId").getValue(Integer.class);

                        // Kiểm tra nếu userId của người dùng là giống với userId cần tìm
                        if (currentUserId != null && currentUserId == userId) {
                            // Nếu tìm thấy người dùng, trả về thông qua callback
                            GroupUser groupUser = userSnapshot.getValue(GroupUser.class);
                            if (groupUser != null) {
                                callback.onUserReceived(groupUser); // Trả về đối tượng người dùng
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("GroupUserAPI", "Failed to retrieve user by userId.", error.toException());
            }
        });
    }

    // Callback interface để trả về thông tin người dùng
    public interface UserCallback {
        void onUserReceived(GroupUser groupUser); // Callback trả về người dùng
    }



    // Callback interface để trả về danh sách groupId
    public interface GroupsCallback {
        void onGroupsReceived(List<Integer> groupIds);
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