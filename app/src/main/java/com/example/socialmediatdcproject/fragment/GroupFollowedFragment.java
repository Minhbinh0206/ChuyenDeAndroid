package com.example.socialmediatdcproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class GroupFollowedFragment extends Fragment {
    FrameLayout frameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_user_fit_tdc_folow, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        frameLayout = requireActivity().findViewById(R.id.third_content_fragment);
        frameLayout.setVisibility(view.GONE);

        ImageView avatarGroup = view.findViewById(R.id.logo_group_user);
        TextView nameGroup = view.findViewById(R.id.name_group_user);
        ImageButton addPostButton = view.findViewById(R.id.imageButton_group_user_newpost);
        Button member = view.findViewById(R.id.button_group_user_member);
        Button post = view.findViewById(R.id.button_group_user_post);
        Button myself = view.findViewById(R.id.button_group_user_me);

        int groupId;

        Intent intent = requireActivity().getIntent();
        groupId = intent.getIntExtra("groupId", -1);

        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();

        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getGroupById(groupId, new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {
                if (group.getAvatar().isEmpty()) {
                    Glide.with(view)
                            .load(R.drawable.avatar_group_default)
                            .circleCrop()
                            .into(avatarGroup);
                }
                else {
                    Glide.with(view)
                            .load(group.getAvatar())
                            .circleCrop()
                            .into(avatarGroup);
                }


                nameGroup.setText(group.getGroupName());

                addPostButton.setOnClickListener(v -> {

                });


            }

            @Override
            public void onGroupsReceived(List<Group> groups) {

            }
        });
    }
}
