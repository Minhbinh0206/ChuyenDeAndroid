package com.example.socialmediatdcproject.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.MemberAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DepartmentFragment extends Fragment {
    private RecyclerView recyclerView; // RecyclerView để hiển thị danh sách người dùng
    FrameLayout frameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.fragment_department_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GroupAPI groupAPI = new GroupAPI();

        // Khởi tạo danh sách người dùng và RecyclerView
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        frameLayout = requireActivity().findViewById(R.id.third_content_fragment);
        frameLayout.setVisibility(view.GONE);

        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(key, new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                Log.d("DepartmentFragment", "Student DepartmentId" + student.getDepartmentId());
                DepartmentAPI departmentAPI = new DepartmentAPI();
                departmentAPI.getDepartmentById(student.getDepartmentId(), new DepartmentAPI.DepartmentCallback() {
                    @Override
                    public void onDepartmentReceived(Department department) {
                        Log.d("Fragment", "Department Name" + department.getDepartmentName());

                        String nameGroup = "Khoa " + department.getDepartmentName();

                        Log.d("Fragment", "onDepartmentReceived: " + nameGroup);
                        groupAPI.getGroupByName(nameGroup, new GroupAPI.GroupCallback() {
                            @Override
                            public void onGroupReceived(Group group) {
                                int groupId = -1;

                                groupId = group.getGroupId();

                                Log.d("DepartmentFragment", "onGroupReceived: " + groupId);

                                TextView nameTraining = getView().findViewById(R.id.name_department);
                                ImageView avatarTraining = getView().findViewById(R.id.logo_department);
                                nameTraining.setText(group.getGroupName());

                                Glide.with(requireContext())
                                        .load(group.getAvatar())
                                        .circleCrop()
                                        .into(avatarTraining);

                                loadPostFromFirebase(groupId);

                                // Tìm các nút
                                Button infoButton = view.findViewById(R.id.button_department_info);
                                Button postButton = view.findViewById(R.id.button_department_post);
                                Button memberButton = view.findViewById(R.id.button_department_member);

                                // Set màu mặc định cho nút "Bài viết"
                                postButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
                                postButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));

                                // Sự kiện khi nhấn vào nút memberButton
                                int finalGroupId = groupId;
                                memberButton.setOnClickListener(v -> {
                                    loadUsersByGroupId(finalGroupId);

                                    // Cập nhật màu cho các nút
                                    changeColorButtonActive(memberButton);
                                    changeColorButtonNormal(infoButton);
                                    changeColorButtonNormal(postButton);
                                });

                                // Sự kiện khi nhấn vào nút postButton
                                postButton.setOnClickListener(v -> {
                                    loadPostFromFirebase(finalGroupId);

                                    // Cập nhật màu cho các nút
                                    changeColorButtonActive(postButton);
                                    changeColorButtonNormal(infoButton);
                                    changeColorButtonNormal(memberButton);
                                });
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

    public void loadPostFromFirebase(int id) {
        ArrayList<Post> postsList = new ArrayList<>(); // Danh sách bài viết

        // Tạo instance của PostAPI
        PostAPI postAPI = new PostAPI();

        // Lấy bài viết theo groupId
        postAPI.getPostByGroupId(id, new PostAPI.PostCallback() {
            @Override
            public void onPostReceived(Post post) {

            }

            @Override
            public void onPostsReceived(List<Post> posts) {
                // Kiểm tra nếu có bài viết
                if (posts.size() > 0) {
                    for (Post p : posts) {
                        if (p != null) {
                            postsList.add(p); // Thêm bài viết vào danh sách
                        }
                    }

                    // Cập nhật RecyclerView với dữ liệu bài viết
                    PostAdapter postAdapter = new PostAdapter(postsList, requireContext());
                    recyclerView.setAdapter(postAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                } else {
                    ArrayList<Post> postsList = new ArrayList<>();

                    // Cập nhật RecyclerView với dữ liệu bài viết
                    PostAdapter postAdapter = new PostAdapter(postsList, requireContext());
                    recyclerView.setAdapter(postAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                }
            }
        });
    }


    private void loadUsersByGroupId(int id) {
        ArrayList<User> memberGroup = new ArrayList<>();
        UserAPI userAPI = new UserAPI();
        userAPI.getAllUsers(new UserAPI.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                // Không cần làm gì ở đây nếu không sử dụng
            }

            @Override
            public void onUsersReceived(List<User> users) {
                GroupUserAPI groupUserAPI = new GroupUserAPI();
                groupUserAPI.getAllGroupUsers(new GroupUserAPI.GroupUserCallback() {
                    @Override
                    public void onGroupUsersReceived(List<GroupUser> groupUsers) {
                        for (GroupUser gu : groupUsers) {
                            groupUserAPI.getGroupUserByIdGroup(gu.getGroupId(), new GroupUserAPI.GroupUserCallback() {
                                @Override
                                public void onGroupUsersReceived(List<GroupUser> groupUsers) {
                                    List<GroupUser> groupUserList = new ArrayList<>();
                                    for (GroupUser gu : groupUsers) {
                                        if (gu.getGroupId() == id) {
                                            groupUserList.add(gu);
                                        }
                                    }

                                    // Chỉ thêm vào memberGroup nếu chưa có
                                    for (GroupUser gus : groupUserList) {
                                        for (User u : users) {
                                            if (u.getUserId() == gus.getUserId() && !memberGroup.contains(u)) {
                                                memberGroup.add(u);
                                            }
                                        }
                                    }

                                    // Cập nhật RecyclerView sau khi thêm tất cả member
                                    MemberAdapter memberAdapter = new MemberAdapter(memberGroup);
                                    recyclerView.removeAllViews();
                                    recyclerView.setAdapter(memberAdapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                                }
                            });
                        }
                    }
                });
            }
        });
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
