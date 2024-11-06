package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.NotifyQuicklyAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.NotifyQuicklyAdapter;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.dataModels.NotifyQuickly;
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

        ImageButton iconBack = findViewById(R.id.icon_back);
        iconBack.setOnClickListener(v -> {
            Intent intent = new Intent(GroupDetaiActivity.this, SharedActivity.class);
            intent.putExtra("keyFragment", 9999);
            startActivity(intent);
            finish();
        });

        Intent intent = getIntent();
        int groupId = intent.getIntExtra("groupId", -1);

        StudentAPI studentAPI = new StudentAPI();

        if (groupId != -1) {
            // Thực hiện việc lấy dữ liệu và kiểm tra người dùng có tham gia group không
            GroupUserAPI groupUserAPI = new GroupUserAPI();

            groupUserAPI.getGroupUserByIdGroup(groupId, new GroupUserAPI.GroupUserCallback() {
                @Override
                public void onGroupUsersReceived(List<GroupUser> groupUsers) {
                    String key = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    studentAPI.getStudentByKey(key, new StudentAPI.StudentCallback() {
                        @Override
                        public void onStudentReceived(Student student) {
                            boolean isJoin = intent.getBooleanExtra("isJoin", false);

                            // Kiểm tra xem user đã tham gia nhóm chưa
                            for (GroupUser gu : groupUsers) {
                                if (gu.getUserId() == student.getUserId()) {
                                    isJoin = true;
                                }
                            }

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            // Nếu user chưa tham gia, hiển thị GroupNotFollowFragment
                            if (isJoin) {
                                fragmentTransaction.replace(R.id.first_content_fragment, new GroupFollowedFragment());

                                fragmentTransaction.commit();
                            } else {
                                GroupAPI groupAPI = new GroupAPI();
                                groupAPI.getGroupById(groupId, new GroupAPI.GroupCallback() {
                                    @Override
                                    public void onGroupReceived(Group group) {
                                        if (group.isGroupDefault()){
                                            fragmentTransaction.replace(R.id.first_content_fragment, new GroupFollowedFragment());
                                        }
                                        else {
                                            fragmentTransaction.replace(R.id.first_content_fragment, new GroupNotFollowFragment());
                                        }

                                        fragmentTransaction.commit();
                                    }

                                    @Override
                                    public void onGroupsReceived(List<Group> groups) {

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
    private void showNotifyQuicklyPopup(int id ,List<NotifyQuickly> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return;  // Nếu không có thông báo thì không làm gì
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(GroupDetaiActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Hiển thị PopupWindow ở vị trí mong muốn
        View notifyButton = findViewById(R.id.icon_back);
        popupWindow.showAsDropDown(notifyButton, 0, 0);

        // Handler để hiển thị từng thông báo tuần tự
        final Handler handler = new Handler();
        final int[] currentIndex = {0}; // Chỉ mục hiện tại của thông báo

        // Hàm để cập nhật thông báo hiện tại
        Runnable updateNotification = new Runnable() {
            @Override
            public void run() {
                if (currentIndex[0] < notifications.size()) {
                    // Cập nhật danh sách thông báo với thông báo hiện tại
                    NotifyQuickly currentNotification = notifications.get(currentIndex[0]);
                    notifyQuicklyAdapter.clearNotifications(); // Xóa thông báo cũ
                    notifyQuicklyAdapter.addNotification(currentNotification); // Thêm thông báo mới
                    notifyQuicklyAdapter.notifyDataSetChanged();

                    // Gọi API để xóa thông báo khỏi Firebase
                    NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();
                    notifyQuicklyAPI.deleteNotification(id ,currentNotification.getNotifyId()); // Gọi phương thức xóa với ID của thông báo

                    // Tăng chỉ mục lên cho thông báo tiếp theo
                    currentIndex[0]++;

                    // Gọi lại runnable sau 5 giây để hiển thị thông báo tiếp theo
                    handler.postDelayed(this, 5000); // Điều chỉnh thời gian nếu cần
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
}
