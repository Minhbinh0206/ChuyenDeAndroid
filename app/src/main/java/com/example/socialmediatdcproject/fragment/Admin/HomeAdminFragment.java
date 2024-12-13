package com.example.socialmediatdcproject.fragment.Admin;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.BusinessAPI;
import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.EventAPI;
import com.example.socialmediatdcproject.API.FilterPostsAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.NotifyAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.HomeAdminActivity;
import com.example.socialmediatdcproject.activity.SharedActivity;
import com.example.socialmediatdcproject.adapter.EventAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Business;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Event;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Notify;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeAdminFragment extends Fragment {
    private RecyclerView recyclerView2;
    FrameLayout frameLayout;
    ArrayList<Post> postList;
    PostAdapter postAdapter;
    ArrayList<Post> allPosts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.fragment_first_admin_content, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postList = new ArrayList<>();
        allPosts = new ArrayList<>();
        recyclerView2 = requireActivity().findViewById(R.id.second_content_fragment);
        postAdapter = new PostAdapter(postList, requireContext());
        recyclerView2.setAdapter(postAdapter);
        recyclerView2.setLayoutManager(new LinearLayoutManager(requireContext()));

        FrameLayout third = requireActivity().findViewById(R.id.third_content_fragment);
        third.setVisibility(View.VISIBLE);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.third_content_fragment, new AdminMainFragment());
        fragmentTransaction.commit();

        third.setVisibility(View.VISIBLE);

        loadPostsFromFirebase();
    }

    private void loadPostsFromFirebase() {
        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getAllGroups(new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {
                // Xử lý nếu cần
            }

            @Override
            public void onGroupsReceived(List<Group> groups) {
                for (Group g : groups) {
                    if (g.isGroupDefault()) {
                        DatabaseReference postReference = FirebaseDatabase.getInstance()
                                .getReference("Posts")
                                .child(String.valueOf(g.getGroupId()));

                        // Lắng nghe sự kiện cho bài viết của nhóm
                        postReference.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                Post post = snapshot.getValue(Post.class);
                                if (post != null) {
                                    // Thêm bài viết vào danh sách chung
                                    handlePostAddition(post);
                                }
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                // Không cần thiết trong trường hợp này
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("PostAPI", "Error listening to post changes: " + error.getMessage());
                            }
                        });
                    }
                }
            }
        });
    }

    private void handlePostAddition(Post post) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        try {
            Date date1 = format.parse(post.getCreatedAt());

            // Kiểm tra nếu date1 hợp lệ
            if (date1 == null) {
                return;
            }

            final boolean[] isAdded = {false};
            int insertPosition = -1; // Vị trí để thêm bài viết

            // Duyệt qua từng bài viết trong postList để so sánh với bài viết mới
            for (int i = 0; i < postList.size(); i++) {
                Post p = postList.get(i);
                Date date2 = format.parse(p.getCreatedAt());

                // Kiểm tra nếu date2 hợp lệ
                if (date2 == null) {
                    continue;
                }

                // So sánh ngày
                if (date1.after(date2)) {
                    // Nếu bài viết mới hơn bài viết hiện tại
                    insertPosition = i; // Ghi nhận vị trí cần chèn bài viết mới
                    break; // Dừng lại khi tìm được vị trí thích hợp
                }
            }

            // Nếu không tìm thấy vị trí thích hợp, bài viết mới nhất sẽ được thêm vào cuối danh sách
            if (insertPosition == -1) {
                insertPosition = postList.size(); // Thêm vào cuối danh sách
            }

            // Nếu bài viết không cần lọc
            if (!post.isFilter()) {
                postList.add(insertPosition, post); // Thêm bài viết vào vị trí thích hợp
                isAdded[0] = true;
            } else {
                // Nếu bài viết cần lọc
                StudentAPI studentAPI = new StudentAPI();
                studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                    @Override
                    public void onStudentReceived(Student student) {
                        FilterPostsAPI filterPostsAPI = new FilterPostsAPI();
                        filterPostsAPI.findUserInReceive(post, student.getUserId(), new FilterPostsAPI.UserInReceiveCallback() {
                            @Override
                            public void onResult(boolean isFound) {
                                if (isFound) {
                                    postList.add(0, post); // Thêm bài viết vào đầu danh sách nếu được lọc
                                    postAdapter.notifyItemInserted(0); // Thông báo RecyclerView có phần tử mới ở đầu
                                    recyclerView2.scrollToPosition(0); // Cuộn lên đầu để hiển thị bài viết mới

                                    isAdded[0] = true;
                                }
                            }
                        });
                    }

                    @Override
                    public void onStudentsReceived(List<Student> students) {
                    }
                });

                AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();
                adminDefaultAPI.getAdminDefaultByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDefaultAPI.AdminDefaultCallBack() {
                    @Override
                    public void onUserReceived(AdminDefault adminDefault) {
                        FilterPostsAPI filterPostsAPI = new FilterPostsAPI();
                        filterPostsAPI.findUserInReceive(post, adminDefault.getUserId(), new FilterPostsAPI.UserInReceiveCallback() {
                            @Override
                            public void onResult(boolean isFound) {
                                if (isFound) {
                                    postList.add(0, post); // Thêm bài viết vào đầu danh sách nếu được lọc
                                    postAdapter.notifyItemInserted(0); // Thông báo RecyclerView có phần tử mới ở đầu
                                    recyclerView2.scrollToPosition(0); // Cuộn lên đầu để hiển thị bài viết mới

                                    isAdded[0] = true;
                                }
                            }
                        });
                    }

                    @Override
                    public void onUsersReceived(List<AdminDefault> adminDefault) {

                    }
                });

                AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
                adminDepartmentAPI.getAdminDepartmentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
                    @Override
                    public void onUserReceived(AdminDepartment adminDepartment) {
                        FilterPostsAPI filterPostsAPI = new FilterPostsAPI();
                        filterPostsAPI.findUserInReceive(post, adminDepartment.getUserId(), new FilterPostsAPI.UserInReceiveCallback() {
                            @Override
                            public void onResult(boolean isFound) {
                                if (isFound) {
                                    postList.add(0, post); // Thêm bài viết vào đầu danh sách nếu được lọc
                                    postAdapter.notifyItemInserted(0); // Thông báo RecyclerView có phần tử mới ở đầu
                                    recyclerView2.scrollToPosition(0); // Cuộn lên đầu để hiển thị bài viết mới

                                    isAdded[0] = true;
                                }
                            }
                        });
                    }

                    @Override
                    public void onUsersReceived(List<AdminDepartment> adminDepartment) {

                    }

                    @Override
                    public void onError(String s) {

                    }
                });

                AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
                adminBusinessAPI.getAdminBusinessByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminBusinessAPI.AdminBusinessCallBack() {
                    @Override
                    public void onUserReceived(AdminBusiness adminBusiness) {
                        FilterPostsAPI filterPostsAPI = new FilterPostsAPI();
                        filterPostsAPI.findUserInReceive(post, adminBusiness.getUserId(), new FilterPostsAPI.UserInReceiveCallback() {
                            @Override
                            public void onResult(boolean isFound) {
                                if (isFound) {
                                    postList.add(0, post); // Thêm bài viết vào đầu danh sách nếu được lọc
                                    postAdapter.notifyItemInserted(0); // Thông báo RecyclerView có phần tử mới ở đầu
                                    recyclerView2.scrollToPosition(0); // Cuộn lên đầu để hiển thị bài viết mới

                                    isAdded[0] = true;
                                }
                            }
                        });
                    }

                    @Override
                    public void onUsersReceived(List<AdminBusiness> adminBusiness) {

                    }

                    @Override
                    public void onError(String s) {

                    }
                });
            }

            // Cập nhật RecyclerView nếu bài viết được thêm vào danh sách
            if (isAdded[0]) {
                postAdapter.notifyItemInserted(insertPosition); // Thông báo RecyclerView về sự thay đổi
                recyclerView2.scrollToPosition(insertPosition); // Cuộn đến vị trí bài viết mới thêm
            }

        } catch (ParseException e) {
            Log.e("DateParse", "Error parsing date: " + e.getMessage());
        }
    }
}
