package com.example.socialmediatdcproject.API;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class StudentAPI {
    private DatabaseReference studentDatabase;

    public StudentAPI() {
        studentDatabase = FirebaseDatabase.getInstance().getReference("Student");
    }

    // Thêm một sinh viên mới vào database
    public void addStudent(Student student, final StudentCallback callback) {
        int userId = student.getUserId(); // userId phải được gán trước đó

        // Kiểm tra xem userId đã tồn tại hay chưa
        studentDatabase.child(String.valueOf(userId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Nếu chưa tồn tại, thêm sinh viên vào Firebase
                    studentDatabase.child(String.valueOf(userId)).setValue(student)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    callback.onStudentReceived(student); // Gọi callback để thông báo thành công
                                } else {
                                    callback.onError("Error adding student: " + task.getException().getMessage());
                                }
                            });
                } else {
                    callback.onError("Error: UserId already exists."); // Xử lý nếu userId đã tồn tại
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError("Error checking userId: " + databaseError.getMessage());
            }
        });
    }


    // Cập nhật thông tin sinh viên
    public void updateStudent(Student student, final StudentCallback callback) {
        String studentId = String.valueOf(student.getUserId());
        studentDatabase.child(studentId).setValue(student)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onStudentReceived(student);
                    } else {
                        callback.onError("Error updating student: " + task.getException().getMessage());
                    }
                });
    }

    // Lấy thông tin sinh viên theo ID
    public void getStudentById(int studentId, final StudentCallback callback) {
        studentDatabase.child(String.valueOf(studentId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Student student = snapshot.getValue(Student.class);
                if (student != null) {
                    callback.onStudentReceived(student);
                } else {
                    callback.onError("Student not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError("Error retrieving student: " + error.getMessage());
            }
        });
    }

    // Xóa sinh viên theo ID
    public void deleteStudent(int studentId, final StudentCallback callback) {
        studentDatabase.child(String.valueOf(studentId)).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onStudentDeleted(studentId); // Bạn cần thêm phương thức này vào StudentCallback
                    } else {
                        callback.onError("Error deleting student: " + task.getException().getMessage());
                    }
                });
    }

    // Lấy tất cả sinh viên
    public void getAllStudents(final StudentCallback callback) {
        studentDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Student> studentList = new ArrayList<>();
                for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                    Student student = studentSnapshot.getValue(Student.class);
                    if (student != null) {
                        studentList.add(student);
                    }
                }
                callback.onStudentsReceived(studentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError("Error retrieving students: " + error.getMessage());
            }
        });
    }

    // Định nghĩa interface StudentCallback
    public interface StudentCallback {
        void onStudentReceived(Student student);
        void onStudentsReceived(List<Student> students);
        void onError(String errorMessage);
        void onStudentDeleted(int studentId); // Thêm phương thức này
    }
}