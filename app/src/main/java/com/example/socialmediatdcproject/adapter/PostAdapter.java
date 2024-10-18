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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_TEXT = 0;  // Kiểu view cho post không có ảnh
    private static final int VIEW_TYPE_IMAGE = 1; // Kiểu view cho post có ảnh

    private ArrayList<Post> postList;
    private Context context;

    // Constructor
    public PostAdapter(ArrayList<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
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

        } else {
            PostTextViewHolder textViewHolder = (PostTextViewHolder) holder;
            setupPostView(textViewHolder, post, userAPI);

        }
    }

    private void setupPostView(PostTextViewHolder holder, Post post, UserAPI userAPI) {
        holder.postcontent.setText(post.getContent());
        holder.postLike.setText(String.valueOf(post.getPostLike()));
        holder.postComment.setText(String.valueOf(post.getPostComment()));

        userAPI.getAllUsers(new UserAPI.UserCallback() {
            @Override
            public void onUserReceived(User user) {}

            @Override
            public void onUsersReceived(List<User> users) {
                for (User u : users) {
                    if (u.getUserId() == post.getUserId()) {
                        holder.postAdminUserId.setText(u.getFullName());
                        Glide.with(context)
                                .load(u.getAvatar())
                                .circleCrop()
                                .into(holder.postAvatar);
                    }
                }
            }
        });

        setupLikeButton(holder, post);
        holder.buttonComment.setOnClickListener(v -> {
            if (v.getContext() instanceof CommentPostActivity) {
                // Không làm gì
            }
            else {
                Intent intent = new Intent(v.getContext(), CommentPostActivity.class);
                intent.putExtra("postId", post.getPostId());
                v.getContext().startActivity(intent);
            }
        });
    }

    private void setupPostView(PostImageViewHolder holder, Post post, UserAPI userAPI) {
        holder.postcontent.setText(post.getContent());
        holder.postLike.setText(String.valueOf(post.getPostLike()));
        holder.postComment.setText(String.valueOf(post.getPostComment()));

        userAPI.getAllUsers(new UserAPI.UserCallback() {
            @Override
            public void onUserReceived(User user) {}

            @Override
            public void onUsersReceived(List<User> users) {
                for (User u : users) {
                    if (u.getUserId() == post.getUserId()) {
                        holder.postAdminUserId.setText(u.getFullName());
                        Glide.with(context)
                                .load(u.getAvatar())
                                .circleCrop()
                                .into(holder.postAvatar);
                    }
                }
            }
        });

        setupLikeButton(holder, post);
        holder.buttonComment.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CommentPostActivity.class);
            intent.putExtra("postId", post.getPostId());
            v.getContext().startActivity(intent);
        });
    }

    private void setupLikeButton(PostTextViewHolder holder, Post post) {
        String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(userKey, new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {

                LikeAPI likeAPI = new LikeAPI();

                // Kiểm tra trạng thái thích
                likeAPI.checkLikeStatus(student.getUserId(), post.getPostId(), new LikeAPI.LikeStatusCallback() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onStatusChecked(boolean isLiked) {
                        holder.buttonLike.setBackgroundColor(isLiked ? context.getResources().getColor(R.color.like) : context.getResources().getColor(R.color.white));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("TAG", "Error checking like status: " + errorMessage);
                    }
                });

                holder.buttonLike.setOnClickListener(v -> {
                    likeAPI.toggleLikeStatus(student.getUserId(), post.getPostId(), new LikeAPI.LikeStatusCallback() {
                        @Override
                        public void onStatusChecked(boolean isLiked) {
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

    private void setupLikeButton(PostImageViewHolder holder, Post post) {
        String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(userKey, new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                LikeAPI likeAPI = new LikeAPI();

                // Lắng nghe sự thay đổi trong số lượt thích
                likeAPI.listenForLikeCountChanges(post.getPostId(), new LikeAPI.LikeCountCallback() {
                    @Override
                    public void onLikeCountUpdated(long newLikeCount) {
                        post.setPostLike((int) newLikeCount);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("TAG", "Error listening for like count changes: " + errorMessage);
                    }
                });

                // Kiểm tra trạng thái thích
                likeAPI.checkLikeStatus(student.getUserId(), post.getPostId(), new LikeAPI.LikeStatusCallback() {
                    @Override
                    public void onStatusChecked(boolean isLiked) {
                        // Cập nhật màu sắc nút thích dựa trên trạng thái thích
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

                holder.buttonLike.setOnClickListener(v -> {
                    likeAPI.toggleLikeStatus(student.getUserId(), post.getPostId(), new LikeAPI.LikeStatusCallback() {
                        @Override
                        public void onStatusChecked(boolean isLiked) {
                            // Cập nhật giao diện
                            if (isLiked) {
                                post.setPostLike(post.getPostLike() + 1); // Tăng lượt thích
                            } else {
                                post.setPostLike(post.getPostLike() - 1); // Giảm lượt thích
                            }

                            holder.postLike.setText(String.valueOf(post.getPostLike()));
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
        TextView postComment;
        ImageView postAvatar;
        LinearLayout buttonLike;
        ImageButton imageButtonLike;

        public PostTextViewHolder(View itemView) {
            super(itemView);
            postcontent = itemView.findViewById(R.id.post_content);
            postAdminUserId = itemView.findViewById(R.id.post_name);
            buttonComment = itemView.findViewById(R.id.button_comment);
            buttonLike = itemView.findViewById(R.id.button_like);
            postLike = itemView.findViewById(R.id.post_like);
            postComment = itemView.findViewById(R.id.post_comment);
            postAvatar = itemView.findViewById(R.id.post_avatar);
            imageButtonLike = itemView.findViewById(R.id.like_button_image);
        }
    }

    public static class PostImageViewHolder extends RecyclerView.ViewHolder {
        TextView postcontent;
        TextView postAdminUserId;
        LinearLayout buttonComment;
        TextView postLike;
        TextView postComment;
        ImageView postAvatar;
        LinearLayout buttonLike;
        ImageButton imageButtonLike;

        public PostImageViewHolder(View itemView) {
            super(itemView);
            postcontent = itemView.findViewById(R.id.post_content);
            postAdminUserId = itemView.findViewById(R.id.post_name);
            buttonComment = itemView.findViewById(R.id.button_comment);
            buttonLike = itemView.findViewById(R.id.button_like);
            postLike = itemView.findViewById(R.id.post_like);
            postComment = itemView.findViewById(R.id.post_comment);
            postAvatar = itemView.findViewById(R.id.post_avatar);
            imageButtonLike = itemView.findViewById(R.id.like_button_image);
        }
    }
}
