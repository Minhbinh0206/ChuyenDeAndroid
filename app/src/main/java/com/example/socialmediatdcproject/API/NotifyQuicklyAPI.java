package com.example.socialmediatdcproject.API;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.dataModels.NotifyQuickly;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class NotifyQuicklyAPI {
    private DatabaseReference notifyDatabase;

    public NotifyQuicklyAPI() {
        // Khởi tạo reference đến nút "NotifyQuicklys" trong Firebase
        notifyDatabase = FirebaseDatabase.getInstance().getReference("NotifyQuicklies");
    }

    // Thêm thông báo nhanh vào Firebase
    public void addNotification(NotifyQuickly notification) {
        String userGetId = String.valueOf(notification.getUserGetId());
        String notificationId = String.valueOf(notification.getNotifyId());

        // Lưu thông báo dưới userGetId, sau đó thông báo sẽ có ID là notificationId
        notifyDatabase.child(userGetId).child(notificationId).setValue(notification)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Thêm thông báo thành công
                        Log.d("NotifyQuicklyAPI", "Notification added successfully.");
                    } else {
                        // Xảy ra lỗi
                        Log.e("NotifyQuicklyAPI", "Failed to add notification.", task.getException());
                    }
                });
    }

    public void deleteNotification(int userGetId, int notificationId) {
        notifyDatabase.child(String.valueOf(userGetId)).child(String.valueOf(notificationId)).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("NotifyQuicklyAPI", "Notification deleted successfully.");
                    } else {
                        Log.e("NotifyQuicklyAPI", "Failed to delete notification.", task.getException());
                    }
                });
    }

    public void setNotificationListener(int userGetId, NotificationCallback callback) {
        DatabaseReference userNotificationsRef = notifyDatabase.child(String.valueOf(userGetId));

        userNotificationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<NotifyQuickly> notifications = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NotifyQuickly notification = snapshot.getValue(NotifyQuickly.class);
                    if (notification != null) {
                        notifications.add(notification);
                    }
                }
                callback.onNotificationsReceived(notifications);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    // Lấy thông báo theo ID người nhận
    public void getNotificationsByUserId(int userGetId, final NotificationCallback callback) {
        notifyDatabase.child(String.valueOf(userGetId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<NotifyQuickly> notifications = new ArrayList<>();
                for (DataSnapshot notifySnapshot : snapshot.getChildren()) {
                    NotifyQuickly notification = notifySnapshot.getValue(NotifyQuickly.class);
                    if (notification != null) {
                        notifications.add(notification);
                    }
                }
                callback.onNotificationsReceived(notifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
                Log.e("NotifyQuicklyAPI", "Failed to get notifications.", error.toException());
            }
        });
    }

    // Lấy tất cả thông báo cho một người dùng
    public void getAllNotifications(final NotificationCallback callback) {
        notifyDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<NotifyQuickly> notifications = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot notifySnapshot : userSnapshot.getChildren()) {
                        NotifyQuickly notification = notifySnapshot.getValue(NotifyQuickly.class);
                        if (notification != null) {
                            notifications.add(notification);
                        }
                    }
                }
                callback.onNotificationsReceived(notifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
                Log.e("NotifyQuicklyAPI", "Failed to get notifications.", error.toException());
            }
        });
    }

    // Định nghĩa interface NotificationCallback
    public interface NotificationCallback {
        void onNotificationsReceived(List<NotifyQuickly> notifications);
    }
}
