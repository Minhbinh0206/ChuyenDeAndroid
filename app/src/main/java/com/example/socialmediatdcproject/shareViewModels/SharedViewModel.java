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

    // không dùng nữa
    private final MutableLiveData<List<Lecturer>> lecturerListLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Student>> studentListLiveData = new MutableLiveData<>(new ArrayList<>());
    //----------------

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
//                        Toast.makeText(context, "Lỗi khi truy vấn dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Hảm không còn dùng
    public LiveData<List<Lecturer>> getLecturerList() {
        return lecturerListLiveData;
    }

    public void removeLecturer(Lecturer lecturer) {
        List<Lecturer> currentList = lecturerListLiveData.getValue();
        if (currentList != null) {
            currentList.remove(lecturer);
            lecturerListLiveData.setValue(new ArrayList<>(currentList)); // Cập nhật LiveData sau khi xóa
        }
    }

    public void removeStudent(Student student) {
        List<Student> currentList = studentListLiveData.getValue();
        if (currentList != null) {
            currentList.remove(student);
            Log.d("ShareViewModel" , "Đã xóa học sinh" + student);
//            Toast.makeText("","Đã xóa thành công", Toast.LENGTH_SHORT).show();
            studentListLiveData.setValue(new ArrayList<>(currentList)); // Cập nhật LiveData sau khi xóa
        }
        Log.d("ShareViewModel" , "Đang xóa học sinh");
    }

    public void setLecturerList(List<Lecturer> lecturers) {
        lecturerListLiveData.setValue(lecturers);
    }

}
