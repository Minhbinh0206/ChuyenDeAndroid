package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.BusinessAPI;
import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.FilterNotifyAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.NotifyAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.NotifyAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.fragment.Admin.AdminDepartmentMemberFragment;
import com.example.socialmediatdcproject.fragment.Admin.AdminFragment;
import com.example.socialmediatdcproject.fragment.Admin.AdminGroupFragment;
import com.example.socialmediatdcproject.fragment.Admin.MainFeatureFragment;
import com.example.socialmediatdcproject.fragment.Admin.RepairButtonFragment;
import com.example.socialmediatdcproject.fragment.Student.NotifyFragment;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Business;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Notify;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
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
                        fragment = new AdminFragment();
                        break;
                    case 1:
                        // Member
                        fragment = new AdminDepartmentMemberFragment();
                        break;
                    case 2:
                        // Group
                        fragment = new AdminGroupFragment();
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
                        fragment = new AdminFragment();
                        break;
                }

                // Thay thế nội dung của FrameLayout bằng Fragment tương ứng nếu fragment không null
                if (fragment != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.first_content_fragment, fragment);
                    fragmentTransaction.addToBackStack(null);
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
        AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
        AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();

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
                } else {
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

                notifyAPI.getAllNotifications(new NotifyAPI.NotificationCallback() {
                    @Override
                    public void onNotificationReceived(Notify notify) {}

                    @Override
                    public void onNotificationsReceived(List<Notify> notifications) {
                        ArrayList<Notify> notifyList = new ArrayList<>();
                        int[] processedPostsCount = {0};  // Biến đếm số bài viết đã xử lý
                        int totalNotificationsCount = notifications.size(); // Tổng số thông báo cần xử lý

                        Log.d("NotifyAPI", "Number of notifications received: " + notifications.size());

                        for (Notify n : notifications) {
                            int count = 0;
                            processNotification(n, adminDepartment.getUserId(), notifyList, processedPostsCount, totalNotificationsCount);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("NotifyAPI", "Error fetching notifications: " + errorMessage);
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

        adminBusinessAPI.getAdminBusinessByKey(mAuth.getCurrentUser().getUid(), new AdminBusinessAPI.AdminBusinessCallBack() {
            @Override
            public void onUserReceived(AdminBusiness adminBusiness) {
                if (adminBusiness.getAvatar() == null) {
                    ImageView imageView = findViewById(R.id.nav_avatar_user);
                    Glide.with(HomeAdminActivity.this)
                            .load(R.drawable.avatar_macdinh)
                            .circleCrop()
                            .into(imageView);
                } else {
                    // Thiết kế giao diện cho avatar
                    ImageView imageView = findViewById(R.id.nav_avatar_user);
                    Glide.with(HomeAdminActivity.this)
                            .load(adminBusiness.getAvatar())
                            .circleCrop()
                            .into(imageView);
                }

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

                notifyAPI.getAllNotifications(new NotifyAPI.NotificationCallback() {
                    @Override
                    public void onNotificationReceived(Notify notify) {}

                    @Override
                    public void onNotificationsReceived(List<Notify> notifications) {
                        if (notifications.size() != 0) {
                            ArrayList<Notify> notifyList = new ArrayList<>();
                            int[] processedPostsCount = {0};  // Biến đếm số bài viết đã xử lý
                            int totalNotificationsCount = notifications.size(); // Tổng số thông báo cần xử lý

                            Log.d("NotifyAPI", "Number of notifications received: " + notifications.size());

                            for (Notify n : notifications) {
                                processNotification(n, adminBusiness.getUserId(), notifyList, processedPostsCount, totalNotificationsCount);
                            }
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("NotifyAPI", "Error fetching notifications: " + errorMessage);
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

        adminDefaultAPI.getAdminDefaultByKey(mAuth.getCurrentUser().getUid(), new AdminDefaultAPI.AdminDefaultCallBack() {
            @Override
            public void onUserReceived(AdminDefault adminDefault) {
                if (!adminDefault.getAdminType().equals("Super")) {
                    if (adminDefault.getAvatar() == null) {
                        ImageView imageView = findViewById(R.id.nav_avatar_user);
                        Glide.with(HomeAdminActivity.this)
                                .load(R.drawable.avatar_macdinh)
                                .circleCrop()
                                .into(imageView);
                    } else {
                        // Thiết kế giao diện cho avatar
                        ImageView imageView = findViewById(R.id.nav_avatar_user);
                        Glide.with(HomeAdminActivity.this)
                                .load(adminDefault.getAvatar())
                                .circleCrop()
                                .into(imageView);
                    }

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

                    notifyAPI.getAllNotifications(new NotifyAPI.NotificationCallback() {
                        @Override
                        public void onNotificationReceived(Notify notify) {}

                        @Override
                        public void onNotificationsReceived(List<Notify> notifications) {
                            ArrayList<Notify> notifyList = new ArrayList<>();
                            int[] processedPostsCount = {0};  // Biến đếm số bài viết đã xử lý
                            int totalNotificationsCount = notifications.size(); // Tổng số thông báo cần xử lý

                            Log.d("NotifyAPI", "Number of notifications received: " + notifications.size());

                            for (Notify n : notifications) {
                                int count = 0;
                                processNotification(n, adminDefault.getUserId(), notifyList, processedPostsCount, totalNotificationsCount);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("NotifyAPI", "Error fetching notifications: " + errorMessage);
                        }
                    });
                }
            }

            @Override
            public void onUsersReceived(List<AdminDefault> adminDefault) {

            }
        });

        // Gán fragment home là mặc định
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Nạp HomeFragment vào first_content_fragment
        fragmentTransaction.replace(R.id.first_content_fragment, new AdminFragment());
        fragmentTransaction.replace(R.id.third_content_fragment, new MainFeatureFragment());

        // Lấy dữ lệu từ firebase
        fragmentTransaction.commit();
    }

    private void processNotification(Notify n, int id, ArrayList<Notify> notifyList, int[] processedPostsCount, int totalNotificationsCount) {
        processedPostsCount[0]++;

        // Nếu thông báo không có bộ lọc, trực tiếp thêm vào notifyList
        if (!n.isFilter()) {
            // Không thêm thông báo không lọc
        } else {
            // Nếu có bộ lọc, kiểm tra qua FilterNotifyAPI
            FilterNotifyAPI filterNotifyAPI = new FilterNotifyAPI();
            filterNotifyAPI.findUserInReceive(n.getNotifyId(), id, new FilterNotifyAPI.UserInReceiveCallback() {
                @Override
                public void onResult(boolean isFound) {
                    if (isFound) {
                        notifyList.add(n);
                        Log.d("True", "Added filtered notify to filteredNotify.");
                    }

                    // Kiểm tra nếu đã xử lý xong tất cả thông báo
                    if (processedPostsCount[0] == totalNotificationsCount) {
                        setCountNotify(notifyList, id);
                    }
                }
            });

            return;  // Đảm bảo không gọi setCountNotify ngay sau khi thêm thông báo lọc
        }

        // Nếu không có bộ lọc, gọi setCountNotify khi tất cả đã được xử lý
        if (processedPostsCount[0] == totalNotificationsCount) {
            setCountNotify(notifyList, id);
        }
    }

    private void setCountNotify(ArrayList<Notify> notifyList, int id) {
        if (notifyList != null && !notifyList.isEmpty()) {
            final int[] countNotify = {0};  // Biến để đếm số lượng thông báo chưa đọc
            final int[] totalCallbacks = {0};  // Biến đếm số lượng callback đã thực thi
            final int[] totalNotifications = {notifyList.size()};  // Tổng số thông báo cần kiểm tra

            NotifyAPI notifyAPI = new NotifyAPI();
            for (Notify n : notifyList) {
                // Gọi API để kiểm tra xem người dùng đã đọc thông báo này chưa
                notifyAPI.checkIfUserHasRead(n.getNotifyId(), id, new NotifyAPI.CheckReadStatusCallback() {
                    @Override
                    public void onResult(boolean hasRead) {
                        if (!hasRead) {
                            countNotify[0]++;  // Tăng đếm nếu thông báo chưa được đọc
                        }

                        // Tăng biến đếm callback đã thực thi
                        totalCallbacks[0]++;

                        // Kiểm tra nếu tất cả callback đã được gọi
                        if (totalCallbacks[0] == totalNotifications[0]) {
                            // Cập nhật giao diện người dùng trong UI thread
                            runOnUiThread(() -> {
                                TextView textView = findViewById(R.id.count_notify);
                                textView.setText(String.valueOf(countNotify[0]));  // Cập nhật số lượng thông báo chưa đọc

                                // Nếu không có thông báo chưa đọc, ẩn TextView
                                if (countNotify[0] == 0) {
                                    textView.setVisibility(View.GONE);
                                } else {
                                    textView.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }
                });
            }
        }
    }

}