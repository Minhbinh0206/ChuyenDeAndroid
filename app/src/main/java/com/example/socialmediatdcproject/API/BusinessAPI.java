package com.example.socialmediatdcproject.API;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.model.Business;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BusinessAPI {

    private DatabaseReference databaseReference;

    // Constructor
    public BusinessAPI() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Businesses");
    }

    // Create (Add a new business)
    public void addBusiness(Business business) {
        String businessId = databaseReference.push().getKey(); // Generate unique ID
        if (businessId != null) {
            databaseReference.child(businessId).setValue(business)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("BusinessAPI", "Business added successfully.");
                        } else {
                            Log.e("BusinessAPI", "Failed to add business.", task.getException());
                        }
                    });
        }
    }

    // Get all businesses
    public void getAllBusinesses(final BusinessCallback callback) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Business> businessList = new ArrayList<>();
                for (DataSnapshot businessSnapshot : snapshot.getChildren()) {
                    Business business = businessSnapshot.getValue(Business.class);
                    if (business != null) {
                        businessList.add(business);
                    }
                }
                callback.onBusinessesReceived(businessList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BusinessAPI", "Error getting all businesses.", error.toException());
            }
        });
    }

    // Get a business by ID
    public void getBusinessById(String businessId, final BusinessCallback callback) {
        databaseReference.child(businessId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Business business = snapshot.getValue(Business.class);
                callback.onBusinessReceived(business);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BusinessAPI", "Error getting business by ID.", error.toException());
            }
        });
    }

    // Update a business
    public void updateBusiness(String businessId, Business business) {
        databaseReference.child(businessId).setValue(business)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("BusinessAPI", "Business updated successfully.");
                    } else {
                        Log.e("BusinessAPI", "Failed to update business.", task.getException());
                    }
                });
    }

    // Delete a business
    public void deleteBusiness(String businessId) {
        databaseReference.child(businessId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("BusinessAPI", "Business deleted successfully.");
                    } else {
                        Log.e("BusinessAPI", "Failed to delete business.", task.getException());
                    }
                });
    }

    // Định nghĩa interface BusinessCallback
    public interface BusinessCallback {
        void onBusinessReceived(Business business);
        void onBusinessesReceived(List<Business> businesses);
    }
}
