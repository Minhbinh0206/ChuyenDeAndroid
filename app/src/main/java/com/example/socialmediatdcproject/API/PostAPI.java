package com.example.socialmediatdcproject.API;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.model.Post;
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
        String postId = postDatabase.push().getKey(); // Tạo ID duy nhất
        post.setPostId(postId.hashCode()); // Gán ID cho bài viết
        postDatabase.child(postId).setValue(post).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("PostAPI", "Post added successfully.");
            } else {
                Log.e("PostAPI", "Failed to add post.", task.getException());
            }
        });
    }

    // Cập nhật bài viết
    public void updatePost(Post post) {
        String postId = String.valueOf(post.getPostId());
        postDatabase.child(postId).setValue(post).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("PostAPI", "Post updated successfully.");
            } else {
                Log.e("PostAPI", "Failed to update post.", task.getException());
            }
        });
    }

    // Lấy tất cả bài viết
    public void getAllPosts(final PostCallback callback) {
        postDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
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
                Log.e("PostAPI", "Error fetching posts: " + error.getMessage());
            }
        });
    }

    // Lấy bài viết theo ID
    public void getPostById(int postId, final PostCallback callback) {
        postDatabase.orderByChild("postId").equalTo(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getChildren().iterator().hasNext() ? snapshot.getChildren().iterator().next().getValue(Post.class) : null;
                callback.onPostReceived(post);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostAPI", "Error fetching post: " + error.getMessage());
            }
        });
    }

    // Lấy bài viết theo group ID
    public void getPostByGroupId(int groupId, final PostCallback callback) {
        postDatabase.orderByChild("groupId").equalTo(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
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

    // Định nghĩa interface PostCallback
    public interface PostCallback {
        void onPostReceived(Post post);
        void onPostsReceived(List<Post> posts);
    }
}
