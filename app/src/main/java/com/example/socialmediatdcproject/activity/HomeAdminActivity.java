package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.NotifyAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.NotifyAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.fragment.Admin.AdminDepartmentFragment;
import com.example.socialmediatdcproject.fragment.Admin.AdminDepartmentMemberFragment;
import com.example.socialmediatdcproject.fragment.Admin.MainFeatureFragment;
import com.example.socialmediatdcproject.fragment.Student.NotifyFragment;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Notify;
import com.example.socialmediatdcproject.model.Post;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class HomeAdminActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    protected DrawerLayout drawerLayout;
    private int currentNotifyIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_layout_home);
        NotifyAPI notifyAPI = new NotifyAPI();

        mAuth = FirebaseAuth.getInstance();

        // Thiết lập DrawerLayout và NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Xử lý sự kiện nhấn vào icon 3 gạch để mở Navigation Drawer
        ImageButton navigationButton = findViewById(R.id.icon_navigation);
        navigationButton.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // Xử lý sự kiện nhấn vào icon thông báo
        ImageButton notifyButton = findViewById(R.id.icon_notify);
        notifyButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = new NotifyFragment();

            // Thay thế nội dung của FrameLayout bằng Fragment tương ứng
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.first_content_fragment, fragment);

            notifyAPI.getNotifications(new NotifyAPI.NotificationCallback() {
                @Override
                public void onNotificationsReceived(List<Notify> notifications) {
                    // Xử lý danh sách thông báo
                    // Setup RecyclerView với Adapter
                    RecyclerView recyclerView = findViewById(R.id.second_content_fragment);
                    NotifyAdapter notifyAdapter = new NotifyAdapter(notifications);
                    recyclerView.setAdapter(notifyAdapter);

                    // Sử dụng LayoutManager cho RecyclerView
                    recyclerView.setLayoutManager(new LinearLayoutManager(HomeAdminActivity.this));
                }

                @Override
                public void onError(String errorMessage) {
                    // Xử lý lỗi
                    System.err.println("Error: " + errorMessage);
                }
            });
            fragmentTransaction.commit();
        });

        // Thêm item vào NavigationView
        addNavigationItems(navigationView);

        // Thiết lập sự kiện click cho icon_back
        ImageButton backButton = navigationView.getHeaderView(0).findViewById(R.id.icon_back);
        backButton.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

    }

    public void loadPostFromFirebase(int id) {
        ArrayList<Post> postsList = new ArrayList<>(); // Danh sách bài viết
        RecyclerView recyclerView = findViewById(R.id.second_content_fragment);

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
                    PostAdapter postAdapter = new PostAdapter(postsList, HomeAdminActivity.this);
                    recyclerView.setAdapter(postAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(HomeAdminActivity.this));
                } else {
                    ArrayList<Post> postsList = new ArrayList<>();

                    // Cập nhật RecyclerView với dữ liệu bài viết
                    PostAdapter postAdapter = new PostAdapter(postsList, HomeAdminActivity.this);
                    recyclerView.setAdapter(postAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(HomeAdminActivity.this));
                }
            }
        });
    }

    private void addNavigationItems(NavigationView navigationView) {
        LinearLayout navLayout = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.nav_container);

        // Danh sách item để thêm vào Navigation Drawer
        int[] icons = {R.drawable.icon_home, R.drawable.icon_group, R.drawable.icon_flag, R.drawable.icon_setting, R.drawable.icon_logout};
        String[] titles = {"Trang chủ", "Thành viên", "Nhóm", "Cài đặt", "Đăng xuất"};

        // Khởi tạo FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Duyệt qua danh sách và thêm từng item
        for (int i = 0; i < icons.length; i++) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.nav_item_layout, navLayout, false);
            ImageView icon = itemView.findViewById(R.id.icon);
            TextView title = itemView.findViewById(R.id.title);
            icon.setImageResource(icons[i]);
            title.setText(titles[i]);

            // Thêm sự kiện click cho mỗi item
            final int index = i;
            itemView.setOnClickListener(v -> {
                Fragment fragment = null;
                switch (index) {
                    case 0:
                        // Home
                        fragment = new AdminDepartmentFragment();
                        break;
                    case 1:
                        // Member
                        fragment = new AdminDepartmentMemberFragment();
                        break;
                    case 2:
                        // Group
                        break;
                    case 3:
                        // Setting
                        break;
                    case 4:
                        // Đăng xuất người dùng và chuyển đến LoginActivity
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signOut();
                        Intent intent = new Intent(HomeAdminActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Đóng SharedActivity
                        return;  // Thoát phương thức để không thực hiện fragmentTransaction
                    default:
                        fragment = new AdminDepartmentFragment();
                        break;
                }

                // Thay thế nội dung của FrameLayout bằng Fragment tương ứng nếu fragment không null
                if (fragment != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.first_content_fragment, fragment);
                    fragmentTransaction.commit();
                }

                // Đóng Navigation Drawer sau khi chọn mục
                drawerLayout.closeDrawer(GravityCompat.START);
            });


            navLayout.addView(itemView);
        }
    }

    // Xử lý khi người dùng nhấn nút Back
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Lấy danh sách các fragment trong back stack
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack(); // Quay lại fragment trước đó
        } else {
            super.onBackPressed(); // Nếu không còn fragment nào trong back stack, thực hiện hành vi mặc định
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotifyAPI notifyAPI = new NotifyAPI();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();

        Intent intent = getIntent();

        adminDepartmentAPI.getAdminDepartmentByKey(mAuth.getCurrentUser().getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                if (adminDepartment.getAvatar() == null) {
                    ImageView imageView = findViewById(R.id.nav_avatar_user);
                    Glide.with(HomeAdminActivity.this)
                            .load(R.drawable.avatar_macdinh)
                            .circleCrop()
                            .into(imageView);
                }else {
                    // Thiết kế giao diện cho avatar
                    ImageView imageView = findViewById(R.id.nav_avatar_user);
                    Glide.with(HomeAdminActivity.this)
                            .load(adminDepartment.getAvatar())
                            .circleCrop()
                            .into(imageView);
                }

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

        // Gán fragment home là mặc định
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Nạp HomeFragment vào first_content_fragment
        fragmentTransaction.replace(R.id.first_content_fragment, new AdminDepartmentFragment());
        fragmentTransaction.replace(R.id.third_content_fragment, new MainFeatureFragment());

        // Lấy dữ lệu từ firebase
        fragmentTransaction.commit();

        notifyAPI.getNotificationsByReadStatus(0, new NotifyAPI.NotificationCallback() {
            @Override
            public void onNotificationsReceived(List<Notify> notifications) {
                TextView countNotify = findViewById(R.id.count_notify);
                countNotify.setText(notifications.size() + "");

                if (notifications.size() == 0) {
                    countNotify.setVisibility(View.GONE);
                }
                else {
                    countNotify.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

}