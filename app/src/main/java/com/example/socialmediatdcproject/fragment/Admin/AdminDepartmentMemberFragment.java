package com.example.socialmediatdcproject.fragment.Admin;

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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.LecturerAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.LecturerAdapter;
import com.example.socialmediatdcproject.adapter.MemberAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Lecturer;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AdminDepartmentMemberFragment extends Fragment {
    private RecyclerView recyclerView; // RecyclerView để hiển thị danh sách người dùng
    FrameLayout frameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.admin_department_members_first, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GroupAPI groupAPI = new GroupAPI();

        // Khởi tạo danh sách người dùng và RecyclerView
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        frameLayout = requireActivity().findViewById(R.id.third_content_fragment);

        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();

        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        adminDepartmentAPI.getAdminDepartmentByKey(key, new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                DepartmentAPI departmentAPI = new DepartmentAPI();
                departmentAPI.getDepartmentById(adminDepartment.getDepartmentId(), new DepartmentAPI.DepartmentCallback() {
                    @Override
                    public void onDepartmentReceived(Department department) {
                        groupAPI.getGroupById(department.getGroupId(), new GroupAPI.GroupCallback() {
                            @Override
                            public void onGroupReceived(Group group) {
                                int groupId = -1;

                                groupId = group.getGroupId();

                                Log.d("AdminDepartmentFragment", "onGroupReceived: " + groupId);

                                TextView nameTraining = view.findViewById(R.id.name_department_admin);
                                ImageView avatarTraining = view.findViewById(R.id.logo_department_admin);
                                nameTraining.setText(group.getGroupName());

                                Glide.with(requireContext())
                                        .load(group.getAvatar())
                                        .circleCrop()
                                        .into(avatarTraining);

                                // Tìm các nút
                                Button studentButton = view.findViewById(R.id.button_department_member_student);
                                Button lecturerButton = view.findViewById(R.id.button_department_member_lecturer);

                                loadStudentsByGroupId(groupId);

                                // Set màu mặc định cho nút
                                changeColorButtonActive(studentButton);
                                changeColorButtonNormal(lecturerButton);

                                // Sự kiện khi nhấn vào nút studentButton
                                int finalGroupId = groupId;
                                studentButton.setOnClickListener(v -> {
                                    loadStudentsByGroupId(finalGroupId);

                                    // Cập nhật màu cho các nút
                                    changeColorButtonActive(studentButton);
                                    changeColorButtonNormal(lecturerButton);
                                });

                                // Sự kiện khi nhấn vào nút lecturerButton
                                lecturerButton.setOnClickListener(v -> {
                                    loadLeturersByGroupId(finalGroupId);

                                    // Cập nhật màu cho các nút
                                    changeColorButtonActive(lecturerButton);
                                    changeColorButtonNormal(studentButton);
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
            public void onUsersReceived(List<AdminDepartment> adminDepartment) {

            }

            @Override
            public void onError(String s) {

            }
        });

        // Hiển thị phần layout dưới
        repairButtonFragment();

        changeFragmentByButtonClick();

        if (isAddOrCancelFragmentVisible()) {
            // Fragment hiện tại là AddOrCancelButtonFragment
            Log.d("AdminDepartmentFragment", "Fragment hiện tại là AddOrCancelButtonFragment");
        } else {
            // Fragment hiện tại là RepairButtonFragment hoặc Fragment khác
            Log.d("AdminDepartmentFragment", "Fragment hiện tại không phải là AddOrCancelButtonFragment");
        }
    }
    private void changeFragmentByButtonClick(){
        RepairButtonFragment fragment = new RepairButtonFragment();
        fragment.setEditAction(() -> {
            // Hành động khi nút chỉnh sửa được nhấn
            replaceFragmentWithAddOrCancel();
        });
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.third_content_fragment, fragment)
                .commit();
    }
    private boolean isAddOrCancelFragmentVisible() {
        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.third_content_fragment);
        return currentFragment instanceof AddOrCancelButtonFragment; // Kiểm tra xem Fragment hiện tại có phải là AddOrCancelButtonFragment không
    }

    // Hiển thị sinh viên có trong khoa
    private void loadStudentsByGroupId(int id) {
        ArrayList<Student> memberGroup = new ArrayList<>();
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getAllStudents(new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {

            }
            @Override
            public void onStudentsReceived(List<Student> students) {
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
                                        for (Student u : students) {
                                            if (u.getUserId() == gus.getUserId() && !memberGroup.contains(u)) {
                                                memberGroup.add(u);
                                            }
                                        }
                                    }

                                    // Cập nhật RecyclerView sau khi thêm tất cả member
                                    MemberAdapter memberAdapter = new MemberAdapter(memberGroup, requireContext());
                                    memberAdapter.notifyDataSetChanged();
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
    // Hiển thị giảng viên có trong khoa
    private void loadLeturersByGroupId(int id) {
        ArrayList<Lecturer> memberGroup = new ArrayList<>();
        LecturerAPI lecturerAPI = new LecturerAPI();
        lecturerAPI.getAllLecturers(new LecturerAPI.LecturerCallback() {
            @Override
            public void onLecturerReceived(Lecturer lecturer) {

            }

            @Override
            public void onLecturersReceived(List<Lecturer> lecturers) {
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
                                        for (Lecturer u : lecturers) {
                                            if (u.getUserId() == gus.getUserId() && !memberGroup.contains(u)) {
                                                memberGroup.add(u);
                                            }
                                        }
                                    }

                                    // Cập nhật RecyclerView sau khi thêm tất cả member
                                    LecturerAdapter lecturerAdapter = new LecturerAdapter(memberGroup, requireContext());
                                    lecturerAdapter.notifyDataSetChanged();
                                    recyclerView.setAdapter(lecturerAdapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {

            }

            @Override
            public void onLecturerDeleted(int lecturerId) {

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

    public void repairButtonFragment(){
        // Gán fragment home là mặc định
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Nạp HomeFragment vào
        fragmentTransaction.replace(R.id.third_content_fragment, new RepairButtonFragment());
        fragmentTransaction.addToBackStack(null); // Thêm dòng này

        // Lấy dữ lệu từ firebase
        fragmentTransaction.commit();
    }

    public void replaceFragmentWithAddOrCancel() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.third_content_fragment, new AddOrCancelButtonFragment());
        fragmentTransaction.addToBackStack(null); // Thêm dòng này
        fragmentTransaction.commit();
    }


}