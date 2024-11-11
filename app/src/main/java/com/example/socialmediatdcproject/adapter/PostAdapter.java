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
import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.BusinessAPI;
import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.LikeAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.CommentPostActivity;
import com.example.socialmediatdcproject.activity.GroupDetaiActivity;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Business;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
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
                    });

                    AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
                    adminDepartmentAPI.getAdminDepartmentById(u.getUserId(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
                        @Override
                        public void onUserReceived(AdminDepartment adminDepartment) {
                            if (adminDepartment.getUserId() == post.getUserId()) {
                                holder.postAdminUserId.setText(adminDepartment.getFullName());
                                Glide.with(context)
                                        .load(u.getAvatar())
                                        .circleCrop()
                                        .into(holder.postAvatar);
                            }
                        }

                        @Override
                        public void onUsersReceived(List<AdminDepartment> adminDepartment) {

                        }

                        @Override
                        public void onError(String s) {

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

        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentById(post.getUserId(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                holder.postAdminUserId.setText(student.getFullName());
                Glide.with(context)
                        .load(student.getAvatar())
                        .circleCrop()
                        .into(holder.postAvatar);

            }

            @Override
            public void onStudentsReceived(List<Student> students) {

            }
        });

        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        adminDepartmentAPI.getAdminDepartmentById(post.getUserId(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                DepartmentAPI departmentAPI = new DepartmentAPI();
                departmentAPI.getDepartmentById(adminDepartment.getDepartmentId(), new DepartmentAPI.DepartmentCallback() {
                    @Override
                    public void onDepartmentReceived(Department department) {
                        GroupAPI groupAPI = new GroupAPI();
                        groupAPI.getGroupById(department.getGroupId(), new GroupAPI.GroupCallback() {
                            @Override
                            public void onGroupReceived(Group group) {
                                holder.postAdminUserId.setText(group.getGroupName());
                                Glide.with(context)
                                        .load(group.getAvatar())
                                        .circleCrop()
                                        .into(holder.postAvatar);
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
            }

            @Override
            public void onUsersReceived(List<AdminDepartment> adminDepartment) {

            }

            @Override
            public void onError(String s) {

            }
        });

        AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
        adminBusinessAPI.getAdminBusinessById(post.getUserId(), new AdminBusinessAPI.AdminBusinessCallBack() {
            @Override
            public void onUserReceived(AdminBusiness adminBusiness) {
                BusinessAPI businessAPI = new BusinessAPI();
                businessAPI.getBusinessById(adminBusiness.getBusinessId(), new BusinessAPI.BusinessCallback() {
                    @Override
                    public void onBusinessReceived(Business business) {
                        GroupAPI groupAPI = new GroupAPI();
                        groupAPI.getGroupById(business.getGroupId(), new GroupAPI.GroupCallback() {
                            @Override
                            public void onGroupReceived(Group group) {
                                holder.postAdminUserId.setText(group.getGroupName());
                                Glide.with(context)
                                        .load(group.getAvatar())
                                        .circleCrop()
                                        .into(holder.postAvatar);
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

            }

            @Override
            public void onUsersReceived(List<AdminBusiness> adminBusiness) {

            }

            @Override
            public void onError(String s) {

            }
        });

        AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();
        adminDefaultAPI.getAdminDefaultById(post.getUserId(), new AdminDefaultAPI.AdminDefaultCallBack() {
            @Override
            public void onUserReceived(AdminDefault adminDefault) {
                GroupAPI groupAPI = new GroupAPI();
                groupAPI.getGroupById(adminDefault.getUserId(), new GroupAPI.GroupCallback() {
                    @Override
                    public void onGroupReceived(Group group) {
                        holder.postAdminUserId.setText(group.getGroupName());
                        Glide.with(context)
                                .load(group.getAvatar())
                                .circleCrop()
                                .into(holder.postAvatar);
                    }

                    @Override
                    public void onGroupsReceived(List<Group> groups) {

                    }
                });
            }

            @Override
            public void onUsersReceived(List<AdminDefault> adminDefault) {

            }
        });

        holder.postAdminUserId.setOnClickListener(v -> {
            GroupAPI groupAPI = new GroupAPI();
            groupAPI.getGroupById(post.getGroupId(), new GroupAPI.GroupCallback() {
                @Override
                public void onGroupReceived(Group group) {
                    Intent intent = new Intent(v.getContext(), GroupDetaiActivity.class);
                    intent.putExtra("groupId", group.getGroupId());
                    if (v.getContext() instanceof GroupDetaiActivity) {

                    }else {
                        v.getContext().startActivity(intent);
                    }
                }

                @Override
                public void onGroupsReceived(List<Group> groups) {

                }
            });
        });

        holder.postAvatar.setOnClickListener(v -> {
            GroupAPI groupAPI = new GroupAPI();
            groupAPI.getGroupById(post.getGroupId(), new GroupAPI.GroupCallback() {
                @Override
                public void onGroupReceived(Group group) {
                    Intent intent = new Intent(v.getContext(), GroupDetaiActivity.class);
                    intent.putExtra("groupId", group.getGroupId());
                    if (v.getContext() instanceof GroupDetaiActivity) {

                    }else {
                        v.getContext().startActivity(intent);
                    }
                }

                @Override
                public void onGroupsReceived(List<Group> groups) {

                }
            });
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

        if (!post.getPostImage().isEmpty()) {
            holder.postImageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(post.getPostImage())
                    .into(holder.postImageView);
        }
        else {
            holder.postImageView.setVisibility(View.GONE);
        }
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

        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        adminDepartmentAPI.getAdminDepartmentByKey(userKey, new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
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
                likeAPI.checkLikeStatus(adminDepartment.getUserId(), post.getPostId(), new LikeAPI.LikeStatusCallback() {
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
                    likeAPI.toggleLikeStatus(adminDepartment.getUserId(), post.getPostId(), new LikeAPI.LikeStatusCallback() {
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
            public void onUsersReceived(List<AdminDepartment> adminDepartment) {

            }

            @Override
            public void onError(String s) {

            }
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
        ImageView postImageView;
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
            postImageView = itemView.findViewById(R.id.post_image);
        }
    }
}
