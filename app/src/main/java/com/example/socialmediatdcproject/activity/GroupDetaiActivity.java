package com.example.socialmediatdcproject.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.NotifyQuicklyAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.NotifyQuicklyAdapter;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.dataModels.NotifyQuickly;
import com.example.socialmediatdcproject.fragment.Student.GroupDefaultFragment;
import com.example.socialmediatdcproject.fragment.Student.GroupFollowedFragment;
import com.example.socialmediatdcproject.fragment.Student.GroupNotFollowFragment;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class GroupDetaiActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_detail_layout);

        LinearLayout layout = findViewById(R.id.navComment);
        NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView2);

        ImageView imageAvatarGroup = findViewById(R.id.avatar_group);
        TextView nameGroup = findViewById(R.id.name_group);

        ImageButton iconBack = findViewById(R.id.icon_back);
        iconBack.setOnClickListener(v -> {
            finish();
        });

        Intent intent = getIntent();
        int groupId = intent.getIntExtra("groupId", -1);

        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getGroupById(groupId, new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {
                nameGroup.setText(group.getGroupName());

                Glide.with(GroupDetaiActivity.this)
                        .load(group.getAvatar())
                        .circleCrop()
                        .into(imageAvatarGroup);
            }

            @Override
            public void onGroupsReceived(List<Group> groups) {

            }
        });

        imageAvatarGroup.setVisibility(View.INVISIBLE);
        nameGroup.setVisibility(View.INVISIBLE);

        // Lắng nghe sự kiện cuộn
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Nếu cuộn xuống (scrollY > oldScrollY), đổi màu nền thành màu xanh
                if (scrollY > oldScrollY) {
                    animateBackgroundColor(layout, Color.TRANSPARENT, getResources().getColor(R.color.defaultBlue));

                    imageAvatarGroup.setVisibility(View.VISIBLE);
                    nameGroup.setVisibility(View.VISIBLE);
                }
                // Nếu cuộn lên (scrollY < oldScrollY), đặt lại màu nền
                else if (scrollY < oldScrollY) {
                    animateBackgroundColor(layout, getResources().getColor(R.color.defaultBlue), Color.TRANSPARENT);

                    imageAvatarGroup.setVisibility(View.INVISIBLE);
                    nameGroup.setVisibility(View.INVISIBLE);
                }
            }
        });

        StudentAPI studentAPI = new StudentAPI();

        if (groupId != -1) {
            // Thực hiện việc lấy dữ liệu và kiểm tra người dùng có tham gia group không
            GroupUserAPI groupUserAPI = new GroupUserAPI();
            groupUserAPI.getAllUsersInGroup(groupId, new GroupUserAPI.GroupUsersCallback() {
                @Override
                public void onUsersReceived(List<Integer> userIds) {
                    Log.d("NM", "onUsersReceived: " + userIds);
                    String key = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    studentAPI.getStudentByKey(key, new StudentAPI.StudentCallback() {
                        @Override
                        public void onStudentReceived(Student student) {
                            boolean isJoin = intent.getBooleanExtra("isJoin", false);

                            // Kiểm tra xem user đã tham gia nhóm chưa
                            for (Integer s : userIds) {
                                if (s == student.getUserId()) {
                                    isJoin = true;
                                }
                            }

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            // Nếu người dùng đã tham gia, hiển thị GroupFollowedFragment
                            if (isJoin) {
                                // Nếu người dùng chưa tham gia, gọi API để lấy thông tin nhóm
                                GroupAPI groupAPI = new GroupAPI();
                                groupAPI.getGroupById(groupId, new GroupAPI.GroupCallback() {
                                    @Override
                                    public void onGroupReceived(Group group) {
                                        // Nếu nhóm là nhóm mặc định, chuyển trực tiếp sang GroupFollowedFragment
                                        if (group.isGroupDefault()) {
                                            fragmentTransaction.replace(R.id.first_content_fragment, new GroupDefaultFragment());
                                            // Gọi commit() sau khi tất cả các thay đổi đã được thực hiện
                                            fragmentTransaction.commit();
                                        } else {
                                            fragmentTransaction.replace(R.id.first_content_fragment, new GroupFollowedFragment());
                                            // Gọi commit() sau khi tất cả các thay đổi đã được thực hiện
                                            fragmentTransaction.commit();
                                        }
                                    }

                                    @Override
                                    public void onGroupsReceived(List<Group> groups) {
                                        // Xử lý nếu cần khi nhận được danh sách nhóm
                                    }
                                });

                            } else {
                                // Nếu người dùng chưa tham gia, gọi API để lấy thông tin nhóm
                                GroupAPI groupAPI = new GroupAPI();
                                groupAPI.getGroupById(groupId, new GroupAPI.GroupCallback() {
                                    @Override
                                    public void onGroupReceived(Group group) {
                                        // Nếu nhóm là nhóm mặc định, chuyển trực tiếp sang GroupFollowedFragment
                                        if (group.isGroupDefault()) {
                                            fragmentTransaction.replace(R.id.first_content_fragment, new GroupDefaultFragment());
                                            // Gọi commit() sau khi tất cả các thay đổi đã được thực hiện
                                            fragmentTransaction.commit();
                                        } else {
                                            // Nếu nhóm không phải mặc định, hiển thị GroupNotFollowFragment trước
                                            fragmentTransaction.replace(R.id.first_content_fragment, new GroupNotFollowFragment());
                                            // Gọi commit() sau khi tất cả các thay đổi đã được thực hiện
                                            fragmentTransaction.commit();
                                        }
                                    }

                                    @Override
                                    public void onGroupsReceived(List<Group> groups) {
                                        // Xử lý nếu cần khi nhận được danh sách nhóm
                                    }
                                });
                            }
                        }

                        @Override
                        public void onStudentsReceived(List<Student> students) {}
                    });
                }
            });
        }
    }

    private void showNotifyQuicklyPopup(int id, List<NotifyQuickly> notifications) {
        if (notifications == null || notifications.isEmpty() || isFinishing() || isDestroyed()) {
            return; // Nếu không có thông báo hoặc Activity không còn hoạt động thì không làm gì
        }

        // Tạo View từ layout của PopupWindow
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.notify_quickly_popup, null);

        // Tạo PopupWindow
        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true); // Thiết lập true để cho phép tắt PopupWindow khi nhấn bên ngoài

        // Thiết lập RecyclerView bên trong PopupWindow
        RecyclerView recyclerView = popupView.findViewById(R.id.recycler_notify_quickly);
        NotifyQuicklyAdapter notifyQuicklyAdapter = new NotifyQuicklyAdapter(new ArrayList<>());
        recyclerView.setAdapter(notifyQuicklyAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Hiển thị PopupWindow ở vị trí mong muốn
        View notifyButton = findViewById(R.id.icon_back);
        if (notifyButton != null) {
            popupWindow.showAsDropDown(notifyButton, 0, 0);
        } else {
            return; // Nếu notifyButton null, không thể hiển thị PopupWindow
        }

        // Handler để hiển thị từng thông báo tuần tự
        final Handler handler = new Handler();
        final int[] currentIndex = {0}; // Chỉ mục hiện tại của thông báo

        // Hàm để cập nhật thông báo hiện tại
        Runnable updateNotification = new Runnable() {
            @Override
            public void run() {
                if (isFinishing() || isDestroyed()) {
                    popupWindow.dismiss();
                    return; // Ngừng chạy nếu Activity bị hủy
                }

                if (currentIndex[0] < notifications.size()) {
                    // Cập nhật danh sách thông báo với thông báo hiện tại
                    NotifyQuickly currentNotification = notifications.get(currentIndex[0]);
                    notifyQuicklyAdapter.clearNotifications(); // Xóa thông báo cũ
                    notifyQuicklyAdapter.addNotification(currentNotification); // Thêm thông báo mới
                    notifyQuicklyAdapter.notifyDataSetChanged();

                    // Gọi API để xóa thông báo khỏi Firebase
                    NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();
                    notifyQuicklyAPI.deleteNotification(id, currentNotification.getNotifyId());

                    // Tăng chỉ mục lên cho thông báo tiếp theo
                    currentIndex[0]++;

                    // Gọi lại runnable sau 5 giây để hiển thị thông báo tiếp theo
                    handler.postDelayed(this, 5000);
                } else {
                    // Đã hiển thị hết thông báo, đóng PopupWindow
                    popupWindow.dismiss();
                }
            }
        };

        // Bắt đầu hiển thị thông báo đầu tiên sau khi popup được mở
        handler.post(updateNotification);
    }

    @Override
    protected void onResume() {
        super.onResume();

        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();

                // Thiết lập listener để theo dõi thông báo cho người dùng hiện tại
                notifyQuicklyAPI.setNotificationListener(student.getUserId(), new NotifyQuicklyAPI.NotificationCallback() {
                    @Override
                    public void onNotificationsReceived(List<NotifyQuickly> notifications) {
                        // Hiển thị thông báo nhanh qua PopupWindow
                        showNotifyQuicklyPopup(student.getUserId(), notifications);
                    }
                });
            }

            @Override
            public void onStudentsReceived(List<Student> students) {}
        });
    }

    private void animateBackgroundColor(View view, int fromColor, int toColor) {
        ValueAnimator colorAnimation = ValueAnimator.ofArgb(fromColor, toColor);
        colorAnimation.setDuration(0); // Thời gian chuyển đổi màu (300ms)
        colorAnimation.addUpdateListener(animator -> {
            view.setBackgroundColor((int) animator.getAnimatedValue());
        });
        colorAnimation.start();
    }

}
