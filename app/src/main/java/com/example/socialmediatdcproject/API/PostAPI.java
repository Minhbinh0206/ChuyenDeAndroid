package com.example.socialmediatdcproject.API;

import androidx.annotation.NonNull;
import com.example.socialmediatdcproject.model.Post;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;

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

    // Get all posts
    public void getAllPosts(ValueEventListener listener) {
        databaseReference.addListenerForSingleValueEvent(listener);
    }

    // Get post by ID
    public void getPostById(int postId, ValueEventListener listener) {
        databaseReference.orderByChild("postId").equalTo(postId).addListenerForSingleValueEvent(listener);
    }

    // Get posts by user ID
    public void getPostByUserId(int id, ValueEventListener listener) {
        databaseReference.orderByChild("userId").equalTo(id).addListenerForSingleValueEvent(listener);
    }

    // Get posts by group ID
    public void getPostByGroupId(int id, ValueEventListener listener) {
        databaseReference.orderByChild("groupId").equalTo(id).addListenerForSingleValueEvent(listener);
    }

    // Get posts by multiple group IDs
    public void getPostsByGroupIds(List<Integer> groupIds, ValueEventListener listener) {
        List<ValueEventListener> listeners = new ArrayList<>();
        List<Post> allPosts = new ArrayList<>();

        for (Integer groupId : groupIds) {
            ValueEventListener groupListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Post post = postSnapshot.getValue(Post.class);
                        if (post != null) {
                            allPosts.add(post); // Add post to the list
                        }
                    }
                    // Notify the original listener with results
                    listener.onDataChange(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.onCancelled(databaseError); // Notify listener on error
                }
            };

            listeners.add(groupListener);
            databaseReference.orderByChild("groupId").equalTo(groupId).addListenerForSingleValueEvent(groupListener);
        }
    }

    // Update post
    public void updatePost(Post post, OnCompleteListener<Void> onCompleteListener) {
        databaseReference.child(String.valueOf(post.getPostId())).setValue(post).addOnCompleteListener(onCompleteListener);
    }

    // Update likes count
    public void updatePostLikes(int postId, int newLikeCount, OnCompleteListener<Void> onCompleteListener) {
        databaseReference.child(String.valueOf(postId)).child("postLike").setValue(newLikeCount).addOnCompleteListener(onCompleteListener);
    }

    // Delete post
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
