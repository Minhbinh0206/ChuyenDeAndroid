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

//        if (postId != -1) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            // Xử lý postId ở đây, ví dụ: lấy dữ liệu bài post từ cơ sở dữ liệu dựa trên postId
//            PostDatabase postDatabase = new PostDatabase();
//            CommentDatabase commentDatabase = new CommentDatabase();
////            Post post = postDatabase.getPostById(postId);
//            ArrayList<Post> posts = new ArrayList<>();
//            posts.add(post);
//
//            ArrayList<Comment> comments = commentDatabase.getCommentsByPostId(post.getPostId());
//            RecyclerView recyclerViewSecond = findViewById(R.id.second_content_comment_post);
//
//            PostAdapter postAdapter = new PostAdapter(posts);
//            CommentAdapter commentAdapter = new CommentAdapter(comments);
//
//            recyclerViewFirst.setAdapter(postAdapter);
//            recyclerViewSecond.setAdapter(commentAdapter);
//
//            // Sử dụng LayoutManager cho RecyclerView
//            recyclerViewFirst.setLayoutManager(new LinearLayoutManager(this));
//            recyclerViewSecond.setLayoutManager(new LinearLayoutManager(this));
//        }

        // Thiết lập iconback
        ImageButton iconBack = findViewById(R.id.icon_back);
        iconBack.setOnClickListener(v -> {
            finish();
        });
    }
}













