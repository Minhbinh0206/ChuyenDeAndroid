package com.example.socialmediatdcproject.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.LikeAPI;
import com.example.socialmediatdcproject.API.NotifyQuicklyAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.CommentPostActivity;
import com.example.socialmediatdcproject.dataModels.NotifyQuickly;
import com.example.socialmediatdcproject.model.Group;
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

public class PostApproveAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_TEXT = 0;  // Kiểu view cho post không có ảnh
    private static final int VIEW_TYPE_IMAGE = 1; // Kiểu view cho post có ảnh

    private ArrayList<Post> postList;
    private Context context;

    // Constructor
    public PostApproveAdapter(ArrayList<Post> postList, Context context) {
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_waiting_approve_layout, parent, false);
        return new PostImageViewHolder(view);

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
                    });

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
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                v.getContext().startActivity(intent);
            }
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

        holder.buttonApprove.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.defaultBlue));
        holder.buttonApprove.setTextColor(ContextCompat.getColorStateList(context, R.color.white));

        holder.buttonApprove.setOnClickListener(v -> {
            post.setStatus(1);

            PostAPI postAPI = new PostAPI();
            postAPI.updatePost(post);

            NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();
            NotifyQuickly notifyQuickly = new NotifyQuickly();
            notifyQuicklyAPI.getAllNotifications(new NotifyQuicklyAPI.NotificationCallback() {
                @Override
                public void onNotificationsReceived(List<NotifyQuickly> notifications) {
                    GroupAPI groupAPI = new GroupAPI();
                    groupAPI.getGroupById(post.getGroupId(), new GroupAPI.GroupCallback() {
                        @Override
                        public void onGroupReceived(Group group) {
                            notifyQuickly.setNotifyId(notifications.size());
                            notifyQuickly.setUserGetId(post.getUserId());
                            notifyQuickly.setUserSendId(group.getAdminUserId());
                            notifyQuickly.setContent("Bài đăng trong " + group.getGroupName() + " của bạn đã được duyệt, vào xem ngay!");
                        }

                        @Override
                        public void onGroupsReceived(List<Group> groups) {

                        }
                    });
                }
            });

            Toast.makeText(context, "Đã duyệt bài viết", Toast.LENGTH_SHORT).show();
        });

        holder.buttonClose.setOnClickListener(v -> {
            post.setStatus(2);

            PostAPI postAPI = new PostAPI();
            postAPI.updatePost(post);

            NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();
            NotifyQuickly notifyQuickly = new NotifyQuickly();
            notifyQuicklyAPI.getAllNotifications(new NotifyQuicklyAPI.NotificationCallback() {
                @Override
                public void onNotificationsReceived(List<NotifyQuickly> notifications) {
                    GroupAPI groupAPI = new GroupAPI();
                    groupAPI.getGroupById(post.getGroupId(), new GroupAPI.GroupCallback() {
                        @Override
                        public void onGroupReceived(Group group) {
                            notifyQuickly.setNotifyId(notifications.size());
                            notifyQuickly.setUserGetId(post.getUserId());
                            notifyQuickly.setUserSendId(group.getAdminUserId());
                            notifyQuickly.setContent("Bài viết của bạn trong nhóm " + group.getGroupName() + " không được duyệt, vui lòng xem lại!");
                        }

                        @Override
                        public void onGroupsReceived(List<Group> groups) {

                        }
                    });
                }
            });

            Toast.makeText(context, "Bài viết đã được gỡ", Toast.LENGTH_SHORT).show();
        });

        holder.imageButtonLike.setBackground(ContextCompat.getDrawable(context, R.drawable.icon_tym));
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

        public PostTextViewHolder(View itemView) {
            super(itemView);
            postcontent = itemView.findViewById(R.id.post_content);
            postAdminUserId = itemView.findViewById(R.id.post_name);
            buttonComment = itemView.findViewById(R.id.button_comment);
            buttonLike = itemView.findViewById(R.id.button_like);
            postLike = itemView.findViewById(R.id.post_like);
            postAvatar = itemView.findViewById(R.id.post_avatar);
            imageButtonLike = itemView.findViewById(R.id.like_button_image);
        }
    }

    public static class PostImageViewHolder extends RecyclerView.ViewHolder {
        TextView postcontent;
        TextView postAdminUserId;
        LinearLayout buttonComment;
        TextView postLike;
        ImageView postAvatar;
        LinearLayout buttonLike;
        ImageButton imageButtonLike;
        ImageButton buttonClose;
        Button buttonApprove;

        public PostImageViewHolder(View itemView) {
            super(itemView);
            postcontent = itemView.findViewById(R.id.post_content);
            postAdminUserId = itemView.findViewById(R.id.post_name);
            buttonComment = itemView.findViewById(R.id.button_comment);
            buttonLike = itemView.findViewById(R.id.button_like);
            postLike = itemView.findViewById(R.id.post_like);
            postAvatar = itemView.findViewById(R.id.post_avatar);
            imageButtonLike = itemView.findViewById(R.id.like_button_image);
            buttonClose = itemView.findViewById(R.id.close_post);
            buttonApprove = itemView.findViewById(R.id.button_approve_create_post);
        }
    }
}
