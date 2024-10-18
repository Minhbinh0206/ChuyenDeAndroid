package com.example.socialmediatdcproject.fragment;

import android.content.Intent;
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
import com.example.socialmediatdcproject.API.BusinessAPI;
import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.EditScreenActivity;
import com.example.socialmediatdcproject.adapter.BussinessAdapter;
import com.example.socialmediatdcproject.adapter.GroupAdapter;
import com.example.socialmediatdcproject.adapter.MemberAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.model.Business;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class PersonalScreenFragment extends Fragment {

    private FirebaseAuth mAuth;

    private RecyclerView recyclerView; // RecyclerView để hiển thị bài viết và doanh nghiệp
    private ArrayList<Post> postsUser = new ArrayList<>(); // Danh sách bài viết
    private ArrayList<Group> groupList = new ArrayList<>(); // Danh sách doanh nghiệp
    private PostAdapter postAdapter; // Adapter cho bài viết
    private GroupAdapter groupAdapter; // Adapter cho doanh nghiệp

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.fragment_personal_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo RecyclerView
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        postAdapter = new PostAdapter(postsUser, requireContext());
        groupAdapter = new GroupAdapter(groupList, requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(postAdapter); // Mặc định hiển thị bài viết


        mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth

        //Làm sao để chuyển id người dùng mới đăng nhập vào
        List<Integer> list = new ArrayList<>();
        list.add(25);
        // Lấy dữ liệu bài viết
        // loadPostFromFirebase(list);

        // Tìm các nút
        Button personalPost = view.findViewById(R.id.personal_user_post);
        Button personalMyGroup = view.findViewById(R.id.personal_user_mygroup);
        Button personalUpdate = view.findViewById(R.id.personal_user_update);

    //Lấy ảnh người dùng truyền qua trang cá nhân

        //tìm ảnh
        ImageView imageUser = view.findViewById(R.id.logo_personal_user_image);
        TextView nameUser = view.findViewById(R.id.name_personnal_user);

        // Lấy thông tin người dùng và cập nhật ảnh
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString(); // Lấy userId từ FirebaseAuth
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(userId, new StudentAPI.StudentCallback() {
                    @Override
                    public void onStudentReceived(Student student) {
                        // Nếu user không null, lấy ảnh và sử dụng Glide để hiển thị
                        if (student != null && student.getAvatar() != null) {
                            String name = student.getFullName();
                            nameUser.setText(name);
                            Glide.with(requireContext())
                                    .load(student.getAvatar())
                                    .circleCrop()
                                    .into(imageUser);
                        }
                    }

                    @Override
                    public void onStudentsReceived(List<Student> students) {

                    }

                    @Override
                    public void onError(String errorMessage) {

                    }

                    @Override
                    public void onStudentDeleted(int studentId) {

                    }

                    //Bắt sự kiện và set màu cho các nút
                    {
                        // Set màu mặc định cho nút "Bài viết"
                        personalPost.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
                        personalPost.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));

                        // Sự kiện khi nhấn vào nút bài viết
                        personalPost.setOnClickListener(v -> {
                            loadPostFromFirebase(list);
                            recyclerView.setAdapter(postAdapter);
                            postAdapter.notifyDataSetChanged();
                            // Cập nhật màu cho các nút
                            updateButtonColors(personalPost, personalUpdate, personalMyGroup);
                        });

                        // Sự kiện khi nhấn vào nút group
                        personalMyGroup.setOnClickListener(v -> {
                        //loadGroupFromFirebase(); // Gọi phương thức để lấy dữ liệu nhóm
                        //recyclerView.setAdapter(groupAdapter); // Hiển thị nhóm
                        //groupAdapter.notifyDataSetChanged();
                        //updateButtonColors(personalMyGroup, personalPost, personalUpdate);
                            // Xử lý cho hiển thị nhóm (điều chỉnh theo nhu cầu của bạn)
                            groupAdapter.notifyDataSetChanged();
                            // Cập nhật màu cho các nút
                            updateButtonColors(personalMyGroup, personalPost, personalUpdate);
                        });


                        // Sự kiện khi nhấn vào nút update
                        personalUpdate.setOnClickListener(v -> {
                            // Chuyển sang màn hình EditScreenActivity (Activity)
                            Intent intent = new Intent(requireContext(), EditScreenActivity.class);
                            startActivity(intent); // Bắt đầu EditScreenActivity

                            // Cập nhật màu sắc cho các nút
                            updateButtonColors(personalUpdate, personalPost, personalMyGroup);
                        });


                    }


                    //Lấy thông tin bài viết từ firebase
                    public void loadPostFromFirebase(List<Integer> listId) {
                        ArrayList<Post> postsList = new ArrayList<>(); // Danh sách bài viết
                        // Tạo instance của PostAPI
                        PostAPI postAPI = new PostAPI();
                        // Tạo một CountDownLatch để chờ cho tất cả các yêu cầu hoàn thành
                        CountDownLatch latch = new CountDownLatch(listId.size());
                        for (int id : listId) {
                            // Lấy bài viết theo groupId
                            postAPI.getPostsByUserId(id, new PostAPI.PostCallback() {
                                @Override
                                public void onPostReceived(Post post) {
                                    // Không sử dụng
                                }
                                @Override
                                public void onPostsReceived(List<Post> posts) {
                                    Log.d("Personal", "onPostsReceived: " + posts.size());
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
                                    postsUser.clear(); // Xóa danh sách hiện tại
                                    postsUser.addAll(postsList); // Thêm tất cả bài viết mới vào danh sách
                                    postAdapter.notifyDataSetChanged(); // Cập nhật adapter
                                    Log.d("POS", "loadPostFromFirebase: " + postAdapter.getItemCount());
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }

                    //Set sự kiện màu cho các nút
                    private void updateButtonColors(Button activeButton, Button inactiveButton, Button inactiveButton2) {
                        // Cập nhật nút đang hoạt động
                        activeButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
                        activeButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));

                        // Cập nhật cho nút không hoạt động đầu tiên
                        inactiveButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
                        inactiveButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));

                        // Cập nhật cho nút không hoạt động thứ hai
                        inactiveButton2.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
                        inactiveButton2.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
                    }


                    private String truncateAddress(String address, int maxLength) {
                        return (address != null && address.length() > maxLength) ?
                                address.substring(0, maxLength) + "..." : address;

                    }


                }

        );
    }
}