package com.example.socialmediatdcproject.API;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.activity.RegisterActivity;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class AdminDepartmentAPI {
    private DatabaseReference userDatabase;

    public AdminDepartmentAPI() {
        // Khởi tạo reference đến nút "users" trong Firebase
        userDatabase = FirebaseDatabase.getInstance().getReference("Admins").child("AdminDepartments");
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
    public void addAdminDepartment(AdminDepartment adminDepartment) {
        String userId = String.valueOf(adminDepartment.getUserId());
        userDatabase.child(userId).setValue(adminDepartment)
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
    public void getAdminDepartmentById(int id, final AdminDepartmentCallBack callback) {
        userDatabase.orderByChild("userId").equalTo(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                        AdminDepartment adminDepartment = studentSnapshot.getValue(AdminDepartment.class);
                        if (adminDepartment != null) {
                            callback.onUserReceived(adminDepartment);
                            return; // Nếu tìm thấy một sinh viên, trả về ngay
                        }
                    }
                }
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
    public void getAllAdminDepartments(final AdminDepartmentCallBack callback) {
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<AdminDepartment> adminDepartmentList = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    AdminDepartment adminDepartment = userSnapshot.getValue(AdminDepartment.class);
                    if (adminDepartment != null) {
                        adminDepartmentList.add(adminDepartment);
                    }
                }
                callback.onUsersReceived(adminDepartmentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
            }
        });
    }

    public void getAdminDepartmentByKey(String key, AdminDepartmentAPI.AdminDepartmentCallBack callback) {
        userDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    AdminDepartment adminDepartment = dataSnapshot.getValue(AdminDepartment.class);
                    callback.onUserReceived(adminDepartment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError("Error retrieving student: " + databaseError.getMessage());
            }
        });
    }

    public interface AdminDepartmentCallBack {
        void onUserReceived(AdminDepartment adminDepartment);
        void onUsersReceived(List<AdminDepartment> adminDepartment);
        void onError(String s);
    }
}
