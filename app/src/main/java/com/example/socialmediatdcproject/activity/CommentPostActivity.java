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

import androidx.activity.EdgeToEdge;
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
import com.example.socialmediatdcproject.adapter.CommentAdapter;
import com.example.socialmediatdcproject.adapter.NotifyAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.database.CommentDatabase;
import com.example.socialmediatdcproject.database.NotifyDatabase;
import com.example.socialmediatdcproject.database.PostDatabase;
import com.example.socialmediatdcproject.fragment.BussinessFragment;
import com.example.socialmediatdcproject.fragment.DepartmentFragment;
import com.example.socialmediatdcproject.fragment.GroupFragment;
import com.example.socialmediatdcproject.fragment.HomeFragment;
import com.example.socialmediatdcproject.fragment.NotifyFragment;
import com.example.socialmediatdcproject.fragment.PostFragment;
import com.example.socialmediatdcproject.fragment.YouthFragment;
import com.example.socialmediatdcproject.model.Comment;
import com.example.socialmediatdcproject.model.Notify;
import com.example.socialmediatdcproject.model.Post;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class CommentPostActivity extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    private FrameLayout firstContentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_post_layout);

        RecyclerView recyclerViewFirst = findViewById(R.id.first_content_comment_post);

        // Nhận postId từ Intent
        Intent intent = getIntent();
        int postId = intent.getIntExtra("postId", -1);  // Nhận postId, giá trị mặc định là -1 nếu không có postId

        if (postId != -1) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            // Xử lý postId ở đây, ví dụ: lấy dữ liệu bài post từ cơ sở dữ liệu dựa trên postId
            PostDatabase postDatabase = new PostDatabase();
            CommentDatabase commentDatabase = new CommentDatabase();
            Post post = postDatabase.getPostById(postId);
            ArrayList<Post> posts = new ArrayList<>();
            posts.add(post);

            ArrayList<Comment> comments = commentDatabase.getCommentsByPostId(post.getPostId());
            RecyclerView recyclerViewSecond = findViewById(R.id.second_content_comment_post);

            PostAdapter postAdapter = new PostAdapter(posts);
            CommentAdapter commentAdapter = new CommentAdapter(comments);

            recyclerViewFirst.setAdapter(postAdapter);
            recyclerViewSecond.setAdapter(commentAdapter);

            // Sử dụng LayoutManager cho RecyclerView
            recyclerViewFirst.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewSecond.setLayoutManager(new LinearLayoutManager(this));
        }

        // Thiết lập iconback
        ImageButton iconBack = findViewById(R.id.icon_back);
        iconBack.setOnClickListener(v -> {
            finish();
        });
    }
}
