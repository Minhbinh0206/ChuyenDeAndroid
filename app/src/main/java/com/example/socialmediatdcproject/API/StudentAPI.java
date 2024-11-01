package com.example.socialmediatdcproject.API;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;
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
        studentDatabase = FirebaseDatabase.getInstance().getReference("Students");
    }

    // Thêm một sinh viên mới vào database
    public void addStudent(Student student, final StudentCallback callback) {
        String uniqueKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        student.setUserId(student.getUserId()); // Đảm bảo userId vẫn được lưu trữ

        // Thêm sinh viên vào Firebase với uniqueKey
        studentDatabase.child(uniqueKey).setValue(student)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onStudentReceived(student);
                    } else {

                    }
                });
    }

    // Lấy sinh viên theo classId
    public void getStudentByClassId(int classId, final StudentCallback callback) {
        studentDatabase.orderByChild("classId").equalTo(classId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Student> studentList = new ArrayList<>();
                for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                    Student student = studentSnapshot.getValue(Student.class);
                    if (student != null) {
                        studentList.add(student);
                    }
                }
                callback.onStudentsReceived(studentList); // Trả về danh sách sinh viên thuộc lớp đã chọn
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }


    // Cập nhật thông tin sinh viên
    public void updateStudent(Student student, final StudentCallback callback) {
        String uniqueKey = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Lấy uniqueKey của sinh viên hiện tại

        // Đảm bảo userId vẫn được lưu trữ trong đối tượng Student
        student.setUserId(student.getUserId());

        // Cập nhật thông tin sinh viên trong Firebase với uniqueKey
        studentDatabase.child(uniqueKey).setValue(student)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onStudentReceived(student);
                    } else {

                    }
                });
    }


    public void getStudentById(int studentId, final StudentCallback callback) {
        studentDatabase.orderByChild("userId").equalTo(studentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                                Student student = studentSnapshot.getValue(Student.class);
                                if (student != null) {
                                    callback.onStudentReceived(student);
                                    return; // Nếu tìm thấy một sinh viên, trả về ngay
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    public void getStudentByKey(String key, StudentCallback callback) {
        studentDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Student student = dataSnapshot.getValue(Student.class);
                    callback.onStudentReceived(student);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

            }
        });
    }

    // Định nghĩa interface StudentCallback
    public interface StudentCallback {
        void onStudentReceived(Student student);
        void onStudentsReceived(List<Student> students);
    }
}
