package com.example.socialmediatdcproject.fragment.Admin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.BusinessAPI;
import com.example.socialmediatdcproject.API.ClassAPI;
import com.example.socialmediatdcproject.API.CollabAPI;
import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.NotifyQuicklyAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.ReceivePostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R; // Import đúng package chứa R
import com.example.socialmediatdcproject.activity.UploadProfileActivity;
import com.example.socialmediatdcproject.adapter.ItemFilterAdapter;
import com.example.socialmediatdcproject.dataModels.Collab;
import com.example.socialmediatdcproject.dataModels.NotifyQuickly;
import com.example.socialmediatdcproject.dataModels.ReceicePost;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Business;
import com.example.socialmediatdcproject.model.Class;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainFeatureFragment extends Fragment {
    private List<String> optionsOfAdminDepartment = new ArrayList<>();
    RecyclerView recyclerView;
    ItemFilterAdapter itemFilterAdapter;
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
        dialog.setContentView(R.layout.post_create_filter_layout);

        // Tìm các view bên trong Dialog
        Button postButtonCreate = dialog.findViewById(R.id.button_post_user_create_new);
        Button postButtonCancle = dialog.findViewById(R.id.button_post_user_create_cancle);
        EditText postContent = dialog.findViewById(R.id.post_content);
        Spinner filterMain = dialog.findViewById(R.id.filter_main);
        recyclerView = dialog.findViewById(R.id.filter_recycle);

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
            public void onUsersReceived(List<AdminDepartment> adminDepartment) { }

            @Override
            public void onError(String s) { }
        });

        // Các tùy chọn cho Spinner
        optionsOfAdminDepartment.add("Toàn bộ học sinh thuộc khoa");
        optionsOfAdminDepartment.add("Doanh nghiệp liên kết với khoa");
        optionsOfAdminDepartment.add("Học sinh thuộc lớp");
        optionsOfAdminDepartment.add("Các cá nhân cụ thể");

        // Set adapter cho Spinner
        ArrayAdapter<String> filterMainAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, optionsOfAdminDepartment);
        filterMainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterMain.setAdapter(filterMainAdapter);

        recyclerView.setVisibility(View.GONE);
        List<Object> receivePostUser = new ArrayList<>();

        final boolean[] isFilter = {false};
        // Gán sự kiện cho Spinner khi người dùng chọn item
        filterMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = optionsOfAdminDepartment.get(position);
                if (selected.equals(optionsOfAdminDepartment.get(0))) {
                    recyclerView.setVisibility(View.GONE);
                    isFilter[0] = false;

                } else if (selected.equals(optionsOfAdminDepartment.get(1))) {
                    recyclerView.setVisibility(View.VISIBLE);
                    loadBusinessFilter();
                    isFilter[0] = true;


                } else if (selected.equals(optionsOfAdminDepartment.get(2))) {
                    recyclerView.setVisibility(View.VISIBLE);
                    loadClassFilter();
                    isFilter[0] = true;

                } else {
                    loadStudentInDepartmentFilter();
                    recyclerView.setVisibility(View.VISIBLE);
                    isFilter[0] = true;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });


        // Gán sự kiện cho nút "Post"
        postButtonCreate.setOnClickListener(v -> {
            List<String> selectedFilter = itemFilterAdapter.getSelectedFilters();

            String content = postContent.getText().toString();
            PostAPI postAPI = new PostAPI();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            adminDepartmentAPI.getAdminDepartmentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
                @Override
                public void onUserReceived(AdminDepartment adminDepartment) {
                    postAPI.getAllPosts(new PostAPI.PostCallback() {
                        @Override
                        public void onPostReceived(Post post) { }

                        @Override
                        public void onPostsReceived(List<Post> posts) {
                            GroupAPI groupAPI = new GroupAPI();
                            DepartmentAPI departmentAPI = new DepartmentAPI();
                            departmentAPI.getDepartmentById(adminDepartment.getDepartmentId(), new DepartmentAPI.DepartmentCallback() {
                                @Override
                                public void onDepartmentReceived(Department department) {
                                    groupAPI.getGroupById(department.getGroupId(), new GroupAPI.GroupCallback() {
                                        @Override
                                        public void onGroupReceived(Group group) {

                                            if (adminDepartment.getUserId() == group.getAdminUserId()) {
                                                Post newPost = new Post();
                                                newPost.setPostId(posts.size());
                                                newPost.setUserId(adminDepartment.getUserId());
                                                newPost.setPostLike(0);
                                                newPost.setPostImage("");
                                                newPost.setContent(content);
                                                newPost.setStatus(Post.APPROVED);
                                                newPost.setFilter(isFilter[0]);
                                                newPost.setGroupId(department.getGroupId());
                                                newPost.setCreatedAt(sdf.format(new Date()));
                                                postAPI.addPost(newPost);

                                                processAdditional(newPost.getPostId(), receivePostUser);

                                                Toast.makeText(requireContext(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onGroupsReceived(List<Group> groups) { }
                                    });
                                }

                                @Override
                                public void onDepartmentsReceived(List<Department> departments) { }
                            });
                        }
                    });
                }

                @Override
                public void onUsersReceived(List<AdminDepartment> adminDepartment) { }

                @Override
                public void onError(String s) { }
            });
            dialog.dismiss();
        });

        postButtonCancle.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.comment_custom));

        int marginInDp = (int) (10 * getResources().getDisplayMetrics().density);
        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.horizontalMargin = marginInDp / (float) getResources().getDisplayMetrics().widthPixels;
            dialog.getWindow().setAttributes(params);
        }

        dialog.show();
    }

    // Phương thức cập nhật RecyclerView sau khi có đủ dữ liệu
    private void updateRecyclerView(ArrayList<String> filterList) {
        if (recyclerView == null) {
            recyclerView = getView().findViewById(R.id.filter_recycle);
        }

        if (recyclerView != null) {
            itemFilterAdapter = new ItemFilterAdapter(filterList, requireContext());
            itemFilterAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(itemFilterAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        } else {
            Log.e("MainFeatureFragment", "RecyclerView is null, cannot update RecyclerView.");
        }
    }

    public void changeColorButtonNormal(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
    }

    private void loadBusinessFilter() {
        CollabAPI collabAPI = new CollabAPI();
        BusinessAPI businessAPI = new BusinessAPI();
        ArrayList<String> filterList = new ArrayList<>();
        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        adminDepartmentAPI.getAdminDepartmentByKey(key, new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                collabAPI.getCollabsByDepartmentId(adminDepartment.getDepartmentId(), new CollabAPI.CollabCallback() {
                    @Override
                    public void onCollabReceived(List<Collab> collabList) {
                        // Nếu collabList rỗng, cập nhật RecyclerView ngay lập tức
                        if (collabList.isEmpty()) {
                            updateRecyclerView(filterList);
                            return;
                        }

                        int totalCollabs = collabList.size();
                        final int[] completedCount = {0}; // Bộ đếm hoàn thành

                        for (Collab c : collabList) {
                            businessAPI.getBusinessById(c.getBusinessId(), new BusinessAPI.BusinessCallback() {
                                @Override
                                public void onBusinessReceived(Business business) {
                                    filterList.add(business.getBussinessName());
                                    Log.d("NAM", "onBusinessReceived: " + business.getBussinessName());

                                    // Kiểm tra khi đã hoàn thành tất cả các yêu cầu business
                                    completedCount[0]++;
                                    if (completedCount[0] == totalCollabs) {
                                        updateRecyclerView(filterList); // Cập nhật RecyclerView khi đã đủ dữ liệu
                                    }
                                }

                                @Override
                                public void onBusinessesReceived(List<Business> businesses) { }
                            });
                        }
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
    }

    private void processAdditional(int id, List<Object> users){
        ReceivePostAPI receivePostAPI = new ReceivePostAPI();
        ReceicePost receicePost = new ReceicePost();
        receicePost.setPostId(id);
        receicePost.setListUserGet(users);
        receivePostAPI.addReceivePost(receicePost);
    }

    private void loadClassFilter() {
        ClassAPI classAPI = new ClassAPI();
        ArrayList<String> filterList = new ArrayList<>();
        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        adminDepartmentAPI.getAdminDepartmentByKey(key, new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                classAPI.getClassesByDepartmentId(adminDepartment.getDepartmentId(), new ClassAPI.ClassCallback() {
                    @Override
                    public void onClassReceived(Class classItem) {

                    }

                    @Override
                    public void onClassesReceived(List<Class> classList) {
                        for (Class c : classList) {
                            filterList.add(c.getClassName());
                        }
                        updateRecyclerView(filterList);
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
    }

    private void loadStudentInDepartmentFilter() {
        ArrayList<String> filterList = new ArrayList<>();
        StudentAPI studentAPI = new StudentAPI();
        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        adminDepartmentAPI.getAdminDepartmentByKey(key, new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                studentAPI.getAllStudents(new StudentAPI.StudentCallback() {
                    @Override
                    public void onStudentReceived(Student student) {

                    }

                    @Override
                    public void onStudentsReceived(List<Student> students) {
                        for (Student student: students) {
                            if (student.getDepartmentId() == adminDepartment.getDepartmentId()) {
                                filterList.add(student.getStudentNumber());

                            }
                        }
                        updateRecyclerView(filterList);

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
    }
}
