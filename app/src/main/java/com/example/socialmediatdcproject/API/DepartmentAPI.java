package com.example.socialmediatdcproject.API;

import android.util.Log;

import androidx.annotation.NonNull;

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
        databaseReference = database.getReference("Department"); // Reference to "department" node
    }

    // Create (Add a new department)
    public void addDepartment(Department department, OnCompleteListener<Void> onCompleteListener) {
        String departmentId = databaseReference.push().getKey();  // Generate unique ID
        department.setDepartmentId(departmentId.hashCode()); // Set the unique ID for the department
        databaseReference.child(departmentId).setValue(department).addOnCompleteListener(onCompleteListener);
    }

    // Get all
    public void getAllDepartments(ValueEventListener listener) {
        databaseReference.addListenerForSingleValueEvent(listener);
    }

    // Get department by id
    public void getDepartmentById(int departmentId, ValueEventListener listener) {
        databaseReference.orderByChild("departmentId").equalTo(departmentId).addListenerForSingleValueEvent(listener);
    }

    public void getDepartmentByName(String departmentName, ValueEventListener listener) {
        databaseReference.orderByChild("departmentName").equalTo(departmentName).addListenerForSingleValueEvent(listener);
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
                // Handle possible errors
            }
        });
    }
}
