package com.example.socialmediatdcproject.activity;

import android.os.Bundle;
import android.util.Log;
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

    private EditText oldPasswordEditText, newPasswordEditText, confirmNewPasswordEditText;
    private Button changePasswordButton;
    private FirebaseAuth mAuth;
    private int userId;
    private DatabaseReference studentsRef, usersRef;
    private boolean passwordChanged = false; // Biến cờ để theo dõi trạng thái thay đổi mật khẩu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.change_password_layout);

        mAuth = FirebaseAuth.getInstance();
        studentsRef = FirebaseDatabase.getInstance().getReference("Students");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        initUi();

        ImageButton iconBack = findViewById(R.id.icon_back);
        iconBack.setOnClickListener(v -> finish());

        changePasswordButton.setOnClickListener(v -> changePassword());
    }

    private void initUi() {
        oldPasswordEditText = findViewById(R.id.editTextOldPassword);
        newPasswordEditText = findViewById(R.id.editTextNewPassword);
        confirmNewPasswordEditText = findViewById(R.id.editTextConfirmNewPassword);
        changePasswordButton = findViewById(R.id.button_summit_change);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (passwordChanged) {
            Toast.makeText(this, "Bạn đã thay đổi mật khẩu. Không thể thay đổi lại ngay lập tức.", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu đã thay đổi mật khẩu
        }
    }

    private void changePassword() {
        String oldPassword = oldPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmNewPassword = confirmNewPasswordEditText.getText().toString().trim();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập toàn bộ trường dữ liệu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không trùng khớp", Toast.LENGTH_SHORT).show();
            return;
        }
        if (oldPassword.equals(newPassword)) {
            Toast.makeText(this, "Mật khẩu mới không được trùng với mật khẩu cũ", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        Log.d("ChangePassword", "user: " + user);

        String studentId = user.getUid();
        Log.d("ChangePassword", "studentId: " + studentId);

        studentsRef.child(studentId).child("userId").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userId = task.getResult().getValue(Integer.class);
                if (userId != -1) {
                    Log.d("ChangePassword", "userId: " + userId);

                    if (user != null) {
                        user.updatePassword(newPassword).addOnCompleteListener(passwordTask -> {
                            if (passwordTask.isSuccessful()) {
                                passwordChanged = true; // Đặt cờ sau khi thay đổi mật khẩu thành công
                                Toast.makeText(ChangePasswordActivity.this, "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();

                                studentsRef.child(studentId).child("password").setValue(newPassword)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
//                                                Toast.makeText(ChangePasswordActivity.this, "Cập nhật mật khẩu vào bảng Student", Toast.LENGTH_SHORT).show();
                                            } else {
//                                                Toast.makeText(ChangePasswordActivity.this, "Không cập nhật mật khẩu vào bảng Student", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                usersRef.child(String.valueOf(userId)).child("password").setValue(newPassword)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
//                                                Toast.makeText(ChangePasswordActivity.this, "Cập nhật mật khẩu vào bảng User", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
//                                                Toast.makeText(ChangePasswordActivity.this, "Failed to cập nhật mật khẩu vào bảng User", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, "Không cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "No user is signed in", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("ChangePassword", "userId không tồn tại trong bảng Students");
                }
            } else {
                Log.d("ChangePassword", "Lỗi khi truy vấn userId từ bảng Students", task.getException());
            }
        });
    }
}
