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

import com.example.socialmediatdcproject.API.BussinessAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.BussinessAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.model.Bussiness;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class BussinessFragment extends Fragment {
    private RecyclerView recyclerView; // RecyclerView để hiển thị bài viết và doanh nghiệp
    private ArrayList<Post> postsBusiness = new ArrayList<>();
    private ArrayList<Bussiness> businesses = new ArrayList<>();
    private PostAdapter postAdapter;
    private BussinessAdapter businessAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.fragment_business_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo RecyclerView
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        postAdapter = new PostAdapter(postsBusiness);
        businessAdapter = new BussinessAdapter(businesses);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(postAdapter); // Mặc định hiển thị bài viết

        List<Integer> list = new ArrayList<>();
        list.add(14);
        list.add(15);
        list.add(16);
        list.add(17);
        list.add(18);
        list.add(19);
        list.add(20);

        // Lấy dữ liệu bài viết
        loadPostFromFirebase(list);

        // Tìm các nút
        Button businessPostButton = view.findViewById(R.id.button_bussiness_post);
        Button businessListButton = view.findViewById(R.id.button_bussiness_list);

        // Set màu mặc định cho nút "Bài viết"
        businessPostButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        businessPostButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));

        // Sự kiện khi nhấn vào nút bài viết
        businessPostButton.setOnClickListener(v -> {
            recyclerView.setAdapter(postAdapter); // Hiển thị bài viết
            postAdapter.notifyDataSetChanged();

            // Cập nhật màu cho các nút
            updateButtonColors(businessPostButton, businessListButton);
        });

        // Sự kiện khi nhấn vào nút danh sách doanh nghiệp
        businessListButton.setOnClickListener(v -> {
            loadBusinesses(); // Tải danh sách doanh nghiệp
            recyclerView.setAdapter(businessAdapter); // Hiển thị doanh nghiệp
            businessAdapter.notifyDataSetChanged();

            // Cập nhật màu cho các nút
            updateButtonColors(businessListButton, businessPostButton);
        });
    }

    public void loadPostFromFirebase(List<Integer> listId) {
        ArrayList<Post> posts = new ArrayList<>(); // Danh sách bài viết

        // Tạo instance của PostAPI
        PostAPI postAPI = new PostAPI();

        // Tạo một CountDownLatch để chờ cho tất cả các yêu cầu hoàn thành
        CountDownLatch latch = new CountDownLatch(listId.size());

        for (int id : listId) {
            // Lấy bài viết theo groupId
            postAPI.getPostByGroupId(id, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                    // Kiểm tra nếu có bài viết
                    if (postSnapshot.exists()) {
                        for (DataSnapshot post : postSnapshot.getChildren()) {
                            Post p = post.getValue(Post.class);
                            if (p != null) {
                                posts.add(p);
                            }
                        }
                    } else {
                        Log.d("Post", "No posts found for groupId: " + id);
                    }
                    latch.countDown(); // Giảm số lượng yêu cầu còn lại
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Post", "Error: " + error.getMessage());
                    latch.countDown(); // Đảm bảo rằng lệnh đếm giảm xuống ngay cả khi có lỗi
                }
            });
        }

        // Chờ cho tất cả yêu cầu hoàn thành
        new Thread(() -> {
            try {
                latch.await(); // Chờ cho tất cả các yêu cầu hoàn tất
                requireActivity().runOnUiThread(() -> {
                    postsBusiness.clear(); // Xóa danh sách hiện tại
                    postsBusiness.addAll(posts); // Thêm tất cả bài viết mới vào danh sách
                    postAdapter.notifyDataSetChanged(); // Cập nhật adapter
                    Log.d("POS", "loadPostFromFirebase: " + postAdapter.getItemCount());
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void loadBusinesses() {
        BussinessAPI bussinessAPI = new BussinessAPI();
        bussinessAPI.getAllBusinesses(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                businesses.clear(); // Xóa danh sách hiện tại
                for (DataSnapshot businessSnapshot : dataSnapshot.getChildren()) {
                    Bussiness business = businessSnapshot.getValue(Bussiness.class);
                    if (business != null) {
                        businesses.add(business); // Thêm doanh nghiệp vào danh sách
                    }
                }
                businessAdapter.notifyDataSetChanged(); // Cập nhật adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }


    private void updateButtonColors(Button activeButton, Button inactiveButton) {
        activeButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        activeButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));

        inactiveButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        inactiveButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
    }
}
