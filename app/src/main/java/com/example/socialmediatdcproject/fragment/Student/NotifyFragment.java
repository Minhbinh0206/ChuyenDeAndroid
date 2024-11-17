package com.example.socialmediatdcproject.fragment.Student;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.FilterNotifyAPI;
import com.example.socialmediatdcproject.API.NotifyAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R; // Import đúng package chứa R
import com.example.socialmediatdcproject.adapter.NotifyAdapter;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Notify;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class NotifyFragment extends Fragment {
    FrameLayout frameLayout;
    RecyclerView recyclerView;
    NotifyAdapter notifyAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for this fragment
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_get_notify_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        frameLayout = requireActivity().findViewById(R.id.third_content_fragment);
        frameLayout.setVisibility(view.GONE);

        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        // Khởi tạo NotifyAdapter với danh sách dữ liệu rỗng
        notifyAdapter = new NotifyAdapter(new ArrayList<Notify>());

        String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Gán NotifyAdapter cho RecyclerView
        recyclerView.setAdapter(notifyAdapter);

        StudentAPI studentAPI = new StudentAPI();
        NotifyAPI notifyAPI = new NotifyAPI();
        studentAPI.getStudentByKey(userKey, new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                notifyAPI.getAllNotifications(new NotifyAPI.NotificationCallback() {
                    @Override
                    public void onNotificationReceived(Notify notify) {}

                    @Override
                    public void onNotificationsReceived(List<Notify> notifications) {
                        ArrayList<Notify> notifyList = new ArrayList<>();
                        int[] processedPostsCount = {0};  // Biến đếm số bài viết đã xử lý
                        int totalNotificationsCount = notifications.size(); // Tổng số thông báo cần xử lý

                        Log.d("NotifyAPI", "Number of notifications received: " + notifications.size());

                        for (Notify n : notifications) {
                            processNotification(n, student.getUserId() , notifyList, processedPostsCount, totalNotificationsCount);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("NotifyAPI", "Error fetching notifications: " + errorMessage);
                    }
                });
            }

            @Override
            public void onStudentsReceived(List<Student> students) {}
        });

        AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();
        AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();

        adminDepartmentAPI.getAdminDepartmentByKey(userKey, new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                notifyAPI.getAllNotifications(new NotifyAPI.NotificationCallback() {
                    @Override
                    public void onNotificationReceived(Notify notify) {}

                    @Override
                    public void onNotificationsReceived(List<Notify> notifications) {
                        ArrayList<Notify> notifyList = new ArrayList<>();
                        int[] processedPostsCount = {0};  // Biến đếm số bài viết đã xử lý
                        int totalNotificationsCount = notifications.size(); // Tổng số thông báo cần xử lý

                        Log.d("NotifyAPI", "Number of notifications received: " + notifications.size());

                        for (Notify n : notifications) {
                            processNotificationByAdmin(n, adminDepartment.getUserId() , notifyList, processedPostsCount, totalNotificationsCount);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("NotifyAPI", "Error fetching notifications: " + errorMessage);
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
        adminBusinessAPI.getAdminBusinessByKey(userKey, new AdminBusinessAPI.AdminBusinessCallBack() {
            @Override
            public void onUserReceived(AdminBusiness adminBusiness) {
                notifyAPI.getAllNotifications(new NotifyAPI.NotificationCallback() {
                    @Override
                    public void onNotificationReceived(Notify notify) {}

                    @Override
                    public void onNotificationsReceived(List<Notify> notifications) {
                        ArrayList<Notify> notifyList = new ArrayList<>();
                        int[] processedPostsCount = {0};  // Biến đếm số bài viết đã xử lý
                        int totalNotificationsCount = notifications.size(); // Tổng số thông báo cần xử lý

                        Log.d("NotifyAPI", "Number of notifications received: " + notifications.size());

                        for (Notify n : notifications) {
                            processNotificationByAdmin(n, adminBusiness.getUserId() , notifyList, processedPostsCount, totalNotificationsCount);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("NotifyAPI", "Error fetching notifications: " + errorMessage);
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
        adminDefaultAPI.getAdminDefaultByKey(userKey, new AdminDefaultAPI.AdminDefaultCallBack() {
            @Override
            public void onUserReceived(AdminDefault adminDefault) {
                notifyAPI.getAllNotifications(new NotifyAPI.NotificationCallback() {
                    @Override
                    public void onNotificationReceived(Notify notify) {}

                    @Override
                    public void onNotificationsReceived(List<Notify> notifications) {
                        ArrayList<Notify> notifyList = new ArrayList<>();
                        int[] processedPostsCount = {0};  // Biến đếm số bài viết đã xử lý
                        int totalNotificationsCount = notifications.size(); // Tổng số thông báo cần xử lý

                        Log.d("NotifyAPI", "Number of notifications received: " + notifications.size());

                        for (Notify n : notifications) {
                            processNotificationByAdmin(n, adminDefault.getUserId() , notifyList, processedPostsCount, totalNotificationsCount);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("NotifyAPI", "Error fetching notifications: " + errorMessage);
                    }
                });
            }

            @Override
            public void onUsersReceived(List<AdminDefault> adminDefault) {

            }
        });
    }


    private void processNotification(Notify n, int userId, ArrayList<Notify> notifyList, int[] processedPostsCount, int totalNotificationsCount) {
        processedPostsCount[0]++;

        // Nếu thông báo không có bộ lọc, trực tiếp thêm vào notifyList
        if (!n.isFilter()) {
            notifyList.add(n);
        } else {
            // Nếu có bộ lọc, kiểm tra qua FilterNotifyAPI
            FilterNotifyAPI filterNotifyAPI = new FilterNotifyAPI();
            filterNotifyAPI.findUserInReceive(n.getNotifyId(), userId, new FilterNotifyAPI.UserInReceiveCallback() {
                @Override
                public void onResult(boolean isFound) {
                    if (isFound) {
                        notifyList.add(n);
                        Log.d("True", "Added filtered notify to filteredNotify.");
                    }

                    // Kiểm tra nếu đã xử lý xong tất cả thông báo
                    if (processedPostsCount[0] == totalNotificationsCount) {
                        checkAndSetupRecyclerView(notifyList);
                    }
                }
            });

            return;  // Đảm bảo không gọi setCountNotify ngay sau khi thêm thông báo lọc
        }

        // Nếu không có bộ lọc, gọi setCountNotify khi tất cả đã được xử lý
        if (processedPostsCount[0] == totalNotificationsCount) {
            checkAndSetupRecyclerView(notifyList);
        }
    }

    private void processNotificationByAdmin(Notify n, int userId, ArrayList<Notify> notifyList, int[] processedPostsCount, int totalNotificationsCount) {
        processedPostsCount[0]++;

        if (!n.isFilter()) {
            // Không nhận thông báo không lọc
        } else {
            // Nếu có bộ lọc, kiểm tra qua FilterNotifyAPI
            FilterNotifyAPI filterNotifyAPI = new FilterNotifyAPI();
            filterNotifyAPI.findUserInReceive(n.getNotifyId(), userId, new FilterNotifyAPI.UserInReceiveCallback() {
                @Override
                public void onResult(boolean isFound) {
                    if (isFound) {
                        notifyList.add(n);
                        Log.d("True", "Added filtered notify to filteredNotify.");
                    }

                    // Kiểm tra nếu đã xử lý xong tất cả thông báo
                    if (processedPostsCount[0] == totalNotificationsCount) {
                        checkAndSetupRecyclerView(notifyList);
                    }
                }
            });

            return;  // Đảm bảo không gọi setCountNotify ngay sau khi thêm thông báo lọc
        }

        // Nếu không có bộ lọc, gọi setCountNotify khi tất cả đã được xử lý
        if (processedPostsCount[0] == totalNotificationsCount) {
            checkAndSetupRecyclerView(notifyList);
        }
    }

    // Phương thức kiểm tra và thiết lập RecyclerView khi đã xử lý tất cả thông báo
    private void checkAndSetupRecyclerView(ArrayList<Notify> notifyList) {
        // Cập nhật RecyclerView với notifyList
        if (notifyList != null && !notifyList.isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
            notifyAdapter.updateList(notifyList);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
    }
}
