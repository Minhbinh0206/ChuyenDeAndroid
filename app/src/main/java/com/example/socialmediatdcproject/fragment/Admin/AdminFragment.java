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
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.NotifyAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.HomeAdminActivity;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Business;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Notify;
import com.example.socialmediatdcproject.model.Post;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AdminFragment extends Fragment {
    RecyclerView recyclerView;
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

                GroupAPI groupAPI = new GroupAPI();
                DepartmentAPI departmentAPI = new DepartmentAPI();
                departmentAPI.getDepartmentById(adminDepartment.getDepartmentId(), new DepartmentAPI.DepartmentCallback() {
                    @Override
                    public void onDepartmentReceived(Department department) {
                        groupAPI.getGroupById(department.getGroupId(), new GroupAPI.GroupCallback() {
                            @Override
                            public void onGroupReceived(Group group) {
                                loadPostFromFirebase(group.getGroupId());
                            }

                            @Override
                            public void onGroupsReceived(List<Group> groups) {

                            }
                        });
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

                GroupAPI groupAPI = new GroupAPI();
                BusinessAPI businessAPI = new BusinessAPI();
                businessAPI.getBusinessById(adminBusiness.getBusinessId(), new BusinessAPI.BusinessCallback() {
                    @Override
                    public void onBusinessReceived(Business business) {
                        groupAPI.getGroupById(business.getGroupId(), new GroupAPI.GroupCallback() {
                            @Override
                            public void onGroupReceived(Group group) {
                                loadPostFromFirebase(group.getGroupId());
                            }

                            @Override
                            public void onGroupsReceived(List<Group> groups) {

                            }
                        });
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

                    GroupAPI groupAPI = new GroupAPI();
                    groupAPI.getGroupById(adminDefault.getGroupId(), new GroupAPI.GroupCallback() {
                        @Override
                        public void onGroupReceived(Group group) {
                            loadPostFromFirebase(group.getGroupId());
                        }

                        @Override
                        public void onGroupsReceived(List<Group> groups) {

                        }
                    });
                }
            }

            @Override
            public void onUsersReceived(List<AdminDefault> adminDefault) {

            }
        });
    }

    public void loadPostFromFirebase(int id) {
        ArrayList<Post> postsList = new ArrayList<>(); // Danh sách bài viết
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);

        // Tạo instance của PostAPI
        PostAPI postAPI = new PostAPI();

        // Lấy bài viết theo groupId
        postAPI.getPostByGroupId(id, new PostAPI.PostCallback() {
            @Override
            public void onPostReceived(Post post) {

            }

            @Override
            public void onPostsReceived(List<Post> posts) {
                postsList.clear();
                // Kiểm tra nếu có bài viết
                if (posts.size() > 0) {
                    for (Post p : posts) {
                        if (p != null) {
                            postsList.add(p); // Thêm bài viết vào danh sách
                        }
                    }

                    // Cập nhật RecyclerView với dữ liệu bài viết
                    PostAdapter postAdapter = new PostAdapter(postsList, getContext());
                    recyclerView.setAdapter(postAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                } else {
                    ArrayList<Post> postsList = new ArrayList<>();

                    // Cập nhật RecyclerView với dữ liệu bài viết
                    PostAdapter postAdapter = new PostAdapter(postsList, getContext());
                    recyclerView.setAdapter(postAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }
        });
    }

}
