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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;

public class HomeAdminFragment extends Fragment {
    private RecyclerView recyclerView; // RecyclerView để hiển thị danh sách người dùng
    private RecyclerView recyclerView2;
    FrameLayout frameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.fragment_home_admin, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FrameLayout third = requireActivity().findViewById(R.id.third_content_fragment);

        UserAPI userAPI = new UserAPI();
        userAPI.getAllUsers(new UserAPI.UserCallback() {
            @Override
            public void onUserReceived(User user) {

            }

            @Override
            public void onUsersReceived(List<User> users) {
                for (User user : users) {
                    if (user.getRoleId() == 2 || user.getRoleId() == 3 || user.getRoleId() == 4 || user.getRoleId() == 5) {
                        third.setVisibility(View.GONE);
                    }
                    else {
                        third.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        recyclerView = view.findViewById(R.id.frame_event);

        loadEventsFromFirebase();

        loadPostsFromFirebase();
    }

    private void loadPostsFromFirebase() {
        PostAPI postAPI = new PostAPI();

        postAPI.getAllPosts(new PostAPI.PostCallback() {
            @Override
            public void onPostReceived(Post post) {
            }

            @Override
            public void onPostsReceived(List<Post> posts) {
                ArrayList<Post> postList = new ArrayList<>();
                ArrayList<Post> filteredPosts = new ArrayList<>();
                int[] processedPostsCount = {0};  // Biến đếm số bài viết đã xử lý
                AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
                AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
                AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();

                for (Post p : posts) {
                    UserAPI userAPI = new UserAPI();
                    userAPI.getUserById(p.getUserId(), new UserAPI.UserCallback() {
                        @Override
                        public void onUserReceived(User user) {
                            if (!p.isFilter()) {
                                if (user.getRoleId() == 2 || user.getRoleId() == 3 || user.getRoleId() == 4 || user.getRoleId() == 5) {
                                    postList.add(p);
                                }
                                processedPostsCount[0]++;
                                if (processedPostsCount[0] == posts.size()) {
                                    postList.addAll(filteredPosts);  // Thêm tất cả bài viết đã lọc vào danh sách chung

                                    // Setup RecyclerView với Adapter sau khi tất cả các bài viết đã được xử lý
                                    RecyclerView recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
                                    PostAdapter postAdapter = new PostAdapter(postList, requireContext());
                                    recyclerView.setAdapter(postAdapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                                }
                            } else {
                                adminDepartmentAPI.getAdminDepartmentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
                                    @Override
                                    public void onUserReceived(AdminDepartment adminDepartment) {
                                        FilterPostsAPI filterPostsAPI = new FilterPostsAPI();
                                        filterPostsAPI.findUserInReceive(p.getPostId(), adminDepartment.getUserId(), new FilterPostsAPI.UserInReceiveCallback() {
                                            @Override
                                            public void onResult(boolean isFound) {
                                                if (isFound) {
                                                    filteredPosts.add(p);
                                                }
                                                processedPostsCount[0]++;
                                                if (processedPostsCount[0] == posts.size()) {
                                                    postList.addAll(filteredPosts);  // Thêm tất cả bài viết đã lọc vào danh sách chung

                                                    // Setup RecyclerView với Adapter sau khi tất cả các bài viết đã được xử lý
                                                    RecyclerView recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
                                                    PostAdapter postAdapter = new PostAdapter(postList, requireContext());
                                                    recyclerView.setAdapter(postAdapter);
                                                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
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
                                adminBusinessAPI.getAdminBusinessByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminBusinessAPI.AdminBusinessCallBack() {
                                    @Override
                                    public void onUserReceived(AdminBusiness adminBusiness) {
                                        FilterPostsAPI filterPostsAPI = new FilterPostsAPI();
                                        filterPostsAPI.findUserInReceive(p.getPostId(), adminBusiness.getUserId(), new FilterPostsAPI.UserInReceiveCallback() {
                                            @Override
                                            public void onResult(boolean isFound) {
                                                if (isFound) {
                                                    filteredPosts.add(p);
                                                }
                                                processedPostsCount[0]++;
                                                if (processedPostsCount[0] == posts.size()) {
                                                    postList.addAll(filteredPosts);  // Thêm tất cả bài viết đã lọc vào danh sách chung

                                                    // Setup RecyclerView với Adapter sau khi tất cả các bài viết đã được xử lý
                                                    RecyclerView recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
                                                    PostAdapter postAdapter = new PostAdapter(postList, requireContext());
                                                    recyclerView.setAdapter(postAdapter);
                                                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
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
                                adminDefaultAPI.getAdminDefaultByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDefaultAPI.AdminDefaultCallBack() {
                                    @Override
                                    public void onUserReceived(AdminDefault adminDefault) {
                                        if (!adminDefault.getAdminType().equals("Super")) {
                                            FilterPostsAPI filterPostsAPI = new FilterPostsAPI();
                                            filterPostsAPI.findUserInReceive(p.getPostId(), adminDefault.getUserId(), new FilterPostsAPI.UserInReceiveCallback() {
                                                @Override
                                                public void onResult(boolean isFound) {
                                                    if (isFound) {
                                                        filteredPosts.add(p);
                                                    }
                                                    processedPostsCount[0]++;
                                                    if (processedPostsCount[0] == posts.size()) {
                                                        postList.addAll(filteredPosts);  // Thêm tất cả bài viết đã lọc vào danh sách chung

                                                        // Setup RecyclerView với Adapter sau khi tất cả các bài viết đã được xử lý
                                                        RecyclerView recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
                                                        PostAdapter postAdapter = new PostAdapter(postList, requireContext());
                                                        recyclerView.setAdapter(postAdapter);
                                                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onUsersReceived(List<AdminDefault> adminDefault) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onUsersReceived(List<User> users) {
                        }
                    });
                }
            }
        });
    }

    private void loadEventsFromFirebase() {
        TextView textView = getView().findViewById(R.id.null_event);
        textView.setVisibility(View.GONE);
        EventAPI eventAPI = new EventAPI();
        ArrayList<Event> eventList = new ArrayList<>();
        eventAPI.getAllEvents(new EventAPI.EventCallback() {
            @Override
            public void onEventReceived(Event event) {

            }

            @Override
            public void onEventsReceived(List<Event> events) {
                eventList.clear();
                eventList.addAll(events);

                if (eventList.isEmpty()) {
                    textView.setVisibility(View.VISIBLE);
                } else {
                    textView.setVisibility(View.GONE);
                }

                // Setup RecyclerView với Adapter
                EventAdapter eventAdapter = new EventAdapter(eventList, getContext());
                recyclerView.setAdapter(eventAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);

                // Tự động cuộn
                autoScrollRecyclerView(recyclerView, eventList.size());

                // Đổi alpha khi cuộn
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    private boolean isScrolling = false; // Biến flag để kiểm tra trạng thái cuộn

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        if (dx != 0 || dy != 0) {
                            if (!isScrolling) {
                                isScrolling = true; // Đánh dấu là đang cuộn
                            }
                        } else {
                            isScrolling = false; // Nếu không có cuộn thì không làm gì
                        }
                    }
                });
            }
        });
    }

    private void autoScrollRecyclerView(RecyclerView recyclerView, int itemCount) {
        final Handler handler = new Handler();
        final int delay = 5000; // 5 giây
        final int[] currentIndex = {0}; // Vị trí hiện tại
        final boolean[] isReversing = {false}; // Xác định chiều di chuyển

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (itemCount > 0) {
                    if (!isReversing[0]) {
                        // Lướt tới
                        currentIndex[0]++;
                        if (currentIndex[0] >= itemCount - 1) {
                            isReversing[0] = true; // Đổi chiều khi đến phần tử cuối
                        }
                    } else {
                        // Lướt lùi
                        currentIndex[0]--;
                        if (currentIndex[0] <= 0) {
                            isReversing[0] = false; // Đổi chiều khi về phần tử đầu tiên
                        }
                    }

                    // Cuộn RecyclerView đến vị trí bằng cách sử dụng LinearSmoothScroller
                    CustomScroller customScroller = new CustomScroller(recyclerView.getContext());
                    customScroller.setTargetPosition(currentIndex[0]);
                    recyclerView.getLayoutManager().startSmoothScroll(customScroller);
                }

                // Lặp lại
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private class CustomScroller extends LinearSmoothScroller {
        public CustomScroller(Context context) {
            super(context);
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return 0.3f; // Điều chỉnh tốc độ cuộn ở đây
        }
    }
}
