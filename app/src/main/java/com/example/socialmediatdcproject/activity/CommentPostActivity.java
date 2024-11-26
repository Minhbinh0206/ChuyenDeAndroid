package com.example.socialmediatdcproject.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.CommentAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.CommentAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Comment;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CommentPostActivity extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    private FrameLayout firstContentFragment;
    private ImageView avatarUser;
    private EditText contentComment;
    private Button submitComment;
    RecyclerView recyclerViewFirst;
    RecyclerView recyclerViewSecond;
    int userId = -1;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private int postId;
    private int groupId;
    private String currentDateAndTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_post_layout);

        recyclerViewFirst = findViewById(R.id.first_content_comment_post);
        recyclerViewSecond = findViewById(R.id.second_content_comment_post);

        avatarUser = findViewById(R.id.avatar_user_comment_create);
        contentComment = findViewById(R.id.content_user_comment_create);
        submitComment = findViewById(R.id.button_submit_comment);

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList, CommentPostActivity.this);
        recyclerViewSecond.setAdapter(commentAdapter);
        recyclerViewSecond.setLayoutManager(new LinearLayoutManager(CommentPostActivity.this));

        ImageButton iconBack = findViewById(R.id.icon_back);
        iconBack.setOnClickListener(v -> finish());

        // Nhận postId và groupId từ Intent
        Intent intent = getIntent();
        postId = intent.getIntExtra("postId", -1);
        groupId = intent.getIntExtra("groupId", -1);

        // Lấy ngày tháng hiện tại
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        currentDateAndTime = sdf.format(Calendar.getInstance().getTime());

        // Lấy thông tin bài viết và bình luận
        if (postId != -1 && groupId != -1) {
            PostAPI postAPI = new PostAPI();
            CommentAPI commentAPI = new CommentAPI();

            // Lấy bài viết
            postAPI.getPostById(groupId, postId, new PostAPI.PostCallback() {
                @Override
                public void onPostReceived(Post post) {
                    ArrayList<Post> posts = new ArrayList<>();
                    posts.add(post);
                    PostAdapter postAdapter = new PostAdapter(posts, CommentPostActivity.this);
                    recyclerViewFirst.setAdapter(postAdapter);
                    recyclerViewFirst.setLayoutManager(new LinearLayoutManager(CommentPostActivity.this));
                }

                @Override
                public void onPostsReceived(List<Post> posts) {
                    // Không cần xử lý ở đây
                }
            });

            // Lấy bình luận và thiết lập RecyclerView
            commentAPI.getCommentsByPostId(groupId, postId, new CommentAPI.CommentCallback() {
                @Override
                public void onCommentReceived(Comment comment) {
                    // Thêm từng bình luận vào danh sách khi cần
                }

                @Override
                public void onCommentsReceived(List<Comment> comments) {
                    commentList.clear();

                    // Lắng nghe các thay đổi bình luận trong Firebase theo thời gian thực
                    listenForNewComments(groupId, postId);
                }
            });
        }

        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StudentAPI studentAPI = new StudentAPI();
        userId = -1;
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
        AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();

        studentAPI.getStudentByKey(key, new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                Glide.with(CommentPostActivity.this)
                        .load(student.getAvatar())
                        .circleCrop()
                        .into(avatarUser);
                userId = student.getUserId();
            }

            @Override
            public void onStudentsReceived(List<Student> students) {
                // Không cần xử lý ở đây
            }
        });

        adminDepartmentAPI.getAdminDepartmentByKey(key, new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                Glide.with(CommentPostActivity.this)
                        .load(adminDepartment.getAvatar())
                        .circleCrop()
                        .into(avatarUser);
                userId = adminDepartment.getUserId();
            }

            @Override
            public void onUsersReceived(List<AdminDepartment> adminDepartment) {
                // Không cần xử lý ở đây
            }

            @Override
            public void onError(String s) {
                // Xử lý lỗi ở đây
            }
        });

        adminBusinessAPI.getAdminBusinessByKey(key, new AdminBusinessAPI.AdminBusinessCallBack() {
            @Override
            public void onUserReceived(AdminBusiness adminBusiness) {
                Glide.with(CommentPostActivity.this)
                        .load(adminBusiness.getAvatar())
                        .circleCrop()
                        .into(avatarUser);
                userId = adminBusiness.getUserId();
            }

            @Override
            public void onUsersReceived(List<AdminBusiness> adminBusiness) {

            }

            @Override
            public void onError(String s) {

            }
        });

        adminDefaultAPI.getAdminDefaultByKey(key, new AdminDefaultAPI.AdminDefaultCallBack() {
            @Override
            public void onUserReceived(AdminDefault adminDefault) {
                if (!adminDefault.getAdminType().equals("Super")) {
                    Glide.with(CommentPostActivity.this)
                            .load(adminDefault.getAvatar())
                            .circleCrop()
                            .into(avatarUser);
                    userId = adminDefault.getUserId();
                }
            }

            @Override
            public void onUsersReceived(List<AdminDefault> adminDefault) {

            }
        });

        // Sự kiện khi nhấn nút submit
        submitComment.setOnClickListener(v -> {
            if (!contentComment.getText().toString().isEmpty()) {
                CommentAPI commentAPI = new CommentAPI();
                commentAPI.getCommentsByPostId(groupId, postId, new CommentAPI.CommentCallback() {
                    @Override
                    public void onCommentReceived(Comment comment) {
                        // Thêm bình luận mới
                    }

                    @Override
                    public void onCommentsReceived(List<Comment> comments) {
                        // Tạo comment mới
                        Comment comment = new Comment();
                        comment.setId(comments.size());
                        comment.setPostId(postId);
                        comment.setUserId(userId);
                        comment.setGroupId(groupId);
                        comment.setContent(contentComment.getText().toString());
                        comment.setCommentLike(0);
                        comment.setCommentCreateAt(currentDateAndTime);

                        commentAPI.addComment(comment, groupId);
                        commentAdapter.notifyDataSetChanged();

                        contentComment.setText(""); // Reset text field

                        Toast.makeText(CommentPostActivity.this, "Bình luận thành công", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void listenForNewComments(int groupId, int postId) {
        // Lắng nghe các thay đổi bình luận từ Firebase
        FirebaseDatabase.getInstance().getReference("Comments")
                .child(String.valueOf(groupId))
                .child(String.valueOf(postId))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                        Comment newComment = dataSnapshot.getValue(Comment.class);
                        if (newComment != null) {
                            commentList.add(newComment); // Thêm bình luận mới vào danh sách
                            commentAdapter.notifyItemInserted(commentList.size() - 1); // Thông báo chỉ thay đổi phần tử mới
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                        // Xử lý khi bình luận bị thay đổi
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        // Xử lý khi bình luận bị xóa
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                        // Xử lý khi bình luận bị di chuyển
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý lỗi
                    }
                });
    }

    //Hàm sắp xếp
    private void sortCommentByDate(){
        Collections.sort(commentList, new Comparator<Comment>() {
            @Override
            public int compare(Comment comment1, Comment comment2) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date1 = format.parse(comment1.getCommentCreateAt());
                    Date date2 = format.parse(comment2.getCommentCreateAt());
                    return  date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;

                }
            }
        });
    }
}
