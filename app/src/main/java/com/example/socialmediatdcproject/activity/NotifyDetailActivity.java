package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmediatdcproject.API.NotifyAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.model.Notify;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class NotifyDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notify_detail_layout);

        int notifyId;

        Intent intent = getIntent();
        notifyId = intent.getIntExtra("notifyId", -1);

        ImageButton iconBack = findViewById(R.id.icon_back_notify);

        iconBack.setOnClickListener(v -> {
            Intent intent1 = new Intent(NotifyDetailActivity.this, SharedActivity.class);
            intent1.putExtra("keyFragment", 999);
            startActivity(intent1);
            finish();
        });

        TextView title = findViewById(R.id.notify_title);
        TextView nameUser = findViewById(R.id.notify_user_send);
        TextView content = findViewById(R.id.notify_content);

        NotifyAPI notifyAPI = new NotifyAPI();
        UserAPI userAPI = new UserAPI();
        notifyAPI.getNotificationById(notifyId, new NotifyAPI.NotificationCallback() {
            @Override
            public void onNotificationReceived(Notify notify) {
                title.setText(notify.getNotifyTitle());
                content.setText(notify.getNotifyContent());
                userAPI.getUserById(notify.getUserSendId(), new UserAPI.UserCallback() {
                    @Override
                    public void onUserReceived(User user) {
                        nameUser.setText(user.getFullName());
                    }

                    @Override
                    public void onUsersReceived(List<User> users) {

                    }
                });
            }

            @Override
            public void onNotificationsReceived(List<Notify> notifications) {

            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }
}
