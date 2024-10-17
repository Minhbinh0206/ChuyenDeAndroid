package com.example.socialmediatdcproject.API;

import com.example.socialmediatdcproject.dataModels.UserLikeComment;
import com.example.socialmediatdcproject.dataModels.UserLikePost;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LikeAPI {
    private DatabaseReference likeDatabase;

    public LikeAPI() {
        likeDatabase = FirebaseDatabase.getInstance().getReference("Like"); // Giả sử "Like" là tên node trong Firebase
    }

    // Thêm hoặc cập nhật lượt thích cho bài viết
    public void likePost(UserLikePost userLikePost, LikeCallback callback) {
        String key = "PostLikes/" + userLikePost.getPostId() + "/" + userLikePost.getUserId();
        likeDatabase.child(key).setValue(userLikePost).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess("Liked successfully");
            } else {
                callback.onError("Error liking post: " + task.getException().getMessage());
            }
        });
    }

    // Xóa lượt thích cho bài viết
    public void unlikePost(int userId, int postId, LikeCallback callback) {
        String key = "PostLikes/" + postId + "/" + userId;
        likeDatabase.child(key).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess("Unliked successfully");
            } else {
                callback.onError("Error unliking post: " + task.getException().getMessage());
            }
        });
    }

    // Thêm hoặc cập nhật lượt thích cho bình luận
    public void likeComment(UserLikeComment userLikeComment, LikeCallback callback) {
        String key = "CommentLikes/" + userLikeComment.getCommentId() + "/" + userLikeComment.getUserId();
        likeDatabase.child(key).setValue(userLikeComment).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess("Liked comment successfully");
            } else {
                callback.onError("Error liking comment: " + task.getException().getMessage());
            }
        });
    }

    // Xóa lượt thích cho bình luận
    public void unlikeComment(int userId, int commentId, LikeCallback callback) {
        String key = "CommentLikes/" + commentId + "/" + userId;
        likeDatabase.child(key).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess("Unliked comment successfully");
            } else {
                callback.onError("Error unliking comment: " + task.getException().getMessage());
            }
        });
    }

    // Kiểm tra lượt thích bài viết
    public void checkLikeStatus(int userId, int postId, LikeStatusCallback callback) {
        String key = "PostLikes/" + postId + "/" + userId;
        likeDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onStatusChecked(dataSnapshot.exists()); // Nếu tồn tại, nghĩa là đã thích
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError("Error checking like status: " + databaseError.getMessage());
            }
        });
    }

    // Kiểm tra lượt thích bình luận
    public void checkLikeComment(int userId, int commentId, LikeStatusCallback callback) {
        String key = "CommentLikes/" + commentId + "/" + userId;
        likeDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onStatusChecked(dataSnapshot.exists()); // Nếu tồn tại, nghĩa là đã thích
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError("Error checking like status: " + databaseError.getMessage());
            }
        });
    }

    // Thay đổi trạng thái thích cho bài viết
    public void toggleLikeStatus(int userId, int postId, LikeStatusCallback callback) {
        String key = "PostLikes/" + postId + "/" + userId;
        likeDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Nếu đã thích, xóa lượt thích
                    unlikePost(userId, postId, new LikeCallback() {
                        @Override
                        public void onSuccess(String message) {
                            callback.onStatusChecked(false); // Đã bỏ thích
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
                            callback.onStatusChecked(true); // Đã thích
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
                callback.onError("Error toggling like status: " + databaseError.getMessage());
            }
        });
    }

    // Thay đổi trạng thái thích cho bình luận
    public void toggleLikeComment(int userId, int commentId, LikeStatusCallback callback) {
        String key = "CommentLikes/" + commentId + "/" + userId;
        likeDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Nếu đã thích, xóa lượt thích
                    unlikeComment(userId, commentId, new LikeCallback() {
                        @Override
                        public void onSuccess(String message) {
                            callback.onStatusChecked(false); // Đã bỏ thích
                        }

                        @Override
                        public void onError(String errorMessage) {
                            callback.onError(errorMessage);
                        }
                    });
                } else {
                    // Nếu chưa thích, thêm lượt thích
                    UserLikeComment userLikeComment = new UserLikeComment(userId, commentId, true);
                    likeComment(userLikeComment, new LikeCallback() {
                        @Override
                        public void onSuccess(String message) {
                            callback.onStatusChecked(true); // Đã thích
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
                callback.onError("Error toggling like status: " + databaseError.getMessage());
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
