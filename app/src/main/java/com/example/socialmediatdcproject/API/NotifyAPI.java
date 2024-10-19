package com.example.socialmediatdcproject.API;

import com.example.socialmediatdcproject.model.Notify;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotifyAPI {

    private DatabaseReference databaseReference;

    public NotifyAPI() {
        // Khởi tạo reference đến Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Notifies");
    }

    public void getNotifications(final NotificationCallback callback) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Notify> notifyList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notify notify = snapshot.getValue(Notify.class);
                    if (notify != null) {
                        notifyList.add(notify);
                    }
                }
                callback.onNotificationsReceived(notifyList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    // Thêm thông báo mới
    public void createNotification(Notify notify, final NotificationCallback callback) {
        String notifyId = databaseReference.push().getKey(); // Tạo ID mới
        notify.setNotifyId(Integer.parseInt(notifyId)); // Cập nhật ID cho Notify
        databaseReference.child(notifyId).setValue(notify).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onNotificationsReceived(List.of(notify)); // Trả về thông báo đã tạo
            } else {
                callback.onError("Failed to create notification.");
            }
        });
    }

    // Cập nhật thông báo
    public void updateNotification(Notify notify, final NotificationCallback callback) {
        String notifyId = String.valueOf(notify.getNotifyId());
        databaseReference.child(notifyId).setValue(notify).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onNotificationsReceived(List.of(notify)); // Trả về thông báo đã cập nhật
            } else {
                callback.onError("Failed to update notification.");
            }
        });
    }

    // Xóa thông báo
    public void deleteNotification(int notifyId, final NotificationCallback callback) {
        databaseReference.child(String.valueOf(notifyId)).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onNotificationsReceived(new ArrayList<>()); // Trả về danh sách trống
            } else {
                callback.onError("Failed to delete notification.");
            }
        });
    }

    // Lấy thông báo theo ID
    public void getNotificationById(int notifyId, final NotificationCallback callback) {
        databaseReference.child(String.valueOf(notifyId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Notify notify = dataSnapshot.getValue(Notify.class);
                if (notify != null) {
                    callback.onNotificationsReceived(List.of(notify)); // Trả về thông báo đã tìm thấy
                } else {
                    callback.onError("Notification not found.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    // Lấy thông báo đọc rồi
    public void getNotificationsByReadStatus(int isRead, final NotificationCallback callback) {
        databaseReference.orderByChild("isRead").equalTo(isRead).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Notify> notifyList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notify notify = snapshot.getValue(Notify.class);
                    if (notify != null) {
                        notifyList.add(notify);
                    }
                }
                callback.onNotificationsReceived(notifyList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }


    public interface NotificationCallback {
        void onNotificationsReceived(List<Notify> notifications);
        void onError(String errorMessage);
    }
}
