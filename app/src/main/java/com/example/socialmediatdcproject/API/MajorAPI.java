package com.example.socialmediatdcproject.API;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.model.Major;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MajorAPI {

    private DatabaseReference databaseReference;

    // Constructor
    public MajorAPI() {
        // FirebaseDatabase instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Major"); // Reference to "major" node
    }

    // Create (Add a new major)
    public void addMajor(Major major, OnCompleteListener<Void> onCompleteListener) {
        String majorId = databaseReference.push().getKey();  // Generate unique ID
        major.setMajorId(majorId.hashCode()); // Set the unique ID for the major
        databaseReference.child(majorId).setValue(major).addOnCompleteListener(onCompleteListener);
    }

    // Get major
    public void getMajorsByDepartmentId(int departmentId, ValueEventListener listener) {
        databaseReference.orderByChild("departmentId").equalTo(departmentId).addListenerForSingleValueEvent(listener);
    }

    // Get all majors
    public void getAllMajors(ValueEventListener listener) {
        databaseReference.addListenerForSingleValueEvent(listener);
    }

    // Get major by id
    public void getMajorById(int majorId, ValueEventListener listener) {
        databaseReference.orderByChild("majorId").equalTo(majorId).addListenerForSingleValueEvent(listener);
    }

    // Update major
    public void updateMajor(Major major, OnCompleteListener<Void> onCompleteListener) {
        databaseReference.child(String.valueOf(major.getMajorId())).setValue(major).addOnCompleteListener(onCompleteListener);
    }

    // Delete major
    public void deleteMajor(int majorId, OnCompleteListener<Void> onCompleteListener) {
        databaseReference.orderByChild("majorId").equalTo(majorId).addListenerForSingleValueEvent(new ValueEventListener() {
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
