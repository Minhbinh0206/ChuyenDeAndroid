package com.example.socialmediatdcproject.API;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Major;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MajorAPI {

    private DatabaseReference majorDatabase;

    public MajorAPI() {
        // Khởi tạo reference đến nút "majors" trong Firebase
        majorDatabase = FirebaseDatabase.getInstance().getReference("Majors");
    }

    // Thêm chuyên ngành mới vào Firebase
    public void addMajor(Major major) {
        String majorId = majorDatabase.push().getKey();  // Generate unique ID
        major.setMajorId(majorId.hashCode()); // Set the unique ID for the major
        majorDatabase.child(majorId).setValue(major)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("MajorAPI", "Major added successfully.");
                    } else {
                        Log.e("MajorAPI", "Failed to add major.", task.getException());
                    }
                });
    }

    // Lấy chuyên ngành theo departmentId
    public void getMajorsByDepartmentId(int departmentId, final MajorCallback callback) {
        majorDatabase.orderByChild("departmentId").equalTo(departmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Major> majorList = new ArrayList<>();
                for (DataSnapshot majorSnapshot : snapshot.getChildren()) {
                    Major major = majorSnapshot.getValue(Major.class);
                    if (major != null) {
                        majorList.add(major);
                    }
                }
                callback.onMajorsReceived(majorList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MajorAPI", "Error getting majors.", error.toException());
            }
        });
    }
    // Lấy chuyên ngành theo tên
    public void getMajorByName(String majorName, final MajorAPI.MajorCallback callback) {
        majorDatabase.orderByChild("majorName").equalTo(majorName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Major major = snapshot.getChildren().iterator().next().getValue(Major.class);
                    callback.onMajorReceived(major);
                } else {
                    callback.onMajorReceived(null); // Handle case where department not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DepartmentAPI", "Error fetching department", error.toException());
            }
        });
    }

    // Lấy tất cả chuyên ngành
    public void getAllMajors(final MajorCallback callback) {
        majorDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Major> majorList = new ArrayList<>();
                for (DataSnapshot majorSnapshot : snapshot.getChildren()) {
                    Major major = majorSnapshot.getValue(Major.class);
                    if (major != null) {
                        majorList.add(major);
                    }
                }
                callback.onMajorsReceived(majorList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MajorAPI", "Error getting all majors.", error.toException());
            }
        });
    }

    // Lấy chuyên ngành theo ID
    public void getMajorById(int majorId, final MajorCallback callback) {
        majorDatabase.orderByChild("majorId").equalTo(majorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Major major = snapshot.getChildren().iterator().next().getValue(Major.class);
                    callback.onMajorReceived(major);
                } else {
                    callback.onMajorReceived(null); // Nếu không tìm thấy
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MajorAPI", "Error getting major by ID.", error.toException());
            }
        });
    }

    // Cập nhật chuyên ngành
    public void updateMajor(Major major) {
        majorDatabase.child(String.valueOf(major.getMajorId())).setValue(major)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("MajorAPI", "Major updated successfully.");
                    } else {
                        Log.e("MajorAPI", "Failed to update major.", task.getException());
                    }
                });
    }

    // Xóa chuyên ngành
    public void deleteMajor(int majorId) {
        majorDatabase.orderByChild("majorId").equalTo(majorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot majorSnapshot : snapshot.getChildren()) {
                    majorSnapshot.getRef().removeValue()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("MajorAPI", "Major deleted successfully.");
                                } else {
                                    Log.e("MajorAPI", "Failed to delete major.", task.getException());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MajorAPI", "Error deleting major.", error.toException());
            }
        });
    }

    // Định nghĩa interface MajorCallback
    public interface MajorCallback {
        void onMajorReceived(Major major);
        void onMajorsReceived(List<Major> majors);
    }
}
