package com.example.socialmediatdcproject.API;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.socialmediatdcproject.model.RollCall;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RollCallAPI {

    private DatabaseReference rollCallRef;

    public RollCallAPI() {
        // Khởi tạo reference đến nút "RollCalls" trong Firebase
        rollCallRef = FirebaseDatabase.getInstance().getReference("RollCalls");
    }

    // Cập nhật RollCall dựa trên ID
    public void updateRollCall(RollCall updatedRollCall) {
        rollCallRef.child(String.valueOf(updatedRollCall.getStudentNumber())).setValue(updatedRollCall)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                    } else {
                    }
                })
                .addOnFailureListener(e -> Log.e("RollCallAPI", "Update failed: ", e));
    }

    // Thêm RollCall mới
    public void addRollCall(RollCall rollCall) {
        // Sử dụng push() để tạo ra một ID duy nhất cho mỗi RollCall
        String uniqueId = rollCallRef.push().getKey();

        if (uniqueId != null) {
            rollCallRef.child(uniqueId).setValue(rollCall)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("RollCallAPI", "RollCall added successfully with ID: " + uniqueId);
                        } else {
                            Log.e("RollCallAPI", "Failed to add RollCall.", task.getException());
                        }
                    });
        } else {
            Log.e("RollCallAPI", "Failed to generate a unique ID for RollCall.");
        }
    }

    // Lấy danh sách RollCall dựa trên userId
    public void getRollCallByUserId(int userId, final RollCallCallback callback) {
        rollCallRef.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<RollCall> rollCallList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            RollCall rollCall = dataSnapshot.getValue(RollCall.class);
                            if (rollCall != null) {
                                rollCallList.add(rollCall);
                            }
                        }
                        callback.onRollCallsReceived(rollCallList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý lỗi
                        Log.e("RollCallAPI", "Failed to retrieve RollCall by userId.", error.toException());
                    }
                });
    }

    // Xóa RollCall dựa trên ID
    public void deleteRollCall(String rollCallId) {
        rollCallRef.child(rollCallId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("RollCallAPI", "RollCall deleted successfully.");
                    } else {
                        Log.e("RollCallAPI", "Failed to delete RollCall.", task.getException());
                    }
                });
    }

    // Lấy tất cả các RollCall
    public void getAllRollCalls(final RollCallCallback callback) {
        rollCallRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<RollCall> rollCallList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    RollCall rollCall = dataSnapshot.getValue(RollCall.class);
                    if (rollCall != null) {
                        rollCallList.add(rollCall);
                    }
                }
                callback.onRollCallsReceived(rollCallList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
                Log.e("RollCallAPI", "Failed to retrieve RollCalls.", error.toException());
            }
        });
    }

    // Định nghĩa interface RollCallCallback
    public interface RollCallCallback {
        void onRollCallsReceived(List<RollCall> rollCalls);
    }
}
