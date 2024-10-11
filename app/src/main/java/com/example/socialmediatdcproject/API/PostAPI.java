package com.example.socialmediatdcproject.API;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.model.Post;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class PostAPI {

    private DatabaseReference databaseReference;

    // Constructor
    public PostAPI() {
        // FirebaseDatabase instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Post"); // Reference to "posts" node
    }

    // Create (Add a new post)
    public void addPost(Post post, OnCompleteListener<Void> onCompleteListener) {
        String postId = databaseReference.push().getKey();  // Generate unique ID
        post.setPostId(postId.hashCode()); // Set the unique ID for the post
        databaseReference.child(postId).setValue(post).addOnCompleteListener(onCompleteListener);
    }

    // Lấy toàn bộ Post
    public void getAllPosts(ValueEventListener listener) {
        databaseReference.addListenerForSingleValueEvent(listener);
    }

    // Lấy post theo id post
    public void getPostById(int postId, ValueEventListener listener) {
        databaseReference.orderByChild("postId").equalTo(postId).addListenerForSingleValueEvent(listener);
    }

    // Lấy post theo id User
    public void getPostByUserId(int id, ValueEventListener listener) {
        databaseReference.orderByChild("userId").equalTo(id).addListenerForSingleValueEvent(listener);
    }

    // Lấy post theo id Group
    public void getPostByGroupId(int id, ValueEventListener listener) {
        databaseReference.orderByChild("groupId").equalTo(id).addListenerForSingleValueEvent(listener);
    }

    // Lấy post theo nhiều groupId
    public void getPostsByGroupIds(List<Integer> groupIds, ValueEventListener listener) {
        // Tạo một Query cho từng groupId và lưu kết quả vào một List
        List<ValueEventListener> listeners = new ArrayList<>();
        List<Post> allPosts = new ArrayList<>();

        for (Integer groupId : groupIds) {
            ValueEventListener groupListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Post post = postSnapshot.getValue(Post.class);
                        if (post != null) {
                            allPosts.add(post); // Thêm bài viết vào danh sách
                        }
                    }
                    // Gọi lại listener đã truyền vào với kết quả
                    listener.onDataChange(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.onCancelled(databaseError); // Gọi lại listener nếu có lỗi
                }
            };

            // Thêm vào danh sách listeners
            listeners.add(groupListener);

            // Tạo query cho groupId
            databaseReference.orderByChild("groupId").equalTo(groupId).addListenerForSingleValueEvent(groupListener);
        }
    }


    // Cập nhat
    public void updatePost(Post post, OnCompleteListener<Void> onCompleteListener) {
        databaseReference.child(String.valueOf(post.getPostId())).setValue(post).addOnCompleteListener(onCompleteListener);
    }

    // Xóa
    public void deletePost(int postId, OnCompleteListener<Void> onCompleteListener) {
        databaseReference.orderByChild("postId").equalTo(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue().addOnCompleteListener(onCompleteListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
