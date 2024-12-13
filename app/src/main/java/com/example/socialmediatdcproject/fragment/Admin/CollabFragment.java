package com.example.socialmediatdcproject.fragment.Admin;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.BusinessAPI;
import com.example.socialmediatdcproject.API.CollabAPI;
import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.FilterPostsAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.NotifyAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.HomeAdminActivity;
import com.example.socialmediatdcproject.activity.SharedActivity;
import com.example.socialmediatdcproject.adapter.GroupAdapter;
import com.example.socialmediatdcproject.adapter.ItemCollabAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.dataModels.Collab;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Business;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Notify;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CollabFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<Post> postList;
    PostAdapter postAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.fragment_collab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FrameLayout third = requireActivity().findViewById(R.id.third_content_fragment);
        third.setVisibility(View.GONE);

        TextView add = view.findViewById(R.id.add_collab);
        add.setVisibility(View.GONE);

        postList = new ArrayList<>();
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        postAdapter = new PostAdapter(postList, requireContext());
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        ImageView avatar = view.findViewById(R.id.admin_department_avatar);
        TextView name = view.findViewById(R.id.admin_department_name);

        AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        adminDepartmentAPI.getAdminDepartmentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                add.setVisibility(View.VISIBLE);

                add.setOnClickListener(v -> {
                    showPopup(adminDepartment.getDepartmentId());
                });

                if (adminDepartment.getAvatar().isEmpty()) {
                    Glide.with(getContext())
                            .load(R.drawable.avatar_macdinh)
                            .circleCrop()
                            .into(avatar);
                } else {
                    // Thiết kế giao diện cho avatar
                    Glide.with(getContext())
                            .load(adminDepartment.getAvatar())
                            .circleCrop()
                            .into(avatar);
                }
                name.setText(adminDepartment.getFullName());

                DepartmentAPI departmentAPI = new DepartmentAPI();
                departmentAPI.getDepartmentById(adminDepartment.getDepartmentId(), new DepartmentAPI.DepartmentCallback() {
                    @Override
                    public void onDepartmentReceived(Department department) {
                        loadCollabOfDepartment(department.getDepartmentId());
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
        adminBusinessAPI.getAdminBusinessByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminBusinessAPI.AdminBusinessCallBack() {
            @Override
            public void onUserReceived(AdminBusiness adminBusiness) {
                add.setVisibility(View.GONE);
                if (adminBusiness.getAvatar().isEmpty()) {
                    Glide.with(getContext())
                            .load(R.drawable.avatar_macdinh)
                            .circleCrop()
                            .into(avatar);
                } else {
                    // Thiết kế giao diện cho avatar
                    Glide.with(getContext())
                            .load(adminBusiness.getAvatar())
                            .circleCrop()
                            .into(avatar);
                }
                name.setText(adminBusiness.getFullName());

                BusinessAPI businessAPI = new BusinessAPI();
                businessAPI.getBusinessById(adminBusiness.getBusinessId(), new BusinessAPI.BusinessCallback() {
                    @Override
                    public void onBusinessReceived(Business business) {
                        loadCollabOfBusiness(business.getBusinessId());

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
    }

    private void showPopup(int userId) {
        // Tạo Dialog
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.popup_add_collab); // Tệp layout tùy chỉnh

        // Set kích thước dialog là match_parent
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // Xử lý các nút và RecyclerView trong dialog
        ImageButton buttonCancel = dialog.findViewById(R.id.close);
        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        RecyclerView collab = dialog.findViewById(R.id.recy);
        ArrayList<Business> businessArrayList = new ArrayList<>();
        CollabAPI collabAPI = new CollabAPI();
        BusinessAPI businessAPI = new BusinessAPI();
        businessAPI.getAllBusinesses(new BusinessAPI.BusinessCallback() {
            @Override
            public void onBusinessReceived(Business business) {

            }

            @Override
            public void onBusinessesReceived(List<Business> businesses) {
                businessArrayList.clear();
                businessArrayList.addAll(businesses);

                ItemCollabAdapter itemCollabAdapter = new ItemCollabAdapter(businessArrayList, requireContext());
                collab.setAdapter(itemCollabAdapter);
                collab.setLayoutManager(new LinearLayoutManager(requireContext()));

                for (Business b: businesses) {
                    collabAPI.getCollabsByDepartmentId(userId, new CollabAPI.CollabCallback() {
                        @Override
                        public void onCollabReceived(List<Collab> collabList) {
                            for (Collab c : collabList) {
                                if (c.getBusinessId() == b.getBusinessId()) {
                                    businessArrayList.remove(b);
                                    itemCollabAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                }


            }
        });

        // Hiển thị dialog
        dialog.show();
    }

    private void loadCollabOfBusiness(int groupId) {
        ArrayList<Group> groupArrayList = new ArrayList<>();
        CollabAPI collabAPI = new CollabAPI();
        collabAPI.getAllCollab(new CollabAPI.CollabCallback() {
            @Override
            public void onCollabReceived(List<Collab> collabList) {
                groupArrayList.clear();
                for (Collab c : collabList) {
                    if (c.getBusinessId() == groupId) {
                        DepartmentAPI departmentAPI = new DepartmentAPI();
                        departmentAPI.getDepartmentById(c.getDepartmentId(), new DepartmentAPI.DepartmentCallback() {
                            @Override
                            public void onDepartmentReceived(Department department) {
                                GroupAPI groupAPI = new GroupAPI();
                                groupAPI.getGroupById(department.getGroupId(), new GroupAPI.GroupCallback() {
                                    @Override
                                    public void onGroupReceived(Group group) {
                                        groupArrayList.add(group);

                                        GroupAdapter groupAdapter = new GroupAdapter(groupArrayList, requireContext());
                                        recyclerView.setAdapter(groupAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
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
                }
            }
        });
    }

    private void loadCollabOfDepartment(int groupId) {
        ArrayList<Group> groupArrayList = new ArrayList<>();
        CollabAPI collabAPI = new CollabAPI();
        collabAPI.getAllCollab(new CollabAPI.CollabCallback() {
            @Override
            public void onCollabReceived(List<Collab> collabList) {
                groupArrayList.clear();
                for (Collab c : collabList) {
                    if (c.getDepartmentId() == groupId) {
                        BusinessAPI businessAPI = new BusinessAPI();
                        businessAPI.getBusinessById(c.getBusinessId(), new BusinessAPI.BusinessCallback() {
                            @Override
                            public void onBusinessReceived(Business business) {
                                GroupAPI groupAPI = new GroupAPI();
                                groupAPI.getGroupById(business.getGroupId(), new GroupAPI.GroupCallback() {
                                    @Override
                                    public void onGroupReceived(Group group) {
                                        groupArrayList.add(group);

                                        GroupAdapter groupAdapter = new GroupAdapter(groupArrayList, requireContext());
                                        recyclerView.setAdapter(groupAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
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
                }
            }
        });
    }
}

