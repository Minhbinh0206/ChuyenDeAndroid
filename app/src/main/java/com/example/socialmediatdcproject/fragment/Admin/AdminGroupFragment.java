package com.example.socialmediatdcproject.fragment.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.BusinessAPI;
import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.GroupAdapter;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.fragment.Student.CreateNewGroupFragment;
import com.example.socialmediatdcproject.fragment.Student.SplitFragment;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Business;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AdminGroupFragment extends Fragment {
    private RecyclerView recyclerView; // RecyclerView để hiển thị danh sách người dùng
    FrameLayout frameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.fragment_admin_layout_group, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView avatarAdmin = view.findViewById(R.id.avatar_admin);
        TextView nameAdmin = view.findViewById(R.id.name_admin);

        TextView textView = requireActivity().findViewById(R.id.null_content);
        textView.setVisibility(View.GONE);

        frameLayout = requireActivity().findViewById(R.id.third_content_fragment);
        frameLayout.setVisibility(View.VISIBLE);

        Fragment searchGroupFragment = new TextGroupFragment();
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.third_content_fragment, searchGroupFragment);
        fragmentTransaction.commit();

        AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();

        String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();

        adminBusinessAPI.getAdminBusinessByKey(userKey, new AdminBusinessAPI.AdminBusinessCallBack() {
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
                                Glide.with(requireContext())
                                        .load(group.getAvatar())
                                        .circleCrop()
                                        .into(avatarAdmin);

                                nameAdmin.setText(group.getGroupName());
                            }

                            @Override
                            public void onGroupsReceived(List<Group> groups) {

                            }
                        });

                        loadGroupsOfAdmin(adminBusiness.getUserId(), textView, business.getBusinessId());

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
        adminDefaultAPI.getAdminDefaultByKey(userKey, new AdminDefaultAPI.AdminDefaultCallBack() {
            @Override
            public void onUserReceived(AdminDefault adminDefault) {
                Glide.with(requireContext())
                        .load(adminDefault.getAvatar())
                        .circleCrop()
                        .into(avatarAdmin);

                nameAdmin.setText(adminDefault.getFullName());

                loadGroupsOfAdmin(adminDefault.getUserId(), textView, adminDefault.getGroupId());
            }

            @Override
            public void onUsersReceived(List<AdminDefault> adminDefault) {

            }
        });
        adminDepartmentAPI.getAdminDepartmentByKey(userKey, new AdminDepartmentAPI.AdminDepartmentCallBack() {
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
                                Glide.with(requireContext())
                                        .load(group.getAvatar())
                                        .circleCrop()
                                        .into(avatarAdmin);

                                nameAdmin.setText(group.getGroupName());
                            }

                            @Override
                            public void onGroupsReceived(List<Group> groups) {

                            }
                        });

                        loadGroupsOfAdmin(adminDepartment.getUserId(), textView, department.getGroupId());

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

        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
    }

    public void loadGroupsOfAdmin(int userId, TextView textView, int groupId) {
        ArrayList<Group> groupsList = new ArrayList<>(); // Khởi tạo lại danh sách mỗi lần gọi
        GroupAPI groupAPI = new GroupAPI();
        GroupUserAPI groupUserAPI = new GroupUserAPI();

        groupUserAPI.getAllGroupsByUserId(userId, new GroupUserAPI.GroupsCallback() {
            @Override
            public void onGroupsReceived(List<Integer> groupIds) {
                for (Integer i : groupIds) {
                    groupAPI.getGroupById(i, new GroupAPI.GroupCallback() {
                        @Override
                        public void onGroupReceived(Group group) {
                            if (!group.isGroupDefault()) {
                                groupsList.add(group);
                            }

                            // Cập nhật RecyclerView với dữ liệu nhóm
                            GroupAdapter groupAdapter = new GroupAdapter(groupsList, requireContext());
                            recyclerView.setAdapter(groupAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

                            // Kiểm tra xem groupsList có rỗng không
                            if (groupsList.isEmpty()) {
                                textView.setVisibility(View.VISIBLE);
                                textView.setText("Hiện bạn chưa quản lý nhóm nào!");
                            } else {
                                textView.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onGroupsReceived(List<Group> groups) {

                        }
                    });
                }
            }
        });
    }
}
