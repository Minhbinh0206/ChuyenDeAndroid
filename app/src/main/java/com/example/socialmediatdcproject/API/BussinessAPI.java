package com.example.socialmediatdcproject.API;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.model.Bussiness;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BussinessAPI {

    private DatabaseReference databaseReference;

    // Constructor
    public BussinessAPI() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Business"); // Reference to "Businesses" node
    }

    // Create (Add a new business)
    public void addBusiness(Bussiness business, OnCompleteListener<Void> onCompleteListener) {
        String businessId = databaseReference.push().getKey();  // Generate unique ID
        databaseReference.child(businessId).setValue(business).addOnCompleteListener(onCompleteListener);
    }

    // Get all businesses
    public void getAllBusinesses(ValueEventListener listener) {
        databaseReference.addListenerForSingleValueEvent(listener);
    }

    // Get a business by ID
    public void getBusinessById(String businessId, ValueEventListener listener) {
        databaseReference.child(businessId).addListenerForSingleValueEvent(listener);
    }

    // Update a business
    public void updateBusiness(String businessId, Bussiness business, OnCompleteListener<Void> onCompleteListener) {
        databaseReference.child(businessId).setValue(business).addOnCompleteListener(onCompleteListener);
    }

    // Delete a business
    public void deleteBusiness(String businessId, OnCompleteListener<Void> onCompleteListener) {
        databaseReference.child(businessId).removeValue().addOnCompleteListener(onCompleteListener);
    }
}
