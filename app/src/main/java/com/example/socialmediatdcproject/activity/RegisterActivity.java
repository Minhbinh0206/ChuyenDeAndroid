package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.R;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, UploadProfileActivity.class);
            startActivity(intent);
        });
    }
}
