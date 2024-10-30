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
import com.example.socialmediatdcproject.API.CommentAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.CommentAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.model.Comment;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
    int userId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_post_layout);

        recyclerViewFirst = findViewById(R.id.first_content_comment_post);

        // Tìm các thành phần UI
        avatarUser = findViewById(R.id.avatar_user_comment_create);
        contentComment = findViewById(R.id.content_user_comment_create);
        submitComment = findViewById(R.id.button_submit_comment);

        // Thiết lập iconBack
        ImageButton iconBack = findViewById(R.id.icon_back);
        iconBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Lấy ngày tháng hiện tại
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = sdf.format(Calendar.getInstance().getTime());

        // Nhận postId từ Intent
        Intent intent = getIntent();
        int postId = intent.getIntExtra("postId", -1);  // Nhận postId, giá trị mặc định là -1 nếu không có postId

        if (postId != -1) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            PostAPI postAPI = new PostAPI();
            CommentAPI commentAPI = new CommentAPI();
            postAPI.getPostById(postId, new PostAPI.PostCallback() {
                @Override
                public void onPostReceived(Post post) {
                    ArrayList<Post> posts = new ArrayList<>();
                    ArrayList<Comment> commentList = new ArrayList<>();
                    // Lắng nghe sự kiện thay đổi trong nút "Comments"
                    DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Comments");
                    commentRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            commentList.clear();  // Xóa danh sách hiện tại trước khi cập nhật
                            for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                                Comment comment = commentSnapshot.getValue(Comment.class);
                                if (comment != null && comment.getPostId() == postId) {  // Chỉ thêm bình luận có postId khớp
                                    commentList.add(comment);
                                }
                            }

                            // Cập nhật RecyclerView với comment mới nhất
                            CommentAdapter commentAdapter = new CommentAdapter(commentList, CommentPostActivity.this);
                            RecyclerView recyclerViewSecond = findViewById(R.id.second_content_comment_post);
                            recyclerViewSecond.setAdapter(commentAdapter);
                            recyclerViewSecond.setLayoutManager(new LinearLayoutManager(CommentPostActivity.this));

                            // Cuộn đến vị trí của bình luận mới nhất
                            recyclerViewSecond.scrollToPosition(commentList.size() - 1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Xử lý lỗi nếu có
                        }
                    });

                    posts.add(post);

                    commentAPI.getCommentsByPostId(post.getPostId(), new CommentAPI.CommentCallback() {
                        @Override
                        public void onCommentReceived(Comment comment) {

                        }

                        @Override
                        public void onCommentsReceived(List<Comment> comments) {
                            commentList.clear();  // Xóa danh sách hiện tại trước khi cập nhật
                            commentList.addAll(comments);
                            RecyclerView recyclerViewSecond = findViewById(R.id.second_content_comment_post);

                            PostAdapter postAdapter = new PostAdapter(posts, CommentPostActivity.this);
                            CommentAdapter commentAdapter = new CommentAdapter(commentList, CommentPostActivity.this);

                            recyclerViewFirst.setAdapter(postAdapter);
                            recyclerViewSecond.setAdapter(commentAdapter);

                            // Sử dụng LayoutManager cho RecyclerView
                            recyclerViewFirst.setLayoutManager(new LinearLayoutManager(CommentPostActivity.this));
                            recyclerViewSecond.setLayoutManager(new LinearLayoutManager(CommentPostActivity.this));

                            // Sự kiện khi nhấn submit
                            submitComment.setOnClickListener(v -> {
                                if (!contentComment.getText().toString().isEmpty()) {
                                    DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Comments");
                                    commentAPI.getAllComments(new CommentAPI.CommentCallback() {
                                        @Override
                                        public void onCommentReceived(Comment comment) {

                                        }

                                        @Override
                                        public void onCommentsReceived(List<Comment> comments) {
                                            int lastId = -1;
                                            lastId = comments.size();

                                            // Tạo id tự tăng
                                            int newCommentId = lastId + 1;

                                            // Tạo key duy nhất bằng Firebase push()
                                            // Tạo đối tượng Comment mới với id tự tăng
                                            Comment comment = new Comment();
                                            comment.setId(newCommentId);
                                            comment.setPostId(postId);
                                            comment.setUserId(userId);
                                            comment.setContent(contentComment.getText().toString());
                                            comment.setCommentLike(0);
                                            comment.setCommentCreateAt(currentDateAndTime);

                                            // Thêm comment vào Firebase
                                            commentRef.child(String.valueOf(newCommentId)).setValue(comment);

                                            // Reset lại nội dung EditText sau khi gửi bình luận
                                            contentComment.setText("");

                                            // Hiển thị Toast thông báo thành công
                                            Toast.makeText(CommentPostActivity.this, "Bình luận thành công", Toast.LENGTH_SHORT).show();

                                            // Cập nhật danh sách bình luận và thông báo cho adapter
                                            commentList.add(comment);  // Thêm comment mới vào danh sách
                                            commentAdapter.notifyItemInserted(commentList.size() - 1);  // Thông báo cho adapter về bình luận mới

                                            // Cuộn đến vị trí của bình luận mới nhất
                                            recyclerViewSecond.scrollToPosition(commentList.size() - 1);
                                        }
                                    });

                                }
                            });
                        }
                    });

                }

                @Override
                public void onPostsReceived(List<Post> posts) {

                }
            });
        }

        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StudentAPI studentAPI = new StudentAPI();
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

            }

            @Override
            public void onError(String errorMessage) {

            }

            @Override
            public void onStudentDeleted(int studentId) {

            }
        });
    }
}