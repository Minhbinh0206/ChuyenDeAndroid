package com.example.socialmediatdcproject.fragment.Admin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.NotifyQuicklyAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R; // Import đúng package chứa R
import com.example.socialmediatdcproject.dataModels.NotifyQuickly;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainFeatureFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for this fragment
        setRetainInstance(true);
        return inflater.inflate(R.layout.admin_fragment_notify_and_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button postBtn = view.findViewById(R.id.admin_create_post);
        Button noticeBtn = view.findViewById(R.id.admin_create_notify);

        changeColorButtonNormal(postBtn);
        changeColorButtonNormal(noticeBtn);

        postBtn.setOnClickListener(v -> {
            showCustomDialog();
        });

        noticeBtn.setOnClickListener(v -> {

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

        changeColorButtonNormal(postButtonCancle);
        changeColorButtonNormal(postButtonCreate);

        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        adminDepartmentAPI.getAdminDepartmentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
                    @Override
                    public void onUserReceived(AdminDepartment adminDepartment) {
                        Glide.with(getContext())
                                .load(adminDepartment.getAvatar())
                                .circleCrop()
                                .into(imageViewAvatar);
                    }

                    @Override
                    public void onUsersReceived(List<AdminDepartment> adminDepartment) {

                    }

                    @Override
                    public void onError(String s) {

                    }
                });

                // Gán sự kiện cho nút "Post"
                postButtonCreate.setOnClickListener(v -> {
                    String content = postContent.getText().toString();
                    PostAPI postAPI = new PostAPI();
                    adminDepartmentAPI.getAdminDepartmentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
                        @Override
                        public void onUserReceived(AdminDepartment adminDepartment) {
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
                                            DepartmentAPI departmentAPI = new DepartmentAPI();
                                            departmentAPI.getDepartmentById(adminDepartment.getDepartmentId(), new DepartmentAPI.DepartmentCallback() {
                                                @Override
                                                public void onDepartmentReceived(Department department) {
                                                    groupAPI.getGroupById(department.getGroupId(), new GroupAPI.GroupCallback() {
                                                        @Override
                                                        public void onGroupReceived(Group group) {
                                                            if (adminDepartment.getUserId() == group.getAdminUserId()) {
                                                                Post post = new Post();
                                                                post.setPostId(posts.size());
                                                                post.setUserId(adminDepartment.getUserId());
                                                                post.setPostLike(0);
                                                                post.setPostImage("");
                                                                post.setContent(content);
                                                                post.setStatus(Post.APPROVED);
                                                                post.setGroupId(department.getGroupId());
                                                                post.setCreatedAt(sdf.format(new Date()));
                                                                postAPI.addPost(post);

                                                                Toast.makeText(requireContext(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                                                            }
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
                                    });
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

    public void changeColorButtonNormal(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
    }
}
