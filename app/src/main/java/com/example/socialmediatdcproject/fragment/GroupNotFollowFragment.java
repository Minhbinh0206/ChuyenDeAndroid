package com.example.socialmediatdcproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GroupNotFollowFragment extends Fragment {
    FrameLayout frameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_user_fit_tdc, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        frameLayout = requireActivity().findViewById(R.id.third_content_fragment);
        frameLayout.setVisibility(view.GONE);

        ImageView avatarGroup = view.findViewById(R.id.logo_group_user_folow);
        TextView nameGroup = view.findViewById(R.id.name_group_user_folow);

        Button button = view.findViewById(R.id.button_group_user_folow);

        int groupId;

        Intent intent = requireActivity().getIntent();
        groupId = intent.getIntExtra("groupId", -1);

        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();

        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getGroupById(groupId, new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {
                if (group.getAvatar() != null) {
                    Glide.with(view)
                            .load(group.getAvatar())
                            .circleCrop()
                            .into(avatarGroup);
                }
                else {

                }

                nameGroup.setText(group.getGroupName());
            }

            @Override
            public void onGroupsReceived(List<Group> groups) {

            }
        });

        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(key, new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                button.setOnClickListener(v -> {
                    GroupUserAPI groupUserAPI = new GroupUserAPI();

                    groupUserAPI.getAllGroupUsers(new GroupUserAPI.GroupUserCallback() {
                        @Override
                        public void onGroupUsersReceived(List<GroupUser> groupUsers) {
                            int lastId = -1;

                            lastId = groupUsers.size();

                            GroupUser groupUser = new GroupUser();
                            groupUser.setGroupId(groupId);
                            groupUser.setUserId(student.getUserId());

                            groupUserAPI.addGroupUser(groupUser, lastId);

                            // Chuyển sang GroupFollowedFragment sau khi thêm thành công
                            Fragment followedFragment = new GroupFollowedFragment();
                            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.first_content_fragment, followedFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    });
                });
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


    }
}
