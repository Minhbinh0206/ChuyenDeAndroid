package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.fragment.GroupFollowedFragment;
import com.example.socialmediatdcproject.fragment.GroupNotFollowFragment;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class GroupDetaiActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_detail_layout);

        ImageButton iconBack = findViewById(R.id.icon_back);
        iconBack.setOnClickListener(v -> {
            Intent intent = new Intent(GroupDetaiActivity.this, SharedActivity.class);
            intent.putExtra("keyFragment", 9999);
            startActivity(intent);
            finish();
        });

        Intent intent = getIntent();
        int groupId = intent.getIntExtra("groupId", -1);

        StudentAPI studentAPI = new StudentAPI();

        if (groupId != -1) {
            // Thực hiện việc lấy dữ liệu và kiểm tra người dùng có tham gia group không
            GroupUserAPI groupUserAPI = new GroupUserAPI();

            groupUserAPI.getGroupUserByIdGroup(groupId, new GroupUserAPI.GroupUserCallback() {
                @Override
                public void onGroupUsersReceived(List<GroupUser> groupUsers) {
                    String key = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    studentAPI.getStudentByKey(key, new StudentAPI.StudentCallback() {
                        @Override
                        public void onStudentReceived(Student student) {
                            boolean isJoin = intent.getBooleanExtra("isJoin", false);

                            // Kiểm tra xem user đã tham gia nhóm chưa
                            for (GroupUser gu : groupUsers) {
                                if (gu.getUserId() == student.getUserId()) {
                                    isJoin = true;
                                }
                            }

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            // Nếu user chưa tham gia, hiển thị GroupNotFollowFragment
                            if (!isJoin) {
                                fragmentTransaction.replace(R.id.first_content_fragment, new GroupNotFollowFragment());
                            } else {
                                // Nếu đã tham gia, hiển thị GroupFollowedFragment
                                fragmentTransaction.replace(R.id.first_content_fragment, new GroupFollowedFragment());
                            }

                            fragmentTransaction.commit();
                        }

                        @Override
                        public void onStudentsReceived(List<Student> students) {}

                        @Override
                        public void onError(String errorMessage) {}

                        @Override
                        public void onStudentDeleted(int studentId) {}
                    });
                }
            });
        }
    }
}
