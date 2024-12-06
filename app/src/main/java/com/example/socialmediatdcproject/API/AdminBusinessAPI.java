package com.example.socialmediatdcproject.API;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.activity.RegisterActivity;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.model.AdminBusiness;
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

public class AdminBusinessAPI {
    private DatabaseReference userDatabase;

    public AdminBusinessAPI() {
        // Khởi tạo reference đến nút "users" trong Firebase
        userDatabase = FirebaseDatabase.getInstance().getReference("Admins").child("AdminBusinesses");
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
    public void addAdminBusiness(String uid, AdminBusiness adminBusiness) {
        userDatabase.child(uid).setValue(adminBusiness)
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
    public void getAdminBusinessById(int id, final AdminBusinessCallBack callback) {
        userDatabase.orderByChild("userId").equalTo(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                                AdminBusiness adminBusiness = studentSnapshot.getValue(AdminBusiness.class);
                                if (adminBusiness != null) {
                                    callback.onUserReceived(adminBusiness);
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

    // Lấy thông tin người dùng theo ID
    public void getAdminBusinessByBusinessId(int id, final AdminBusinessCallBack callback) {
        userDatabase.orderByChild("businessId").equalTo(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                                AdminBusiness adminBusiness = studentSnapshot.getValue(AdminBusiness.class);
                                if (adminBusiness != null) {
                                    callback.onUserReceived(adminBusiness);
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
    public void getAllAdminBusinesses(final AdminBusinessCallBack callback) {
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<AdminBusiness> adminBusinessList = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    AdminBusiness adminBusiness = userSnapshot.getValue(AdminBusiness.class);
                    if (adminBusiness != null) {
                        adminBusinessList.add(adminBusiness);
                    }
                }
                callback.onUsersReceived(adminBusinessList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
            }
        });
    }

    public void getAdminBusinessByKey(String key, AdminBusinessAPI.AdminBusinessCallBack callback) {
        userDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    AdminBusiness adminBusiness = dataSnapshot.getValue(AdminBusiness.class);
                    callback.onUserReceived(adminBusiness);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError("Error retrieving student: " + databaseError.getMessage());
            }
        });
    }

    public interface AdminBusinessCallBack {
        void onUserReceived(AdminBusiness adminBusiness);
        void onUsersReceived(List<AdminBusiness> adminBusiness);
        void onError(String s);
    }
}
