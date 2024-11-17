package com.example.socialmediatdcproject.fragment.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.FilterPostsAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.SharedActivity;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AdminFragment extends Fragment {
    private RecyclerView recyclerView; // RecyclerView để hiển thị danh sách người dùng
    FrameLayout frameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.admin_department_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo danh sách người dùng và RecyclerView
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        frameLayout = requireActivity().findViewById(R.id.third_content_fragment);

        ImageView avatar = view.findViewById(R.id.admin_department_avatar);
        TextView name = view.findViewById(R.id.admin_department_name);
        ConstraintLayout constraintLayout = view.findViewById(R.id.admin_department_background);

//        loadPostsFromFirebase();

        AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();

        adminDepartmentAPI.getAdminDepartmentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                // Kiểm tra nếu fragment đã được đính kèm vào context
                if (isAdded() && adminDepartment.getDepartmentId() != -1) {
                    Glide.with(requireContext())
                            .load(adminDepartment.getAvatar())
                            .circleCrop()
                            .into(avatar);

                    name.setText(adminDepartment.getFullName());
                }
            }

            @Override
            public void onUsersReceived(List<AdminDepartment> adminDepartment) {
                // Xử lý khi nhận được danh sách AdminDepartment
            }

            @Override
            public void onError(String s) {
                // Xử lý lỗi
            }
        });
        adminBusinessAPI.getAdminBusinessByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminBusinessAPI.AdminBusinessCallBack() {
            @Override
            public void onUserReceived(AdminBusiness adminBusiness) {
                // Kiểm tra nếu fragment đã được đính kèm vào context
                if (isAdded() && adminBusiness.getBusinessId() != -1) {
                    Glide.with(requireContext())
                            .load(adminBusiness.getAvatar())
                            .circleCrop()
                            .into(avatar);

                    name.setText(adminBusiness.getFullName());
                }
            }

            @Override
            public void onUsersReceived(List<AdminBusiness> adminBusiness) {

            }

            @Override
            public void onError(String s) {

            }
        });
        adminDefaultAPI.getAdminDefaultByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDefaultAPI.AdminDefaultCallBack() {
            @Override
            public void onUserReceived(AdminDefault adminDefault) {
                // Kiểm tra nếu fragment đã được đính kèm vào context
                if (isAdded() && adminDefault.getUserId() != -1) {
                    Glide.with(requireContext())
                            .load(adminDefault.getAvatar())
                            .circleCrop()
                            .into(avatar);

                    name.setText(adminDefault.getFullName());
                }
            }

            @Override
            public void onUsersReceived(List<AdminDefault> adminDefault) {

            }
        });
    }

//    private void loadPostsFromFirebase() {
//        PostAPI postAPI = new PostAPI();
//
//        postAPI.getAllPosts(new PostAPI.PostCallback() {
//            @Override
//            public void onPostReceived(Post post) {
//            }
//
//            @Override
//            public void onPostsReceived(List<Post> posts) {
//                ArrayList<Post> postList = new ArrayList<>();
//                ArrayList<Post> filteredPosts = new ArrayList<>();
//                int[] processedPostsCount = {0};  // Biến đếm số bài viết đã xử lý
//
//                for (Post p : posts) {
//                    UserAPI userAPI = new UserAPI();
//                    userAPI.getUserById(p.getUserId(), new UserAPI.UserCallback() {
//                        @Override
//                        public void onUserReceived(User user) {
//                            if (!p.isFilter()) {
//                                if (user.getRoleId() == 2 || user.getRoleId() == 3 || user.getRoleId() == 4 || user.getRoleId() == 5) {
//                                    postList.add(p);
//                                }
//                                processedPostsCount[0]++;
//                                if (processedPostsCount[0] == posts.size()) {
//                                    postList.addAll(filteredPosts);  // Thêm tất cả bài viết đã lọc vào danh sách chung
//
//                                    // Setup RecyclerView với Adapter sau khi tất cả các bài viết đã được xử lý
//                                    PostAdapter postAdapter = new PostAdapter(postList, requireActivity());
//                                    recyclerView.setAdapter(postAdapter);
//                                    recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
//                                }
//                            } else {
//                                StudentAPI studentAPI = new StudentAPI();
//                                studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
//                                    @Override
//                                    public void onStudentReceived(Student student) {
//                                        FilterPostsAPI filterPostsAPI = new FilterPostsAPI();
//                                        filterPostsAPI.findUserInReceive(p.getPostId(), student.getUserId(), new FilterPostsAPI.UserInReceiveCallback() {
//                                            @Override
//                                            public void onResult(boolean isFound) {
//                                                if (isFound) {
//                                                    filteredPosts.add(p);
//                                                }
//                                                processedPostsCount[0]++;
//                                                if (processedPostsCount[0] == posts.size()) {
//                                                    postList.addAll(filteredPosts);  // Thêm tất cả bài viết đã lọc vào danh sách chung
//
//                                                    // Setup RecyclerView với Adapter sau khi tất cả các bài viết đã được xử lý
//
//                                                    PostAdapter postAdapter = new PostAdapter(postList, requireActivity());
//                                                    recyclerView.setAdapter(postAdapter);
//                                                    recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
//                                                }
//                                            }
//                                        });
//                                    }
//
//                                    @Override
//                                    public void onStudentsReceived(List<Student> students) {
//                                    }
//                                });
//                            }
//                        }
//
//                        @Override
//                        public void onUsersReceived(List<User> users) {
//                        }
//                    });
//                }
//            }
//        });
//    }

}
