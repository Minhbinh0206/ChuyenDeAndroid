package com.example.socialmediatdcproject.fragment.Student;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.GroupAdapter;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {
    RecyclerView recyclerView;
    FrameLayout thirdContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        View view = inflater.inflate(R.layout.fragment_group_first, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Lấy RecyclerView từ layout của Activity (shared_layout)
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        thirdContent = requireActivity().findViewById(R.id.third_content_fragment);
        thirdContent.setVisibility(view.getVisibility());

        // Mặc định vào group list
        loadGroups();

        // Tìm các nút
        Button groupAvailable = view.findViewById(R.id.button_group_available);
        Button groupCreateNew = view.findViewById(R.id.button_group_create_new);


        // Set màu mặc định cho nút "Bài viết"
        groupAvailable.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        groupAvailable.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
        groupCreateNew.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        groupCreateNew.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));

        // Sự kiện khi nhấn vào nút postButton
        groupAvailable.setOnClickListener(v -> {
            loadGroups();

            // Cập nhật màu cho các nút
            groupCreateNew.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            groupCreateNew.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            groupAvailable.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            groupAvailable.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
        });



        // Sự kiện khi nhấn vào nút memberButton
        groupCreateNew.setOnClickListener(v -> {
            String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StudentAPI studentAPI = new StudentAPI();
            studentAPI.getStudentByKey(key, new StudentAPI.StudentCallback() {
                @Override
                public void onStudentReceived(Student student) {
                    loadGroupsByUserIdIsAdmin(student.getUserId());
                }

                @Override
                public void onStudentsReceived(List<Student> students) {
                    // Không dùng trong ngữ cảnh này
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("GroupFragment", "Error fetching student: " + errorMessage);
                }

                @Override
                public void onStudentDeleted(int studentId) {
                    // Không dùng trong ngữ cảnh này
                }
            });

            // Cập nhật màu cho các nút
            groupAvailable.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            groupAvailable.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            groupCreateNew.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            groupCreateNew.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
        });
    }

    public void loadGroups() {
        // Chuyển sang GroupFollowedFragment sau khi thêm thành công
        Fragment searchGroupFragment = new SearchGroupFragment();
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.third_content_fragment, searchGroupFragment);
        fragmentTransaction.commit();

        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getAllGroups(new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {
                // Không dùng trong ngữ cảnh này
            }

            @Override
            public void onGroupsReceived(List<Group> groups) {
                if (isAdded()) {  // Kiểm tra fragment đã gắn vào context
                    ArrayList<Group> groupsList = new ArrayList<>();
                    for (Group group : groups) {
                        if (group != null) {
                            groupsList.add(group);
                        }
                    }
                    GroupAdapter groupAdapter = new GroupAdapter(groupsList, requireContext());
                    recyclerView.setAdapter(groupAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                }
            }
        });
    }

    public void loadGroupsByUserIdIsAdmin(int id) {
        // Chuyển sang GroupFollowedFragment sau khi thêm thành công
        Fragment createNewGroupFragment = new CreateNewGroupFragment();
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.third_content_fragment, createNewGroupFragment);
        fragmentTransaction.commit();

        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getAllGroups(new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {
                // Không dùng trong ngữ cảnh này
            }

            @Override
            public void onGroupsReceived(List<Group> groups) {
                if (isAdded()) {  // Kiểm tra fragment đã gắn vào context
                    ArrayList<Group> groupsList = new ArrayList<>();
                    for (Group group : groups) {
                        if (group.getAdminUserId() == id) {
                            groupsList.add(group);
                        }
                    }
                    GroupAdapter groupAdapter = new GroupAdapter(groupsList, requireContext());
                    recyclerView.setAdapter(groupAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                }
            }
        });
    }
    // Phương thức để cập nhật RecyclerView với danh sách nhóm
    private void updateRecyclerView(List<Group> groups) {
        GroupAdapter groupAdapter = new GroupAdapter(groups, requireContext());
        recyclerView.setAdapter(groupAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }
}
