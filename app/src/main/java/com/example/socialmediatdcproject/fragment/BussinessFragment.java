package com.example.socialmediatdcproject.fragment;

import android.os.Bundle;
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
import com.example.socialmediatdcproject.adapter.BussinessAdapter;
import com.example.socialmediatdcproject.adapter.MemberAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.database.BussinessDatabase;
import com.example.socialmediatdcproject.database.GroupDatabase;
import com.example.socialmediatdcproject.database.PostDatabase;
import com.example.socialmediatdcproject.model.Bussiness;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.User;

import java.util.ArrayList;
import java.util.List;

public class BussinessFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for this fragment
        return inflater.inflate(R.layout.fragment_business_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo danh sách bài đăng
        ArrayList<Post> postsBussiness = new ArrayList<>();
        ArrayList<Bussiness> bussinesses = new ArrayList<>();

        // Lấy RecyclerView từ layout của Activity (shared_layout)
        RecyclerView recyclerView = requireActivity().findViewById(R.id.second_content_fragment);

        // Mặc định vào bài viết trước
        PostDatabase postDatabase = new PostDatabase();
        BussinessDatabase bussinessDatabase = new BussinessDatabase();

        // Lấy dữ liệu bài viết
        bussinesses = bussinessDatabase.dataBussiness();

        PostAdapter postAdapter = new PostAdapter(postsBussiness);
        BussinessAdapter bussinessAdapter = new BussinessAdapter(bussinesses);

        recyclerView.setAdapter(postAdapter);

        // Thiết lập LayoutManager cho RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Tìm các nút
        Button bussinessPost = view.findViewById(R.id.button_bussiness_post);
        Button bussinessList = view.findViewById(R.id.button_bussiness_list);

        // Set màu mặc định cho nút "Bài viết"
        bussinessPost.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        bussinessPost.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));

        // Sự kiện khi nhấn vào nút postButton
        bussinessPost.setOnClickListener(v -> {
            // Thiết lập Adapter cho RecyclerView (khởi tạo Adapter với danh sách postsDepartment)
            recyclerView.setAdapter(postAdapter);
            postAdapter.notifyDataSetChanged();

            // Cập nhật màu cho các nút
            bussinessList.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            bussinessList.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            bussinessPost.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            bussinessPost.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
        });

        // Sự kiện khi nhấn vào nút memberButton
        bussinessList.setOnClickListener(v -> {
            recyclerView.setAdapter(bussinessAdapter);
            bussinessAdapter.notifyDataSetChanged();

            // Cập nhật màu cho các nút
            bussinessPost.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            bussinessPost.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            bussinessList.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            bussinessList.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
        });
    }
}