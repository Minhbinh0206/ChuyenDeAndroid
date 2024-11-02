package com.example.socialmediatdcproject.API;

import android.util.Log;
import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.dataModels.Collab;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CollabAPI {

    private DatabaseReference collabRef;

    public CollabAPI() {
        // Khởi tạo reference đến nút "UsersInGroup" trong Firebase
        collabRef = FirebaseDatabase.getInstance().getReference("Collabs");
    }

    public void addCollab(Collab collab) {
        // Sử dụng push() để tạo ra một ID duy nhất cho mỗi GroupUser
        String uniqueId = collabRef.push().getKey();

        if (uniqueId != null) {
            collabRef.child(uniqueId).setValue(collab)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("GroupUserAPI", "GroupUser added successfully with ID: " + uniqueId);
                        } else {
                            Log.e("GroupUserAPI", "Failed to add GroupUser.", task.getException());
                        }
                    });
        } else {
            Log.e("GroupUserAPI", "Failed to generate a unique ID for GroupUser.");
        }
    }

    // Lấy GroupUser dựa trên groupId
    public void getCollabsByDepartmentId(int departmentId, final CollabCallback callback) {
        collabRef.orderByChild("departmentId").equalTo(departmentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Collab> collabList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Collab collab = dataSnapshot.getValue(Collab.class);
                            if (collab != null) {
                                collabList.add(collab);
                            }
                        }
                        callback.onCollabReceived(collabList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("CollabAPI", "Failed to retrieve collabs by departmentId.", error.toException());
                    }
                });
    }

    // Lấy tất cả GroupUser
    public void getAllCollab(final CollabCallback callback) {
        collabRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Collab> collabList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Collab collab = dataSnapshot.getValue(Collab.class);
                    if (collab != null) {
                        collabList.add(collab);
                    }
                }
                callback.onCollabReceived(collabList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
                Log.e("GroupUserAPI", "Failed to retrieve GroupUsers.", error.toException());
            }
        });
    }

    // Định nghĩa interface GroupUserCallback
    public interface CollabCallback {
        void onCollabReceived(List<Collab> collabList);
    }
}
