package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.MessageAPI;
import com.example.socialmediatdcproject.API.NotifyQuicklyAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.MessageAdapter;
import com.example.socialmediatdcproject.dataModels.Message;
import com.example.socialmediatdcproject.dataModels.NotifyQuickly;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class MessengerDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter friendPersonalAdapter;
    private ArrayList<Message> listMessage = new ArrayList<>();
    private HashSet<String> messageIds = new HashSet<>(); // Tạo một HashSet để theo dõi các ID của tin nhắn đã có
    private boolean isSendMessagesLoaded = false;
    private boolean isReceiveMessagesLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.messenger_detail_layout);

        recyclerView = findViewById(R.id.recycle_messenger_detail);
        friendPersonalAdapter = new MessageAdapter(listMessage, MessengerDetailActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(friendPersonalAdapter);

        ImageButton buttonBackHome = findViewById(R.id.icon_back_home);
        buttonBackHome.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();

        isSendMessagesLoaded = false;
        isReceiveMessagesLoaded = false;

        Intent intent = getIntent();
        int studentId = intent.getIntExtra("studentId", -1);

        ImageButton submitMessage = findViewById(R.id.submit_mess);
        EditText contentMessage = findViewById(R.id.content_mess);



        // Set Avatar
        StudentAPI studentAPI = new StudentAPI();
        if (studentId != -1) {
            studentAPI.getStudentById(studentId, new StudentAPI.StudentCallback() {
                @Override
                public void onStudentReceived(Student student) {
                    ImageView imageView = findViewById(R.id.avatar_user);
                    Glide.with(MessengerDetailActivity.this)
                            .load(student.getAvatar())
                            .circleCrop()
                            .into(imageView);

                    TextView name = findViewById(R.id.name_user);
                    name.setText(student.getFullName());

                    // Thiết lập sự kiện cho nút gửi tin nhắn
                    submitMessage.setOnClickListener(v -> {
                        MessageAPI messageAPI = new MessageAPI();
                        Message messSend = new Message();
                        Message messReceive = new Message();

                        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                            @Override
                            public void onStudentReceived(Student myStudent) {
                                String messageContent = contentMessage.getText().toString().trim();
                                String currentDateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                                if (!messageContent.isEmpty()) {
                                    // Cấu hình tin nhắn Send
                                    messSend.setMyUserId(myStudent.getUserId());
                                    messSend.setYourUserId(studentId);
                                    messSend.setContent(messageContent);
                                    messSend.setMessageType("Send");
                                    messSend.setCreateAt(currentDateAndTime);

                                    // Cấu hình tin nhắn Receive
                                    messReceive.setMyUserId(studentId);
                                    messReceive.setYourUserId(myStudent.getUserId());
                                    messReceive.setContent(messageContent);
                                    messReceive.setMessageType("Receive");
                                    messReceive.setCreateAt(currentDateAndTime);

                                    // Lưu tin nhắn vào Firebase
                                    messageAPI.addMessage(messSend);
                                    messageAPI.addMessage(messReceive);

                                    NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();
                                    notifyQuicklyAPI.getAllNotifications(new NotifyQuicklyAPI.NotificationCallback() {
                                        @Override
                                        public void onNotificationsReceived(List<NotifyQuickly> notifications) {
                                            NotifyQuickly notifyQuickly = new NotifyQuickly();
                                            notifyQuickly.setUserSendId(myStudent.getUserId());
                                            notifyQuickly.setUserGetId(studentId);
                                            notifyQuickly.setContent("Bạn có tin nhắn mới từ " + myStudent.getFullName());

                                            notifyQuicklyAPI.addNotification(notifyQuickly);
                                        }
                                    });

                                    // Cập nhật giao diện ngay lập tức với chỉ tin nhắn Send
                                    contentMessage.setText(""); // Xóa nội dung tin nhắn sau khi gửi
                                    if (!messageIds.contains(messSend.getMessageId())) {
                                        friendPersonalAdapter.notifyDataSetChanged(); // Cập nhật giao diện
                                        scrollToBottom(); // Cuộn xuống cuối danh sách tin nhắn
                                    }
                                }
                            }

                            @Override
                            public void onStudentsReceived(List<Student> students) {}
                        });
                    });
                }

                @Override
                public void onStudentsReceived(List<Student> students) {}
            });
        }

        // Hiển thị tin nhắn
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                displayMessageRealtime(student, studentId);
            }

            @Override
            public void onStudentsReceived(List<Student> students) {}
        });
    }

    private void displayMessageRealtime(Student student, int studentId) {
        MessageAPI messageAPI = new MessageAPI();
        int myUserId = student.getUserId();

        // Nếu bạn cũng muốn tải tin nhắn ban đầu từ Firebase
        messageAPI.getAllMessages(myUserId, new MessageAPI.MessageCallback() {
            @Override
            public void onMessagesReceived(List<Message> messages) {
                for (Message message : messages) {
                    if (message != null) {
                        // Kiểm tra nếu tin nhắn đã được thêm vào danh sách
                        if (!listMessage.contains(message) && message.getYourUserId() == studentId) {
                            listMessage.add(message);
                        }
                    }
                }
                // Lắng nghe các thay đổi trong tin nhắn
                listenForMessageChanges(myUserId, studentId);
                friendPersonalAdapter.notifyDataSetChanged();
                updateMessageList();
                Log.d("LOA", "onMessageListReceived: " + listMessage.size());
            }

            @Override
            public void onMessageAdded(Message message) {
                // Không cần xử lý ở đây
            }
        });

    }

    private void sortMessagesByDate() {
        // Sắp xếp theo thứ tự tăng dần (tin nhắn mới nhất ở dưới cùng)
        listMessage.sort((m1, m2) -> m1.getCreateAt().compareTo(m2.getCreateAt()));
    }

    private void updateMessageList() {
        if (isSendMessagesLoaded && isReceiveMessagesLoaded) {
            // Sắp xếp tin nhắn theo thời gian tạo
            sortMessagesByDate();
            scrollToBottom(); // Cuộn xuống cuối danh sách tin nhắn
        }
    }

    private void listenForMessageChanges(int myUserId, int yourStudentId) {
        MessageAPI messageAPI = new MessageAPI();

        messageAPI.listenForMessages(myUserId, new MessageAPI.MessageCallback() {
            @Override
            public void onMessagesReceived(List<Message> messages) {
                listMessage.clear();
                for (Message message : messages) {
                    // Kiểm tra xem tin nhắn đã có trong danh sách chưa
                    if (!listMessage.contains(message) && message.getYourUserId() == yourStudentId) {
                        listMessage.add(message); // Thêm tất cả tin nhắn mới vào danh sách
                    }
                }
                friendPersonalAdapter.notifyDataSetChanged(); // Cập nhật giao diện
                scrollToBottom(); // Cuộn xuống cuối danh sách tin nhắn
            }

            @Override
            public void onMessageAdded(Message message) {
                // Không cần xử lý ở đây
            }
        });
    }

    private void scrollToBottom() {
        recyclerView.scrollToPosition(listMessage.size() - 1);
    }
}