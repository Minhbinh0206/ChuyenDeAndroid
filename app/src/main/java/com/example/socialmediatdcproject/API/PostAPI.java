package com.example.socialmediatdcproject.API;

import android.util.Log;

import androidx.annotation.NonNull;
import com.example.socialmediatdcproject.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostAPI {

    private DatabaseReference postsRef;

    public PostAPI() {
        // Lấy tham chiếu tới node "posts" trong Realtime Database
        postsRef = FirebaseDatabase.getInstance().getReference("Post");

        Log.d("Home", "PostAPI: " + postsRef);
    }

    // 1. Create a new post
    public void addPost(Post post, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        // Sử dụng postId làm key để thêm post
        postsRef.child(String.valueOf(post.getPostId()))
                .setValue(post)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    // Get All
    public Post getPostById(int postId) {
        final Post[] postResult = new Post[1];

        postsRef.child(String.valueOf(postId)).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (snapshot.exists()) {
                            postResult[0] = snapshot.getValue(Post.class);
                        } else {
                            postResult[0] = null; // Không tìm thấy post
                        }
                    } else {
                        postResult[0] = null; // Lỗi khi lấy dữ liệu
                    }
                });

        return postResult[0];
    }


    public ArrayList<Post> getAllPosts() {
        ArrayList<Post> postList = new ArrayList<>();

        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    postList.add(post);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });

        return postList;
    }

    // 4. Update a post by ID
    public void updatePost(Post post, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        // Cập nhật post dựa vào postId
        postsRef.child(String.valueOf(post.getPostId()))
                .setValue(post)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    // 5. Delete a post by ID
    public void deletePost(int postId, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        postsRef.child(String.valueOf(postId))
                .removeValue()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    //Get post by user id
    public ArrayList<Post> getPostsByUserId(int userId) {
        ArrayList<Post> postList = new ArrayList<>();

        postsRef.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Post post = snapshot.getValue(Post.class);
                            postList.add(post);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý lỗi nếu cần
                    }
                });

        return postList;
    }


    // 7. Read posts by group ID
    public ArrayList<Post> getPostsByGroupId(int groupId) {
        ArrayList<Post> postList = new ArrayList<>();

        postsRef.orderByChild("groupId").equalTo(groupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Post post = snapshot.getValue(Post.class);
                            postList.add(post);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý lỗi nếu cần
                    }
                });

        return postList;
    }

}
