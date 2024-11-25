package com.example.socialmediatdcproject.API;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.socialmediatdcproject.dataModels.FilterPost;
import com.example.socialmediatdcproject.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterPostsAPI {
    private DatabaseReference receivePostDatabase;

    public FilterPostsAPI() {
        // Khởi tạo reference đến nút "Filters/FilterPosts" trong Firebase
        receivePostDatabase = FirebaseDatabase.getInstance().getReference("Filters").child("FilterPosts");
    }

    public void findUserInReceive(Post post, int userId, final UserInReceiveCallback callback) {
        // Tham chiếu đến nút postId trong FilterPosts
        DatabaseReference postRef = receivePostDatabase.child(String.valueOf(post.getGroupId())).child(String.valueOf(post.getPostId())).child("Receive");

        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot receiveSnapshot : snapshot.getChildren()) {
                    Integer receivedUserId = receiveSnapshot.child("userId").getValue(Integer.class);
                    if (receivedUserId != null && receivedUserId == userId) {
                        found = true;
                        break;
                    }
                }
                callback.onResult(found);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FilterPostsAPI", "Error checking user in receive list", error.toException());
                callback.onResult(false);
            }
        });
    }

    // Thêm FilterPost mới vào Firebase với postId làm khóa và danh sách người nhận trong "Receive"
    public void addReceivePost(FilterPost receivePost, Post post) {
        String postKey = String.valueOf(receivePost.getPostId());

        // Lưu postId trước
        receivePostDatabase.child(String.valueOf(post.getGroupId())).child(postKey).child("postId").setValue(receivePost.getPostId())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FilterPostsAPI", "postId added successfully.");
                    } else {
                        Log.e("FilterPostsAPI", "Failed to add postId.", task.getException());
                    }
                });

        // Lưu danh sách Receive dưới dạng các cặp userId
        List<Integer> listUserGet = receivePost.getListUserGet();
        if (listUserGet != null) {
            for (int i = 0; i < listUserGet.size(); i++) {
                receivePostDatabase.child(String.valueOf(post.getGroupId())).child(postKey).child("Receive").child(String.valueOf(i)).child("userId")
                        .setValue(listUserGet.get(i))
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("FilterPostsAPI", "Receive userId added successfully.");
                            } else {
                                Log.e("FilterPostsAPI", "Failed to add Receive userId.", task.getException());
                            }
                        });
            }
        }
    }

    // Lấy tất cả ReceivePost từ Firebase
    public void getAllReceivePosts(final ReceivePostCallback callback) {
        receivePostDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FilterPost> receivePostList = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    FilterPost receivePost = postSnapshot.getValue(FilterPost.class);
                    if (receivePost != null) {
                        receivePostList.add(receivePost);
                    }
                }
                callback.onReceivePostsReceived(receivePostList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FilterPostsAPI", "Failed to retrieve all receive posts.", error.toException());
            }
        });
    }
    // Định nghĩa interface để nhận kết quả kiểm tra
    public interface UserInReceiveCallback {
        void onResult(boolean isFound);
    }
    // Định nghĩa interface ReceivePostCallback
    public interface ReceivePostCallback {
        void onReceivePostReceived(FilterPost receivePost);
        void onReceivePostsReceived(List<FilterPost> receivePosts);
    }
}
