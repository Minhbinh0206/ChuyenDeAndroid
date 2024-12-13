package com.example.socialmediatdcproject.API;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Department;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class DepartmentAPI {

    private DatabaseReference databaseReference;

    // Constructor
    public DepartmentAPI() {
        // FirebaseDatabase instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Departments"); // Reference to "Departments" node
    }

    // Thêm Department
    public void addDepartment(Department department) {
        String key = department.getDepartmentId() + ""; // Sử dụng adminDepartmentId làm key
        databaseReference.child(key).setValue(department)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Thêm thành công
                        Log.d("UserAPI", "User added successfully.");
                    } else {
                        // Xảy ra lỗi
                        Log.e("UserAPI", "Failed to add user.", task.getException());
                    }
                });
    }

    // Get all departments
    public void getAllDepartments(final DepartmentCallback callback) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Department> departmentList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Department department = ds.getValue(Department.class);
                    if (department != null) {
                        departmentList.add(department);
                    }
                }
                callback.onDepartmentsReceived(departmentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DepartmentAPI", "Error fetching departments", error.toException());
            }
        });
    }

    // Get department by id
    public void getDepartmentById(int departmentId, final DepartmentCallback callback) {
        databaseReference.orderByChild("departmentId").equalTo(departmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Department department = snapshot.getChildren().iterator().next().getValue(Department.class);
                    callback.onDepartmentReceived(department);
                } else {
                    callback.onDepartmentReceived(null); // Handle case where department not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DepartmentAPI", "Error fetching department", error.toException());
            }
        });
    }

    public void getDepartmentByName(String departmentName, final DepartmentCallback callback) {
        databaseReference.orderByChild("departmentName").equalTo(departmentName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Department department = snapshot.getChildren().iterator().next().getValue(Department.class);
                    callback.onDepartmentReceived(department);
                } else {
                    callback.onDepartmentReceived(null); // Handle case where department not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DepartmentAPI", "Error fetching department", error.toException());
            }
        });
    }

    // Update department
    public void updateDepartment(Department department, OnCompleteListener<Void> onCompleteListener) {
        databaseReference.child(String.valueOf(department.getDepartmentId())).setValue(department).addOnCompleteListener(onCompleteListener);
    }

    // Delete department
    public void deleteDepartment(int departmentId, OnCompleteListener<Void> onCompleteListener) {
        databaseReference.orderByChild("departmentId").equalTo(departmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue().addOnCompleteListener(onCompleteListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DepartmentAPI", "Error deleting department", databaseError.toException());
            }
        });
    }

    // Callback interface for Department operations
    public interface DepartmentCallback {
        void onDepartmentReceived(Department department);
        void onDepartmentsReceived(List<Department> departments);
    }
}
