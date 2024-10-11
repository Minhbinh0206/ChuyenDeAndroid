package com.example.socialmediatdcproject.API;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class UserAPI {
    private DatabaseReference userDatabase;

    public UserAPI() {
        // Khởi tạo reference đến nút "users" trong Firebase
        userDatabase = FirebaseDatabase.getInstance().getReference("User");
    }

    public void addUser(User user) {
        getAllUsers(new UserCallback() {
            @Override
            public void onUserReceived(User user) {
                // Không cần thực hiện ở đây
            }

            @Override
            public void onUsersReceived(List<User> users) {
                // Tính toán userId mới
                int newUserId = users.size(); // Sử dụng kích thước mảng hiện tại

                // Gán userId cho người dùng
                user.setUserId(newUserId);

                // Sử dụng newUserId làm khóa trong Firebase
                userDatabase.child(String.valueOf(newUserId)).setValue(user)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Người dùng đã được thêm thành công
                            } else {
                                // Xảy ra lỗi
                            }
                        });
            }
        });
    }


    // Cập nhật thông tin người dùng
    public void updateUser(User user) {
        String userId = String.valueOf(user.getUserId());
        userDatabase.child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Cập nhật thành công
                    } else {
                        // Xảy ra lỗi
                    }
                });
    }

    // Lấy thông tin người dùng theo ID
    public void getUserById(int userId, final UserCallback callback) {
        userDatabase.child(String.valueOf(userId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                callback.onUserReceived(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
            }
        });
    }

    // Xóa người dùng theo ID
    public void deleteUser(int userId) {
        userDatabase.child(String.valueOf(userId)).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Xóa thành công
                    } else {
                        // Xảy ra lỗi
                    }
                });
    }

    // Lấy tất cả người dùng
    public void getAllUsers(final UserCallback callback) {
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> userList = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        userList.add(user);
                    }
                }
                callback.onUsersReceived(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
            }
        });
    }

    // Định nghĩa interface UserCallback
    public interface UserCallback {
        void onUserReceived(User user);
        void onUsersReceived(List<User> users);
    }
}
