package com.example.socialmediatdcproject.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.GroupAdapter;
import com.example.socialmediatdcproject.adapter.MemberAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {
    RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.fragment_group_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Lấy RecyclerView từ layout của Activity (shared_layout)
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);

        loadGroups();

        // Tìm các nút
        Button groupAvailable = view.findViewById(R.id.button_group_available);
        Button groupCreateNew = view.findViewById(R.id.button_group_create_new);

        // Set màu mặc định cho nút "Bài viết"
        groupAvailable.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        groupAvailable.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));

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

            // Cập nhật màu cho các nút
            groupAvailable.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            groupAvailable.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            groupCreateNew.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            groupCreateNew.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
        });
    }

    public void loadGroups(){
        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getAllGroups(new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {

            }

            @Override
            public void onGroupsReceived(List<Group> groups) {
                ArrayList<Group> groupsList = new ArrayList<>();
                for (Group group : groupsList) {
                    if (group != null) {
                        groups.add(group);
                    }
                }
                GroupAdapter groupAdapter = new GroupAdapter(groups, requireContext());
                recyclerView.setAdapter(groupAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            }
        });
    }
}

