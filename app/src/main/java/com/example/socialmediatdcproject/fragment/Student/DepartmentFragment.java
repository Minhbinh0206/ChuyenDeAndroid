package com.example.socialmediatdcproject.fragment.Student;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.MemberAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class DepartmentFragment extends Fragment {
    private RecyclerView recyclerView; // RecyclerView để hiển thị danh sách người dùng
    FrameLayout frameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.fragment_group_default, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GroupAPI groupAPI = new GroupAPI();

        // Khởi tạo danh sách người dùng và RecyclerView
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        frameLayout = requireActivity().findViewById(R.id.third_content_fragment);
        frameLayout.setVisibility(view.GONE);

        RecyclerView recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        recyclerView.setVisibility(View.VISIBLE);

        FrameLayout frameLayout = requireActivity().findViewById(R.id.third_content_fragment);
        frameLayout.setVisibility(View.GONE);

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
                        groupAPI.getGroupById(department.getGroupId(), new GroupAPI.GroupCallback() {
                            @Override
                            public void onGroupReceived(Group group) {
                                int groupId = -1;

                                groupId = group.getGroupId();

                                Log.d("DepartmentFragment", "onGroupReceived: " + groupId);

                                TextView nameTraining = view.findViewById(R.id.name_group_default);
                                ImageView avatarTraining = view.findViewById(R.id.avatar_group_default);
                                nameTraining.setText(group.getGroupName());

                                Glide.with(requireContext())
                                        .load(group.getAvatar())
                                        .circleCrop()
                                        .into(avatarTraining);

                                loadPostFromFirebase(groupId);

                                // Tìm các nút
                                Button postButton = view.findViewById(R.id.button_group_default_post);
                                Button eventButton = view.findViewById(R.id.button_group_default_event);

                                // Set màu mặc định cho nút "Bài viết"
                                changeColorButtonActive(postButton);

                                // Sự kiện khi nhấn vào nút memberButton
                                int finalGroupId = groupId;

                                // Sự kiện khi nhấn vào nút postButton
                                postButton.setOnClickListener(v -> {
                                    loadPostFromFirebase(finalGroupId);

                                    // Cập nhật màu cho các nút
                                    changeColorButtonActive(postButton);
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
        });
    }

    public void loadPostFromFirebase(int id) {
        ArrayList<Post> postsList = new ArrayList<>(); // Danh sách bài viết

        // Tạo instance của PostAPI
        PostAPI postAPI = new PostAPI();

        // Lấy bài viết theo groupId
        postAPI.getPostsByGroupId(id, new PostAPI.PostCallback() {
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

    public void changeColorButtonActive(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
    }

    public void changeColorButtonNormal(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.buttonDefault));
        btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.black));
    }
}
