package com.example.socialmediatdcproject.shareViewModels;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.socialmediatdcproject.model.Lecturer;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isEditMode = new MutableLiveData<>(false);

    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference lecturersRef = FirebaseDatabase.getInstance().getReference("Lecturers");
    private DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("Students");

    public LiveData<Boolean> getIsEditMode() {
        return isEditMode;
    }

    public void setEditMode(boolean editMode) {
        isEditMode.setValue(editMode);
    }
    public void removeStudentFromGroup(int groupId, int studentId) {
        databaseRef.child("UsersInGroup")
                .orderByChild("groupId")
                .equalTo(groupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot entrySnapshot : snapshot.getChildren()) {
                            Integer entryStudentId = entrySnapshot.child("userId").getValue(Integer.class);

                            Log.d("SharedViewModel" , "groupId: " + groupId);
                            Log.d("SharedViewModel" , "entryStudentId: " + entryStudentId);

                            // Kiểm tra nếu entry này có studentId khớp
                            if (entryStudentId != null && entryStudentId.equals(studentId)) {
                                // Xóa bản ghi nếu khớp cả groupId và studentId
                                entrySnapshot.getRef().removeValue()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Log.d("SharedViewModel", "Xóa học sinh thành công khỏi nhóm trong Firebase");
//                                                Toast.makeText(context, "Xóa học sinh thành công khỏi nhóm", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.e("SharedViewModel", "Lỗi khi xóa học sinh", task.getException());
//                                                Toast.makeText(context, "Lỗi khi xóa học sinh khỏi nhóm", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("SharedViewModel", "Lỗi khi truy vấn UserInGroup", error.toException());
//                        Toast.makeText(context, "Lỗi khi truy vấn dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void removeLecturerFromGroup(int groupId, int lecturerId) {
        databaseRef.child("UsersInGroup")
                .orderByChild("groupId")
                .equalTo(groupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot entrySnapshot : snapshot.getChildren()) {
                            Integer entryLecturerId = entrySnapshot.child("userId").getValue(Integer.class);

                            // Kiểm tra nếu entry này có lecturerId khớp
                            if (entryLecturerId != null && entryLecturerId.equals(lecturerId)) {
                                // Xóa bản ghi nếu khớp cả groupId và lecturerId
                                entrySnapshot.getRef().removeValue()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Log.d("SharedViewModel", "Xóa giảng viên thành công khỏi nhóm trong Firebase");
//                                                Toast.makeText(context, "Xóa giảng viên thành công khỏi nhóm", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.e("SharedViewModel", "Lỗi khi xóa giảng viên", task.getException());
//                                                Toast.makeText(context, "Lỗi khi xóa giảng viên khỏi nhóm", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("SharedViewModel", "Lỗi khi truy vấn UserInGroup", error.toException());
                    }
                });
    }
    // lấy danh sách giảng viên
    public void fetchLecturersList() {
        lecturersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> lecturersList = new ArrayList<>();
                for (DataSnapshot lecturerSnapshot : dataSnapshot.getChildren()) {
                    String lecturerName = lecturerSnapshot.child("fullName").getValue(String.class);
                    if (lecturerName != null) {
                        lecturersList.add(lecturerName);
                    }
                }
                Log.d("SharedViewModel", "Lecturers List: " + lecturersList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("SharedViewModel", "Failed to read lecturers list", databaseError.toException());
            }
        });
    }

    // lấy danh sách sinh viên
    public void fetchStudentsList() {
        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> studentsList = new ArrayList<>();
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String studentName = studentSnapshot.child("fullName").getValue(String.class);
                    if (studentName != null) {
                        studentsList.add(studentName);
                    }
                }
                // Bạn có thể xử lý danh sách này ở đây (ví dụ: cập nhật UI)
                Log.d("SharedViewModel", "Students List: " + studentsList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("SharedViewModel", "Failed to read students list", databaseError.toException());
            }
        });
    }

    public void addStudentToGroup(int groupId, int studentId) {
        DatabaseReference usersInGroupRef = databaseRef.child("UsersInGroup");

        String userInGroupId = usersInGroupRef.push().getKey();
        if (userInGroupId != null) {
            // Thêm dữ liệu
            usersInGroupRef.child(userInGroupId).child("groupId").setValue(groupId);
            usersInGroupRef.child(userInGroupId).child("userId").setValue(studentId)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("SharedViewModel", "Thêm sinh viên vào nhóm thành công");
                        } else {
                            Log.e("SharedViewModel", "Lỗi khi thêm sinh viên vào nhóm", task.getException());
                        }
                    });
        }
    }

    public void addLecturerToGroup(int groupId, int lecturerId) {
        DatabaseReference usersInGroupRef = databaseRef.child("UsersInGroup");
        String userInGroupId = usersInGroupRef.push().getKey();
        if (userInGroupId != null) {
            // Thêm dữ liệu
            usersInGroupRef.child(userInGroupId).child("groupId").setValue(groupId);
            usersInGroupRef.child(userInGroupId).child("userId").setValue(lecturerId)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("SharedViewModel", "Thêm giảng viên vào nhóm thành công");
                        } else {
                            Log.e("SharedViewModel", "Lỗi khi thêm giảng viên vào nhóm", task.getException());
                        }
                    });
        }
    }
}
