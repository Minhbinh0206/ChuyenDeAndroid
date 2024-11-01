package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        mAuth = FirebaseAuth.getInstance();

        initUi();

        if (mAuth != null) {
            currentUser = mAuth.getCurrentUser();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Kiểm tra xem người dùng đã đăng nhập hay chưa
                FirebaseUser currentUser = mAuth.getCurrentUser();
                StudentAPI studentAPI = new StudentAPI();
                AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();

                if (currentUser != null) {
                    studentAPI.getStudentByKey(currentUser.getUid(), new StudentAPI.StudentCallback() {
                        @Override
                        public void onStudentReceived(Student student) {
                            Log.d("TAG", "User: " + student.getFullName());

                            if (student.getUserId() != -1) {
                                // Nếu đã đăng nhập, chuyển đến SharedActivity
                                Intent intent = new Intent(SplashScreenActivity.this, SharedActivity.class);
                                startActivity(intent);
                                finish(); // Đóng LoginActivity để không quay lại
                            } else {
                                Toast.makeText(SplashScreenActivity.this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onStudentsReceived(List<Student> students) {}
                    });

                    adminDepartmentAPI.getAdminDepartmentByKey(currentUser.getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
                        @Override
                        public void onUserReceived(AdminDepartment adminDepartment) {
                            if (adminDepartment.getUserId() != -1) {
                                // Nếu đã đăng nhập, chuyển đến SharedActivity
                                Intent intent = new Intent(SplashScreenActivity.this, HomeAdminActivity.class);
                                startActivity(intent);
                                finish(); // Đóng LoginActivity để không quay lại
                            }
                        }

                        @Override
                        public void onUsersReceived(List<AdminDepartment> adminDepartment) {

                        }

                        @Override
                        public void onError(String s) {

                        }
                    });
                }
                else {
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Đóng LoginActivity để không quay lại
                }
            }
        }, 3000);
    }

    private void initUi() {
        // Thiết kế giao diện
        avatar = findViewById(R.id.image_logo);
        Glide.with(this)
                .load(R.drawable.image_logo_login)
                .circleCrop()
                .into(avatar);
    }
}
