package com.example.socialmediatdcproject.API;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.dataModels.ReceicePost;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReceivePostAPI {
    private DatabaseReference receivePostDatabase;

    public ReceivePostAPI() {
        // Khởi tạo reference đến nút "ReceivePosts" trong Firebase
        receivePostDatabase = FirebaseDatabase.getInstance().getReference("ReceivePosts");
    }

    // Thêm ReceivePost mới vào Firebase với postId làm khóa
    public void addReceivePost(ReceicePost receivePost) {
        String postKey = String.valueOf(receivePost.getPostId()); // Dùng postId làm khóa
        receivePostDatabase.child(postKey).setValue(receivePost)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ReceivePostAPI", "ReceivePost added successfully.");
                    } else {
                        Log.e("ReceivePostAPI", "Failed to add ReceivePost.", task.getException());
                    }
                });
    }

    // Lấy tất cả ReceivePost từ Firebase
    public void getAllReceivePosts(final ReceivePostCallback callback) {
        receivePostDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ReceicePost> receivePostList = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ReceicePost receivePost = postSnapshot.getValue(ReceicePost.class);
                    if (receivePost != null) {
                        receivePostList.add(receivePost);
                    }
                }
                callback.onReceivePostsReceived(receivePostList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ReceivePostAPI", "Failed to retrieve all receive posts.", error.toException());
            }
        });
    }

    // Cập nhật thông tin ReceivePost
    public void updateReceivePost(ReceicePost receivePost) {
        String postKey = String.valueOf(receivePost.getPostId()); // Dùng postId làm khóa
        receivePostDatabase.child(postKey).setValue(receivePost)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ReceivePostAPI", "ReceivePost updated successfully.");
                    } else {
                        Log.e("ReceivePostAPI", "Failed to update ReceivePost.", task.getException());
                    }
                });
    }

    // Xóa ReceivePost theo postId
    public void deleteReceivePost(int postId) {
        String postKey = String.valueOf(postId); // Dùng postId làm khóa
        receivePostDatabase.child(postKey).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ReceivePostAPI", "ReceivePost deleted successfully.");
                    } else {
                        Log.e("ReceivePostAPI", "Failed to delete ReceivePost.", task.getException());
                    }
                });
    }

    // Lấy ReceivePost theo postId
    public void getReceivePostById(int postId, final ReceivePostCallback callback) {
        String postKey = String.valueOf(postId); // Dùng postId làm khóa
        receivePostDatabase.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReceicePost receivePost = snapshot.getValue(ReceicePost.class);
                callback.onReceivePostReceived(receivePost);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ReceivePostAPI", "Failed to retrieve receive post.", error.toException());
            }
        });
    }

    // Định nghĩa interface ReceivePostCallback
    public interface ReceivePostCallback {
        void onReceivePostReceived(ReceicePost receivePost);
        void onReceivePostsReceived(List<ReceicePost> receivePosts);
    }
}
