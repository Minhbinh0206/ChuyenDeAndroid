package com.example.socialmediatdcproject.fragment.Student;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.MemberAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.adapter.PostApproveAdapter;
import com.example.socialmediatdcproject.adapter.PostMyselfAdapter;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupFollowedFragment extends Fragment {
    FrameLayout frameLayout;
    RecyclerView recyclerView;

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
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);

        ImageView avatarGroup = view.findViewById(R.id.image_group_user_follow);
        TextView nameGroup = view.findViewById(R.id.name_group_user_follow);
        Button memberBtn = view.findViewById(R.id.button_group_user_member);
        Button postBtn = view.findViewById(R.id.button_group_user_post);
        Button myselfBtn = view.findViewById(R.id.button_group_user_me);

        int groupId;

        changeColorButtonActive(postBtn);
        changeColorButtonNormal(memberBtn);
        changeColorButtonNormal(myselfBtn);

        Intent intent = requireActivity().getIntent();
        groupId = intent.getIntExtra("groupId", -1);

        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadPostFromFirebase(groupId, 1);

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

                if (group.isGroupDefault()) {
                    frameLayout.setVisibility(View.GONE);
                }
                else {
                    frameLayout.setVisibility(View.VISIBLE);
                    // Chuyển sang GroupFollowedFragment sau khi thêm thành công
                    Fragment searchGroupFragment = new CreateNewPostFragment();
                    FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.third_content_fragment, searchGroupFragment);
                    fragmentTransaction.commit();
                }

                nameGroup.setText(group.getGroupName());

                StudentAPI studentAPI = new StudentAPI();
                studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                    @Override
                    public void onStudentReceived(Student student) {
                        if (group.getAdminUserId() == student.getUserId()) {
                            myselfBtn.setText("Quản lý");

                            postBtn.setOnClickListener(v -> {
                                Fragment searchGroupFragment = new CreateNewPostFragment();
                                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.third_content_fragment, searchGroupFragment);
                                fragmentTransaction.commit();

                                loadPostFromFirebase(groupId, 1);

                                changeColorButtonActive(postBtn);
                                changeColorButtonNormal(memberBtn);
                                changeColorButtonNormal(myselfBtn);
                            });

                            myselfBtn.setOnClickListener(v -> {
                                if (group.isPrivate()) {
                                    Fragment searchGroupFragment = new ManagerGroupFragment();
                                    FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.third_content_fragment, searchGroupFragment);
                                    fragmentTransaction.commit();
                                }else {
                                    loadPostApproveFromFirebase(groupId, 0);
                                }

                                changeColorButtonActive(myselfBtn);
                                changeColorButtonNormal(memberBtn);
                                changeColorButtonNormal(postBtn);
                            });

                            memberBtn.setOnClickListener(v -> {
                                Fragment searchGroupFragment = new SplitFragment();
                                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.third_content_fragment, searchGroupFragment);
                                fragmentTransaction.commit();

                                loadUsersByGroupId(groupId);

                                changeColorButtonActive(memberBtn);
                                changeColorButtonNormal(myselfBtn);
                                changeColorButtonNormal(postBtn);
                            });
                        }
                        else {
                            myselfBtn.setText("Tôi");

                            postBtn.setOnClickListener(v -> {
                                loadPostFromFirebase(groupId, 1);

                                changeColorButtonActive(postBtn);
                                changeColorButtonNormal(memberBtn);
                                changeColorButtonNormal(myselfBtn);
                            });

                            myselfBtn.setOnClickListener(v -> {
                                loadPostMyselfFromFirebase(groupId);

                                changeColorButtonActive(myselfBtn);
                                changeColorButtonNormal(memberBtn);
                                changeColorButtonNormal(postBtn);
                            });

                            memberBtn.setOnClickListener(v -> {
                                loadUsersByGroupId(groupId);

                                changeColorButtonActive(memberBtn);
                                changeColorButtonNormal(myselfBtn);
                                changeColorButtonNormal(postBtn);
                            });
                        }
                    }

                    @Override
                    public void onStudentsReceived(List<Student> students) {

                    }
                });
            }

            @Override
            public void onGroupsReceived(List<Group> groups) {

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

    public void loadPostFromFirebase(int id, int status) {
        ArrayList<Post> postsList = new ArrayList<>(); // Danh sách bài viết

        // Tham chiếu đến bảng "posts" trong Firebase Realtime Database
        FirebaseDatabase.getInstance().getReference("Posts")
                .orderByChild("groupId") // Giả sử bạn có trường "groupId" để lọc bài viết theo nhóm
                .equalTo(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        postsList.clear(); // Xóa danh sách bài viết cũ

                        // Duyệt qua từng bài viết trong "posts"
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Post post = postSnapshot.getValue(Post.class);
                            if (post.getStatus() == status) {
                                postsList.add(post); // Thêm bài viết vào danh sách
                            }
                        }

                        // Cập nhật RecyclerView với dữ liệu bài viết
                        PostAdapter postAdapter = new PostAdapter(postsList, getContext());
                        recyclerView.setAdapter(postAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý lỗi nếu cần
                        Toast.makeText(requireContext(), "Failed to load posts: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void loadPostApproveFromFirebase(int id, int status) {
        ArrayList<Post> postsList = new ArrayList<>(); // Danh sách bài viết

        // Tham chiếu đến bảng "posts" trong Firebase Realtime Database
        FirebaseDatabase.getInstance().getReference("Posts")
                .orderByChild("groupId") // Giả sử bạn có trường "groupId" để lọc bài viết theo nhóm
                .equalTo(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        postsList.clear(); // Xóa danh sách bài viết cũ

                        // Duyệt qua từng bài viết trong "posts"
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Post post = postSnapshot.getValue(Post.class);
                            if (post.getStatus() == status) {
                                postsList.add(post); // Thêm bài viết vào danh sách
                            }
                        }

                        // Cập nhật RecyclerView với dữ liệu bài viết
                        PostApproveAdapter postAdapter = new PostApproveAdapter(postsList, requireContext());
                        recyclerView.setAdapter(postAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý lỗi nếu cần
                        Toast.makeText(requireContext(), "Failed to load posts: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void loadPostMyselfFromFirebase(int id) {
        ArrayList<Post> postsList = new ArrayList<>(); // Danh sách bài viết

        // Tham chiếu đến bảng "posts" trong Firebase Realtime Database
        FirebaseDatabase.getInstance().getReference("Posts")
                .orderByChild("groupId") // Giả sử bạn có trường "groupId" để lọc bài viết theo nhóm
                .equalTo(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        postsList.clear(); // Xóa danh sách bài viết cũ

                        // Duyệt qua từng bài viết trong "posts"
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Post post = postSnapshot.getValue(Post.class);
                            StudentAPI studentAPI = new StudentAPI();
                            studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                                @Override
                                public void onStudentReceived(Student student) {
                                    if (post.getUserId() == student.getUserId()) {
                                        postsList.add(post);
                                    }

                                    // Cập nhật RecyclerView với dữ liệu bài viết
                                    PostMyselfAdapter postAdapter = new PostMyselfAdapter(postsList, requireContext());
                                    recyclerView.setAdapter(postAdapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                                }

                                @Override
                                public void onStudentsReceived(List<Student> students) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý lỗi nếu cần
                        Toast.makeText(requireContext(), "Failed to load posts: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUsersByGroupId(int id) {
        ArrayList<Student> memberGroup = new ArrayList<>();
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getAllStudents(new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {

            }

            @Override
            public void onStudentsReceived(List<Student> students) {
                GroupUserAPI groupUserAPI = new GroupUserAPI();
                groupUserAPI.getAllGroupUsers(new GroupUserAPI.GroupUserCallback() {
                    @Override
                    public void onGroupUsersReceived(List<GroupUser> groupUsers) {
                        for (GroupUser gu : groupUsers) {
                            groupUserAPI.getGroupUserByIdGroup(gu.getGroupId(), new GroupUserAPI.GroupUserCallback() {
                                @Override
                                public void onGroupUsersReceived(List<GroupUser> groupUsers) {
                                    List<GroupUser> groupUserList = new ArrayList<>();
                                    for (GroupUser gu : groupUsers) {
                                        if (gu.getGroupId() == id) {
                                            groupUserList.add(gu);
                                        }
                                    }

                                    // Chỉ thêm vào memberGroup nếu chưa có
                                    for (GroupUser gus : groupUserList) {
                                        for (Student u : students) {
                                            if (u.getUserId() == gus.getUserId() && !memberGroup.contains(u)) {
                                                memberGroup.add(u);
                                            }
                                        }
                                    }

                                    // Cập nhật RecyclerView sau khi thêm tất cả member
                                    MemberAdapter memberAdapter = new MemberAdapter(memberGroup, requireContext());
                                    recyclerView.removeAllViews();
                                    recyclerView.setAdapter(memberAdapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                                }
                            });
                        }
                    }
                });
            }
        });
    }

}
