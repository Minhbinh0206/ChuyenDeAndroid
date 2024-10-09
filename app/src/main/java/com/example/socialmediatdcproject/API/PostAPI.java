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
