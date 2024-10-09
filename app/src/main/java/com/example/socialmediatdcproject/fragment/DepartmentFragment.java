package com.example.socialmediatdcproject.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.MemberAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.database.GroupDatabase;
import com.example.socialmediatdcproject.database.PostDatabase;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DepartmentFragment extends Fragment {
    private RecyclerView memberRecyclerView; // RecyclerView để hiển thị danh sách người dùng
    private MemberAdapter memberAdapter; // Adapter cho RecyclerView
    private List<User> userList; // Danh sách người dùng

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.fragment_department_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo danh sách người dùng và RecyclerView
        userList = new ArrayList<>();
        memberRecyclerView = view.findViewById(R.id.second_content_fragment); // Giả sử bạn đã thêm RecyclerView này trong layout

        loadPostFromFirebase();

        // Tìm các nút
        Button infoButton = view.findViewById(R.id.button_department_info);
        Button postButton = view.findViewById(R.id.button_department_post);
        Button memberButton = view.findViewById(R.id.button_department_member);

        // Set màu mặc định cho nút "Bài viết"
        postButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        postButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));

        // Sự kiện khi nhấn vào nút memberButton
        memberButton.setOnClickListener(v -> {
            loadUsersByGroupId(3); // Tải người dùng từ groupId 3

            // Cập nhật màu cho các nút
            infoButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            infoButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            postButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            postButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            memberButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
            memberButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
        });
    }

    public void loadPostFromFirebase() {
        // Khởi tạo danh sách bài đăng
        ArrayList<Post> postsTraining = new ArrayList<>();

        // Lấy RecyclerView từ layout của Activity (shared_layout)
        RecyclerView recyclerView = requireActivity().findViewById(R.id.second_content_fragment);

        // Lấy Group của phòng đào tạo
        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getGroupById(User.ID_ADMIN_DEPARTMENT_CNTT, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Nếu hàm get by ID thì trả về là 1 đối tượng, cụ thể ở đây là Group
                if (snapshot.exists()) {
                    // Tạo mới 1 thằng group để lưu giá trị của firebase trả về
                    Group g = snapshot.getValue(Group.class);

                    // Set dữ liệu cho fragment
                    TextView nameTraining = getView().findViewById(R.id.name_department);
                    ImageView avatarTraining = getView().findViewById(R.id.logo_department);
                    nameTraining.setText(g.getGroupName());

                    String imageUrl = g.getAvatar();
                    Log.d("TrainingFragment", "Image URL: " + imageUrl);
                    // Sử dụng Glide để tải hình ảnh
                    Glide.with(requireContext())
                            .load(g.getAvatar())
                            .into(avatarTraining);

                    // Mặc định vào bài viết trước
                    PostAPI postAPI = new PostAPI();
                    postAPI.getPostByGroupId(g.getGroupId(), new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                            if (postSnapshot.exists()) {
                                // Duyệt qua tất cả các bài viết
                                for (DataSnapshot post : postSnapshot.getChildren()) {
                                    Post p = post.getValue(Post.class); // Lấy từng bài viết
                                    postsTraining.add(p); // Thêm vào danh sách
                                }

                                // Lấy dữ liệu bài viết
                                PostAdapter postAdapter = new PostAdapter(postsTraining);
                                recyclerView.setAdapter(postAdapter);

                                // Thiết lập LayoutManager cho RecyclerView
                                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                            } else {
                                // Xử lý trường hợp không tìm thấy bài viết
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Xử lý lỗi nếu có
                        }
                    });
                } else {
                    // Xử lý trường hợp không tìm thấy nhóm
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });
    }

    private void loadUsersByGroupId(int groupId) {
        // Khởi tạo API để lấy người dùng
        UserAPI userAPI = new UserAPI();
        userAPI.getUsersByGroupId(groupId, new UserAPI.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                // Không sử dụng ở đây
            }

            @Override
            public void onUsersReceived(List<User> users) {
                userList.clear(); // Xóa danh sách trước khi thêm mới
                userList.addAll(users); // Thêm danh sách người dùng vào
                memberAdapter = new MemberAdapter(userList); // Tạo adapter cho RecyclerView
                memberRecyclerView.setAdapter(memberAdapter); // Thiết lập adapter cho RecyclerView
                memberRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext())); // Thiết lập LayoutManager
            }
        });
    }
}
