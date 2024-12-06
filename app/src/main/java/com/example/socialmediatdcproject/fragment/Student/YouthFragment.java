package com.example.socialmediatdcproject.fragment.Student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.EventAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.R; // Import đúng package chứa R
import com.example.socialmediatdcproject.adapter.EventAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.model.Event;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.User;

import java.util.ArrayList;
import java.util.List;

public class YouthFragment extends Fragment {
    FrameLayout frameLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for this fragment
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_group_default, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        frameLayout = requireActivity().findViewById(R.id.third_content_fragment);

        RecyclerView recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        recyclerView.setVisibility(View.VISIBLE);

        FrameLayout frameLayout = requireActivity().findViewById(R.id.third_content_fragment);
        frameLayout.setVisibility(View.GONE);

        Button post = view.findViewById(R.id.button_group_default_post);
        Button event = view.findViewById(R.id.button_group_default_event);
        changeColorButtonActive(post);
        changeColorButtonNormal(event);

        loadPostFromFirebase();

        post.setOnClickListener(v -> {
            loadPostFromFirebase();

            changeColorButtonActive(post);
            changeColorButtonNormal(event);
        });

        event.setOnClickListener(v -> {
            loadEventFromFirebase();

            changeColorButtonActive(event);
            changeColorButtonNormal(post);
        });
    }

    public void loadPostFromFirebase() {
        // Khởi tạo danh sách bài đăng
        ArrayList<Post> postsTraining = new ArrayList<>();

        // Lấy RecyclerView từ layout của Activity (shared_layout)
        RecyclerView recyclerView = requireActivity().findViewById(R.id.second_content_fragment);

        // Lấy Group của phòng đào tạo
        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getGroupById(User.ID_ADMIN_DOANTHANHNIEN, new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {
                // Set dữ liệu cho fragment
                TextView nameTraining = getView().findViewById(R.id.name_group_default);
                ImageView avatarTraining = getView().findViewById(R.id.avatar_group_default);
                nameTraining.setText(group.getGroupName());

                String imageUrl = group.getAvatar();
                Log.d("TrainingFragment", "Image URL: " + imageUrl);
                // Sử dụng Glide để tải hình ảnh
                Glide.with(requireContext())
                        .load(group.getAvatar())
                        .into(avatarTraining);

                // Mặc định vào bài viết trước
                PostAPI postAPI = new PostAPI();
                postAPI.getPostsByGroupId(group.getGroupId(), new PostAPI.PostCallback() {
                    @Override
                    public void onPostReceived(Post post) {

                    }

                    @Override
                    public void onPostsReceived(List<Post> posts) {
                        // Duyệt qua tất cả các bài viết
                        for (Post p : posts) {
                            postsTraining.add(p); // Thêm vào danh sách
                        }

                        // Lấy dữ liệu bài viết
                        PostAdapter postAdapter = new PostAdapter(postsTraining, requireContext());

                        recyclerView.setAdapter(postAdapter);

                        // Thiết lập LayoutManager cho RecyclerView
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

                        if (postsTraining.isEmpty()) {
                            TextView textView = requireActivity().findViewById(R.id.null_content_notify);
                            textView.setVisibility(View.VISIBLE);
                            textView.setText("Hiện chưa có bài đăng nào");
                        }
                        else {
                            TextView textView = requireActivity().findViewById(R.id.null_content_notify);
                            textView.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onGroupsReceived(List<Group> groups) {


            }
        });
    }

    public void loadEventFromFirebase() {
        // Khởi tạo danh sách bài đăng
        ArrayList<Event> eventList = new ArrayList<>();

        // Lấy RecyclerView từ layout của Activity (shared_layout)
        RecyclerView recyclerView = requireActivity().findViewById(R.id.second_content_fragment);

        // Lấy Group của phòng đào tạo
        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getGroupById(User.ID_ADMIN_DOANTHANHNIEN, new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {
                // Set dữ liệu cho fragment
                TextView nameTraining = getView().findViewById(R.id.name_group_default);
                ImageView avatarTraining = getView().findViewById(R.id.avatar_group_default);
                nameTraining.setText(group.getGroupName());

                String imageUrl = group.getAvatar();
                Log.d("TrainingFragment", "Image URL: " + imageUrl);
                // Sử dụng Glide để tải hình ảnh
                Glide.with(requireContext())
                        .load(group.getAvatar())
                        .into(avatarTraining);

                EventAPI eventAPI = new EventAPI();
                eventAPI.getAllEventByOneAdmin(group.getAdminUserId(), new EventAPI.EventCallback() {
                    @Override
                    public void onEventReceived(Event event) {

                    }

                    @Override
                    public void onEventsReceived(List<Event> events) {
                        // Duyệt qua tất cả các bài viết
                        eventList.addAll(events);

                        // Lấy dữ liệu bài viết
                        EventAdapter eventAdapter = new EventAdapter(eventList, requireContext());

                        recyclerView.setAdapter(eventAdapter);

                        // Thiết lập LayoutManager cho RecyclerView
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

                        if (eventList.isEmpty()) {
                            TextView textView = requireActivity().findViewById(R.id.null_content_notify);
                            textView.setVisibility(View.VISIBLE);
                            textView.setText("Hiện chưa tổ chức sự kiện nào");
                        }
                        else {
                            TextView textView = requireActivity().findViewById(R.id.null_content_notify);
                            textView.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onGroupsReceived(List<Group> groups) {


            }
        });
    }

    public void changeColorButtonActive(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
    }

    public void changeColorButtonNormal(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.buttonDefault));
        btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.black));
    }

}