//
//package com.example.socialmediatdcproject.activity;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.socialmediatdcproject.API.CommentAPI;
//import com.example.socialmediatdcproject.API.PostAPI;
//import com.example.socialmediatdcproject.API.StudentAPI;
//import com.example.socialmediatdcproject.R;
//import com.example.socialmediatdcproject.adapter.CommentAdapter;
//import com.example.socialmediatdcproject.adapter.NotifyAdapter;
//import com.example.socialmediatdcproject.adapter.PostAdapter;
//import com.example.socialmediatdcproject.fragment.BussinessFragment;
//import com.example.socialmediatdcproject.fragment.DepartmentFragment;
//import com.example.socialmediatdcproject.fragment.GroupFragment;
//import com.example.socialmediatdcproject.fragment.HomeFragment;
//import com.example.socialmediatdcproject.fragment.NotifyFragment;
//import com.example.socialmediatdcproject.fragment.PostFragment;
//import com.example.socialmediatdcproject.fragment.YouthFragment;
//import com.example.socialmediatdcproject.model.Comment;
//import com.example.socialmediatdcproject.model.Notify;
//import com.example.socialmediatdcproject.model.Post;
//import com.example.socialmediatdcproject.model.Student;
//import com.google.android.material.navigation.NavigationView;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.sql.Date;
//import java.sql.Time;
//import java.util.ArrayList;
//import java.util.List;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Locale;
//
//public class CommentPostActivity extends AppCompatActivity {
//    protected DrawerLayout drawerLayout;
//    private FrameLayout firstContentFragment;
//    private ImageView avatarUser;
//    private EditText contentComment;
//    private Button submitComment;
//    RecyclerView recyclerViewFirst;
//    int userId = -1;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.comment_post_layout);
//
//        recyclerViewFirst = findViewById(R.id.first_content_comment_post);
//
//        // Tìm các thành phần UI
//        avatarUser = findViewById(R.id.avatar_user_comment_create);
//        contentComment = findViewById(R.id.content_user_comment_create);
//        submitComment = findViewById(R.id.button_submit_comment);
//
//        // Thiết lập iconBack
//        ImageButton iconBack = findViewById(R.id.icon_back);
//        iconBack.setOnClickListener(v -> finish());
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        // Lấy ngày tháng hiện tại
//        @SuppressLint("SimpleDateFormat")
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        String currentDateAndTime = sdf.format(Calendar.getInstance().getTime());
//
//        // Nhận postId từ Intent
//        Intent intent = getIntent();
//        int postId = intent.getIntExtra("postId", -1);  // Nhận postId, giá trị mặc định là -1 nếu không có postId
//
//        if (postId != -1) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            PostAPI postAPI = new PostAPI();
//            CommentAPI commentAPI = new CommentAPI();
//            postAPI.getPostById(postId, new PostAPI.PostCallback() {
//                @Override
//                public void onPostReceived(Post post) {
//                    ArrayList<Post> posts = new ArrayList<>();
//                    ArrayList<Comment> commentList = new ArrayList<>();
//                    posts.add(post);
//
//                    commentAPI.getCommentsByPostId(post.getPostId(), new CommentAPI.CommentCallback() {
//                        @Override
//                        public void onCommentReceived(Comment comment) {
//
//                        }
//
//                        @Override
//                        public void onCommentsReceived(List<Comment> comments) {
//                            commentList.addAll(comments);
//                            RecyclerView recyclerViewSecond = findViewById(R.id.second_content_comment_post);
//
//                            PostAdapter postAdapter = new PostAdapter(posts, CommentPostActivity.this);
//                            CommentAdapter commentAdapter = new CommentAdapter(commentList, CommentPostActivity.this);
//
//                            recyclerViewFirst.setAdapter(postAdapter);
//                            recyclerViewSecond.setAdapter(commentAdapter);
//
//                            // Sử dụng LayoutManager cho RecyclerView
//                            recyclerViewFirst.setLayoutManager(new LinearLayoutManager(CommentPostActivity.this));
//                            recyclerViewSecond.setLayoutManager(new LinearLayoutManager(CommentPostActivity.this));
//
//                            // Sự kiện khi nhấn submit
//                            submitComment.setOnClickListener(v -> {
//                                if (!contentComment.getText().toString().isEmpty()) {
//                                    DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Comments");
//                                    commentAPI.getAllComments(new CommentAPI.CommentCallback() {
//                                        @Override
//                                        public void onCommentReceived(Comment comment) {
//
//                                        }
//
//                                        @Override
//                                        public void onCommentsReceived(List<Comment> comments) {
//                                            int lastId = -1;
//                                            lastId = comments.size();
//
//                                            // Tạo id tự tăng
//                                            int newCommentId = lastId + 1;
//
//                                            // Tạo key duy nhất bằng Firebase push()
//                                            // Tạo đối tượng Comment mới với id tự tăng
//                                            Comment comment = new Comment();
//                                            comment.setId(newCommentId);
//                                            comment.setPostId(postId);
//                                            comment.setUserId(userId);
//                                            comment.setContent(contentComment.getText().toString());
//                                            comment.setCommentLike(0);
//                                            comment.setCommentCreateAt(currentDateAndTime);
//
//                                            // Thêm comment vào Firebase
//                                            commentRef.child(String.valueOf(newCommentId)).setValue(comment);
//
//                                            // Reset lại nội dung EditText sau khi gửi bình luận
//                                            contentComment.setText("");
//
//                                            // Hiển thị Toast thông báo thành công
//                                            Toast.makeText(CommentPostActivity.this, "Bình luận thành công", Toast.LENGTH_SHORT).show();
//
//                                            // Cập nhật danh sách bình luận và thông báo cho adapter
//                                            commentList.add(comment);  // Thêm comment mới vào danh sách
//                                            commentAdapter.notifyItemInserted(commentList.size() - 1);  // Thông báo cho adapter về bình luận mới
//
//                                            // Cuộn đến vị trí của bình luận mới nhất
//                                            recyclerViewSecond.scrollToPosition(commentList.size() - 1);
//                                        }
//                                    });
//
//                                }
//                            });
//                        }
//                    });
//
//                }
//
//                @Override
//                public void onPostsReceived(List<Post> posts) {
//
//                }
//            });
//        }
//
//        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        StudentAPI studentAPI = new StudentAPI();
//        studentAPI.getStudentByKey(key, new StudentAPI.StudentCallback() {
//            @Override
//            public void onStudentReceived(Student student) {
//                Glide.with(CommentPostActivity.this)
//                        .load(student.getAvatar())
//                        .circleCrop()
//                        .into(avatarUser);
//                userId = student.getUserId();
//            }
//
//            @Override
//            public void onStudentsReceived(List<Student> students) {
//
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//
//            }
//
//            @Override
//            public void onStudentDeleted(int studentId) {
//
//            }
//        });
//    }
//}

