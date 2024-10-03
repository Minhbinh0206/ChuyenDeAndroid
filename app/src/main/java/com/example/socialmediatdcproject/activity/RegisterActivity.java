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
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.database.UserDatabase;
import com.example.socialmediatdcproject.model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword, editTextPasswordConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UserDatabase userDatabase = new UserDatabase();

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register_layout);

        // Thiết kế giao diện
        // - Bo tròn logo
        ImageView imageView = findViewById(R.id.image_logo);
        Glide.with(this)
                .load(R.drawable.image_logo_login) // Hoặc URL nếu bạn dùng ảnh từ mạng
                .circleCrop()
                .into(imageView);

        ImageView iconEmail = findViewById(R.id.icon_profile);
        Glide.with(this)
                .load(R.drawable.icon_profile) // Hoặc URL nếu bạn dùng ảnh từ mạng
                .circleCrop()
                .into(iconEmail);

        // Xử lý chức năng
        // - Chuyển qua trang đăng nhập
        TextView textSignUp = findViewById(R.id.textLogIn);
        textSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // - Chuyển qua trang tạo hồ sơ thông tin
//        Button buttonRegister = findViewById(R.id.buttonRegister);
//        buttonRegister.setOnClickListener(v -> {
//            Intent intent = new Intent(RegisterActivity.this, UploadProfileActivity.class);
//            startActivity(intent);
//        });

        // Xử lý chức năng đăng ký
        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(v -> {
            // Lấy dữ liệu
            EditText fullNameEditText = findViewById(R.id.fullName_register);
            EditText emailEditText = findViewById(R.id.email_register);
            EditText passwordEditText = findViewById(R.id.password_register);
            EditText passwordConfirmEditText = findViewById(R.id.password_confirm_register);

            String fullName = fullNameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String passwordConfirm = passwordConfirmEditText.getText().toString();

            // Kiểm tra thông tin
            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(passwordConfirm)) {
                Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            userDatabase.dataUser();
            // Thêm người dùng vào cơ sở dữ liệu
            User newUser = new User(fullName, email, password);
            Log.d("Nguoi dung moi" , "fullname" + fullName);
            Log.d("Nguoi dung moi" , "email" + email);
            Log.d("Nguoi dung moi" , "password" + password);
            userDatabase.addUser(newUser);

            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

            // Chuyển qua trang tạo hồ sơ thông tin
            Intent intent = new Intent(RegisterActivity.this, UploadProfileActivity.class);
            startActivity(intent);
        });
    }
}
