package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmediatdcproject.API.NotifyAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.model.Notify;
import com.example.socialmediatdcproject.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotifyDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notify_detail_layout);

        int notifyId;
        int notifyUserSend;

        Intent intent = getIntent();
        notifyId = intent.getIntExtra("notifyId", -1);
        notifyUserSend = intent.getIntExtra("userSend", -1);

        ImageButton iconBack = findViewById(R.id.icon_back_notify);
        Button button = findViewById(R.id.button_return_notify);

        iconBack.setOnClickListener(v -> {
            finish();
        });

        button.setOnClickListener(v -> {
            finish();
        });

        TextView title = findViewById(R.id.title_notice);
        TextView nameUser = findViewById(R.id.name_user_send_notice);
        TextView content = findViewById(R.id.content_notice);
        TextView createAt = findViewById(R.id.create_at_notice);

        // Định dạng hiển thị ngày tháng
        SimpleDateFormat displayFormat = new SimpleDateFormat("'Ngày' dd 'Tháng' MM 'Năm' yyyy");

        NotifyAPI notifyAPI = new NotifyAPI();
        UserAPI userAPI = new UserAPI();
        notifyAPI.getNotificationById(notifyId, notifyUserSend, new NotifyAPI.NotificationCallback() {
            @Override
            public void onNotificationReceived(Notify notify) {
                title.setText(notify.getNotifyTitle());
                content.setText(notify.getNotifyContent());

                // Lấy thông tin người gửi
                userAPI.getUserById(notify.getUserSendId(), new UserAPI.UserCallback() {
                    @Override
                    public void onUserReceived(User user) {
                        nameUser.setText(user.getFullName());
                    }

                    @Override
                    public void onUsersReceived(List<User> users) {
                        // Không sử dụng
                    }
                });

                // Chuyển đổi chuỗi ISO 8601 thành đối tượng Date
                String createAtString = notify.getCreateAt(); // Giả sử tạoAt là chuỗi ISO 8601: "2024-11-13T10:51:59Z"
                SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

                try {
                    Date createAtDate = iso8601Format.parse(createAtString);
                    String formattedDate = displayFormat.format(createAtDate);  // Định dạng ngày tháng theo mẫu mong muốn
                    createAt.setText(formattedDate);  // Hiển thị ngày tháng trên UI
                } catch (ParseException e) {
                    e.printStackTrace();
                    // Xử lý lỗi nếu không thể phân tích chuỗi ngày tháng
                }
            }

            @Override
            public void onNotificationsReceived(List<Notify> notifications) {
                // Không sử dụng
            }

            @Override
            public void onError(String errorMessage) {
                // Xử lý lỗi
            }
        });
    }
}
