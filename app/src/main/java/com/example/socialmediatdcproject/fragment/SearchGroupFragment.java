package com.example.socialmediatdcproject.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

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

    private GroupAdapter adapter;
    private List<Group> groupList;
    private EditText editTextSearch;
    private ImageButton iconSearchGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_group, container, false);

        // Ánh xạ các view từ layout
        editTextSearch = view.findViewById(R.id.edit_text_search);
        iconSearchGroup = view.findViewById(R.id.icon_search_group);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_find_group);

        // Khởi tạo danh sách nhóm và adapter
        groupList = new ArrayList<>();
        adapter = new GroupAdapter(groupList, requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Sự kiện khi nhấn vào nút tìm kiếm
        iconSearchGroup.setOnClickListener(v -> searchGroupByName(editTextSearch.getText().toString()));

        // TextWatcher để theo dõi và lọc danh sách nhóm khi thay đổi text trong EditText
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Tìm kiếm khi người dùng nhập text
                searchGroupByName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    // Hàm để tìm kiếm nhóm dựa trên văn bản nhập
    private void searchGroupByName(String query) {
        if (query.isEmpty()) {
            groupList.clear();
            adapter.notifyDataSetChanged();
            return;
        }

        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getGroupByName(query, new GroupAPI.GroupCallback() {
            @Override
            public void onGroupsReceived(List<Group> groups) {
                groupList.clear();
                groupList.addAll(groups);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onGroupReceived(Group group) {
                // Không sử dụng trong tìm kiếm
            }
        });
    }
}
