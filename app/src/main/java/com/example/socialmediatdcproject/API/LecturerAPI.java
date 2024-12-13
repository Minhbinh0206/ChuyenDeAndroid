package com.example.socialmediatdcproject.API;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.model.Lecturer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class LecturerAPI {
    private DatabaseReference lecturerDatabase;

    public LecturerAPI() {
        lecturerDatabase = FirebaseDatabase.getInstance().getReference("Lecturers");
    }

    // Thêm một giảng viên mới vào database
    public void addLecturer(Lecturer lecturer, final LecturerCallback callback) {
        String uniqueKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        lecturer.setUserId(lecturer.getUserId()); // Đảm bảo userId vẫn được lưu trữ

        // Thêm giảng viên vào Firebase với uniqueKey
        lecturerDatabase.child(uniqueKey).setValue(lecturer)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onLecturerReceived(lecturer);
                    } else {
                        callback.onError("Error adding lecturer: " + task.getException().getMessage());
                    }
                });
    }

    // Cập nhật thông tin giảng viên
    public void updateLecturer(Lecturer lecturer, final LecturerCallback callback) {
        String uniqueKey = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Lấy uniqueKey của giảng viên hiện tại

        // Đảm bảo userId vẫn được lưu trữ trong đối tượng Lecturer
        lecturer.setUserId(lecturer.getUserId());

        // Cập nhật thông tin giảng viên trong Firebase với uniqueKey
        lecturerDatabase.child(uniqueKey).setValue(lecturer)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onLecturerReceived(lecturer);
                    } else {
                        callback.onError("Error updating lecturer: " + task.getException().getMessage());
                    }
                });
    }

    // Lấy giảng viên theo ID
    public void getLecturerById(int lecturerId, final LecturerCallback callback) {
        lecturerDatabase.orderByChild("userId").equalTo(lecturerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot lecturerSnapshot : snapshot.getChildren()) {
                                Lecturer lecturer = lecturerSnapshot.getValue(Lecturer.class);
                                if (lecturer != null) {
                                    callback.onLecturerReceived(lecturer);
                                    return; // Nếu tìm thấy một giảng viên, trả về ngay
                                }
                            }
                        } else {
                            callback.onError("Lecturer not found.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError("Error retrieving lecturer: " + error.getMessage());
                    }
                });
    }

    // Lấy giảng viên theo khóa (uniqueKey)
    public void getLecturerByKey(String key, LecturerCallback callback) {
        lecturerDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Lecturer lecturer = dataSnapshot.getValue(Lecturer.class);
                    callback.onLecturerReceived(lecturer);
                } else {
                    callback.onError("Lecturer not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError("Error retrieving lecturer: " + databaseError.getMessage());
            }
        });
    }

    // Xóa giảng viên theo ID
    public void deleteLecturer(int lecturerId, final LecturerCallback callback) {
        lecturerDatabase.child(String.valueOf(lecturerId)).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onLecturerDeleted(lecturerId);
                    } else {
                        callback.onError("Error deleting lecturer: " + task.getException().getMessage());
                    }
                });
    }

    // Lấy tất cả giảng viên
    public void getAllLecturers(final LecturerCallback callback) {
        lecturerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Lecturer> lecturerList = new ArrayList<>();
                for (DataSnapshot lecturerSnapshot : snapshot.getChildren()) {
                    Lecturer lecturer = lecturerSnapshot.getValue(Lecturer.class);
                    if (lecturer != null) {
                        lecturerList.add(lecturer);
                    }
                }
                callback.onLecturersReceived(lecturerList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError("Error retrieving lecturers: " + error.getMessage());
            }
        });
    }

    // Định nghĩa interface LecturerCallback
    public interface LecturerCallback {
        void onLecturerReceived(Lecturer lecturer);
        void onLecturersReceived(List<Lecturer> lecturers);
        void onError(String errorMessage);
        void onLecturerDeleted(int lecturerId);
    }
}
