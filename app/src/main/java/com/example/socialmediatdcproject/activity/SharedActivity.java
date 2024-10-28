package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.NotifyAPI;
import com.example.socialmediatdcproject.API.NotifyQuicklyAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.NotifyAdapter;
import com.example.socialmediatdcproject.adapter.NotifyQuicklyAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.dataModels.NotifyQuickly;
import com.example.socialmediatdcproject.fragment.BussinessFragment;
import com.example.socialmediatdcproject.fragment.DepartmentFragment;
import com.example.socialmediatdcproject.fragment.FriendsScreenFragment;
import com.example.socialmediatdcproject.fragment.GroupFollowedFragment;
import com.example.socialmediatdcproject.fragment.GroupFragment;
import com.example.socialmediatdcproject.fragment.GroupNotFollowFragment;
import com.example.socialmediatdcproject.fragment.HomeFragment;
import com.example.socialmediatdcproject.fragment.NotifyFragment;
import com.example.socialmediatdcproject.fragment.PersonalScreenFragment;
import com.example.socialmediatdcproject.fragment.TrainingFragment;
import com.example.socialmediatdcproject.fragment.YouthFragment;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Notify;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SharedActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    protected DrawerLayout drawerLayout;
    private FrameLayout firstContentFragment;
    private int currentNotifyIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shared_layout);
        NotifyAPI notifyAPI = new NotifyAPI();

        mAuth = FirebaseAuth.getInstance();

        // Thiết lập DrawerLayout và NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        firstContentFragment = findViewById(R.id.first_content_fragment); // Khởi tạo FrameLayout

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
                    recyclerView.setLayoutManager(new LinearLayoutManager(SharedActivity.this));
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

    private void loadPostsFromFirebase() {
        PostAPI postAPI = new PostAPI();  // Sử dụng PostAPI để lấy dữ liệu từ Firebase

        postAPI.getAllPosts(new PostAPI.PostCallback() {
            @Override
            public void onPostReceived(Post post) {

            }

            @Override
            public void onPostsReceived(List<Post> posts) {
                ArrayList<Post> postList = new ArrayList<>();

                for (Post p : posts) {
                    UserAPI userAPI = new UserAPI();
                    userAPI.getUserById(p.getUserId(), new UserAPI.UserCallback() {
                        @Override
                        public void onUserReceived(User user) {
                            if (user.getRoleId() == User.ROLE_PHONGDAOTAO || user.getRoleId() == User.ROLE_DOANTHANHNIEN || user.getRoleId() == User.ROLE_ADMIN_DEPARTMENT || user.getRoleId() == User.ROLE_ADMIN_BUSSINESS) {
                                postList.add(p);
                            }

                            // Setup RecyclerView với Adapter
                            RecyclerView recyclerView = findViewById(R.id.second_content_fragment);
                            PostAdapter postAdapter = new PostAdapter(postList, SharedActivity.this);
                            recyclerView.setAdapter(postAdapter);

                            // Sử dụng LayoutManager cho RecyclerView
                            recyclerView.setLayoutManager(new LinearLayoutManager(SharedActivity.this));
                        }

                        @Override
                        public void onUsersReceived(List<User> users) {

                        }
                    });

                }
            }
        });
    }

    private void addNavigationItems(NavigationView navigationView) {
        LinearLayout navLayout = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.nav_container);

        // Danh sách item để thêm vào Navigation Drawer
        int[] icons = {R.drawable.icon_home, R.drawable.icon_flag, R.drawable.icon_department, R.drawable.icon_department, R.drawable.icon_department, R.drawable.icon_group, R.drawable.icon_profile, R.drawable.icon_logout};
        String[] titles = {"Trang chủ", "Phòng đào tạo", "Khoa", "Doanh nghiệp", "Đoàn thanh niên", "Câu lạc bộ - Nhóm", "Trang cá nhân", "Đăng xuất"};

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
                        fragment = new HomeFragment();
                        loadPostsFromFirebase();
                        break;
                    case 1:
                        // Phòng đào tạo
                        fragment = new TrainingFragment();
                        break;
                    case 2:
                        // Khoa
                        fragment = new DepartmentFragment();
                        break;
                    case 3:
                        // Doanh nghiệp
                        fragment = new BussinessFragment();
                        break;
                    case 4:
                        // Đoàn thanh niên
                        fragment = new YouthFragment();
                        break;
                    case 5:
                        // Group
                        fragment = new GroupFragment();
                        break;
                    case 6:
                        // Trang cá nhân
                        fragment = new PersonalScreenFragment();
                        break;
                    case 7:
                        // Đăng xuất người dùng và chuyển đến LoginActivity
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signOut();
                        Intent intent = new Intent(SharedActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Đóng SharedActivity
                        return;  // Thoát phương thức để không thực hiện fragmentTransaction
                    default:
                        fragment = new HomeFragment();
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

    private void showNotifyQuicklyPopup(int id ,List<NotifyQuickly> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return;  // Nếu không có thông báo thì không làm gì
        }

        // Tạo View từ layout của PopupWindow
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.notify_quickly_popup, null);

        // Tạo PopupWindow
        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true); // Thiết lập true để cho phép tắt PopupWindow khi nhấn bên ngoài

        // Thiết lập RecyclerView bên trong PopupWindow
        RecyclerView recyclerView = popupView.findViewById(R.id.recycler_notify_quickly);
        NotifyQuicklyAdapter notifyQuicklyAdapter = new NotifyQuicklyAdapter(new ArrayList<>());
        recyclerView.setAdapter(notifyQuicklyAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SharedActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Hiển thị PopupWindow ở vị trí mong muốn
        View notifyButton = findViewById(R.id.icon_notify);
        popupWindow.showAsDropDown(notifyButton, 0, 0);

        // Handler để hiển thị từng thông báo tuần tự
        final Handler handler = new Handler();
        final int[] currentIndex = {0}; // Chỉ mục hiện tại của thông báo

        // Hàm để cập nhật thông báo hiện tại
        Runnable updateNotification = new Runnable() {
            @Override
            public void run() {
                if (currentIndex[0] < notifications.size()) {
                    // Cập nhật danh sách thông báo với thông báo hiện tại
                    NotifyQuickly currentNotification = notifications.get(currentIndex[0]);
                    notifyQuicklyAdapter.clearNotifications(); // Xóa thông báo cũ
                    notifyQuicklyAdapter.addNotification(currentNotification); // Thêm thông báo mới
                    notifyQuicklyAdapter.notifyDataSetChanged();

                    // Gọi API để xóa thông báo khỏi Firebase
                    NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();
                    notifyQuicklyAPI.deleteNotification(id ,currentNotification.getNotifyId()); // Gọi phương thức xóa với ID của thông báo

                    // Tăng chỉ mục lên cho thông báo tiếp theo
                    currentIndex[0]++;

                    // Gọi lại runnable sau 5 giây để hiển thị thông báo tiếp theo
                    handler.postDelayed(this, 5000); // Điều chỉnh thời gian nếu cần
                } else {
                    // Đã hiển thị hết thông báo, đóng PopupWindow
                    popupWindow.dismiss();
                }
            }
        };

        // Bắt đầu hiển thị thông báo đầu tiên sau khi popup được mở
        handler.post(updateNotification);
    }


    @Override
    protected void onResume() {
        super.onResume();
        NotifyAPI notifyAPI = new NotifyAPI();

        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();

                // Thiết lập listener để theo dõi thông báo cho người dùng hiện tại
                notifyQuicklyAPI.setNotificationListener(student.getUserId(), new NotifyQuicklyAPI.NotificationCallback() {
                    @Override
                    public void onNotificationsReceived(List<NotifyQuickly> notifications) {
                        // Hiển thị thông báo nhanh qua PopupWindow
                        showNotifyQuicklyPopup(student.getUserId(), notifications);
                    }
                });
            }

            @Override
            public void onStudentsReceived(List<Student> students) {}

            @Override
            public void onError(String errorMessage) {}

            @Override
            public void onStudentDeleted(int studentId) {}
        });

        Intent intent = getIntent();
        int key = intent.getIntExtra("keyFragment", -1);
        int studentId = intent.getIntExtra("studentId", -1);
        int groupId = intent.getIntExtra("groupId", -1);

        //Tìm kiếm hình ảnh user
        ImageView imageView = findViewById(R.id.nav_avatar_user);

        studentAPI.getStudentByKey(mAuth.getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                if (student.getAvatar() == null) {
                    ImageView imageView = findViewById(R.id.nav_avatar_user);
                    Glide.with(SharedActivity.this)
                            .load(R.drawable.avatar_macdinh)
                            .circleCrop()
                            .into(imageView);
                }else {
                    // Thiết kế giao diện cho avatar
                    ImageView imageView = findViewById(R.id.nav_avatar_user);
                    Glide.with(SharedActivity.this)
                            .load(student.getAvatar())
                            .circleCrop()
                            .into(imageView);
                }

                // Thêm sự kiện click vào avatar chuyển sang trang cá nhân
                imageView.setOnClickListener(v -> {
                    // Chuyển sang màn hình PersonalScreenFragment
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    PersonalScreenFragment personalScreenFragment = new PersonalScreenFragment();
                    fragmentTransaction.replace(R.id.first_content_fragment, personalScreenFragment);

                    fragmentTransaction.commit();
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

        Log.d("TAG", "onResume: " + key);

        if (key != -1) {
            switch (key){
                case 999:
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
                            recyclerView.setLayoutManager(new LinearLayoutManager(SharedActivity.this));
                        }

                        @Override
                        public void onError(String errorMessage) {
                            // Xử lý lỗi
                            System.err.println("Error: " + errorMessage);
                        }
                    });
                    fragmentTransaction.commit();
                    break;
                case 9999:
                    fragment = new GroupFragment();
                    fragmentManager = getSupportFragmentManager();
                    // Thay thế nội dung của FrameLayout bằng Fragment tương ứng nếu fragment không null
                    if (fragment != null) {
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.first_content_fragment, fragment);
                        fragmentTransaction.commit();
                    }
                    break;
                default:
                    // Gán fragment home là mặc định
                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();

                    // Nạp HomeFragment vào first_content_fragment
                    fragmentTransaction.replace(R.id.first_content_fragment, new HomeFragment());

                    // Lấy dữ lệu từ firebase
                    loadPostsFromFirebase();

                    fragmentTransaction.commit();
                    break;
            }
        }else {
            if (studentId != -1) {
                // Gán fragment home là mặc định
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Nạp HomeFragment vào first_content_fragment
                fragmentTransaction.replace(R.id.first_content_fragment, new FriendsScreenFragment());

                fragmentTransaction.commit();
            }
            else {
                    // Gán fragment home là mặc định
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    // Nạp HomeFragment vào first_content_fragment
                    fragmentTransaction.replace(R.id.first_content_fragment, new HomeFragment());

                    // Lấy dữ lệu từ firebase
                    loadPostsFromFirebase();

                    fragmentTransaction.commit();

            }
        }

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