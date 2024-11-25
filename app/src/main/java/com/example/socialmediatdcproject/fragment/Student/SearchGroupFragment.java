package com.example.socialmediatdcproject.fragment.Student;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.MajorAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.UploadProfileActivity;
import com.example.socialmediatdcproject.adapter.GroupAdapter;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Major;

import java.util.ArrayList;
import java.util.List;

public class SearchGroupFragment extends Fragment {

    private GroupAdapter adapter;
    private List<Group> groupList;
    private List<Group> filteredGroupList;
    private List<Major> majorList;
    private EditText editTextSearch;
    private Spinner spinnerFilter;
    private DepartmentAPI departmentAPI;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_group, container, false);

        // Ánh xạ các view từ layout
        editTextSearch = view.findViewById(R.id.edit_text_search);
//        spinnerFilter = view.findViewById(R.id.filter_group);
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);

        // Khởi tạo danh sách nhóm và adapter
        groupList = new ArrayList<>();
        filteredGroupList = new ArrayList<>(); // Khởi tạo filteredGroupList

        // Gọi API để lấy dữ liệu nhóm
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

        // TextWatcher để theo dõi và lọc danh sách nhóm khi thay đổi text trong EditText
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Tìm kiếm khi người dùng nhập text
                filterGroupsByKey(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Tìm kiếm khi người dùng nhập text
                filterGroupsByKey(s.toString());
            }
        });

//        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        return view;
    }

    // Hàm để lọc danh sách nhóm dựa trên văn bản nhập
    private void filterGroupsByKey(String query) {
        filteredGroupList.clear(); // Xóa nội dung hiện tại của filteredGroupList
        if (query.isEmpty()) {
            filteredGroupList.addAll(groupList); // Nếu chuỗi tìm kiếm rỗng, hiển thị toàn bộ danh sách
            adapter.notifyDataSetChanged(); // Cập nhật adapter

        } else {
            for (Group group : groupList) {
                if (group.getGroupName().toLowerCase().contains(query.toLowerCase())) { // Tìm kiếm không phân biệt hoa thường
                    filteredGroupList.add(group);
                    adapter.notifyDataSetChanged(); // Cập nhật adapter
                }
            }
        }
    }
//    private void loadDepartments() {
//        departmentAPI.getAllDepartments(new DepartmentAPI.DepartmentCallback() {
//            @Override
//            public void onDepartmentReceived(Department department) {
//
//            }
//
//            @Override
//            public void onDepartmentsReceived(List<Department> departments) {
//                for (Department department : departments) {
//                    if (department != null) {
//                        optionsDepartment.add(department.getDepartmentName());
//                    }
//                }
//                ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(UploadProfileActivity.this, android.R.layout.simple_spinner_item, optionsDepartment);
//                departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                departmentSpinner.setAdapter(departmentAdapter);
//            }
//        });
//    }
}
