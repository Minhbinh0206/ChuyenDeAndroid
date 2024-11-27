package com.example.socialmediatdcproject.fragment.Student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.socialmediatdcproject.API.FilterPostsAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.PostAPI;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupFollowedFragment extends Fragment {
    FrameLayout frameLayout;
    RecyclerView recyclerView;
    ArrayList<Post> postList;
    PostAdapter postAdapter;

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
        postList = new ArrayList<>();
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        recyclerView.setVisibility(View.VISIBLE);

        ImageView avatarGroup = view.findViewById(R.id.image_group_user_follow);
        TextView nameGroup = view.findViewById(R.id.name_group_user_follow);
        Button memberBtn = view.findViewById(R.id.button_group_user_member);
        Button postBtn = view.findViewById(R.id.button_group_user_post);
        Button myselfBtn = view.findViewById(R.id.button_group_user_me);
        ImageView iconSetting = view.findViewById(R.id.settingGroup);

        int groupId;

        changeColorButtonActive(postBtn);
        changeColorButtonNormal(memberBtn);
        changeColorButtonNormal(myselfBtn);

        TextView textView = requireActivity().findViewById(R.id.null_content_notify);

        Intent intent = requireActivity().getIntent();
        groupId = intent.getIntExtra("groupId", -1);

        loadPostFromFirebase(groupId, textView);

        iconSetting.setVisibility(View.GONE);

        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getGroupById(groupId, new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {
                Glide.with(view)
                        .load(group.getAvatar())
                        .circleCrop()
                        .into(avatarGroup);

                if (group.isGroupDefault()) {
                    frameLayout.setVisibility(View.GONE);
                    iconSetting.setVisibility(View.GONE);
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
                            iconSetting.setVisibility(View.VISIBLE);

                            myselfBtn.setText("Quản lý");

                            postBtn.setOnClickListener(v -> {
                                frameLayout.setVisibility(View.VISIBLE);

                                Fragment searchGroupFragment = new CreateNewPostFragment();
                                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.third_content_fragment, searchGroupFragment);
                                fragmentTransaction.commit();

                                loadPostFromFirebase(groupId, textView);

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
                                    frameLayout.setVisibility(View.GONE);
                                }

                                changeColorButtonActive(myselfBtn);
                                changeColorButtonNormal(memberBtn);
                                changeColorButtonNormal(postBtn);
                            });

                            memberBtn.setOnClickListener(v -> {
                                frameLayout.setVisibility(View.GONE);

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
                            iconSetting.setVisibility(View.GONE);

                            myselfBtn.setText("Tôi");

                            postBtn.setOnClickListener(v -> {
                                loadPostFromFirebase(groupId, textView);

                                changeColorButtonActive(postBtn);
                                changeColorButtonNormal(memberBtn);
                                changeColorButtonNormal(myselfBtn);
                            });

                            myselfBtn.setOnClickListener(v -> {
                                loadPostMyselfFromFirebase(groupId, textView);

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

    public void loadPostFromFirebase(int id, TextView textView) {
        postList.clear();

        postAdapter = new PostAdapter(postList, requireContext());
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        if (postList.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
            textView.setText("Hiện chưa có bài viết nào");
        }
        else {
            textView.setVisibility(View.GONE);
        }

        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getGroupById(id, new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {
                DatabaseReference postReference = FirebaseDatabase.getInstance()
                        .getReference("Posts")
                        .child(String.valueOf(group.getGroupId()));

                Query postQuery = postReference.orderByChild("status").equalTo(1);

                postQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (!isAdded()) return; // Đảm bảo Fragment vẫn còn hoạt động

                        Post post = snapshot.getValue(Post.class);
                        if (post != null) {
                            handlePostAddition(post);
                            if (textView.getVisibility() == View.VISIBLE) {
                                textView.setVisibility(View.GONE); // Ẩn thông báo
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (!isAdded()) return; // Đảm bảo Fragment vẫn còn hoạt động

                        Post updatedPost = snapshot.getValue(Post.class);
                        if (updatedPost != null) {
                            handlePostUpdate(updatedPost);
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        Post removedPost = snapshot.getValue(Post.class);
                        if (removedPost != null) {
                            handlePostRemoval(removedPost);
                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        // Không cần xử lý trong trường hợp này
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("PostAPI", "Error listening to post changes: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onGroupsReceived(List<Group> groups) {
                // Không cần xử lý ở đây
            }
        });
    }

    private void handlePostAddition(Post post) {
        if (post.getStatus() == 1) {
            postList.add(0, post); // Thêm bài viết vào đầu danh sách
            postAdapter.notifyItemInserted(0); // Thông báo RecyclerView
            recyclerView.scrollToPosition(0); // Cuộn lên đầu
        }
    }

    private void handlePostUpdate(Post updatedPost) {
        if (updatedPost.getStatus() == 1) {
            postList.add(0, updatedPost); // Thêm bài viết vào đầu danh sách
            postAdapter.notifyItemInserted(0); // Thông báo RecyclerView
            recyclerView.scrollToPosition(0); // Cuộn lên đầu
        }
    }

    private void handlePostRemoval(Post removedPost) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getPostId() == removedPost.getPostId()) {
                postList.remove(i);
                postAdapter.notifyItemRemoved(i);
                break;
            }
        }
    }

    public void loadPostMyselfFromFirebase(int id, TextView textView) {
        ArrayList<Post> myPosts = new ArrayList<>();
        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getGroupById(id ,new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {
                PostAPI postAPI = new PostAPI();
                postAPI.getPostsByGroupId(group.getGroupId(), new PostAPI.PostCallback() {
                    @Override
                    public void onPostReceived(Post post) {

                    }

                    @Override
                    public void onPostsReceived(List<Post> posts) {
                        StudentAPI studentAPI = new StudentAPI();
                        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                            @Override
                            public void onStudentReceived(Student student) {
                                myPosts.clear();
                                for (Post p : posts) {
                                    if (p.getUserId() == student.getUserId()) {
                                        myPosts.add(p);
                                    }
                                }

                                if (myPosts.isEmpty()) {
                                    textView.setVisibility(View.VISIBLE);
                                    textView.setText("Bạn chưa đăng bài viết nào");
                                }
                                else {
                                    textView.setVisibility(View.GONE);
                                }

                                PostMyselfAdapter postMyselfAdapter = new PostMyselfAdapter(myPosts, requireContext());
                                recyclerView.setAdapter(postMyselfAdapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                            }

                            @Override
                            public void onStudentsReceived(List<Student> students) {

                            }
                        });
                    }
                });
            }

            @Override
            public void onGroupsReceived(List<Group> groups) {

            }
        });
    }

    public void loadUsersByGroupId(int groupId){
        ArrayList<Student> memberList = new ArrayList<>();

        // Cập nhật RecyclerView với dữ liệu bài viết
        MemberAdapter memberAdapter = new MemberAdapter(memberList, requireContext());
        recyclerView.setAdapter(memberAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        GroupUserAPI groupUserAPI = new GroupUserAPI();
        groupUserAPI.getAllUsersInGroup(groupId, new GroupUserAPI.GroupUsersCallback() {
            @Override
            public void onUsersReceived(List<Integer> userIds) {
                for (Integer i : userIds) {
                    StudentAPI studentAPI = new StudentAPI();
                    studentAPI.getStudentById(i, new StudentAPI.StudentCallback() {
                        @Override
                        public void onStudentReceived(Student student) {
                            memberList.add(student);
                            memberAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onStudentsReceived(List<Student> students) {

                        }
                    });
                }
            }
        });
    }

}
