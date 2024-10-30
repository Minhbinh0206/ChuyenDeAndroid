package com.example.socialmediatdcproject.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.socialmediatdcproject.R;

public class SettingActivity extends AppCompatActivity {
    private ImageButton btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_screen_layout);

        btnBack = findViewById(R.id.icon_back);

        btnBack.setOnClickListener(v -> finish());
    }


}
