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
        notifyDatabase.child(String.valueOf(notify.getUserSendId())).child(String.valueOf(notify.getNotifyId())).setValue(notify).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("NotifyAPI", "Notification added successfully.");
            } else {
                Log.e("NotifyAPI", "Failed to add notification.", task.getException());
            }
        });
    }

    // Lấy tất cả thông báo
    public void getAllNotifies(final NotificationCallback callback) {
        notifyDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Notify> notifyList = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot notifySnapshot : userSnapshot.getChildren()) {
                        Notify notify = notifySnapshot.getValue(Notify.class);
                        if (notify != null) {
                            notifyList.add(notify);
                        }
                    }
                }
                callback.onNotificationsReceived(notifyList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NotifyAPI", "Error fetching all notifications: " + error.getMessage());
                callback.onError(error.getMessage());
            }
        });
    }


    // Kiểm tra xem userId đã tồn tại trong danh sách isReaded của thông báo hay chưa
    public void checkIfUserHasRead(Notify notify, int userId, final CheckReadStatusCallback callback) {
        // Lấy thông báo theo notifyId
        notifyDatabase.child(String.valueOf(notify.getUserSendId())).child(String.valueOf(notify.getNotifyId())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy thông báo hiện tại
                    Notify notify = snapshot.getValue(Notify.class);
                    if (notify != null) {
                        // Lấy danh sách isReaded hiện tại
                        List<Integer> isReaded = notify.getIsReaded();
                        if (isReaded != null && isReaded.contains(userId)) {
                            // Nếu userId có trong isReaded, trả về true
                            callback.onResult(true);
                        } else {
                            // Nếu userId không có trong isReaded, trả về false
                            callback.onResult(false);
                        }
                    } else {
                        callback.onResult(false);  // Nếu không lấy được thông báo
                    }
                } else {
                    callback.onResult(false);  // Nếu không tìm thấy thông báo
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NotifyAPI", "Error fetching notification: " + error.getMessage());
                callback.onResult(false);  // Nếu có lỗi trong quá trình lấy dữ liệu
            }
        });
    }

    // Định nghĩa interface để trả về kết quả kiểm tra
    public interface CheckReadStatusCallback {
        void onResult(boolean hasRead);
    }

    // Thêm userId vào danh sách isReaded trong thông báo
    public void addUserToRead(Notify notify, int userId) {
        // Lấy thông báo theo notifyId
        notifyDatabase.child(String.valueOf(notify.getUserSendId())).child(String.valueOf(notify.getNotifyId())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy thông báo hiện tại
                    Notify notify = snapshot.getValue(Notify.class);
                    if (notify != null) {
                        // Lấy danh sách isReaded hiện tại
                        List<Integer> isReaded = notify.getIsReaded();
                        if (isReaded == null) {
                            // Nếu danh sách isReaded là null, tạo danh sách mới
                            isReaded = new ArrayList<>();
                        }

                        // Thêm userId vào danh sách nếu chưa có
                        if (!isReaded.contains(userId)) {
                            isReaded.add(userId);
                        }

                        // Cập nhật lại danh sách isReaded trong notify
                        notify.setIsReaded(isReaded);

                        // Lưu lại thông báo đã được cập nhật lên Firebase
                        notifyDatabase.child(String.valueOf(notify.getUserSendId())).child(String.valueOf(notify.getNotifyId())).setValue(notify).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("NotifyAPI", "User added to isReaded successfully.");
                            } else {
                                Log.e("NotifyAPI", "Failed to add user to isReaded.", task.getException());
                            }
                        });
                    }
                } else {
                    Log.e("NotifyAPI", "Notification not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NotifyAPI", "Error fetching notification: " + error.getMessage());
            }
        });
    }

    // Lấy tất cả thông báo
    public void getAllNotificationsByUserId(int userId,final NotificationCallback callback) {
        notifyDatabase.child(String.valueOf(userId)).addValueEventListener(new ValueEventListener() {
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
    public void getNotificationById(int notifyId, int userSend, final NotificationCallback callback) {
        notifyDatabase.child(String.valueOf(userSend)).orderByChild("notifyId").equalTo(notifyId).addListenerForSingleValueEvent(new ValueEventListener() {
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
