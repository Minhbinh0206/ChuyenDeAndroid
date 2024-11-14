package com.example.socialmediatdcproject.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmediatdcproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ChangePasswordPrefs";
    private static final String PASSWORD_CHANGED_KEY = "passwordChanged";

    private EditText oldPasswordEditText, newPasswordEditText, confirmNewPasswordEditText;
    private Button changePasswordButton;
    private FirebaseAuth mAuth;
    private int userId;
    private DatabaseReference studentsRef, usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_layout);

        mAuth = FirebaseAuth.getInstance();
        studentsRef = FirebaseDatabase.getInstance().getReference("Students");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        initUi();
        setupListeners();

        // Kiểm tra trạng thái passwordChanged từ SharedPreferences
        if (isPasswordChanged()) {
            Toast.makeText(this, "Bạn đã thay đổi mật khẩu. Không thể thay đổi lại ngay lập tức.", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu đã thay đổi mật khẩu
        }
    }

    private void initUi() {
        oldPasswordEditText = findViewById(R.id.editTextOldPassword);
        newPasswordEditText = findViewById(R.id.editTextNewPassword);
        confirmNewPasswordEditText = findViewById(R.id.editTextConfirmNewPassword);
        changePasswordButton = findViewById(R.id.button_summit_change);
    }

    private void setupListeners() {
        ImageButton iconBack = findViewById(R.id.icon_back);
        iconBack.setOnClickListener(v -> finish());

        changePasswordButton.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPassword = oldPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmNewPassword = confirmNewPasswordEditText.getText().toString().trim();

        if (!validateInputs(oldPassword, newPassword, confirmNewPassword)) return;

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            updatePasswordInAuth(user, newPassword);
        } else {
            Toast.makeText(this, "No user is signed in", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs(String oldPassword, String newPassword, String confirmNewPassword) {
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập toàn bộ trường dữ liệu", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không trùng khớp", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (oldPassword.equals(newPassword)) {
            Toast.makeText(this, "Mật khẩu mới không được trùng với mật khẩu cũ", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Optional: Check for password strength
        if (newPassword.length() < 8) {
            Toast.makeText(this, "Mật khẩu mới phải có ít nhất 8 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void updatePasswordInAuth(FirebaseUser user, String newPassword) {
        user.updatePassword(newPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ChangePasswordActivity.this, "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                savePasswordChanged(true); // Lưu trạng thái passwordChanged vào SharedPreferences
                updatePasswordInDatabase(user.getUid(), newPassword);
            } else {
                Toast.makeText(ChangePasswordActivity.this, "Không cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePasswordInDatabase(String studentId, String newPassword) {
        studentsRef.child(studentId).child("userId").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userId = task.getResult().getValue(Integer.class);
                if (userId != -1) {
                    studentsRef.child(studentId).child("password").setValue(newPassword)
                            .addOnCompleteListener(updateTask -> {
                                if (!updateTask.isSuccessful()) {
                                    Log.e("ChangePassword", "Không cập nhật mật khẩu vào bảng Student");
                                }
                            });

                    usersRef.child(String.valueOf(userId)).child("password").setValue(newPassword)
                            .addOnCompleteListener(updateTask -> {
                                if (!updateTask.isSuccessful()) {
                                    Log.e("ChangePassword", "Failed to cập nhật mật khẩu vào bảng User");
                                } else {
                                    finish();
                                }
                            });
                } else {
                    Log.d("ChangePassword", "userId không tồn tại trong bảng Students");
                }
            } else {
                Log.e("ChangePassword", "Lỗi khi truy vấn userId từ bảng Students", task.getException());
            }
        });
    }

    private void savePasswordChanged(boolean isChanged) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PASSWORD_CHANGED_KEY, isChanged);
        editor.apply();
    }

    private boolean isPasswordChanged() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PASSWORD_CHANGED_KEY, false);
    }
}
