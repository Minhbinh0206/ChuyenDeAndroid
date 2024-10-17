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

import com.example.socialmediatdcproject.API.BusinessAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.BussinessAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.model.Business;
import com.example.socialmediatdcproject.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class BussinessFragment extends Fragment {
    private RecyclerView recyclerView; // RecyclerView để hiển thị bài viết và doanh nghiệp
    private ArrayList<Post> postsBusiness = new ArrayList<>();
    private ArrayList<Business> businessesList = new ArrayList<>();
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
        postAdapter = new PostAdapter(postsBusiness, requireContext());
        businessAdapter = new BussinessAdapter(businessesList);
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
            loadPostFromFirebase(list);
            recyclerView.setAdapter(postAdapter); // Hiển thị doanh nghiệp
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
        ArrayList<Post> postsList = new ArrayList<>(); // Danh sách bài viết

        // Tạo instance của PostAPI
        PostAPI postAPI = new PostAPI();

        // Tạo một CountDownLatch để chờ cho tất cả các yêu cầu hoàn thành
        CountDownLatch latch = new CountDownLatch(listId.size());

        for (int id : listId) {
            // Lấy bài viết theo groupId
            postAPI.getPostByGroupId(id, new PostAPI.PostCallback() {
                @Override
                public void onPostReceived(Post post) {
                    // Không sử dụng
                }

                @Override
                public void onPostsReceived(List<Post> posts) {
                    Log.d("PostBusiness", "onPostsReceived: " + posts.size());
                    postsList.addAll(posts); // Thêm tất cả bài viết nhận được
                    latch.countDown(); // Đếm ngược
                }
            });
        }

        // Chờ cho tất cả yêu cầu hoàn thành
        new Thread(() -> {
            try {
                latch.await(); // Chờ cho tất cả các yêu cầu hoàn tất
                requireActivity().runOnUiThread(() -> {
                    postsBusiness.clear(); // Xóa danh sách hiện tại
                    postsBusiness.addAll(postsList); // Thêm tất cả bài viết mới vào danh sách
                    postAdapter.notifyDataSetChanged(); // Cập nhật adapter
                    Log.d("POS", "loadPostFromFirebase: " + postAdapter.getItemCount());
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadBusinesses() {
        BusinessAPI bussinessAPI = new BusinessAPI();
        bussinessAPI.getAllBusinesses(new BusinessAPI.BusinessCallback() {
            @Override
            public void onBusinessReceived(Business business) {

            }

            @Override
            public void onBusinessesReceived(List<Business> businesses) {
                for (Business business: businesses) {
                    if (business != null) {
                        businessesList.add(business); // Thêm doanh nghiệp vào danh sách
                    }
                }
                businessAdapter.notifyDataSetChanged(); // Cập nhật adapter
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

