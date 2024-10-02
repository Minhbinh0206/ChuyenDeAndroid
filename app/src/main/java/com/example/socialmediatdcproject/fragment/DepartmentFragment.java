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
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.database.PostDatabase;
import com.example.socialmediatdcproject.model.Post;

import java.util.ArrayList;

public class DepartmentFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.fragment_department, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo danh sách bài đăng
        ArrayList<Post> postsDepartment = new ArrayList<>();

        // Lấy RecyclerView từ layout của Activity (shared_layout)
        RecyclerView recyclerView = requireActivity().findViewById(R.id.second_content_fragment);

        // Mặc định vào bài viết trước
        PostDatabase postDatabase = new PostDatabase();
        for (Post p : postDatabase.dataPost()) {
            if (p.getUserId() == 10) {  // Điều kiện lấy bài đăng theo userId
                postsDepartment.add(p);
            }
        }

        // Kiểm tra RecyclerView không null
        if (recyclerView != null) {
            // Thiết lập LayoutManager cho RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

            // Thiết lập Adapter cho RecyclerView (khởi tạo Adapter với danh sách postsDepartment)
            PostAdapter postAdapter = new PostAdapter(postsDepartment);
            recyclerView.setAdapter(postAdapter);

            // Tìm các nút
            Button infoButton = view.findViewById(R.id.button_department_info);
            Button postButton = view.findViewById(R.id.button_department_post);
            Button memberButton = view.findViewById(R.id.button_department_member);

            // Set màu mặc định cho nút "Bài viết"
            postButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            postButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));

            // Sự kiện khi nhấn vào nút postButton
            postButton.setOnClickListener(v -> {
                // Thông báo cho Adapter rằng dữ liệu đã thay đổi để cập nhật lại giao diện
                postAdapter.notifyDataSetChanged();

                // Cập nhật màu cho các nút
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
                infoButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
                infoButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
                postButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
                postButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
                memberButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
                memberButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
            });
        } else {
            // Log lỗi nếu không tìm thấy RecyclerView
            Log.e("DepartmentFragment", "RecyclerView not found");
        }
    }
}
