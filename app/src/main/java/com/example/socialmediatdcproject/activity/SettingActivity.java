package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.socialmediatdcproject.R;

import org.w3c.dom.Text;

public class SettingActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private TextView btnChangePassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_screen_layout);

        btnBack = findViewById(R.id.icon_back);

        btnBack.setOnClickListener(v -> finish());

        btnChangePassword = findViewById(R.id.text_view_change_password);

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, ChangePasswordActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);
        });




    }


}
