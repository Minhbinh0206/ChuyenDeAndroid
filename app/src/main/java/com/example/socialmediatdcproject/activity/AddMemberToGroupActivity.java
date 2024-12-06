package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.LecturerAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.LecturerAdapter;
import com.example.socialmediatdcproject.adapter.MemberAdapter;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Lecturer;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.shareViewModels.SharedViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddMemberToGroupActivity extends AppCompatActivity {
    private Button submitBtn , cancelBtn;
    private RecyclerView memberRecyclerView;
    private EditText searchField;
//    private Spinner filterSpinner;
    private MemberAdapter memberAdapter = null;
    private LecturerAdapter lecturerAdapter;
    private List<Student> allMembers = new ArrayList<>();
    private List<Student> filteredMembers = new ArrayList<>();
    private List<Lecturer> allLecturers = new ArrayList<>();
    private List<Lecturer> filteredLecturers = new ArrayList<>();// Danh sách được lọc
    private int groupId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_add_new_member_layout);

        // Ánh xạ view
        init();

        Intent intent = getIntent();
        groupId = intent.getIntExtra("groupId" , -1);

        // Load danh sách thành viên chưa có trong nhóm
        loadStudentsByGroupId(groupId);
        loadLecturersByGroupId(groupId);

        memberAdapter = new MemberAdapter(filteredMembers , this);
        memberAdapter.updateList(filteredMembers);
        memberAdapter.getItemCount();
        memberRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        memberRecyclerView.setAdapter(memberAdapter);

        // Tìm kiếm
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMembers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        submitBtn.setOnClickListener(v -> {
            List<Integer> selectedIds = memberAdapter.getSelectedUserIds();
            if (!selectedIds.isEmpty()) {
                SharedViewModel sharedViewModel = new SharedViewModel();
                for (int userId : selectedIds) {
                    sharedViewModel.addStudentToGroup(groupId, userId);
                }
                Toast.makeText(this, "Đã thêm thành viên vào nhóm!", Toast.LENGTH_SHORT).show();
                memberAdapter.updateList(memberAdapter.getMembers()); // Làm mới danh sách
            } else {
                Toast.makeText(this, "Chưa chọn thành viên nào!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStudentsByGroupId(int id) {
        StudentAPI studentAPI = new StudentAPI();

        // Lấy danh sách tất cả sinh viên
        studentAPI.getAllStudents(new StudentAPI.StudentCallback() {
            @Override
            public void onStudentsReceived(List<Student> students) {
                GroupUserAPI groupUserAPI = new GroupUserAPI();

                // Lấy danh sách userIds trong nhóm
                groupUserAPI.getAllUsersInGroup(id, new GroupUserAPI.GroupUsersCallback() {
                    @Override
                    public void onUsersReceived(List<Integer> userIds) {
                        // Lọc sinh viên chưa có trong nhóm
                        List<Student> unassignedStudents = new ArrayList<>();

                        for (Student student : students) {
                            if (!userIds.contains(student.getUserId())) {
                                unassignedStudents.add(student);
                            }
                        }
                        allMembers.addAll(unassignedStudents);

                        // Cập nhật danh sách hiển thị
                        filteredMembers.clear();
                        filteredMembers.addAll(unassignedStudents);
                        memberAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onStudentReceived(Student student) {
                // Không sử dụng trong trường hợp này
            }
        });
    }

    private void loadLecturersByGroupId(int id) {
        LecturerAPI lecturerAPI = new LecturerAPI();
        // Lấy danh sách tất cả giang viên
        lecturerAPI.getAllLecturers(new LecturerAPI.LecturerCallback() {
            @Override
            public void onLecturerReceived(Lecturer lecturer) {

            }

            @Override
            public void onLecturersReceived(List<Lecturer> lecturers) {
                GroupUserAPI groupUserAPI = new GroupUserAPI();
                groupUserAPI.getAllUsersInGroup(id, new GroupUserAPI.GroupUsersCallback() {
                    @Override
                    public void onUsersReceived(List<Integer> userIds) {
                        List<Lecturer> unassignedLecturers = new ArrayList<>();
                        // Duyệt qua danh sách userIds để kiểm tra sinh viên
                        for (Lecturer lecturer : lecturers) {
                            if (!unassignedLecturers.contains(lecturer)
//                                    &&
//                                    !userIds.contains(lecturer.getUserId())
                            ) {
                                unassignedLecturers.add(lecturer);
                            }
                        }
                        filteredLecturers.clear();
                        filteredLecturers.addAll(unassignedLecturers);
                        lecturerAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                // Nothing
            }

            @Override
            public void onLecturerDeleted(int lecturerId) {
                // Nothing
            }
        });
    }


     private void filterMembers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            filteredMembers.clear();
            filteredMembers.addAll(allMembers);
        } else {
            List<Student> tempFilteredList = new ArrayList<>();
            for (Student student : allMembers) {
                if (student.getFullName().toLowerCase().contains(keyword.toLowerCase())) {
                    tempFilteredList.add(student);
                }
            }
            filteredMembers.clear();
            filteredMembers.addAll(tempFilteredList);  // Cập nhật danh sách đã lọc
        }

        memberAdapter.notifyDataSetChanged();  // Cập nhật RecyclerView
    }

    private void init(){
        submitBtn = findViewById(R.id.btn_add);
        cancelBtn = findViewById(R.id.btn_cancel);
        memberRecyclerView = findViewById(R.id.memberRecyclerView);
        searchField = findViewById(R.id.searchField);
        cancelBtn.setOnClickListener(v -> finish());
        submitBtn.setOnClickListener(v -> finish());
    }
}
