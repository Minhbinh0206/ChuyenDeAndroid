package com.example.socialmediatdcproject.API;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.model.Comment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommentAPI {
    private DatabaseReference commentDatabase;

    public CommentAPI() {
        // Khởi tạo reference đến nút "comments" trong Firebase
        commentDatabase = FirebaseDatabase.getInstance().getReference("Comments");
    }

    // Thêm bình luận mới vào Firebase
    public void addComment(Comment comment) {
        String commentId = String.valueOf(comment.getId());
        commentDatabase.child(commentId).setValue(comment)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Thêm thành công
                        Log.d("CommentAPI", "Comment added successfully.");
                    } else {
                        // Xảy ra lỗi
                        Log.e("CommentAPI", "Failed to add comment.", task.getException());
                    }
                });
    }
    // Lấy tất cả bình luận từ Firebase (không lọc theo postId)
    public void getAllComments(final CommentCallback callback) {
        commentDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Comment> commentList = new ArrayList<>();
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        commentList.add(comment);
                    }
                }
                callback.onCommentsReceived(commentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CommentAPI", "Failed to retrieve all comments.", error.toException());
            }
        });
    }

    // Cập nhật thông tin bình luận
    public void updateComment(Comment comment) {
        String commentId = String.valueOf(comment.getId());
        commentDatabase.child(commentId).setValue(comment)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Cập nhật thành công
                        Log.d("CommentAPI", "Comment updated successfully.");
                    } else {
                        // Xảy ra lỗi
                        Log.e("CommentAPI", "Failed to update comment.", task.getException());
                    }
                });
    }

    // Xóa bình luận theo ID
    public void deleteComment(int commentId) {
        commentDatabase.child(String.valueOf(commentId)).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Xóa thành công
                        Log.d("CommentAPI", "Comment deleted successfully.");
                    } else {
                        // Xảy ra lỗi
                        Log.e("CommentAPI", "Failed to delete comment.", task.getException());
                    }
                });
    }

    // Lấy thông tin bình luận theo ID
    public void getCommentById(int commentId, final CommentCallback callback) {
        commentDatabase.child(String.valueOf(commentId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Comment comment = snapshot.getValue(Comment.class);
                callback.onCommentReceived(comment);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
                Log.e("CommentAPI", "Failed to retrieve comment.", error.toException());
            }
        });
    }

    // Lấy tất cả bình luận cho một bài viết cụ thể
    public void getCommentsByPostId(int postId, final CommentCallback callback) {
        commentDatabase.orderByChild("postId").equalTo(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Comment> commentList = new ArrayList<>();
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        commentList.add(comment);
                    }
                }
                callback.onCommentsReceived(commentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
                Log.e("CommentAPI", "Failed to retrieve comments.", error.toException());
            }
        });
    }

    // Định nghĩa interface CommentCallback
    public interface CommentCallback {
        void onCommentReceived(Comment comment);
        void onCommentsReceived(List<Comment> comments);
    }
}
