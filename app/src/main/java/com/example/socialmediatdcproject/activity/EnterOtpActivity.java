package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmediatdcproject.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class  EnterOtpActivity extends AppCompatActivity {

    private EditText otpEditText;
    private Button verifyOtpButton;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);

        otpEditText = findViewById(R.id.editTextOtp);
        verifyOtpButton = findViewById(R.id.buttonVerifyOtp);

        // Lấy verificationId từ Intent
        verificationId = getIntent().getStringExtra("verificationId");

        verifyOtpButton.setOnClickListener(v -> {
            String otpCode = otpEditText.getText().toString().trim();
            if (otpCode.isEmpty() || otpCode.length() < 6) {
                Toast.makeText(this, "Vui lòng nhập mã OTP hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            verifyOtp(otpCode);
        });
    }

    private void verifyOtp(String otpCode) {
        if (verificationId == null) {
            Toast.makeText(this, "Không tìm thấy mã xác minh. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otpCode);

        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // OTP xác thực thành công
                        Toast.makeText(this, "Xác thực OTP thành công!", Toast.LENGTH_SHORT).show();

                        // Quay lại màn hình đổi mật khẩu
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Xác thực OTP thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}