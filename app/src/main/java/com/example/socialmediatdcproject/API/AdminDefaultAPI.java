package com.example.socialmediatdcproject.API;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.activity.RegisterActivity;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
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

public class AdminDefaultAPI {
    private DatabaseReference userDatabase;

    public AdminDefaultAPI() {
        // Khởi tạo reference đến nút "users" trong Firebase
        userDatabase = FirebaseDatabase.getInstance().getReference("Admins").child("AdminDefaults");
    }

    // Lấy thông tin người dùng theo ID
    public void getAdminDefaultByType(String type, final AdminDefaultCallBack callback) {
        userDatabase.orderByChild("adminType").equalTo(type)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                                AdminDefault adminDefault = studentSnapshot.getValue(AdminDefault.class);
                                if (adminDefault != null) {
                                    callback.onUserReceived(adminDefault);
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
    public void getAdminDefaultById(int id, final AdminDefaultCallBack callback) {
        userDatabase.orderByChild("userId").equalTo(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                                AdminDefault adminDefault = studentSnapshot.getValue(AdminDefault.class);
                                if (adminDefault != null) {
                                    callback.onUserReceived(adminDefault);
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

    public void getAdminDefaultByKey(String key, AdminDefaultAPI.AdminDefaultCallBack callback) {
        userDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    AdminDefault adminBusiness = dataSnapshot.getValue(AdminDefault.class);
                    callback.onUserReceived(adminBusiness);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface AdminDefaultCallBack {
        void onUserReceived(AdminDefault adminDefault);
        void onUsersReceived(List<AdminDefault> adminDefault);
    }
}
