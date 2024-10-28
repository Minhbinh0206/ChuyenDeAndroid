package com.example.socialmediatdcproject.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.LikeAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.CommentPostActivity;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_TEXT = 0;  // Kiểu view cho post không có ảnh
    private static final int VIEW_TYPE_IMAGE = 1; // Kiểu view cho post có ảnh

    private ArrayList<Post> postList;
    private Context context;

    // Constructor
    public PostAdapter(ArrayList<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
        sortPostsByDate();
    }

    // Hàm sắp xếp danh sách bài viết mới nhất lên đầu
    private void sortPostsByDate() {
        Collections.sort(postList, new Comparator<Post>() {
            @Override
            public int compare(Post post1, Post post2) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                try {
                    Date date1 = format.parse(post1.getCreatedAt());
                    Date date2 = format.parse(post2.getCreatedAt());
                    return date2.compareTo(date1); // Sắp xếp giảm dần
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }


    @Override
    public int getItemViewType(int position) {
        Post post = postList.get(position);
        return (post.getPostImage() != null) ? VIEW_TYPE_IMAGE : VIEW_TYPE_TEXT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_IMAGE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_post_image, parent, false);
            return new PostImageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_post, parent, false);
            return new PostTextViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = postList.get(position);
        UserAPI userAPI = new UserAPI();

        if (holder.getItemViewType() == VIEW_TYPE_IMAGE) {
            PostImageViewHolder imageViewHolder = (PostImageViewHolder) holder;
            setupPostView(imageViewHolder, post, userAPI);
            imageViewHolder.postCreateAt.setText(getTimeAgo(post.getCreatedAt())); // Hiển thị thời gian đăng bài

        } else {
            PostTextViewHolder textViewHolder = (PostTextViewHolder) holder;
            setupPostView(textViewHolder, post, userAPI);
            textViewHolder.postCreateAt.setText(getTimeAgo(post.getCreatedAt())); // Hiển thị thời gian đăng bài
        }
    }
    // Hàm tính thời gian "trước" để hiển thị như Facebook
    private String getTimeAgo(String createdAt) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Điều chỉnh định dạng cho đúng với chuỗi thời gian của bạn
        try {
            Date createdDate = format.parse(createdAt);
            Date currentDate = new Date();

            long diffInMillis = currentDate.getTime() - createdDate.getTime();
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
            long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
            long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            if (minutes == 0) {
                return "Vừa xong";
            } else if (minutes < 60) {
                return minutes + " phút trước";
            } else if (hours < 24) {
                return hours + " giờ trước";
            } else {
                return days + " ngày trước";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void setupPostView(PostTextViewHolder holder, Post post, UserAPI userAPI) {
        holder.postcontent.setText(post.getContent());
        holder.postLike.setText(String.valueOf(post.getPostLike()));
        userAPI.getAllUsers(new UserAPI.UserCallback() {
            @Override
            public void onUserReceived(User user) {}

            @Override
            public void onUsersReceived(List<User> users) {
                for (User u : users) {
                    StudentAPI studentAPI = new StudentAPI();
                    studentAPI.getStudentById(u.getUserId(), new StudentAPI.StudentCallback() {
                        @Override
                        public void onStudentReceived(Student student) {
                            if (student.getUserId() == post.getUserId()) {
                                holder.postAdminUserId.setText(student.getFullName());
                                Glide.with(context)
                                        .load(u.getAvatar())
                                        .circleCrop()
                                        .into(holder.postAvatar);
                            }
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
        });

        setupLikeButton(holder, post);
        holder.buttonComment.setOnClickListener(v -> {

        });
    }

    private void setupPostView(PostImageViewHolder holder, Post post, UserAPI userAPI) {
        holder.postcontent.setText(post.getContent());
        holder.postLike.setText(String.valueOf(post.getPostLike()));
        userAPI.getAllUsers(new UserAPI.UserCallback() {
            @Override
            public void onUserReceived(User user) {}

            @Override
            public void onUsersReceived(List<User> users) {
                for (User u : users) {
                    StudentAPI studentAPI = new StudentAPI();
                    final boolean[] isStudent = {false};
                    studentAPI.getStudentById(u.getUserId(), new StudentAPI.StudentCallback() {
                        @Override
                        public void onStudentReceived(Student student) {
                            isStudent[0] = true;
                            if (student.getUserId() == post.getUserId()) {
                                holder.postAdminUserId.setText(student.getFullName());
                                Glide.with(context)
                                        .load(student.getAvatar())
                                        .circleCrop()
                                        .into(holder.postAvatar);
                            }
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
                    if (isStudent[0] == false) {
                        if (u.getUserId() == post.getUserId()) {
                            holder.postAdminUserId.setText(u.getFullName());
                            Glide.with(context)
                                    .load(u.getAvatar())
                                    .circleCrop()
                                    .into(holder.postAvatar);
                        }
                    }
                }
            }
        });

        setupLikeButton(holder, post);
        holder.buttonComment.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CommentPostActivity.class);
            intent.putExtra("postId", post.getPostId());

            if (v.getContext() instanceof CommentPostActivity) {
                //
            }
            else {
                v.getContext().startActivity(intent);
            }
        });
        // Cài đặt hiển thị cho số lượng bình luận realtime
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comments");
        commentsRef.orderByChild("postId").equalTo(post.getPostId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Cập nhật số lượng bình luận theo postId
                long commentCount = snapshot.getChildrenCount();
                holder.textViewComent.setText(String.valueOf(commentCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CommentListener", "Failed to read comments count.", error.toException());
            }
        });
    }

    private void setupLikeButton(PostTextViewHolder holder, Post post) {
        String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(userKey, new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                LikeAPI likeAPI = new LikeAPI();

                // Lắng nghe sự thay đổi trong số lượt thích theo thời gian thực
                likeAPI.listenForLikeCountChanges(post.getPostId(), new LikeAPI.LikeCountCallback() {
                    @Override
                    public void onLikeCountUpdated(long newLikeCount) {
                        post.setPostLike((int) newLikeCount);  // Cập nhật số lượt thích
                        holder.postLike.setText(String.valueOf(post.getPostLike()));  // Cập nhật TextView hiển thị số lượt thích
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("TAG", "Error listening for like count changes: " + errorMessage);
                    }
                });

                // Kiểm tra trạng thái thích của người dùng hiện tại
                likeAPI.checkLikeStatus(student.getUserId(), post.getPostId(), new LikeAPI.LikeStatusCallback() {
                    @Override
                    public void onStatusChecked(boolean isLiked) {
                        holder.buttonLike.setBackgroundColor(isLiked ? context.getResources().getColor(R.color.like) : context.getResources().getColor(R.color.white));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("TAG", "Error checking like status: " + errorMessage);
                    }
                });

                // Toggle like status khi người dùng bấm vào nút Like
                holder.buttonLike.setOnClickListener(v -> {
                    likeAPI.toggleLikeStatus(student.getUserId(), post.getPostId(), new LikeAPI.LikeStatusCallback() {
                        @Override
                        public void onStatusChecked(boolean isLiked) {
                            // Cập nhật trạng thái nút Like
                            holder.buttonLike.setBackgroundColor(isLiked ? context.getResources().getColor(R.color.like) : context.getResources().getColor(R.color.white));
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("TAG", "Error toggling like status: " + errorMessage);
                        }
                    });
                });
            }

            @Override
            public void onStudentsReceived(List<Student> students) {}

            @Override
            public void onError(String errorMessage) {}

            @Override
            public void onStudentDeleted(int studentId) {}
        });
    }


    private void setupLikeButton(PostImageViewHolder holder, Post post) {
        String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(userKey, new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                LikeAPI likeAPI = new LikeAPI();

                // Lắng nghe sự thay đổi của số lượt thích theo thời gian thực
                likeAPI.listenForLikeCountChanges(post.getPostId(), new LikeAPI.LikeCountCallback() {
                    @Override
                    public void onLikeCountUpdated(long newLikeCount) {
                        post.setPostLike((int) newLikeCount);  // Cập nhật số lượt thích trong model
                        holder.postLike.setText(String.valueOf(post.getPostLike()));  // Cập nhật TextView hiển thị số lượt thích
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("TAG", "Error listening for like count changes: " + errorMessage);
                    }
                });

                // Kiểm tra trạng thái thích của người dùng hiện tại
                likeAPI.checkLikeStatus(student.getUserId(), post.getPostId(), new LikeAPI.LikeStatusCallback() {
                    @Override
                    public void onStatusChecked(boolean isLiked) {
                        // Cập nhật giao diện nút Like
                        holder.postLike.setTextColor(isLiked ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.black));
                        holder.buttonLike.setBackground(isLiked ? context.getResources().getDrawable(R.drawable.button_custom_liked) : context.getResources().getDrawable(R.drawable.button_custom));
                        holder.imageButtonLike.setBackgroundColor(isLiked ? context.getResources().getColor(R.color.like) : context.getResources().getColor(R.color.white));
                        holder.imageButtonLike.setImageResource(isLiked ? R.drawable.icon_tym_like : R.drawable.icon_tym);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("TAG", "Error checking like status: " + errorMessage);
                    }
                });

                // Toggle trạng thái thích khi nhấn nút Like
                holder.buttonLike.setOnClickListener(v -> {
                    likeAPI.toggleLikeStatus(student.getUserId(), post.getPostId(), new LikeAPI.LikeStatusCallback() {
                        @Override
                        public void onStatusChecked(boolean isLiked) {
                            // Cập nhật giao diện nút Like và TextView
                            if (isLiked) {
                                post.setPostLike(post.getPostLike() + 1);  // Tăng số lượt thích
                            } else {
                                post.setPostLike(post.getPostLike() - 1);  // Giảm số lượt thích
                            }

                            holder.postLike.setText(String.valueOf(post.getPostLike()));  // Cập nhật số lượt thích hiển thị
                            holder.postLike.setTextColor(isLiked ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.black));
                            holder.buttonLike.setBackground(isLiked ? context.getResources().getDrawable(R.drawable.button_custom_liked) : context.getResources().getDrawable(R.drawable.button_custom));
                            holder.imageButtonLike.setBackgroundColor(isLiked ? context.getResources().getColor(R.color.like) : context.getResources().getColor(R.color.white));
                            holder.imageButtonLike.setImageResource(isLiked ? R.drawable.icon_tym_like : R.drawable.icon_tym);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("TAG", "Error toggling like status: " + errorMessage);
                        }
                    });
                });
            }

            @Override
            public void onStudentsReceived(List<Student> students) {}

            @Override
            public void onError(String errorMessage) {}

            @Override
            public void onStudentDeleted(int studentId) {}
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostTextViewHolder extends RecyclerView.ViewHolder {
        TextView postcontent;
        TextView postAdminUserId;
        LinearLayout buttonComment;
        TextView postLike;
        ImageView postAvatar;
        LinearLayout buttonLike;
        ImageButton imageButtonLike;
        TextView postCreateAt; // Thêm TextView cho thời gian đăng

        public PostTextViewHolder(View itemView) {
            super(itemView);
            postcontent = itemView.findViewById(R.id.post_content);
            postAdminUserId = itemView.findViewById(R.id.post_name);
            buttonComment = itemView.findViewById(R.id.button_comment);
            buttonLike = itemView.findViewById(R.id.button_like);
            postLike = itemView.findViewById(R.id.post_like);
            postAvatar = itemView.findViewById(R.id.post_avatar);
            imageButtonLike = itemView.findViewById(R.id.like_button_image);
            postCreateAt = itemView.findViewById(R.id.post_create_at); // Ánh xạ ID cho thời gian đăng
        }
    }

    public static class PostImageViewHolder extends RecyclerView.ViewHolder {
        TextView postcontent;
        TextView postAdminUserId;
        LinearLayout buttonComment;
        TextView postLike;
        ImageView postAvatar;
        LinearLayout buttonLike;
        TextView textViewComent;
        ImageButton imageButtonLike;
        TextView postCreateAt; // Thêm TextView cho thời gian đăng

        public PostImageViewHolder(View itemView) {
            super(itemView);
            postcontent = itemView.findViewById(R.id.post_content);
            postAdminUserId = itemView.findViewById(R.id.post_name);
            buttonComment = itemView.findViewById(R.id.button_comment);
            buttonLike = itemView.findViewById(R.id.button_like);
            postLike = itemView.findViewById(R.id.post_like);
            postAvatar = itemView.findViewById(R.id.post_avatar);
            textViewComent = itemView.findViewById(R.id.post_comment);
            imageButtonLike = itemView.findViewById(R.id.like_button_image);
            postCreateAt = itemView.findViewById(R.id.post_create_at); // Ánh xạ ID cho thời gian đăng
        }
    }
}
