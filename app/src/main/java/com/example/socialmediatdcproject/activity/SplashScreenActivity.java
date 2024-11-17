package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
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
                AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
                AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();

                if (currentUser != null) {
                    checkUserProfile(); // Kiểm tra trạng thái đăng ký trước khi điều hướng
                    checkUserVerified(); // Kiểm tra trạng thái đăng ký đã xác thực hay chưa

                    studentAPI.getStudentByKey(currentUser.getUid(), new StudentAPI.StudentCallback() {
                        @Override
                        public void onStudentReceived(Student student) {
                            Log.d("TAG", "User: " + student.getFullName());

                            if (student.getUserId() != -1) {
                                Intent intent = new Intent(SplashScreenActivity.this, SharedActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                startActivity(intent);
                                finish();
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
                                Intent intent = new Intent(SplashScreenActivity.this, HomeAdminActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onUsersReceived(List<AdminDepartment> adminDepartment) {}

                        @Override
                        public void onError(String s) {}
                    });

                    adminBusinessAPI.getAdminBusinessByKey(currentUser.getUid(), new AdminBusinessAPI.AdminBusinessCallBack() {
                        @Override
                        public void onUserReceived(AdminBusiness adminBusiness) {
                            if (adminBusiness.getUserId() != -1) {
                                Intent intent = new Intent(SplashScreenActivity.this, HomeAdminActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onUsersReceived(List<AdminBusiness> adminBusiness) {

                        }

                        @Override
                        public void onError(String s) {

                        }
                    });

                    adminDefaultAPI.getAdminDefaultByKey(currentUser.getUid(), new AdminDefaultAPI.AdminDefaultCallBack() {
                        @Override
                        public void onUserReceived(AdminDefault adminDefault) {
                            if (!adminDefault.getAdminType().equals("Super")) {
                                if (adminDefault.getUserId() != -1) {
                                    Intent intent = new Intent(SplashScreenActivity.this, HomeAdminActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                    startActivity(intent);
                                    finish();
                                }
                                else {

                                }
                            }
                        }

                        @Override
                        public void onUsersReceived(List<AdminDefault> adminDefault) {

                        }
                    });
                } else {
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    startActivity(intent);
                    finish();
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

    private void checkUserProfile() {
        // Kiểm tra trạng thái trong SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isRegistering = sharedPreferences.getBoolean("isRegistering", false);

        if (isRegistering) {
            // Nếu đang trong quá trình đăng ký, điều hướng đến UploadProfileActivity
            Intent intent = new Intent(SplashScreenActivity.this, UploadProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);
            finish();

            // Đặt lại trạng thái để không chuyển đến UploadProfileActivity lần nữa
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("isRegistering", false);
//            editor.apply();
        }
    }
    private void checkUserVerified() {
        // Kiểm tra trạng thái trong SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isVerified = sharedPreferences.getBoolean("isEmailVerificationPending", false);

        if (isVerified) {
            // Nếu đang trong quá trình đăng ký, điều hướng đến RegisterActivity
            Intent intent = new Intent(SplashScreenActivity.this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);
            finish();

            // Đặt lại trạng thái để không chuyển đến UploadProfileActivity lần nữa
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("isEmailVerificationPending", false);
//            editor.apply();
        }
    }
}
