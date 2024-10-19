package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_layout);

        mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Nếu đã đăng nhập, chuyển thẳng đến SharedActivity
            Intent intent = new Intent(LoginActivity.this, SharedActivity.class);
            startActivity(intent);
            finish(); // Đóng LoginActivity để không quay lại
            return; // Ngừng thực hiện phần còn lại của onCreate
        }

        // Thiết kế giao diện
        ImageView imageView = findViewById(R.id.image_logo);
        Glide.with(this)
                .load(R.drawable.image_logo_login)
                .circleCrop()
                .into(imageView);

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
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Truy vấn để tìm userId và roleId từ bảng Users
                                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
                                usersRef.orderByChild("email").equalTo(email)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                                        // Lấy userId và roleId từ dữ liệu của người dùng
                                                        int userId = userSnapshot.child("userId").getValue(Integer.class);
                                                        int roleId = userSnapshot.child("roleId").getValue(Integer.class);

                                                        // Truy vấn để lấy tên role từ bảng Roles
                                                        DatabaseReference rolesRef = FirebaseDatabase.getInstance().getReference("Roles");
                                                        rolesRef.child(String.valueOf(roleId)).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot roleSnapshot) {
                                                                String roleName = roleSnapshot.child("roleName").getValue(String.class);
                                                                // Mã kiểm tra vai trò admin
                                                                Set<Integer> adminRoleIds = new HashSet<>(Arrays.asList(2, 3, 4, 5));
                                                                boolean isAdmin = adminRoleIds.contains(roleId);

                                                                for (int id : adminRoleIds) {
                                                                    if (roleId == id) {
                                                                        isAdmin = true ;
                                                                        break;
                                                                    }
                                                                }

                                                                if (isAdmin) {
                                                                    Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                                                    startActivity(intent);
                                                                } else {
                                                                    Intent intent = new Intent(LoginActivity.this, SharedActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                                finish();
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                Toast.makeText(LoginActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(LoginActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
