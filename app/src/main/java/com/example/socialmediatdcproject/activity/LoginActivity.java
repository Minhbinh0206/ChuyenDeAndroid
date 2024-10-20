package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    boolean isLoggin = false;
    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_layout);

        mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        FirebaseUser currentUser = mAuth.getCurrentUser();
        StudentAPI studentAPI = new StudentAPI();

        if (currentUser != null) {
            studentAPI.getStudentByKey(currentUser.getUid(), new StudentAPI.StudentCallback() {
                @Override
                public void onStudentReceived(Student student) {
                    isLoggin = true;
                    Log.d("TAG", "User: " + student.getFullName());

                    // Nếu đã đăng nhập, chuyển thẳng đến SharedActivity
                    Intent intent = new Intent(LoginActivity.this, SharedActivity.class);
                    startActivity(intent);
                    finish(); // Đóng LoginActivity để không quay lại
                }

                @Override
                public void onStudentsReceived(List<Student> students) {

                }

                @Override
                public void onError(String errorMessage) {

                }

                @Override
                public void onStudentDeleted(int studentId) {

                }
            });
        } else {
            // Người dùng chưa đăng nhập, chuyển qua màn hình UploadProfileActivity
            Intent intent = new Intent(LoginActivity.this, UploadProfileActivity.class);
            startActivity(intent);
            finish(); // Đóng LoginActivity để không quay lại
        }

        // Thiết kế giao diện
        avatar = findViewById(R.id.image_logo);
        Glide.with(this)
                .load(R.drawable.image_logo_login)
                .circleCrop()
                .into(avatar);

        // Chuyển qua trang đăng ký
        TextView textSignUp = findViewById(R.id.textSignUp);
        textSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Chuyển qua trang quên mật khẩu
        TextView textForgotPassword = findViewById(R.id.textForgotPassword);
        textForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Xử lý đăng nhập
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(view -> {
            EditText editTextEmail = findViewById(R.id.email_login);
            EditText editTextPassword = findViewById(R.id.password_login);

            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            // Kiểm tra nếu email hoặc mật khẩu rỗng
            if (email.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Đăng nhập với Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            // Đăng nhập thành công, chuyển đến SharedActivity
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, SharedActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            // Hiển thị thông báo lỗi đăng nhập
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
