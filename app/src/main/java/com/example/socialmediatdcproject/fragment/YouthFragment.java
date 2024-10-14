package com.example.socialmediatdcproject.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.MemberAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class YouthFragment extends Fragment {
    private RecyclerView recyclerView; // RecyclerView để hiển thị danh sách người dùng

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.fragment_youth_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo danh sách người dùng và RecyclerView
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment); // Giả sử bạn đã thêm RecyclerView này trong layout

        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getGroupById(User.ID_ADMIN_DOANTHANHNIEN, new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {
                if (group != null) {
                    TextView nameTraining = getView().findViewById(R.id.name_department);
                    ImageView avatarTraining = getView().findViewById(R.id.logo_department);
                    nameTraining.setText(group.getGroupName());

                    Glide.with(requireContext())
                            .load(group.getAvatar())
                            .into(avatarTraining);
                }
            }

            @Override
            public void onGroupsReceived(List<Group> groups) {

            }
        });

                loadPostFromFirebase(User.ID_ADMIN_DOANTHANHNIEN);

        // Tìm các nút
        Button infoButton = view.findViewById(R.id.button_youth_info);
        Button postButton = view.findViewById(R.id.button_youth_post);
        Button memberButton = view.findViewById(R.id.button_youth_member);
        Button groupButton = view.findViewById(R.id.button_youth_group);

        // Set màu mặc định cho nút "Bài viết"
        postButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        postButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));

        // Sự kiện khi nhấn vào nút memberButton
        memberButton.setOnClickListener(v -> {
            loadUsersByGroupId(User.ID_ADMIN_DOANTHANHNIEN);

            // Cập nhật màu cho các nút
            changeColorButtonActive(memberButton);
            changeColorButtonNormal(infoButton);
            changeColorButtonNormal(postButton);
            changeColorButtonNormal(groupButton);
        });

        // Sự kiện khi nhấn vào nút postButton
        postButton.setOnClickListener(v -> {
            loadPostFromFirebase(User.ID_ADMIN_DOANTHANHNIEN);

            // Cập nhật màu cho các nút
            changeColorButtonActive(postButton);
            changeColorButtonNormal(infoButton);
            changeColorButtonNormal(memberButton);
            changeColorButtonNormal(groupButton);
        });
    }

    public void loadPostFromFirebase(int id) {
        ArrayList<Post> postsList = new ArrayList<>();

        // Tạo instance của PostAPI
        PostAPI postAPI = new PostAPI();

        // Lấy bài viết theo groupId
        postAPI.getPostByGroupId(id, new PostAPI.PostCallback() {
            @Override
            public void onPostReceived(Post post) {

            }

            @Override
            public void onPostsReceived(List<Post> posts) {
                // Kiểm tra nếu có bài viết
                for (Post p : posts) {
                    if (p != null) {
                        postsList.add(p); // Thêm bài viết vào danh sách
                    }
                }

                // Cập nhật RecyclerView với dữ liệu bài viết
                PostAdapter postAdapter = new PostAdapter(postsList, requireContext());
                recyclerView.setAdapter(postAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

            }
        });
    }


    private void loadUsersByGroupId(int id) {
        ArrayList<User> memberGroup = new ArrayList<>();
        UserAPI userAPI = new UserAPI();
        userAPI.getAllUsers(new UserAPI.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                // Không cần làm gì ở đây nếu không sử dụng
            }

            @Override
            public void onUsersReceived(List<User> users) {
                GroupUserAPI groupUserAPI = new GroupUserAPI();
                groupUserAPI.getAllGroupUsers(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<GroupUser> groupList = new ArrayList<>();

                        for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                            GroupUser groupUser = groupSnapshot.getValue(GroupUser.class);
                            if (groupUser != null) {
                                groupList.add(groupUser);
                            }
                        }

                        Log.d("Group", "Total groups: " + groupList.size());

                        for (GroupUser gu : groupList) {
                            groupUserAPI.getGroupUserByIdGroup(gu.getGroupId(), new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    List<GroupUser> groupUserList = new ArrayList<>();
                                    for (DataSnapshot groupUserSnapshot : snapshot.getChildren()) {
                                        GroupUser groupUser = groupUserSnapshot.getValue(GroupUser.class);
                                        if (groupUser.getGroupId() == id) {
                                            groupUserList.add(groupUser);
                                        }
                                    }

                                    // Chỉ thêm vào memberGroup nếu chưa có
                                    for (GroupUser gus : groupUserList) {
                                        for (User u : users) {
                                            if (u.getUserId() == gus.getUserId() && !memberGroup.contains(u)) {
                                                memberGroup.add(u);
                                            }
                                        }
                                    }

                                    // Cập nhật RecyclerView sau khi thêm tất cả member
                                    MemberAdapter memberAdapter = new MemberAdapter(memberGroup);
                                    recyclerView.removeAllViews();
                                    recyclerView.setAdapter(memberAdapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("Group", "Error: " + error.getMessage());
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Group", "Error: " + error.getMessage());
                    }
                });
            }
        });
    }

    public void changeColorButtonActive(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
    }

    public void changeColorButtonNormal(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
    }
}
