package com.example.socialmediatdcproject.API;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediatdcproject.dataModels.Message;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessageAPI {
    private DatabaseReference messageDatabase;

    public MessageAPI() {
        // Khởi tạo reference đến nút "Messages" trong Firebase
        messageDatabase = FirebaseDatabase.getInstance().getReference("Messages");
    }


    public void setMessageRead(int myUserId, int messageId) {
        String userIdKey = String.valueOf(myUserId);
        messageDatabase.child(userIdKey).child(String.valueOf(messageId)).child("read").setValue(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("MessageAPI", "Message marked as read.");
                    } else {
                        Log.e("MessageAPI", "Failed to mark message as read.", task.getException());
                    }
                });
    }

    // Update a message by user ID and message ID
    public void updateMessage(int myUserId, int id ,Message updatedMessage) {
        String userIdKey = String.valueOf(myUserId);

        // Update the message at the specific location
        messageDatabase.child(userIdKey).child(String.valueOf(id)).setValue(updatedMessage)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("MessageAPI", "Message updated successfully.");
                    } else {
                        Log.e("MessageAPI", "Failed to update message.", task.getException());
                    }
                });
    }


    // Thêm tin nhắn mới vào Firebase với khóa là số tự tăng dưới nhánh myUserId
    public void addMessage(Message message) {
        String myUserId = String.valueOf(message.getMyUserId());

        // Đếm tổng số tin nhắn hiện tại dưới nhánh myUserId
        messageDatabase.child(myUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long messageCount = dataSnapshot.getChildrenCount();
                // Sử dụng messageCount làm khóa để lưu tin nhắn mới
                messageDatabase.child(myUserId).child(String.valueOf(messageCount)).setValue(message)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("MessageAPI", "Message added successfully at position: " + messageCount);
                            } else {
                                Log.e("MessageAPI", "Failed to add message.", task.getException());
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MessageAPI", "Failed to count messages.", databaseError.toException());
            }
        });
    }

    // Xóa tin nhắn theo ID người gửi và ID của tin nhắn (số thứ tự)
    public void deleteMessage(int myUserId, int messageId) {
        String userIdKey = String.valueOf(myUserId);

        // Xóa tin nhắn dưới khóa messageId (số thứ tự)
        messageDatabase.child(userIdKey).child(String.valueOf(messageId)).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("MessageAPI", "Message deleted successfully.");
                    } else {
                        Log.e("MessageAPI", "Failed to delete message.", task.getException());
                    }
                });
    }

    // Lấy tất cả tin nhắn của một người dùng
    public void getAllMessages(int myUserId, MessageCallback callback) {
        String userIdKey = String.valueOf(myUserId);

        // Lấy toàn bộ tin nhắn của myUserId
        messageDatabase.child(userIdKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    if (message != null) {
                        messages.add(message);
                    }
                }
                callback.onMessagesReceived(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MessageAPI", "Failed to retrieve messages.", databaseError.toException());
            }
        });
    }

    // Lấy tất cả tin nhắn của một người dùng
    public void getAllMessagesRealtime(int myUserId, MessageCallback callback) {
        String userIdKey = String.valueOf(myUserId);

        // Lấy toàn bộ tin nhắn của myUserId
        messageDatabase.child(userIdKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    if (message != null) {
                        messages.add(message);
                    }
                }
                callback.onMessagesReceived(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MessageAPI", "Failed to retrieve messages.", databaseError.toException());
            }
        });
    }

    // Lắng nghe tin nhắn mới của một người dùng
    public void listenForNewMessages(int myUserId, MessageCallback callback) {
        DatabaseReference messageRef = messageDatabase.child(String.valueOf(myUserId));
        messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Message newMessage = dataSnapshot.getValue(Message.class);
                if (newMessage != null) {
                    callback.onMessageAdded(newMessage); // Chỉ thêm tin nhắn mới
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Optional: xử lý khi tin nhắn được thay đổi
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Optional: xử lý khi tin nhắn bị xóa
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Optional: xử lý khi tin nhắn được di chuyển
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MessageAPI", "Error listening for messages: " + databaseError.getMessage());
            }
        });
    }
    public void listenForMessages(int userId, MessageCallback callback) {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("Messages").child(String.valueOf(userId));

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messages.add(message);
                }
                callback.onMessagesReceived(messages); // Gọi callback với danh sách tin nhắn mới
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi ở đây
            }
        });
    }


    // Trong MessageAPI.java
    public void getMessagesByType(int myUserId, MessageListCallback callback) {
        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("Messages").child(myUserId+"");

        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Message> messageList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        messageList.add(message);
                    }
                }
                callback.onMessageListReceived(messageList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MessageAPI", "Failed to fetch messages by type.", databaseError.toException());
            }
        });
    }

    // Interface để callback danh sách tin nhắn
    public interface MessageListCallback {
        void onMessageListReceived(List<Message> messageList);
        void onError(String errorMessage);
    }

    // Định nghĩa interface MessageCallback
    public interface MessageCallback {
        void onMessagesReceived(List<Message> messages);
        void onMessageAdded(Message message);
    }
}
