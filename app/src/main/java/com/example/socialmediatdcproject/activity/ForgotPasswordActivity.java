package com.example.socialmediatdcproject.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmediatdcproject.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private String emailToSent;
    private Button sendToEmail;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.forgot_password_layout);

        mAuth = FirebaseAuth.getInstance();

        ImageButton iconBack = findViewById(R.id.icon_back);
        iconBack.setOnClickListener(v -> {
            finish();
        });


        sendToEmail = findViewById(R.id.forgot_password_sendToMail);

        sendToEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emailToSent = ((EditText) findViewById(R.id.forgot_password_email)).getText().toString();

                if (isValidEmail(emailToSent)) {
                    mAuth.sendPasswordResetEmail(emailToSent)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotPasswordActivity.this, "A password reset link has been sent to your email.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
