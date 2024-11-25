package com.example.socialmediatdcproject.API;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.model.Comment;
import com.example.socialmediatdcproject.model.Group;
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

    // Thêm bình luận mới vào Firebase với id là khóa
    public void addComment(Comment comment, int group) {
        // Lấy groupId, postId và commentId từ đối tượng
        String groupId = String.valueOf(group); // groupId của nhóm
        String postId = String.valueOf(comment.getPostId()); // postId của bài viết
        String commentId = String.valueOf(comment.getId()); // commentId của bình luận

        // Lưu bình luận vào Firebase với cấu trúc groupId -> postId -> commentId
        commentDatabase.child(groupId)               // groupId là khóa chính đầu tiên
                .child(postId)                       // postId là khóa chính thứ hai
                .child(commentId)                    // commentId là khóa duy nhất cho bình luận
                .setValue(comment)                   // Lưu giá trị bình luận vào Firebase
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("CommentAPI", "Comment added successfully.");
                    } else {
                        Log.e("CommentAPI", "Failed to add comment.", task.getException());
                    }
                });
    }

    // Cập nhật bình luận
    public void updateComment(Comment comment, int group) {
        String groupId = String.valueOf(group);
        String postId = String.valueOf(comment.getPostId());
        String commentId = String.valueOf(comment.getId());

        // Cập nhật bình luận trong Firebase
        commentDatabase.child(groupId)
                .child(postId)
                .child(commentId)
                .setValue(comment)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("CommentAPI", "Comment updated successfully.");
                    } else {
                        Log.e("CommentAPI", "Failed to update comment.", task.getException());
                    }
                });
    }

    // Xóa bình luận theo khóa duy nhất
    public void deleteComment(int commentId) {
        String uniqueKey = String.valueOf(commentId); // Sử dụng id của comment làm khóa
        commentDatabase.child(uniqueKey).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("CommentAPI", "Comment deleted successfully.");
                    } else {
                        Log.e("CommentAPI", "Failed to delete comment.", task.getException());
                    }
                });
    }

    // Lấy thông tin bình luận theo commentId
    public void getCommentById(int groupId, int postId, int commentId, final CommentCallback callback) {
        String groupIdStr = String.valueOf(groupId);
        String postIdStr = String.valueOf(postId);
        String commentIdStr = String.valueOf(commentId);

        // Lấy bình luận cụ thể từ groupId, postId và commentId
        commentDatabase.child(groupIdStr)
                .child(postIdStr)
                .child(commentIdStr)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Comment comment = snapshot.getValue(Comment.class);
                        callback.onCommentReceived(comment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("CommentAPI", "Failed to retrieve comment.", error.toException());
                    }
                });
    }

    // Lấy tất cả bình luận cho một bài viết cụ thể trong nhóm
    public void getCommentsByPostId(int groupId, int postId, final CommentCallback callback) {
        String groupIdStr = String.valueOf(groupId);
        String postIdStr = String.valueOf(postId);

        // Truy vấn các bình luận trong nhóm và bài viết
        commentDatabase.child(groupIdStr)
                .child(postIdStr)
                .addListenerForSingleValueEvent(new ValueEventListener() {
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