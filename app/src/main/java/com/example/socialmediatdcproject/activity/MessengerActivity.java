package com.example.socialmediatdcproject.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.FriendAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.FriendPersonalAdapter;
import com.example.socialmediatdcproject.adapter.MemberMessengerAdapter;
import com.example.socialmediatdcproject.adapter.SearchFriendAdapter;
import com.example.socialmediatdcproject.dataModels.Friends;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MessengerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MemberMessengerAdapter friendPersonalAdapter;
    private ArrayList<Student> listFriends = new ArrayList<>();
    private int myId;
    private ArrayList<Student> filteredList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.messenger_layout);

        ImageButton buttonBackHome = findViewById(R.id.icon_back_home);
        buttonBackHome.setOnClickListener(v -> finish());

        displayFriends();
    }

    private void displayFriends() {
        StudentAPI studentAPI = new StudentAPI();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) return; // Kiểm tra nếu người dùng chưa đăng nhập

        studentAPI.getStudentByKey(currentUser.getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                listFriends.clear();  // Xóa danh sách bạn bè trước khi thêm dữ liệu mới

                FriendAPI friendAPI = new FriendAPI();
                friendAPI.getAllFriends(new FriendAPI.FriendCallback() {
                    @Override
                    public void onFriendsReceived(List<Friends> friendsList) {
                        for (Friends f : friendsList) {
                            if (f.getStatus() == 3) {
                                if (f.getMyUserId() == student.getUserId()) {
                                    Log.d("SV", "Id 1: " + f.getYourUserId());
                                    studentAPI.getStudentById(f.getYourUserId(), new StudentAPI.StudentCallback() {
                                        @Override
                                        public void onStudentReceived(Student studentF) {
                                            listFriends.add(studentF);

                                            filteredList = new ArrayList<>(listFriends);

                                            RecyclerView recyclerView = findViewById(R.id.recycle_messenger);
                                            friendPersonalAdapter = new MemberMessengerAdapter(filteredList, MessengerActivity.this);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(MessengerActivity.this));
                                            recyclerView.setAdapter(friendPersonalAdapter);
                                        }

                                        @Override
                                        public void onStudentsReceived(List<Student> students) {}

                                        @Override
                                        public void onError(String errorMessage) {}

                                        @Override
                                        public void onStudentDeleted(int studentId) {}
                                    });
                                }
                                else {
                                    studentAPI.getStudentById(f.getMyUserId(), new StudentAPI.StudentCallback() {
                                        @Override
                                        public void onStudentReceived(Student studentF) {
                                            if (listFriends.contains(studentF)) {
                                                listFriends.add(studentF);
                                            }

                                            filteredList = new ArrayList<>(listFriends);

                                            RecyclerView recyclerView = findViewById(R.id.recycle_messenger);
                                            friendPersonalAdapter = new MemberMessengerAdapter(filteredList, MessengerActivity.this);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(MessengerActivity.this));
                                            recyclerView.setAdapter(friendPersonalAdapter);
                                        }

                                        @Override
                                        public void onStudentsReceived(List<Student> students) {}

                                        @Override
                                        public void onError(String errorMessage) {}

                                        @Override
                                        public void onStudentDeleted(int studentId) {}
                                    });
                                }
                            }
                        }

                        // Sử dụng TextWatcher để theo dõi thay đổi văn bản trong EditText
                        EditText dataSearch = findViewById(R.id.search_mess_friends);
                        dataSearch.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                filterFriends(s.toString()); // Gọi hàm để lọc dữ liệu khi có thay đổi
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("MessengerActivity", "Error fetching student: " + errorMessage);
            }

            @Override
            public void onStudentsReceived(List<Student> students) {}

            @Override
            public void onStudentDeleted(int studentId) {}
        });
    }


    // Hàm để lọc danh sách bạn bè dựa trên văn bản nhập
    private void filterFriends(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(listFriends); // Nếu chuỗi tìm kiếm rỗng, hiển thị toàn bộ danh sách
        } else {
            for (Student student : listFriends) {
                if (String.valueOf(student.getFullName()).contains(query)) {
                    filteredList.add(student);
                }
            }
        }
        friendPersonalAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set Avatar
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                ImageView imageView = findViewById(R.id.avatar_user);
                Glide.with(MessengerActivity.this)
                        .load(student.getAvatar())
                        .circleCrop()
                        .into(imageView);
            }

            @Override
            public void onStudentsReceived(List<Student> students) {

            }

            @Override
            public void onError(String errorMessage) {
                Log.e("MessengerActivity", "Error fetching avatar: " + errorMessage);
            }

            @Override
            public void onStudentDeleted(int studentId) {

            }
        });
    }
}