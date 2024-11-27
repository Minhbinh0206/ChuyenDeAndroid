package com.example.socialmediatdcproject.API;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediatdcproject.dataModels.Message;
import com.example.socialmediatdcproject.model.Post;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class PostAPI {
    private DatabaseReference postDatabase;

    public PostAPI() {
        // Khởi tạo reference đến nút "Posts" trong Firebase
        postDatabase = FirebaseDatabase.getInstance().getReference("Posts");
    }

    // Thêm bài viết mới vào Firebase
    public void addPost(Post post) {
        String groupId = String.valueOf(post.getGroupId());
        String postId = String.valueOf(post.getPostId());

        postDatabase.child(groupId).child(postId).setValue(post).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("PostAPI", "Post added successfully under groupId: " + groupId);
            } else {
                Log.e("PostAPI", "Failed to add post.", task.getException());
            }
        });
    }

    // Cập nhật bài viết
    public void updatePost(Post post) {
        String groupId = String.valueOf(post.getGroupId());
        String postId = String.valueOf(post.getPostId());

        postDatabase.child(groupId).child(postId).setValue(post).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("PostAPI", "Post updated successfully under groupId: " + groupId);
            } else {
                Log.e("PostAPI", "Failed to update post.", task.getException());
            }
        });
    }

    // Lắng nghe bài viết mới trong một nhóm cụ thể
    public void listenForNewPost(int groupId, PostCallback callback) {
        postDatabase.child(String.valueOf(groupId)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Post post = snapshot.getValue(Post.class);
                if (post != null) {
                    callback.onPostReceived(post);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Xử lý khi bài viết thay đổi
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Xử lý khi bài viết bị xóa
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Không thường dùng
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error reading data", databaseError.toException());
            }
        });
    }

    // Lấy tất cả bài viết trong một nhóm
    public void getPostsByGroupId(int groupId, PostCallback callback) {
        postDatabase.child(String.valueOf(groupId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Post> postList = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        postList.add(post);
                    }
                }
                callback.onPostsReceived(postList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostAPI", "Error fetching posts by group ID: " + error.getMessage());
            }
        });
    }

    // Lấy bài viết theo ID (trong một nhóm cụ thể)
    public void getPostById(int groupId, int postId, PostCallback callback) {
        postDatabase.child(String.valueOf(groupId)).child(String.valueOf(postId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                callback.onPostReceived(post);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostAPI", "Error fetching post by ID: " + error.getMessage());
            }
        });
    }

    // Lấy tất cả bài viết của một user
    public void getPostsByUserId(int userId, PostCallback callback) {
        postDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Post> postList = new ArrayList<>();
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot postSnapshot : groupSnapshot.getChildren()) {
                        Post post = postSnapshot.getValue(Post.class);
                        if (post != null && post.getUserId() == userId) {
                            postList.add(post);
                        }
                    }
                }
                callback.onPostsReceived(postList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostAPI", "Error fetching posts by user ID: " + error.getMessage());
            }
        });
    }

    // Định nghĩa interface PostCallback
    public interface PostCallback {
        void onPostReceived(Post post);
        void onPostsReceived(List<Post> posts);
    }
}
