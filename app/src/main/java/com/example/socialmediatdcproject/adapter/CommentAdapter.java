package com.example.socialmediatdcproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.CommentAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.LikeAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Comment;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentsList;
    private Context context;

    // Constructor
    public CommentAdapter(List<Comment> commentList, Context context) {
        this.commentsList = commentList;
        this.context = context;
        sortCommentByDate();
    }

    //Hàm sắp xếp
    private void sortCommentByDate(){
        Collections.sort(commentsList, new Comparator<Comment>() {
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

    // Tạo ViewHolder
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder holder, int position) {
        Comment comment = commentsList.get(position);
        StudentAPI studentAPI = new StudentAPI();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        if (comment != null) {

            // Gán dữ liệu cho các view
            studentAPI.getAllStudents(new StudentAPI.StudentCallback() {
                @Override
                public void onStudentReceived(Student student) {}

                @Override
                public void onStudentsReceived(List<Student> students) {
                    for (Student s: students) {
                        if (s.getUserId() == comment.getUserId()){
                            holder.commentUserId.setText(s.getFullName());
                            Glide.with(context)
                                    .load(s.getAvatar())
                                    .circleCrop()
                                    .into(holder.avatar);
                        }
                    }
                }
            });

            adminDepartmentAPI.getAllAdminDepartments(new AdminDepartmentAPI.AdminDepartmentCallBack() {
                @Override
                public void onUserReceived(AdminDepartment adminDepartment) {}

                @Override
                public void onUsersReceived(List<AdminDepartment> adminDepartments) {
                    for (AdminDepartment adminDepartment: adminDepartments) {
                        if (adminDepartment.getUserId() == comment.getUserId()){
                            holder.commentUserId.setText(adminDepartment.getFullName());
                            Glide.with(context)
                                    .load(adminDepartment.getAvatar())
                                    .circleCrop()
                                    .into(holder.avatar);
                        }
                    }
                }

                @Override
                public void onError(String s) {}
            });

            holder.commentLike.setText(String.valueOf(comment.getCommentLike()));
            holder.commentContent.setText(comment.getContent());

            // Gán dữ liệu hiển thị thời gian cho comment
            holder.commentCreateAt.setText(getTimeAgo(comment.getCommentCreateAt()));

            //imageViewHolder.postCreateAt.setText(getTimeAgo(post.getCreatedAt())); // Hiển thị thời gian đăng bài

            Log.d("comment time create", "onBindViewHolder: ");

            setupLikeButton(holder, comment);

        } else {
            Log.e("CommentAdapter", "User at position " + position + " is null");
        }
    }

    // Hàm tính thời gian hiển thị bài viết
    private String getTimeAgo(String createdAt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date createdDate = sdf.parse(createdAt);
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

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    private void setupLikeButton(CommentViewHolder holder, Comment comment) {
        String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StudentAPI studentAPI = new StudentAPI();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();

        studentAPI.getStudentByKey(userKey, new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                LikeAPI likeAPI = new LikeAPI();

                GroupAPI groupAPI = new GroupAPI();

                // Lắng nghe thay đổi số lượt thích theo thời gian thực
                likeAPI.listenForLikeCountChangesComment(comment ,new LikeAPI.LikeCountCallback() {
                    @Override
                    public void onLikeCountUpdated(long newLikeCount) {
                        comment.setCommentLike((int) newLikeCount);  // Cập nhật số lượt thích
                        holder.commentLike.setText(String.valueOf(comment.getCommentLike()));  // Cập nhật giao diện TextView hiển thị số lượt thích
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("CommentAdapter", "Error listening for like count changes: " + errorMessage);
                    }
                });

                // Kiểm tra trạng thái like cho comment hiện tại
                likeAPI.checkLikeComment(student.getUserId(), comment, new LikeAPI.LikeStatusCallback() {
                    @Override
                    public void onStatusChecked(boolean isLiked) {
                        // Cập nhật giao diện dựa trên trạng thái like
                        holder.commentLikeImage.setBackground(isLiked
                                ? context.getResources().getDrawable(R.drawable.icon_tym_red)
                                : context.getResources().getDrawable(R.drawable.icon_tym));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("CommentAdapter", "Error checking like status: " + errorMessage);
                    }
                });

                // Sự kiện khi nhấn vào icon like
                holder.commentLikeImage.setOnClickListener(v -> {
                    likeAPI.toggleLikeComment(student.getUserId(), comment, new LikeAPI.LikeStatusCallback() {
                        @Override
                        public void onStatusChecked(boolean isLiked) {
                            // Cập nhật số lượt thích
                            if (isLiked) {
                                comment.setCommentLike(comment.getCommentLike() + 1);
                            } else {
                                comment.setCommentLike(comment.getCommentLike() - 1);
                            }

                            // Cập nhật lại giao diện
                            holder.commentLikeImage.setBackground(isLiked
                                    ? context.getResources().getDrawable(R.drawable.icon_tym_red)
                                    : context.getResources().getDrawable(R.drawable.icon_tym));
                            holder.commentLike.setText(String.valueOf(comment.getCommentLike()));
                           // holder.commentCreateAt.setText(getTimeAgo(comment.getCommentCreateAt()));

                            // Cập nhật comment trong cơ sở dữ liệu
                            CommentAPI commentAPI = new CommentAPI();
                            commentAPI.updateComment(comment, comment.getGroupId());
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("CommentAdapter", "Error toggling like status: " + errorMessage);
                        }
                    });
                });
            }

            @Override
            public void onStudentsReceived(List<Student> students) {}
        });

        adminDepartmentAPI.getAdminDepartmentByKey(userKey, new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                LikeAPI likeAPI = new LikeAPI();

                // Lắng nghe thay đổi số lượt thích theo thời gian thực
                likeAPI.listenForLikeCountChangesComment(comment, new LikeAPI.LikeCountCallback() {
                    @Override
                    public void onLikeCountUpdated(long newLikeCount) {
                        comment.setCommentLike((int) newLikeCount);  // Cập nhật số lượt thích
                        holder.commentLike.setText(String.valueOf(comment.getCommentLike()));  // Cập nhật giao diện TextView hiển thị số lượt thích
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("CommentAdapter", "Error listening for like count changes: " + errorMessage);
                    }
                });

                // Kiểm tra trạng thái like cho comment hiện tại
                likeAPI.checkLikeComment(adminDepartment.getUserId(), comment, new LikeAPI.LikeStatusCallback() {
                    @Override
                    public void onStatusChecked(boolean isLiked) {
                        // Cập nhật giao diện dựa trên trạng thái like
                        holder.commentLikeImage.setBackground(isLiked
                                ? context.getResources().getDrawable(R.drawable.icon_tym_red)
                                : context.getResources().getDrawable(R.drawable.icon_tym));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("CommentAdapter", "Error checking like status: " + errorMessage);
                    }
                });

                // Sự kiện khi nhấn vào icon like
                holder.commentLikeImage.setOnClickListener(v -> {
                    likeAPI.toggleLikeComment(adminDepartment.getUserId(), comment, new LikeAPI.LikeStatusCallback() {
                        @Override
                        public void onStatusChecked(boolean isLiked) {
                            // Cập nhật số lượt thích
                            if (isLiked) {
                                comment.setCommentLike(comment.getCommentLike() + 1);
                            } else {
                                comment.setCommentLike(comment.getCommentLike() - 1);
                            }

                            // Cập nhật lại giao diện
                            holder.commentLikeImage.setBackground(isLiked
                                    ? context.getResources().getDrawable(R.drawable.icon_tym_red)
                                    : context.getResources().getDrawable(R.drawable.icon_tym));
                            holder.commentLike.setText(String.valueOf(comment.getCommentLike()));

                            // Cập nhật comment trong cơ sở dữ liệu
                            CommentAPI commentAPI = new CommentAPI();
                            commentAPI.updateComment(comment , comment.getGroupId());
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("CommentAdapter", "Error toggling like status: " + errorMessage);
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

    // Lớp ViewHolder
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentUserId;
        TextView commentContent;
        TextView commentLike;
        ImageView avatar;
        ImageView commentLikeImage;
        TextView commentCreateAt;


        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentUserId = itemView.findViewById(R.id.comment_user_name);
            commentContent = itemView.findViewById(R.id.comment_content);
            commentLike = itemView.findViewById(R.id.comment_like);
            avatar = itemView.findViewById(R.id.comment_avatar);
            commentLikeImage = itemView.findViewById(R.id.comment_like_image);
            commentCreateAt = itemView.findViewById(R.id.comment_create_at);

        }

    }


}
