package com.example.socialmediatdcproject.fragment.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.BusinessAPI;
import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.FilterPostsAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.NotifyAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.HomeAdminActivity;
import com.example.socialmediatdcproject.activity.SharedActivity;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Business;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Notify;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AdminFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<Post> postList;
    PostAdapter postAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.admin_department_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FrameLayout third = requireActivity().findViewById(R.id.third_content_fragment);
        third.setVisibility(View.VISIBLE);

        postList = new ArrayList<>();
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        postAdapter = new PostAdapter(postList, requireContext());
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        ImageView avatar = view.findViewById(R.id.admin_department_avatar);
        TextView name = view.findViewById(R.id.admin_department_name);
        ConstraintLayout constraintLayout = view.findViewById(R.id.admin_department_background);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.third_content_fragment, new MainFeatureFragment());
        fragmentTransaction.commit();

        AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();
        adminDepartmentAPI.getAdminDepartmentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                if (adminDepartment.getAvatar() == null) {
                    Glide.with(getContext())
                            .load(R.drawable.avatar_macdinh)
                            .circleCrop()
                            .into(avatar);
                } else {
                    // Thiết kế giao diện cho avatar
                    Glide.with(getContext())
                            .load(adminDepartment.getAvatar())
                            .circleCrop()
                            .into(avatar);
                }
                name.setText(adminDepartment.getFullName());

                DepartmentAPI departmentAPI = new DepartmentAPI();
                departmentAPI.getDepartmentById(adminDepartment.getDepartmentId(), new DepartmentAPI.DepartmentCallback() {
                    @Override
                    public void onDepartmentReceived(Department department) {
                        loadPostsFromFirebase(department.getGroupId());
                    }

                    @Override
                    public void onDepartmentsReceived(List<Department> departments) {

                    }
                });
            }

            @Override
            public void onUsersReceived(List<AdminDepartment> adminDepartment) {

            }

            @Override
            public void onError(String s) {

            }
        });
        adminBusinessAPI.getAdminBusinessByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminBusinessAPI.AdminBusinessCallBack() {
            @Override
            public void onUserReceived(AdminBusiness adminBusiness) {
                if (adminBusiness.getAvatar() == null) {
                    Glide.with(getContext())
                            .load(R.drawable.avatar_macdinh)
                            .circleCrop()
                            .into(avatar);
                } else {
                    // Thiết kế giao diện cho avatar
                    Glide.with(getContext())
                            .load(adminBusiness.getAvatar())
                            .circleCrop()
                            .into(avatar);
                }
                name.setText(adminBusiness.getFullName());

                BusinessAPI businessAPI = new BusinessAPI();
                businessAPI.getBusinessById(adminBusiness.getBusinessId(), new BusinessAPI.BusinessCallback() {
                    @Override
                    public void onBusinessReceived(Business business) {
                        loadPostsFromFirebase(business.getGroupId());

                    }

                    @Override
                    public void onBusinessesReceived(List<Business> businesses) {

                    }
                });
            }

            @Override
            public void onUsersReceived(List<AdminBusiness> adminBusiness) {

            }

            @Override
            public void onError(String s) {

            }
        });
        adminDefaultAPI.getAdminDefaultByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDefaultAPI.AdminDefaultCallBack() {
            @Override
            public void onUserReceived(AdminDefault adminDefault) {
                if (!adminDefault.getAdminType().equals("Super")) {
                    if (adminDefault.getAvatar() == null) {
                        Glide.with(getContext())
                                .load(R.drawable.avatar_macdinh)
                                .circleCrop()
                                .into(avatar);
                    } else {
                        // Thiết kế giao diện cho avatar
                        Glide.with(getContext())
                                .load(adminDefault.getAvatar())
                                .circleCrop()
                                .into(avatar);
                    }
                    name.setText(adminDefault.getFullName());

                    loadPostsFromFirebase(adminDefault.getGroupId());
                }
            }

            @Override
            public void onUsersReceived(List<AdminDefault> adminDefault) {

            }
        });
    }

    private void loadPostsFromFirebase(int groupId) {
        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getGroupById(groupId ,new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {
                DatabaseReference postReference = FirebaseDatabase.getInstance()
                        .getReference("Posts")
                        .child(String.valueOf(group.getGroupId()));

                postReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Post post = snapshot.getValue(Post.class);
                        if (post != null) {
                            handlePostAddition(post);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        // Nếu cần lắng nghe bài viết bị chỉnh sửa
                        Post updatedPost = snapshot.getValue(Post.class);
                        if (updatedPost != null) {
                            handlePostUpdate(updatedPost);
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        // Nếu cần lắng nghe bài viết bị xóa
                        Post removedPost = snapshot.getValue(Post.class);
                        if (removedPost != null) {
                            handlePostRemoval(removedPost);
                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        // Không cần thiết trong trường hợp này
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("PostAPI", "Error listening to post changes: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onGroupsReceived(List<Group> groups) {

            }
        });
    }

    private void handlePostAddition(Post post) {
        if (!post.isFilter()) {
            // Nếu bài viết không cần lọc
            postList.add(0, post); // Thêm bài viết mới vào đầu danh sách
            postAdapter.notifyItemInserted(0); // Thông báo RecyclerView rằng có phần tử mới ở đầu
            recyclerView.scrollToPosition(0); // Cuộn lên đầu để hiển thị bài viết mới
        } else {
            // Nếu bài viết cần lọc
            StudentAPI studentAPI = new StudentAPI();
            studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                @Override
                public void onStudentReceived(Student student) {
                    FilterPostsAPI filterPostsAPI = new FilterPostsAPI();
                    filterPostsAPI.findUserInReceive(post, student.getUserId(), new FilterPostsAPI.UserInReceiveCallback() {
                        @Override
                        public void onResult(boolean isFound) {
                            if (isFound) {
                                // Nếu bài viết không cần lọc
                                postList.add(0, post); // Thêm bài viết mới vào đầu danh sách
                                postAdapter.notifyItemInserted(0); // Thông báo RecyclerView rằng có phần tử mới ở đầu
                                recyclerView.scrollToPosition(0); // Cuộn lên đầu để hiển thị bài viết mới
                            }
                        }
                    });
                }

                @Override
                public void onStudentsReceived(List<Student> students) {
                }
            });
        }
    }

    private void handlePostUpdate(Post updatedPost) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getPostId() == updatedPost.getPostId()) {
                postList.set(i, updatedPost);
                postAdapter.notifyItemChanged(i);
                break;
            }
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

}
