package com.example.socialmediatdcproject.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.NotifyQuicklyAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.CreateNewGroupActivity;
import com.example.socialmediatdcproject.dataModels.NotifyQuickly;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CreateNewPostFragment extends Fragment {
    int groupId;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView avatar = view.findViewById(R.id.avatar_user_create_post);

        Intent intent = requireActivity().getIntent();
        groupId = intent.getIntExtra("groupId", -1);

        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                Glide.with(getContext())
                        .load(student.getAvatar())
                        .circleCrop()
                        .into(avatar);
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

        TextView textView = view.findViewById(R.id.create_post_action);
        textView.setOnClickListener(v -> {
            showCustomDialog();
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showCustomDialog() {
        // Tạo Dialog
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.post_create_layout);

        // Tìm các view bên trong Dialog
        Button postButtonCreate = dialog.findViewById(R.id.button_post_user_create_new);
        Button postButtonCancle = dialog.findViewById(R.id.button_post_user_create_cancle);
        EditText postContent = dialog.findViewById(R.id.post_content);

        ImageButton addImage = dialog.findViewById(R.id.post_add_image);
        ImageButton changeBanckground = dialog.findViewById(R.id.post_change_background);
        ImageButton addSurvey = dialog.findViewById(R.id.post_icon_survey);

        ImageView imageViewAvatar = dialog.findViewById(R.id.avatar_user_create_post);

        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                Glide.with(getContext())
                        .load(student.getAvatar())
                        .circleCrop()
                        .into(imageViewAvatar);
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

        changeColorButtonActive(postButtonCreate);
        changeColorButtonActive(postButtonCancle);

        // Gán sự kiện cho nút "Post"
        postButtonCreate.setOnClickListener(v -> {
            String content = postContent.getText().toString();
            PostAPI postAPI = new PostAPI();
            studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                @Override
                public void onStudentReceived(Student student) {
                    postAPI.getAllPosts(new PostAPI.PostCallback() {
                        @Override
                        public void onPostReceived(Post post) {

                        }

                        @Override
                        public void onPostsReceived(List<Post> posts) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

                            NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();
                            NotifyQuickly notifyQuickly = new NotifyQuickly();
                            notifyQuicklyAPI.getAllNotifications(new NotifyQuicklyAPI.NotificationCallback() {
                                @Override
                                public void onNotificationsReceived(List<NotifyQuickly> notifications) {
                                    GroupAPI groupAPI = new GroupAPI();
                                    groupAPI.getGroupById(groupId, new GroupAPI.GroupCallback() {
                                        @Override
                                        public void onGroupReceived(Group group) {
                                            if (student.getUserId() == group.getAdminUserId()) {
                                                Post post = new Post();
                                                post.setPostId(posts.size());
                                                post.setUserId(student.getUserId());
                                                post.setPostLike(0);
                                                post.setPostImage("");
                                                post.setContent(content);
                                                post.setStatus(Post.APPROVED);
                                                post.setGroupId(groupId);
                                                post.setCreatedAt(sdf.format(new Date()));
                                                postAPI.addPost(post);

                                                Toast.makeText(requireContext(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Post post = new Post();
                                                post.setPostId(posts.size());
                                                post.setUserId(student.getUserId());
                                                post.setPostLike(0);
                                                post.setPostImage("");
                                                post.setContent(content);
                                                post.setStatus(Post.WAITING);
                                                post.setGroupId(groupId);
                                                post.setCreatedAt(sdf.format(new Date()));
                                                postAPI.addPost(post);

                                                Toast.makeText(requireContext(), "Bài đăng của bạn đang chờ phê duyệt", Toast.LENGTH_SHORT).show();

                                                notifyQuickly.setNotifyId(notifications.size());
                                                notifyQuickly.setUserSendId(student.getUserId());
                                                notifyQuickly.setUserGetId(group.getAdminUserId());
                                                notifyQuickly.setContent(student.getFullName() + " vừa đăng bài mới và đang chờ bạn duuyệt!");
                                                notifyQuicklyAPI.addNotification(notifyQuickly);
                                            }
                                        }

                                        @Override
                                        public void onGroupsReceived(List<Group> groups) {

                                        }
                                    });
                                }
                            });
                        }
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

            dialog.dismiss();
        });

        postButtonCancle.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // Cài đặt kích thước cho Dialog
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.comment_custom));

        // Thiết lập marginHorizontal 10dp cho Dialog
        int marginInDp = (int) (10 * getResources().getDisplayMetrics().density);
        if (dialog.getWindow() != null) {
            // Lấy WindowManager.LayoutParams
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.horizontalMargin = marginInDp / (float) getResources().getDisplayMetrics().widthPixels; // margin dưới dạng tỉ lệ so với chiều rộng màn hình

            dialog.getWindow().setAttributes(params);
        }

        // Hiển thị Dialog
        dialog.show();
    }

    public void changeColorButtonActive(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
    }

    public void changeColorButtonNormal(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
    }
}

