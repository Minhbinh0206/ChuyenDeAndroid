package com.example.socialmediatdcproject.fragment.Student;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.BusinessAPI;
import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.GroupAdapter;
import com.example.socialmediatdcproject.model.Business;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;

import java.util.ArrayList;
import java.util.List;

public class SearchGroupFragment extends Fragment {

    private GroupAdapter adapter;
    private List<Group> groupList;
    private List<Group> filteredGroupList;
    private EditText editTextSearch;
    ImageButton iconFilter;
    Spinner spinnerFilter;
    TextView textView;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_group, container, false);

        // Ánh xạ các view từ layout
        editTextSearch = view.findViewById(R.id.edit_text_search);
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment); // Chỉnh sửa từ requireActivity() thành view

        iconFilter = view.findViewById(R.id.icon_filter);
        spinnerFilter = view.findViewById(R.id.spinner_filter);
        textView = view.findViewById(R.id.text_filter);

        // Tạo adapter cho spinner
        String[] filterOptions = {"Toàn bộ nhóm", "Trường", "Khoa", "Doanh nghiệp", "Khác"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                filterOptions
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);

        iconFilter.setOnClickListener(v -> {
            spinnerFilter.performClick();
        });

        textView.setOnClickListener(v -> {
            spinnerFilter.performClick();
        });

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lấy nội dung của mục được chọn
                String selectedItem = parent.getItemAtPosition(position).toString();

                // Thực hiện hành động dựa trên mục được chọn
                switch (selectedItem) {
                    case "Trường":
                        loadGroupDefault();
                        break;
                    case "Khoa":
                        loadGroupDepartment();
                        break;
                    case "Doanh nghiệp":
                        loadGroupBusiness();
                        break;
                    case "Khác":
                        loadGroupStudent();
                        break;
                    default:
                        loadAllGroups();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý trường hợp không có gì được chọn
            }
        });
        // TextWatcher để theo dõi và lọc danh sách nhóm khi thay đổi text trong EditText
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Tìm kiếm khi người dùng nhập text
                filterGroups(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    // Hàm để lọc danh sách nhóm dựa trên văn bản nhập
    private void filterGroups(String query) {
        if (query.isEmpty()) {
            loadAllGroups();
        } else {
            filteredGroupList.clear();
            for (Group group : groupList) {
                if (group.getGroupName().toLowerCase().contains(query.toLowerCase())) { // Tìm kiếm không phân biệt hoa thường
                    filteredGroupList.add(group);
                    adapter.notifyDataSetChanged(); // Cập nhật adapter
                }
            }
        }
    }
    private void loadGroupDefault(){
        // Khởi tạo danh sách nhóm và adapter
        groupList = new ArrayList<>();
        filteredGroupList.clear();
        adapter.notifyDataSetChanged();
        GroupAPI groupAPI = new GroupAPI();
        for (int i = 0; i < 4; i++) {
            groupAPI.getGroupById(i, new GroupAPI.GroupCallback() {
                @Override
                public void onGroupReceived(Group group) {
                    Log.d("HIN", "onGroupsReceived: " + group.getGroupName());

                    groupList.add(group);
                    filteredGroupList = new ArrayList<>(groupList);
                    adapter = new GroupAdapter(filteredGroupList, getContext());
                    adapter.notifyDataSetChanged();
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onGroupsReceived(List<Group> groups) {

                }
            });
        }

    }

    private void loadGroupDepartment(){
        // Khởi tạo danh sách nhóm và adapter
        groupList = new ArrayList<>();
        if (filteredGroupList != null) {
            filteredGroupList.clear();
            adapter.notifyDataSetChanged();
        }

        DepartmentAPI departmentAPI = new DepartmentAPI();
        GroupAPI groupAPI = new GroupAPI();
        departmentAPI.getAllDepartments(new DepartmentAPI.DepartmentCallback() {
            @Override
            public void onDepartmentReceived(Department department) {

            }

            @Override
            public void onDepartmentsReceived(List<Department> departments) {
                for (Department d: departments) {
                    groupAPI.getGroupById(d.getGroupId(), new GroupAPI.GroupCallback() {
                        @Override
                        public void onGroupReceived(Group group) {
                            Log.d("HIN", "onGroupsReceived: " + group.getGroupName());

                            groupList.add(group);
                            filteredGroupList = new ArrayList<>(groupList);
                            adapter = new GroupAdapter(filteredGroupList, getContext());
                            adapter.notifyDataSetChanged();
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onGroupsReceived(List<Group> groups) {

                        }
                    });
                }


            }
        });
    }

    private void loadGroupBusiness(){
        // Khởi tạo danh sách nhóm và adapter
        groupList = new ArrayList<>();
        if (filteredGroupList != null) {
            filteredGroupList.clear();
            adapter.notifyDataSetChanged();
        }

        BusinessAPI businessAPI = new BusinessAPI();
        businessAPI.getAllBusinesses(new BusinessAPI.BusinessCallback() {
            @Override
            public void onBusinessReceived(Business business) {

            }

            @Override
            public void onBusinessesReceived(List<Business> businesses) {
                for (Business b: businesses) {
                    GroupAPI groupAPI = new GroupAPI();
                    groupAPI.getGroupById(b.getGroupId(), new GroupAPI.GroupCallback() {
                        @Override
                        public void onGroupReceived(Group group) {
                            Log.d("HIN", "onGroupsReceived: " + group.getGroupName());

                            groupList.add(group);

                            filteredGroupList = new ArrayList<>(groupList);
                            adapter = new GroupAdapter(filteredGroupList, getContext());
                            adapter.notifyDataSetChanged();
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onGroupsReceived(List<Group> groups) {

                        }
                    });
                }
            }
        });
    }

    private void loadGroupStudent(){
        // Khởi tạo danh sách nhóm và adapter
        groupList = new ArrayList<>();
        if (filteredGroupList != null) {
            filteredGroupList.clear();
            adapter.notifyDataSetChanged();
        }

        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getAllGroups(new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {

            }

            @Override
            public void onGroupsReceived(List<Group> groups) {
                for (Group g : groups) {
                    if (!g.isGroupDefault()) {
                        Log.d("HIN", "onGroupsReceived: " + g.getGroupName());
                        groupList.add(g);

                        filteredGroupList = new ArrayList<>(groupList);
                        adapter = new GroupAdapter(filteredGroupList, getContext());
                        adapter.notifyDataSetChanged();
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(adapter);
                    }
                }

            }
        });
    }

    private void loadAllGroups(){
        // Khởi tạo danh sách nhóm và adapter
        groupList = new ArrayList<>();
        if (filteredGroupList != null) {
            filteredGroupList.clear();
            adapter.notifyDataSetChanged();
        }


        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getAllGroups(new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {

            }

            @Override
            public void onGroupsReceived(List<Group> groups) {
                groupList.addAll(groups);

                filteredGroupList = new ArrayList<>(groupList);
                adapter = new GroupAdapter(filteredGroupList, getContext());
                adapter.notifyDataSetChanged();
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);
            }
        });
    }
}
