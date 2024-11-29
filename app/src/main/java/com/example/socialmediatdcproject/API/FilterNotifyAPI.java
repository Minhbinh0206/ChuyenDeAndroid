package com.example.socialmediatdcproject.API;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.socialmediatdcproject.dataModels.FilterNotify;
import com.example.socialmediatdcproject.model.Notify;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FilterNotifyAPI {
    private DatabaseReference notifyDatabase;

    public FilterNotifyAPI() {
        // Khởi tạo reference đến nút "Filters/FilterNotifies" trong Firebase
        notifyDatabase = FirebaseDatabase.getInstance().getReference("Filters").child("FilterNotifies");
    }

    // Thêm FilterNotify mới vào Firebase với notifyId làm khóa và danh sách người nhận trong "Receive"
    public void addReceiveNotify(FilterNotify receiveNotify, int userSend) {
        String notifyKey = String.valueOf(receiveNotify.getNotifyId());

        // Lưu notifyId trước
        notifyDatabase.child(String.valueOf(userSend)).child(notifyKey).child("notifyId").setValue(receiveNotify.getNotifyId())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FilterNotifyAPI", "notifyId added successfully.");
                    } else {
                        Log.e("FilterNotifyAPI", "Failed to add notifyId.", task.getException());
                    }
                });

        // Lưu danh sách Receive dưới dạng các cặp userId
        List<Integer> listUserReceive = receiveNotify.getListUserGet();
        if (listUserReceive != null) {
            for (int i = 0; i < listUserReceive.size(); i++) {
                notifyDatabase.child(String.valueOf(userSend)).child(notifyKey).child("Receive").child(String.valueOf(i)).child("userId")
                        .setValue(listUserReceive.get(i))
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("FilterNotifyAPI", "Receive userId added successfully.");
                            } else {
                                Log.e("FilterNotifyAPI", "Failed to add Receive userId.", task.getException());
                            }
                        });
            }
        }
    }

    // Lấy tất cả ReceiveNotify từ Firebase
    public void getAllReceiveNotifies(final ReceiveNotifyCallback callback) {
        notifyDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FilterNotify> receiveNotifyList = new ArrayList<>();
                for (DataSnapshot notifySnapshot : snapshot.getChildren()) {
                    FilterNotify receiveNotify = notifySnapshot.getValue(FilterNotify.class);
                    if (receiveNotify != null) {
                        receiveNotifyList.add(receiveNotify);
                    }
                }
                callback.onReceiveNotifiesReceived(receiveNotifyList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FilterNotifyAPI", "Failed to retrieve all receive notifies.", error.toException());
            }
        });
    }

    // Kiểm tra xem user có trong danh sách Receive cho một notifyId nhất định không
    public void findUserInReceive(Notify notify, int userId, final UserInReceiveCallback callback) {
        DatabaseReference notifyRef = notifyDatabase.child(String.valueOf(notify.getUserSendId())).child(String.valueOf(notify.getNotifyId())).child("Receive");

        notifyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot receiveSnapshot : snapshot.getChildren()) {
                    Integer receivedUserId = receiveSnapshot.child("userId").getValue(Integer.class);
                    if (receivedUserId != null && receivedUserId == userId) {
                        found = true;
                        break;
                    }
                }
                callback.onResult(found);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FilterNotifyAPI", "Error checking user in receive list", error.toException());
                callback.onResult(false);
            }
        });
    }

    // Định nghĩa interface để nhận kết quả kiểm tra
    public interface UserInReceiveCallback {
        void onResult(boolean isFound);
    }

    // Định nghĩa interface ReceiveNotifyCallback
    public interface ReceiveNotifyCallback {
        void onReceiveNotifyReceived(FilterNotify receiveNotify);
        void onReceiveNotifiesReceived(List<FilterNotify> receiveNotifies);
    }
}
