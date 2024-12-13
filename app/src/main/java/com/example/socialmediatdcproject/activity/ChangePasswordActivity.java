package com.example.socialmediatdcproject.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmediatdcproject.API.EmailSender;
import com.example.socialmediatdcproject.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ChangePasswordPrefs";
    private static final String PASSWORD_CHANGED_KEY = "passwordChanged";
    private static final String KEY_PASSWORD_RESET_CONFIRMED = "passwordResetConfirmed";
    private boolean isOtpVerified = false; // Biến để theo dõi trạng thái xác thực OTP

    private EditText oldPasswordEditText, newPasswordEditText, confirmNewPasswordEditText;
    private Button changePasswordButton , verifyOtpButton;
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

    // Khởi tạo các thành phần giao diện
    private void initUi() {
        oldPasswordEditText = findViewById(R.id.editTextOldPassword);
        newPasswordEditText = findViewById(R.id.editTextNewPassword);
        confirmNewPasswordEditText = findViewById(R.id.editTextConfirmNewPassword);
        changePasswordButton = findViewById(R.id.button_summit_change);
        verifyOtpButton = findViewById(R.id.buttonVerifyOtp);
    }
    // Thiết lập các sự kiện cho các thành phần giao diện
    private void setupListeners() {
        ImageButton iconBack = findViewById(R.id.icon_back);
        iconBack.setOnClickListener(v -> finish());

        changePasswordButton.setOnClickListener(v -> {
            EditText otpEditText = findViewById(R.id.editTextOtp);
            String inputOtp = otpEditText.getText().toString().trim();

            if (inputOtp.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            verifyOtp(inputOtp);

            if (isOtpVerified) {
                verificationChangePassword(); // Chỉ cho phép tiếp tục nếu OTP đã được xác thực
            } else {
                Toast.makeText(this, "Bạn cần xác thực OTP trước", Toast.LENGTH_SHORT).show();
            }
        });
        verifyOtpButton.setOnClickListener(v -> {
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            String email = firebaseUser.getEmail();
            if (email != null) {
                // Gửi email reset mật khẩu
                sendOtpToEmail(email);
            }
        });
    }
    // Yêu cầu xác thực từ người dùng
    private void verificationChangePassword() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String oldPassword = oldPasswordEditText.getText().toString().trim();

            if (oldPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mật khẩu cũ", Toast.LENGTH_SHORT).show();
                return;
            }
            // Lấy thông tin người dùng hiện tại
            String email = firebaseUser.getEmail();


            // Tiếp tục nếu đã xác nhận
            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
            firebaseUser.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            changePassword();
                        } else {
                            Toast.makeText(this, "Mật khẩu cũ không đúng. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập trước!", Toast.LENGTH_SHORT).show();
        }
    }

        // Thay đổi mật khẩu
    private void changePassword() {
        String oldPassword = oldPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmNewPassword = confirmNewPasswordEditText.getText().toString().trim();

        // Kiểm tra giá trị nhập vào
        if (!validateInputs(oldPassword, newPassword, confirmNewPassword)) return;

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            updatePasswordInAuth(user, newPassword);
        } else {
            Toast.makeText(this, "Không tìm thấy người dùng hiện tại", Toast.LENGTH_SHORT).show();
        }
    }

    // Kiểm tra giá trị nhập vào có hợp lệ không
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

    // Cập nhật mật khẩu vào Firebase Authentication
    private void updatePasswordInAuth(@NonNull FirebaseUser user, String newPassword) {
        user.updatePassword(newPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ChangePasswordActivity.this, "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                savePasswordChanged(true); // Lưu trạng thái passwordChanged vào SharedPreferences
                updatePasswordInDatabase(user.getUid(), newPassword);
            } else {
                Toast.makeText(this, "Cập nhật mật khẩu thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cập nhật mật khẩu vào bảng Student và User
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
                                    Log.e("ChangePassword", "Không cập nhật mật khẩu vào bảng User");
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

    // Lưu trạng thái passwordChanged vào SharedPreferences
    private void savePasswordChanged(boolean isChanged) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PASSWORD_CHANGED_KEY, isChanged);
        editor.apply();
    }

    // Kiểm tra trạng thái passwordChanged từ SharedPreferences
    private boolean isPasswordChanged() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PASSWORD_CHANGED_KEY, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetPasswordChanged();
    }

    private void resetPasswordChanged() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PASSWORD_CHANGED_KEY, false); // Reset trạng thái
        editor.apply();
    }

    private void sendOtpToEmail(String email) {
        // Tạo mã OTP 6 chữ số
        int otp = (int) (Math.random() * 900000) + 100000;

        // Lưu OTP vào SharedPreferences hoặc Firebase Database
        saveOtpToPreferences(otp);

        // Cấu hình email
        String subject = "Mã OTP thay đổi mật khẩu";
        String message = "Mã OTP của bạn là: " + otp + ". Mã này sẽ hết hạn sau 5 phút.";

        // Gửi email
        EmailSender.sendEmail(email, subject, message, new EmailSender.EmailCallback() {
            @Override
            public void onSuccess() {
                Log.d("ChangePassword", "Gửi OTP thành công");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ChangePassword", "Gửi OTP thất bại", e);
                Toast.makeText(ChangePasswordActivity.this, "Không thể gửi OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveOtpToPreferences(int otp) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("otp", otp);
        editor.apply();
    }

    private int getSavedOtp() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt("otp", -1); // -1 là giá trị mặc định nếu không tìm thấy OTP
    }

    private void verifyOtp(String inputOtp) {
        int savedOtp = getSavedOtp();
        if (savedOtp == -1) {
            Toast.makeText(this, "OTP không hợp lệ. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (String.valueOf(savedOtp).equals(inputOtp)) {
            // OTP chính xác
            Toast.makeText(this, "Xác minh OTP thành công", Toast.LENGTH_SHORT).show();
            isOtpVerified = true; // Đánh dấu OTP đã được xác thực
            changePassword(); // Tiến hành thay đổi mật khẩu
        } else {
            // OTP sai
            isOtpVerified = false;
            Toast.makeText(this, "OTP không chính xác", Toast.LENGTH_SHORT).show();
        }
    }




}
