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
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword, editTextPasswordConfirm;
    private FirebaseAuth auth; // Thêm FirebaseAuth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register_layout);

        // Khởi tạo FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Thiết kế giao diện
        ImageView imageView = findViewById(R.id.image_logo);
        Glide.with(this)
                .load(R.drawable.image_logo_login)
                .circleCrop()
                .into(imageView);

        // Xử lý chức năng
        TextView textLogIn = findViewById(R.id.textLogIn);
        textLogIn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Xử lý chức năng đăng ký
        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(v -> {
            // Lấy dữ liệu
            EditText fullNameEditText = findViewById(R.id.fullName_register);
            EditText emailEditText = findViewById(R.id.email_register);
            EditText passwordEditText = findViewById(R.id.password_register);
            EditText passwordConfirmEditText = findViewById(R.id.password_confirm_register);

            String fullName = fullNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String passwordConfirm = passwordConfirmEditText.getText().toString().trim();

            // Kiểm tra thông tin đầu vào
            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(passwordConfirm)) {
                Toast.makeText(RegisterActivity.this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Đăng ký người dùng với Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            if (firebaseUser != null) {
                                UserAPI userAPI = new UserAPI();
                                userAPI.getAllUsers(new UserAPI.UserCallback() {
                                    @Override
                                    public void onUserReceived(User user) {

                                    }

                                    @Override
                                    public void onUsersReceived(List<User> users) {
                                        int count = 0;
                                        for (User u : users) {
                                            count++;
                                        }
                                        int userId = count;
                                        User userDTB = new User();
                                        userDTB.setUserId(userId); // Gán userId từ UID
                                        userDTB.setEmail(email);
                                        userDTB.setPassword(password);
                                        userDTB.setFullName(fullName);

                                        userAPI.addUser(userDTB); // Thêm người dùng vào cơ sở dữ liệu

                                        // Chuyển đến hoạt động tiếp theo
                                        Intent intent = new Intent(RegisterActivity.this, UploadProfileActivity.class);
                                        intent.putExtra("userId", userId);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Đăng ký không thành công: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
