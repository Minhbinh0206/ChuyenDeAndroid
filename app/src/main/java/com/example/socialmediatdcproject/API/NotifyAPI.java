package com.example.socialmediatdcproject.API;

import com.example.socialmediatdcproject.model.Notify;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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

    public interface NotificationCallback {
        void onNotificationsReceived(List<Notify> notifications);
        void onError(String errorMessage);
    }
}
