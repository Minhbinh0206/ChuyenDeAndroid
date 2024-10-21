package com.example.socialmediatdcproject.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.FriendAPI; // Import FriendAPI
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.SearchFriendAdapter;
import com.example.socialmediatdcproject.dataModels.Friends;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class SearchFriendActivity extends AppCompatActivity {
    private SearchFriendAdapter adapter;
    private List<Student> friendList;
    private List<Student> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_friends_layout);

        ImageButton iconBack = findViewById(R.id.icon_back_search);
        iconBack.setOnClickListener(v -> finish());

        // Giả sử friendList chứa toàn bộ danh sách bạn bè
        friendList = new ArrayList<>();
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                studentAPI.getAllStudents(new StudentAPI.StudentCallback() {
                    @Override
                    public void onStudentReceived(Student student) {}

                    @Override
                    public void onStudentsReceived(List<Student> students) {
                        for (Student s : students) {
                            if (s.getUserId() != student.getUserId()) {
                                friendList.add(s);
                            }
                        }

                        filteredList = new ArrayList<>(friendList);

                        // Khởi tạo RecyclerView và Adapter
                        RecyclerView recyclerView = findViewById(R.id.recycleview_friends);
                        adapter = new SearchFriendAdapter(filteredList, SearchFriendActivity.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(SearchFriendActivity.this));
                        recyclerView.setAdapter(adapter);

                        // Sử dụng TextWatcher để theo dõi thay đổi văn bản trong EditText
                        EditText dataSearch = findViewById(R.id.search_data);
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

                    @Override
                    public void onError(String errorMessage) {}

                    @Override
                    public void onStudentDeleted(int studentId) {}
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
    }

    // Hàm để lọc danh sách bạn bè dựa trên văn bản nhập
    private void filterFriends(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(friendList); // Nếu chuỗi tìm kiếm rỗng, hiển thị toàn bộ danh sách
        } else {
            for (Student student : friendList) {
                if (String.valueOf(student.getFullName()).contains(query)) {
                    filteredList.add(student);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    // Kiểm tra tình trạng bạn bè
    private void checkFriendStatus(Student student) {
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student currentStudent) {
                // Kiểm tra danh sách bạn bè
                FriendAPI friendAPI = new FriendAPI();
                friendAPI.getAllFriends(new FriendAPI.FriendCallback() {
                    @Override
                    public void onFriendsReceived(List<Friends> friendsList) {
                        boolean isFriend = false;
                        String myUserId_yourUserId = currentStudent.getUserId() + "_" + student.getUserId();

                        for (Friends friend : friendsList) {
                            if ((friend.getMyUserId() + "_" + friend.getYourUserId()).equals(myUserId_yourUserId) ||
                                    (friend.getYourUserId() + "_" + friend.getMyUserId()).equals(myUserId_yourUserId)) {
                                isFriend = true;
                                break;
                            }
                        }

                        // Nếu không phải bạn, gửi lời mời kết bạn
                        if (!isFriend) {
                            sendFriendRequest(currentStudent.getUserId(), student.getUserId());
                        } else {
                            // Nếu đã là bạn, có thể thực hiện hành động khác
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

    // Gửi lời mời kết bạn
    private void sendFriendRequest(int myUserId, int yourUserId) {
        Friends friend = new Friends();
        friend.setMyUserId(myUserId);
        friend.setYourUserId(yourUserId);
        friend.setStatus(0); // 0: Đang chờ kết bạn
        FriendAPI friendAPI = new FriendAPI();
        friendAPI.addFriend(friend);
    }
}
