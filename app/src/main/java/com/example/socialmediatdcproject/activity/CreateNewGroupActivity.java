package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.FileObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.fragment.GroupFollowedFragment;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class CreateNewGroupActivity extends AppCompatActivity {
    private int adminUserId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.create_new_group_layout);

        Button cancleAction = findViewById(R.id.cancle_create);
        Button submitAction = findViewById(R.id.submit_create);
        EditText fieldNameGroup = findViewById(R.id.content_name_group);
        Switch fieldIsPrivate = findViewById(R.id.content_private_group);

        // Lấy user
        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(key, new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                adminUserId = student.getUserId();
            }

            @Override
            public void onStudentsReceived(List<Student> students) {

            }

            @Override
            public void onError(String errorMessage) {

            }

            @Override
            public void onStudentDeleted(int studentId) {

            }
        });

        // Hủy tạo nhóm
        cancleAction.setOnClickListener(v -> {
            finish();
        });

        boolean isJoin = false;

        // Xác nhận tạo nhóm
        submitAction.setOnClickListener(v -> {
            String nameGroup = fieldNameGroup.getText().toString();
            String avatar = "";
            boolean isPrivate = fieldIsPrivate.isChecked();


            GroupAPI groupAPI = new GroupAPI();
            Group g = new Group();

            GroupUserAPI groupUserAPI = new GroupUserAPI();
            GroupUser groupUser = new GroupUser();


            // Lấy danh sách nhóm để lấy groupId mới
            groupAPI.getAllGroups(new GroupAPI.GroupCallback() {
                @Override
                public void onGroupReceived(Group group) {}

                @Override
                public void onGroupsReceived(List<Group> groups) {
                    int lastId = groups.size();
                    g.setGroupId(lastId);
                    g.setGroupName(nameGroup);
                    g.setAdminUserId(adminUserId);
                    g.setPrivate(isPrivate);
                    g.setAvatar(avatar);

                    // Thêm nhóm vào cơ sở dữ liệu
                    groupAPI.addGroup(g);

                    groupUser.setUserId(adminUserId);
                    groupUser.setGroupId(g.getGroupId());
                    groupUserAPI.addGroupUser(groupUser);

                    boolean isJoin = true;

                    // Chuyển sang GroupDetailActivity với groupId
                    Intent intent = new Intent(CreateNewGroupActivity.this, GroupDetailActivity.class);
                    intent.putExtra("groupId", g.getGroupId());
                    intent.putExtra("isJoin", isJoin);
                    startActivity(intent);
                }
            });
        });
    }
}
