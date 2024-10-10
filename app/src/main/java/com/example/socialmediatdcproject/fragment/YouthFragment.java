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

import com.example.socialmediatdcproject.R; // Import đúng package chứa R
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

public class YouthFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for this fragment
        return inflater.inflate(R.layout.fragment_youth_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo danh sách bài đăng
        ArrayList<Post> postsYouth = new ArrayList<>();
        ArrayList<Group> groupsYouth = new ArrayList<>();

        // Lấy RecyclerView từ layout của Activity (shared_layout)
        RecyclerView recyclerView = requireActivity().findViewById(R.id.second_content_fragment);

        // Mặc định vào bài viết trước
        PostDatabase postDatabase = new PostDatabase();
        GroupDatabase groupDatabase = new GroupDatabase();

        for (Group g : groupDatabase.dataGroups()) {
            if (g.getAdminUserId() == User.ID_ADMIN_DOANTHANHNIEN){
                if (g.getGroupId() == User.ID_ADMIN_DOANTHANHNIEN) {
                    continue;
                }
                else {
                    groupsYouth.add(g);
                }
            }
        }

        // Lấy dữ liệu bài viết
        for (Post p : postDatabase.dataPost()) {
            if (p.getUserId() == User.ID_ADMIN_DOANTHANHNIEN) {
                postsYouth.add(p);
            }
        }

        PostAdapter postAdapter = new PostAdapter(postsYouth);
        GroupAdapter groupAdapter = new GroupAdapter(groupsYouth);

        recyclerView.setAdapter(postAdapter);

        // Thiết lập LayoutManager cho RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Tìm các nút
        Button infoButton = view.findViewById(R.id.button_youth_info);
        Button postButton = view.findViewById(R.id.button_youth_post);
        Button groupButton = view.findViewById(R.id.button_youth_group);
        Button memberButton = view.findViewById(R.id.button_youth_member);

        // Set màu mặc định cho nút "Bài viết"
        postButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        postButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));

        // Sự kiện khi nhấn vào nút postButton
        postButton.setOnClickListener(v -> {
            // Thiết lập Adapter cho RecyclerView (khởi tạo Adapter với danh sách postsDepartment)
            recyclerView.setAdapter(postAdapter);
            postAdapter.notifyDataSetChanged();

            // Cập nhật màu cho các nút
            groupButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            groupButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            infoButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            infoButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            memberButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            memberButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            postButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            postButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
        });

        // Sự kiện khi nhấn vào nút memberButton
        memberButton.setOnClickListener(v -> {

            // Cập nhật màu cho các nút
            groupButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            groupButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            infoButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            infoButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            postButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            postButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            memberButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            memberButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
        });

        // Sự kiện khi nhấn vào nút groupButton
        groupButton.setOnClickListener(v -> {
            recyclerView.setAdapter(groupAdapter);
            groupAdapter.notifyDataSetChanged();

            // Cập nhật màu cho các nút
            groupButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            groupButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
            infoButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            infoButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            postButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            postButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            memberButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            memberButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        });
    }
}
