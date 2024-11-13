package com.example.socialmediatdcproject.API;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.socialmediatdcproject.model.Class;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import java.util.ArrayList;
import java.util.List;

public class ClassAPI {

    private DatabaseReference databaseReference;

    // Constructor
    public ClassAPI() {
        // FirebaseDatabase instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Classes"); // Reference to "Classes" node
    }

    // Create (Add a new class)
    public void addClass(Class classItem, OnCompleteListener<Void> onCompleteListener) {
        String classId = databaseReference.push().getKey();  // Generate unique ID
        classItem.setId(classId.hashCode()); // Set the unique ID for the class
        databaseReference.child(classId).setValue(classItem).addOnCompleteListener(onCompleteListener);
    }

    // Get classes by className
    public void getClassByClassName(String className, final ClassCallback callback) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Class> classList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Class classItem = ds.getValue(Class.class);
                    if (classItem != null && className.equals(classItem.getClassName())) {
                        classList.add(classItem);
                    }
                }
                callback.onClassesReceived(classList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ClassAPI", "Error fetching classes by className", error.toException());
            }
        });
    }

    // Get all classes
    public void getAllClasses(final ClassCallback callback) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Class> classList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Class classItem = ds.getValue(Class.class);
                    if (classItem != null) {
                        classList.add(classItem);
                    }
                }
                callback.onClassesReceived(classList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ClassAPI", "Error fetching classes", error.toException());
            }
        });
    }

    // Get classes by departmentId
    public void getClassesByDepartmentId(int departmentId, final ClassCallback callback) {
        databaseReference.orderByChild("departmentId").equalTo(departmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Class> classList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Class classItem = ds.getValue(Class.class);
                    if (classItem != null) {
                        classList.add(classItem);
                    }
                }
                callback.onClassesReceived(classList); // Return list of classes with the same departmentId
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ClassAPI", "Error fetching classes by departmentId", error.toException());
            }
        });
    }

    public void getClassesByMajorId(int majorId, final ClassCallback callback) {
        databaseReference.orderByChild("majorId").equalTo(majorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Class> classList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Class classItem = ds.getValue(Class.class);
                    if (classItem != null) {
                        classList.add(classItem);
                    }
                }
                callback.onClassesReceived(classList); // Return list of classes with the same departmentId
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ClassAPI", "Error fetching classes by departmentId", error.toException());
            }
        });
    }


    // Update class
    public void updateClass(Class classItem, OnCompleteListener<Void> onCompleteListener) {
        databaseReference.child(String.valueOf(classItem.getId())).setValue(classItem).addOnCompleteListener(onCompleteListener);
    }

    // Delete class
    public void deleteClass(int classId, OnCompleteListener<Void> onCompleteListener) {
        databaseReference.orderByChild("classId").equalTo(classId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue().addOnCompleteListener(onCompleteListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ClassAPI", "Error deleting class", databaseError.toException());
            }
        });
    }

    // Callback interface for Class operations
    public interface ClassCallback {
        void onClassReceived(Class classItem);
        void onClassesReceived(List<Class> classList);
    }
}
