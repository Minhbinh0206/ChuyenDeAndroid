package com.example.socialmediatdcproject.API;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.model.Notify;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class NotifyAPI {
    private DatabaseReference notifyDatabase;

    public NotifyAPI() {
        // Khởi tạo reference đến nút "Notifies" trong Firebase
        notifyDatabase = FirebaseDatabase.getInstance().getReference("Notifies");
    }

    // Thêm thông báo mới vào Firebase
    public void addNotification(Notify notify) {
        notify.setNotifyId(notify.getNotifyId()); // Cập nhật ID cho Notify
        notifyDatabase.child(notify.getNotifyId()+"").setValue(notify).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("NotifyAPI", "Notification added successfully.");
            } else {
                Log.e("NotifyAPI", "Failed to add notification.", task.getException());
            }
        });
    }

    // Cập nhật thông báo
    public void updateNotification(Notify notify) {
        String notifyId = String.valueOf(notify.getNotifyId());
        notifyDatabase.child(notifyId).setValue(notify).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("NotifyAPI", "Notification updated successfully.");
            } else {
                Log.e("NotifyAPI", "Failed to update notification.", task.getException());
            }
        });
    }

    // Lấy tất cả thông báo
    public void getAllNotifications(final NotificationCallback callback) {
        notifyDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Notify> notifyList = new ArrayList<>();
                for (DataSnapshot notifySnapshot : snapshot.getChildren()) {
                    Notify notify = notifySnapshot.getValue(Notify.class);
                    if (notify != null) {
                        notifyList.add(notify);
                    }
                }
                callback.onNotificationsReceived(notifyList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NotifyAPI", "Error fetching notifications: " + error.getMessage());
            }
        });
    }

    // Lấy thông báo theo ID
    public void getNotificationById(int notifyId, final NotificationCallback callback) {
        notifyDatabase.orderByChild("notifyId").equalTo(notifyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Notify notify = snapshot.getChildren().iterator().hasNext() ? snapshot.getChildren().iterator().next().getValue(Notify.class) : null;
                callback.onNotificationReceived(notify);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NotifyAPI", "Error fetching notification: " + error.getMessage());
            }
        });
    }

    // Lấy thông báo theo trạng thái đọc
    public void getNotificationsByReadStatus(int isRead, final NotificationCallback callback) {
        notifyDatabase.orderByChild("isRead").equalTo(isRead).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Notify> notifyList = new ArrayList<>();
                for (DataSnapshot notifySnapshot : snapshot.getChildren()) {
                    Notify notify = notifySnapshot.getValue(Notify.class);
                    if (notify != null) {
                        notifyList.add(notify);
                    }
                }
                callback.onNotificationsReceived(notifyList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NotifyAPI", "Error fetching notifications by read status: " + error.getMessage());
            }
        });
    }

    // Định nghĩa interface NotificationCallback
    public interface NotificationCallback {
        void onNotificationReceived(Notify notify);
        void onNotificationsReceived(List<Notify> notifications);
        void onError(String errorMessage);
    }
}
