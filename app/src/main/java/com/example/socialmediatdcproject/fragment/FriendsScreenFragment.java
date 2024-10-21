package com.example.socialmediatdcproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.FriendAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.NotifyAPI;
import com.example.socialmediatdcproject.API.NotifyQuicklyAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.SharedActivity;
import com.example.socialmediatdcproject.adapter.FriendPersonalAdapter;
import com.example.socialmediatdcproject.adapter.GroupAdapter;
import com.example.socialmediatdcproject.dataModels.Friends;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.dataModels.NotifyQuickly;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Notify;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendsScreenFragment extends Fragment {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView; // RecyclerView để hiển thị nhóm
    private ArrayList<Group> groupList = new ArrayList<>(); // Danh sách nhóm
    Fragment friendsFragment;
    FragmentTransaction fragmentTransaction;
    StudentAPI studentAPI = new StudentAPI();
    FriendAPI friendAPI; // Thêm FriendAPI
    int myId = -1;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext())); // Thiết lập LayoutManager

        mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth
        friendAPI = new FriendAPI(); // Khởi tạo FriendAPI

        // Tìm các nút
        Button personalFriends = view.findViewById(R.id.personal_friends);
        Button personalMyGroup = view.findViewById(R.id.personal_mygroup);
        ImageButton personalUpdate = view.findViewById(R.id.personal_user_update);

        // Tìm ảnh
        ImageView imageUser = view.findViewById(R.id.logo_personal_user_image);
        TextView nameUser = view.findViewById(R.id.name_personnal_user);

        Intent intentg = requireActivity().getIntent();
        int studentId = intentg.getIntExtra("studentId", -1);

        // Set màu mặc định cho nút "Bài viết"
        updateButtonColorsActive(personalFriends);

        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        studentAPI.getStudentByKey(key, new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                updateFriendButton(student, studentId, personalFriends, friendAPI);
            }

            @Override
            public void onStudentsReceived(List<Student> students) {}

            @Override
            public void onError(String errorMessage) {}

            @Override
            public void onStudentDeleted(int studentId) {}
        });

        updateButtonColorsNormal(personalMyGroup);
        displayFriends(studentId);
        // Lấy thông tin sinh viên
        studentAPI.getStudentById(studentId, new StudentAPI.StudentCallback() {
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
            public void onStudentsReceived(List<Student> students) {}

            @Override
            public void onError(String errorMessage) {}

            @Override
            public void onStudentDeleted(int studentId) {}
        });

        // Thiết lập nút cho giao diện
        personalFriends.setOnClickListener(v -> {
            displayFriends(3);

            // Cập nhật màu cho các nút
            updateButtonColorsActive(personalFriends);
            updateButtonColorsNormal(personalMyGroup);
        });

        personalMyGroup.setOnClickListener(v -> {
            loadGroupFromFirebase(studentId);

            // Cập nhật màu cho các nút
            updateButtonColorsActive(personalMyGroup);
            updateButtonColorsNormal(personalFriends);
        });

        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                listenForFriendStatusChanges(student, student.getUserId() +"", studentId, personalFriends);
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
        });

    }

    // Hàm lắng nghe trạng thái bạn bè
    private void listenForFriendStatusChanges(Student student, String myUserId, int studentId, Button personalFriends) {
        FirebaseDatabase.getInstance().getReference("Friends")
                .child(myUserId + "_" + studentId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Integer status = snapshot.child("status").getValue(Integer.class);
                            if (status != null) {
                                // Cần thêm tham số student và friendAPI vào đây
                                updateFriendButton(student, studentId, personalFriends, friendAPI);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", error.getMessage());
                    }
                });

        FirebaseDatabase.getInstance().getReference("Friends")
                .child(studentId + "_" + myUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Integer status = snapshot.child("status").getValue(Integer.class);
                            if (status != null) {
                                // Cần thêm tham số student và friendAPI vào đây
                                updateFriendButton(student, studentId, personalFriends, friendAPI);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", error.getMessage());
                    }
                });
    }

    private void updateFriendButton(Student student, int studentId, Button personalFriends, FriendAPI friendAPI) {
        friendAPI.checkFriendStatus(student.getUserId(), studentId, new FriendAPI.FriendStatusCallback() {
            @Override
            public void onStatusReceived(int status) {
                switch (status) {
                    case 0:
                        // Người lạ
                        personalFriends.setText("Thêm bạn");
                        setupAddFriendListener(personalFriends, student, studentId, friendAPI);
                        break;
                    case 1:
                        // Mình gửi lời mời
                        personalFriends.setText("Hủy");
                        setupCancelFriendRequestListener(personalFriends, student, studentId, friendAPI);
                        break;
                    case 2:
                        personalFriends.setText("Chấp nhận");
                        setupAcceptFriendRequestListener(personalFriends, student, studentId, friendAPI);
                        break;
                    case 3:
                        // Đã là bạn bè
                        personalFriends.setText("Bạn bè");
                        break;
                    default:
                        personalFriends.setText("Thêm bạn");
                        break;
                }
            }
        });
    }

    private void setupAddFriendListener(Button button, Student student, int studentId, FriendAPI friendAPI) {
        button.setOnClickListener(v -> {
            int currentUserId = student.getUserId();
            int friendUserId = studentId;

            Friends friends = new Friends(currentUserId, friendUserId, 1);
            Friends friends1 = new Friends(friendUserId, currentUserId, 2);

            friendAPI.updateFriendStatus(friends);
            friendAPI.updateFriendStatus(friends1);

            Toast.makeText(requireContext(), "Đã gửi lời mời kết bạn", Toast.LENGTH_SHORT).show();

            NotifyQuickly notifyQuickly = new NotifyQuickly();
            NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();

            // Lấy tất cả thông báo hiện có
            notifyQuicklyAPI.getAllNotifications(new NotifyQuicklyAPI.NotificationCallback() {
                @Override
                public void onNotificationsReceived(List<NotifyQuickly> notifications) {
                    notifyQuickly.setNotifyId(notifications.size());
                    notifyQuickly.setUserSendId(currentUserId); // ID của người gửi
                    notifyQuickly.setUserGetId(friendUserId); // ID của người nhận
                    notifyQuickly.setContent(student.getFullName() + " vừa gửi cho bạn lời mời kết bạn mới.");

                    notifyQuicklyAPI.addNotification(notifyQuickly);
                }
            });

            updateFriendButton(student, studentId, button, friendAPI);
        });
    }

    private void setupCancelFriendRequestListener(Button button, Student student, int studentId, FriendAPI friendAPI) {
        button.setOnClickListener(v -> {
            int currentUserId = student.getUserId();
            int friendUserId = studentId;

            Friends friends = new Friends(currentUserId, friendUserId, 0);
            Friends friends1 = new Friends(friendUserId, currentUserId, 0);

            friendAPI.updateFriendStatus(friends);
            friendAPI.updateFriendStatus(friends1);

            Toast.makeText(requireContext(), "Hủy gửi lời mời thành công", Toast.LENGTH_SHORT).show();

            NotifyQuickly notifyQuickly = new NotifyQuickly();
            NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();

            // Lấy tất cả thông báo hiện có
            notifyQuicklyAPI.getAllNotifications(new NotifyQuicklyAPI.NotificationCallback() {
                @Override
                public void onNotificationsReceived(List<NotifyQuickly> notifications) {
                    notifyQuickly.setNotifyId(notifications.size());
                    notifyQuickly.setUserSendId(currentUserId); // ID của người gửi
                    notifyQuickly.setUserGetId(friendUserId); // ID của người nhận
                    notifyQuickly.setContent(student.getFullName() + " đã thu hồi lại lời mời kết bạn.");

                    notifyQuicklyAPI.addNotification(notifyQuickly);
                }
            });

            updateFriendButton(student, studentId, button, friendAPI);
        });
    }

    private void setupAcceptFriendRequestListener(Button button, Student student, int studentId, FriendAPI friendAPI) {
        button.setOnClickListener(v -> {
            Friends friend = new Friends(student.getUserId(), studentId, 3);
            Friends friend1 = new Friends(studentId, student.getUserId(), 3);

            friendAPI.updateFriendStatus(friend);
            friendAPI.updateFriendStatus(friend1);

            Toast.makeText(requireContext(), "Giờ hai bạn đã là bạn bè!", Toast.LENGTH_SHORT).show();

            NotifyQuickly notifyQuickly = new NotifyQuickly();
            NotifyQuickly notifyQuickly2 = new NotifyQuickly();
            NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();

            // Lấy tất cả thông báo hiện có
            notifyQuicklyAPI.getAllNotifications(new NotifyQuicklyAPI.NotificationCallback() {
                @Override
                public void onNotificationsReceived(List<NotifyQuickly> notifications) {
                    studentAPI.getStudentById(studentId, new StudentAPI.StudentCallback() {
                        @Override
                        public void onStudentReceived(Student studentSecond) {
                            notifyQuickly.setNotifyId(notifications.size());
                            notifyQuickly.setUserSendId(student.getUserId()); // ID của người gửi
                            notifyQuickly.setUserGetId(studentId); // ID của người nhận
                            notifyQuickly.setContent("Chúc mừng " + student.getFullName() + " và " + studentSecond.getFullName() + " đã trở thành bạn bè.");

                            notifyQuickly2.setNotifyId(notifications.size());
                            notifyQuickly2.setUserSendId(studentId); // ID của người gửi
                            notifyQuickly2.setUserGetId(student.getUserId()); // ID của người nhận
                            notifyQuickly2.setContent("Chúc mừng " + studentSecond.getFullName() + " và " + student.getFullName() + " đã trở thành bạn bè.");

                            notifyQuicklyAPI.addNotification(notifyQuickly);
                            notifyQuicklyAPI.addNotification(notifyQuickly2);
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
                    });
                }
            });

            updateFriendButton(student, studentId, button, friendAPI);
        });
    }

    public void loadGroupFromFirebase(int id) {
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentById(id, new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                groupList.clear();
                GroupAPI groupAPI = new GroupAPI();
                GroupUserAPI groupUserAPI = new GroupUserAPI();
                groupUserAPI.getGroupUserByIdUser(student.getUserId(), new GroupUserAPI.GroupUserCallback() {
                    @Override
                    public void onGroupUsersReceived(List<GroupUser> groupUsers) {
                        CountDownLatch latch = new CountDownLatch(groupUsers.size()); // Đếm số lượng nhóm

                        for (GroupUser gu : groupUsers) {
                            groupAPI.getGroupById(gu.getGroupId(), new GroupAPI.GroupCallback() {
                                @Override
                                public void onGroupReceived(Group group) {
                                    Log.d("New 1", "onGroupReceived: " + group.getGroupName());
                                    groupList.add(group);
                                    latch.countDown(); // Giảm số lượng đếm

                                    if (latch.getCount() == 0) {
                                        // Thiết lập adapter khi tất cả nhóm đã được lấy
                                        GroupAdapter groupAdapter = new GroupAdapter(groupList, requireContext());
                                        recyclerView.setAdapter(groupAdapter);
                                        groupAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onGroupsReceived(List<Group> groups) {}
                            });
                        }
                    }
                });
            }

            @Override
            public void onStudentsReceived(List<Student> students) {}

            @Override
            public void onError(String errorMessage) {}

            @Override
            public void onStudentDeleted(int studentId) {}
        });
    }
    //Set sự kiện màu cho các nút
    private void updateButtonColorsActive(Button activeButton){
        // Cập nhật nút đang hoạt động
        activeButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        activeButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
    }

    //Set sự kiện màu cho các nút
    private void updateButtonColorsNormal(Button button){
        // Cập nhật nút đang hoạt động
        button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        button.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
    }

    private void displayFriends(int studentId){
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        ArrayList<Student> listFriends = new ArrayList<>();

        FriendAPI friendAPI = new FriendAPI();
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentById(studentId, new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                myId = student.getUserId();

                friendAPI.getAllFriends(new FriendAPI.FriendCallback() {
                    @Override
                    public void onFriendsReceived(List<Friends> friendsList) {
                        for (Friends f : friendsList) {
                            if (f.getStatus() == 3) {
                                if (f.getMyUserId() == myId) {
                                    studentAPI.getStudentById(f.getYourUserId(), new StudentAPI.StudentCallback() {
                                        @Override
                                        public void onStudentReceived(Student student) {
                                            boolean isAlreadyInList = false;
                                            for (Student fri : listFriends) {
                                                if (fri.getUserId() == student.getUserId()) {
                                                    isAlreadyInList = true;
                                                    break;
                                                }
                                            }

                                            // Chỉ thêm sinh viên nếu chưa tồn tại trong danh sách
                                            if (!isAlreadyInList) {
                                                listFriends.add(student);
                                            }

                                            Log.d("FR", "onStudentReceived: " + listFriends.size());

                                            FriendPersonalAdapter friendPersonalAdapter = new FriendPersonalAdapter(listFriends, requireContext());
                                            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                                            recyclerView.setAdapter(friendPersonalAdapter);
                                            friendPersonalAdapter.notifyDataSetChanged();
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
                                    });
                                }
                                else if (f.getYourUserId() == myId){
                                    studentAPI.getStudentById(f.getMyUserId(), new StudentAPI.StudentCallback() {
                                        @Override
                                        public void onStudentReceived(Student student) {
                                            boolean isAlreadyInList = false;
                                            for (Student fri : listFriends) {
                                                if (fri.getUserId() == student.getUserId()) {
                                                    isAlreadyInList = true;
                                                    break;
                                                }
                                            }

                                            // Chỉ thêm sinh viên nếu chưa tồn tại trong danh sách
                                            if (!isAlreadyInList) {
                                                listFriends.add(student);
                                            }

                                            Log.d("FR2", "onStudentReceived: " + listFriends.size());

                                            FriendPersonalAdapter friendPersonalAdapter = new FriendPersonalAdapter(listFriends, requireContext());
                                            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
                                            recyclerView.setLayoutManager(layoutManager);
                                            recyclerView.setAdapter(friendPersonalAdapter);
                                            friendPersonalAdapter.notifyDataSetChanged();
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
                                    });
                                }
                            }
                        }
                    }
                });
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
        });
        FriendPersonalAdapter friendPersonalAdapter = new FriendPersonalAdapter(listFriends, requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(friendPersonalAdapter);
        friendPersonalAdapter.notifyDataSetChanged();
    }

}