package com.example.socialmediatdcproject.shareViewModels;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.LecturerAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.model.Group;
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
        // Tạo đối tượng GroupUserAPI
        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getGroupById(groupId , new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {
                GroupUserAPI groupUserAPI = new GroupUserAPI();
                // Lắng nghe tất cả người dùng trong nhóm để tìm studentId cần xóa
                groupUserAPI.getAllUsersInGroup(groupId, new GroupUserAPI.GroupUsersCallback() {
                    @Override
                    public void onUsersReceived(List<Integer> userIds) {
                        // Kiểm tra nếu studentId có trong danh sách userIds
                        Log.d("SharedViewModel", "Users in group: " + userIds.size());
                        for (Integer userId : userIds) {
                            if (userId == studentId) {
                                // Xóa người dùng khỏi nhóm
                                groupUserAPI.deleteGroupUser(groupId, studentId);
                                Log.d("SharedViewModel", "Removed student with ID " + studentId + " from group " + groupId);
                                break;
                            }
                        }
                    }
                });
            }

            @Override
            public void onGroupsReceived(List<Group> groups) {

            }
        });

    }

    public void removeLecturerFromGroup(int groupId, int lecturerId) {
        // Tạo đối tượng GroupUserAPI
        GroupUserAPI groupUserAPI = new GroupUserAPI();

        // Lắng nghe tất cả người dùng trong nhóm để tìm studentId cần xóa
        groupUserAPI.getAllUsersInGroup(groupId, new GroupUserAPI.GroupUsersCallback() {
            @Override
            public void onUsersReceived(List<Integer> userIds) {
                // Kiểm tra nếu studentId có trong danh sách userIds
                for (Integer userId : userIds) {
                    if (userId == lecturerId) {
                        // Xóa người dùng khỏi nhóm
                        groupUserAPI.deleteGroupUser(groupId, lecturerId );
                        Log.d("SharedViewModel", "Removed with ID " + lecturerId + " from group " + groupId);
                        break;
                    }
                }
            }
        });
    }

    public void addStudentToGroup(int groupId, int studentId) {
        // Tạo đối tượng GroupUserAPI
        GroupUserAPI groupUserAPI = new GroupUserAPI();

        // Kiểm tra xem sinh viên đã có trong nhóm hay chưa
        groupUserAPI.getAllUsersInGroup(groupId, new GroupUserAPI.GroupUsersCallback() {
            @Override
            public void onUsersReceived(List<Integer> userIds) {
                // Nếu sinh viên chưa có trong nhóm, thêm sinh viên vào nhóm
                if (!userIds.contains(studentId)) {
                    // Tạo đối tượng GroupUser với thông tin sinh viên
                    GroupUser groupUser = new GroupUser(groupId, studentId);
                    groupUserAPI.addGroupUser(groupUser);  // Thêm sinh viên vào nhóm
                    Log.d("SharedViewModel", "Added student with ID " + studentId + " to group " + groupId);
                } else {
                    Log.d("SharedViewModel", "Student with ID " + studentId + " is already in group " + groupId);
                }
            }
        });
    }

    public void addLecturerToGroup(int groupId, int lecturerId) {
        // Tạo đối tượng GroupUserAPI
        GroupUserAPI groupUserAPI = new GroupUserAPI();

        // Kiểm tra xem giảng viên đã có trong nhóm hay chưa
        groupUserAPI.getAllUsersInGroup(groupId, new GroupUserAPI.GroupUsersCallback() {
            @Override
            public void onUsersReceived(List<Integer> userIds) {
                // Nếu giảng viên chưa có trong nhóm, thêm giảng viên vào nhóm
                if (!userIds.contains(lecturerId)) {
                    // Tạo đối tượng GroupUser với thông tin giảng viên
                    GroupUser groupUser = new GroupUser(groupId, lecturerId);
                    groupUserAPI.addGroupUser(groupUser);  // Thêm giảng viên vào nhóm
                    Log.d("SharedViewModel", "Added lecturer with ID " + lecturerId + " to group " + groupId);
                } else {
                    Log.d("SharedViewModel", "Lecturer with ID " + lecturerId + " is already in group " + groupId);
                }
            }
        });
    }


}
