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

import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.GroupAdapter;
import com.example.socialmediatdcproject.adapter.MemberAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.database.GroupDatabase;
import com.example.socialmediatdcproject.database.PostDatabase;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.User;

import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {

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
        RecyclerView recyclerView = requireActivity().findViewById(R.id.second_content_fragment);

        GroupDatabase groupDatabase = new GroupDatabase();

        ArrayList<Group> groups = new ArrayList<>();
        for (Group g: groupDatabase.dataGroups()) {
            groups.add(g);
        }

        GroupAdapter groupAdapter = new GroupAdapter(groups);
        recyclerView.setAdapter(groupAdapter);
        // Thiết lập LayoutManager cho RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Tìm các nút
        Button groupAvailable = view.findViewById(R.id.button_group_available);
        Button groupCreateNew = view.findViewById(R.id.button_group_create_new);

        // Set màu mặc định cho nút "Bài viết"
        groupAvailable.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        groupAvailable.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));

        // Sự kiện khi nhấn vào nút postButton
        groupAvailable.setOnClickListener(v -> {
            // Thiết lập Adapter cho RecyclerView (khởi tạo Adapter với danh sách postsDepartment)
            recyclerView.setAdapter(groupAdapter);
            groupAdapter.notifyDataSetChanged();

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
}

