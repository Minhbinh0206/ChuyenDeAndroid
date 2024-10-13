package com.example.socialmediatdcproject.API;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.activity.RegisterActivity;
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

    // Thêm người dùng mới vào Firebase
    public void addUser(User user) {
        String userId = String.valueOf(user.getUserId());
        userDatabase.child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Thêm thành công
                        Log.d("UserAPI", "User added successfully.");
                    } else {
                        // Xảy ra lỗi
                        Log.e("UserAPI", "Failed to add user.", task.getException());
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
