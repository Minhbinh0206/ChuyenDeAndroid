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
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.NotifyAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.database.NotifyDatabase;
import com.example.socialmediatdcproject.database.PostDatabase;
import com.example.socialmediatdcproject.fragment.BussinessFragment;
import com.example.socialmediatdcproject.fragment.DepartmentFragment;
import com.example.socialmediatdcproject.fragment.GroupFragment;
import com.example.socialmediatdcproject.fragment.HomeFragment;
import com.example.socialmediatdcproject.fragment.NotifyFragment;
import com.example.socialmediatdcproject.fragment.YouthFragment;
import com.example.socialmediatdcproject.model.Notify;
import com.example.socialmediatdcproject.model.Post;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class SharedActivity extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    private FrameLayout firstContentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shared_layout);

        // Thiết lập DrawerLayout và NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        firstContentFragment = findViewById(R.id.first_content_fragment); // Khởi tạo FrameLayout

        // Thiết kế giao diện cho avatar
        ImageView imageView = findViewById(R.id.nav_avatar_user);
        Glide.with(this)
                .load(R.drawable.avatar_user)
                .circleCrop()
                .into(imageView);

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

            // Hiển thị tất cả thông báo
            NotifyDatabase notifyDatabase = new NotifyDatabase();
            ArrayList<Notify> notifies = notifyDatabase.dataNotifies();

            // Setup RecyclerView với Adapter
            RecyclerView recyclerView = findViewById(R.id.second_content_fragment);
            NotifyAdapter notifyAdapter = new NotifyAdapter(notifies);
            recyclerView.setAdapter(notifyAdapter);

            // Sử dụng LayoutManager cho RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            fragmentTransaction.commit();
        });

        // Thêm item vào NavigationView
        addNavigationItems(navigationView);

        // Gán fragment home là mặc định
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Nạp HomeFragment vào first_content_fragment
            fragmentTransaction.replace(R.id.first_content_fragment, new HomeFragment());

            // Giả dữ liệu các post
            PostDatabase postDatabase = new PostDatabase();
            ArrayList<Post> posts = postDatabase.dataPost();

            // Setup RecyclerView với Adapter
            RecyclerView recyclerView = findViewById(R.id.second_content_fragment);
            PostAdapter postAdapter = new PostAdapter(posts);
            recyclerView.setAdapter(postAdapter);

            // Sử dụng LayoutManager cho RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            fragmentTransaction.commit();
        }


        // Thiết lập sự kiện click cho icon_back
        ImageButton backButton = navigationView.getHeaderView(0).findViewById(R.id.icon_back);
        backButton.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

    }

    private void addNavigationItems(NavigationView navigationView) {
        LinearLayout navLayout = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.nav_container);

        // Danh sách item để thêm vào Navigation Drawer
        int[] icons = {R.drawable.icon_home, R.drawable.icon_flag, R.drawable.icon_department, R.drawable.icon_department, R.drawable.icon_department, R.drawable.icon_group, R.drawable.icon_personal};
        String[] titles = {"Trang chủ", "Phòng đào tạo", "Khoa", "Doanh nghiệp", "Đoàn thanh niên", "Câu lạc bộ - Nhóm", "Trang cá nhân"};

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
            final int index = i; // Lưu chỉ số để sử dụng trong sự kiện click
            itemView.setOnClickListener(v -> {
                Fragment fragment = null;
                switch (index) {
                    case 0:
                        // Home
                        fragment = new HomeFragment();

                        // Giả dữ liệu các post
                        PostDatabase postDatabase = new PostDatabase();
                        ArrayList<Post> posts = postDatabase.dataPost();

                        // Setup RecyclerView với Adapter
                        RecyclerView recyclerView = findViewById(R.id.second_content_fragment);
                        PostAdapter postAdapter = new PostAdapter(posts);
                        recyclerView.setAdapter(postAdapter);

                        // Sử dụng LayoutManager cho RecyclerView
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));

                        break;
                    case 1:
                        // Phòng đào tạo
                        fragment = new DepartmentFragment();
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
                    default:
                        fragment = new HomeFragment();
                        break;
                }

                // Thay thế nội dung của FrameLayout bằng Fragment tương ứng
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.first_content_fragment, fragment);
                fragmentTransaction.commit();

                // Đóng Navigation Drawer sau khi chọn mục
                drawerLayout.closeDrawer(GravityCompat.START);
            });

            navLayout.addView(itemView);
        }
    }

    // Xử lý khi người dùng nhấn nút Back
    @Override
    public void onBackPressed() {
        // Kiểm tra nếu Navigation Drawer đang mở thì đóng lại
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed(); // Nếu Drawer không mở thì thực hiện hành vi mặc định
        }
    }
}