package com.example.socialmediatdcproject.API;

import com.example.socialmediatdcproject.dataModels.UserLikePost;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LikeAPI {
    private DatabaseReference likeDatabase;

    public LikeAPI() {
        likeDatabase = FirebaseDatabase.getInstance().getReference("LikePost"); // Giả sử "likes" là tên node trong Firebase
    }

    // Thêm hoặc cập nhật lượt thích
    public void likePost(UserLikePost userLikePost, LikeCallback callback) {
        String key = userLikePost.getUserId() + "_" + userLikePost.getPostId();
        likeDatabase.child(key).setValue(userLikePost).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess("Liked successfully");
            } else {
                callback.onError("Error liking post: " + task.getException().getMessage());
            }
        });
    }

    // Xóa lượt thích
    public void unlikePost(int userId, int postId, LikeCallback callback) {
        String key = userId + "_" + postId;
        likeDatabase.child(key).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess("Unliked successfully");
            } else {
                callback.onError("Error unliking post: " + task.getException().getMessage());
            }
        });
    }

    // Kiểm tra lượt thích
    public void checkLikeStatus(int userId, int postId, LikeStatusCallback callback) {
        String key = userId + "_" + postId;
        likeDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserLikePost userLikePost = dataSnapshot.getValue(UserLikePost.class);
                    callback.onStatusChecked(userLikePost.isLiked());
                } else {
                    callback.onStatusChecked(false); // Nếu không tìm thấy, trả về false
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError("Error checking like status: " + databaseError.getMessage());
            }
        });
    }

    public void toggleLikeStatus(int userId, int postId, LikeStatusCallback callback) {
        String key = userId + "_" + postId;

        likeDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Nếu đã thích, xóa lượt thích
                    unlikePost(userId, postId, new LikeCallback() {
                        @Override
                        public void onSuccess(String message) {
                            callback.onStatusChecked(false); // Trả về trạng thái chưa thích
                        }

                        @Override
                        public void onError(String errorMessage) {
                            callback.onError(errorMessage);
                        }
                    });
                } else {
                    // Nếu chưa thích, thêm lượt thích
                    UserLikePost userLikePost = new UserLikePost(userId, postId, true);
                    likePost(userLikePost, new LikeCallback() {
                        @Override
                        public void onSuccess(String message) {
                            callback.onStatusChecked(true); // Trả về trạng thái đã thích
                        }

                        @Override
                        public void onError(String errorMessage) {
                            callback.onError(errorMessage);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError("Error checking like status: " + databaseError.getMessage());
            }
        });
    }

    public interface LikeCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
    }

    public interface LikeStatusCallback {
        void onStatusChecked(boolean isLiked);
        void onError(String errorMessage);
    }
}
