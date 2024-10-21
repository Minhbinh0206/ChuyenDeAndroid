package com.example.socialmediatdcproject.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmediatdcproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText oldPasswordEditText, newPasswordEditText, confirmNewPasswordEditText, captchaEditText;
    private Button changePasswordButton, sendCaptchaButton;
    private FirebaseAuth mAuth;
    private DatabaseReference studentsRef; // Tham chiếu đến bảng Students

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.change_password_layout);

        // Khởi tạo Firebase Auth và Database Reference
        mAuth = FirebaseAuth.getInstance();
        studentsRef = FirebaseDatabase.getInstance().getReference("Students");

        initUi();

        // Xử lý sự kiện nhấn nút quay lại
        ImageButton iconBack = findViewById(R.id.icon_back);
        iconBack.setOnClickListener(v -> finish());

        // Xử lý sự kiện nhấn nút thay đổi mật khẩu
        changePasswordButton.setOnClickListener(v -> changePassword());

        // Xử lý sự kiện nhấn nút gửi Captcha
        sendCaptchaButton.setOnClickListener(v -> sendCaptcha());
    }

    private void initUi() {
        // Ánh xạ các View
        oldPasswordEditText = findViewById(R.id.editTextOldPassword);
        newPasswordEditText = findViewById(R.id.editTextNewPassword);
        confirmNewPasswordEditText = findViewById(R.id.editTextConfirmNewPassword);
        captchaEditText = findViewById(R.id.send_capCha_to_user);
        changePasswordButton = findViewById(R.id.button_summit_change);
        sendCaptchaButton = findViewById(R.id.send_captcha_button); // Nút gửi Captcha
    }

    private void changePassword() {
        String oldPassword = oldPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmNewPassword = confirmNewPasswordEditText.getText().toString().trim();
        String captcha = captchaEditText.getText().toString().trim();

        // Kiểm tra tính hợp lệ của các trường nhập liệu
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty() || captcha.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "New password and confirmation do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy thông tin người dùng hiện tại
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Thay đổi mật khẩu
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();

                    // Cập nhật mật khẩu mới vào bảng Students
                    String userId = user.getUid();
                    studentsRef.child(userId).child("password").setValue(newPassword)
                            .addOnCompleteListener(updateTask -> {
                                if (updateTask.isSuccessful()) {
                                    Toast.makeText(ChangePasswordActivity.this, "Password updated in Students successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ChangePasswordActivity.this, "Failed to update password in Students", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user is signed in", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendCaptcha() {
        // Logic để gửi captcha (tạm thời)
        Toast.makeText(this, "Captcha sent!", Toast.LENGTH_SHORT).show(); // Chỉ là một thông báo mẫu
    }
}
