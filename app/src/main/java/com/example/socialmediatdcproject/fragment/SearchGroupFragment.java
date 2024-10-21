package com.example.socialmediatdcproject.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.GroupAdapter;
import com.example.socialmediatdcproject.model.Group;

import java.util.ArrayList;
import java.util.List;

public class SearchGroupFragment extends Fragment {

    private EditText editTextSearch;
    private ImageButton btnSearch;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_group, container, false);
        editTextSearch = view.findViewById(R.id.edit_text_search);
        btnSearch = view.findViewById(R.id.search_group);
        recyclerView = view.findViewById(R.id.recyclerView); // Thay đổi ID nếu cần

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Thiết lập sự kiện nhấn
        btnSearch.setOnClickListener(v -> searchGroups());

        return view;
    }

    //Hàm tìm kiếm group
    private void searchGroups() {
        String searchQuery = editTextSearch.getText().toString().trim();
        if (!searchQuery.isEmpty()) {

            String normalizedQuery = searchQuery.toLowerCase();
            Log.d("SearchGroupFragment", "Searching for group: " + normalizedQuery);

            GroupAPI groupAPI = new GroupAPI();
            groupAPI.getGroupByName(normalizedQuery, new GroupAPI.GroupCallback() {
                @Override
                public void onGroupReceived(Group group) {
                    if (group != null) {
                        Log.d("SearchGroupFragment", "Group found: " + group.getGroupName());
                        List<Group> groups = new ArrayList<>();
                        groups.add(group);
                        updateRecyclerView(groups);
                    } else {
                        Log.d("SearchGroupFragment", "No group found");
                        Toast.makeText(requireContext(), "Không tìm thấy nhóm nào!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onGroupsReceived(List<Group> groups) {
                    if (groups != null && !groups.isEmpty()) {
                        Log.d("SearchGroupFragment", "Groups received: " + groups.size());
                        updateRecyclerView(groups);
                    } else {
                        Log.d("SearchGroupFragment", "No groups found");
                    }
                }
            });
        } else {
            Toast.makeText(requireContext(), "Vui lòng nhập tên nhóm để tìm kiếm!", Toast.LENGTH_SHORT).show();
        }
    }



    //Tạo lại danh sách nhóm vừa mới tiềm kiếm
    private void updateRecyclerView(List<Group> groups) {
        GroupAdapter groupAdapter = new GroupAdapter(groups, requireContext());
        recyclerView.setAdapter(groupAdapter);
    }
}
