package com.example.socialmediatdcproject.API;

import com.example.socialmediatdcproject.dataModels.UserLikeComment;
import com.example.socialmediatdcproject.dataModels.UserLikePost;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class LikeAPI {
    private DatabaseReference likeDatabase;

    public LikeAPI() {
        likeDatabase = FirebaseDatabase.getInstance().getReference("Like");
    }

    public void likePost(UserLikePost userLikePost, LikeCallback callback) {
        String key = "PostLikes/" + userLikePost.getPostId() + "/" + userLikePost.getUserId();
        likeDatabase.child(key).setValue(userLikePost).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts").child(String.valueOf(userLikePost.getPostId()));
                postRef.child("postLike").setValue(ServerValue.increment(1));
                callback.onSuccess("Liked successfully");
            } else {
                callback.onError("Error liking post: " + task.getException().getMessage());
            }
        });
    }

    public void unlikePost(int userId, int postId, LikeCallback callback) {
        String key = "PostLikes/" + postId + "/" + userId;
        likeDatabase.child(key).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts").child(String.valueOf(postId));
                postRef.child("postLike").setValue(ServerValue.increment(-1));
                callback.onSuccess("Unliked successfully");
            } else {
                callback.onError("Error unliking post: " + task.getException().getMessage());
            }
        });
    }

    public void likeComment(UserLikeComment userLikeComment, LikeCallback callback) {
        String key = "CommentLikes/" + userLikeComment.getCommentId() + "/" + userLikeComment.getUserId();
        likeDatabase.child(key).setValue(userLikeComment).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Comments").child(String.valueOf(userLikeComment.getCommentId()));
                commentRef.child("commentLike").setValue(ServerValue.increment(1));
                callback.onSuccess("Liked comment successfully");
            } else {
                callback.onError("Error liking comment: " + task.getException().getMessage());
            }
        });
    }

    public void unlikeComment(int userId, int commentId, LikeCallback callback) {
        String key = "CommentLikes/" + commentId + "/" + userId;
        likeDatabase.child(key).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Comments").child(String.valueOf(commentId));
                commentRef.child("commentLike").setValue(ServerValue.increment(-1));
                callback.onSuccess("Unliked comment successfully");
            } else {
                callback.onError("Error unliking comment: " + task.getException().getMessage());
            }
        });
    }

    public void checkLikeStatus(int userId, int postId, LikeStatusCallback callback) {
        String key = "PostLikes/" + postId + "/" + userId;
        likeDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onStatusChecked(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError("Error checking like status: " + databaseError.getMessage());
            }
        });
    }

    public void checkLikeComment(int userId, int commentId, LikeStatusCallback callback) {
        String key = "CommentLikes/" + commentId + "/" + userId;
        likeDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onStatusChecked(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError("Error checking like status: " + databaseError.getMessage());
            }
        });
    }

    public void checkStatusPost(int postId, LikeStatusCallback callback) {
        likeDatabase.child(String.valueOf(postId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onStatusChecked(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError("Error checking like status: " + databaseError.getMessage());
            }
        });
    }

    public void toggleLikeStatus(int userId, int postId, LikeStatusCallback callback) {
        String key = "PostLikes/" + postId + "/" + userId;
        likeDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    unlikePost(userId, postId, new LikeCallback() {
                        @Override
                        public void onSuccess(String message) {
                            callback.onStatusChecked(false);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            callback.onError(errorMessage);
                        }
                    });
                } else {
                    UserLikePost userLikePost = new UserLikePost(userId, postId, true);
                    likePost(userLikePost, new LikeCallback() {
                        @Override
                        public void onSuccess(String message) {
                            callback.onStatusChecked(true);
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

    public void toggleLikeComment(int userId, int commentId, LikeStatusCallback callback) {
        String key = "CommentLikes/" + commentId + "/" + userId;
        likeDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    unlikeComment(userId, commentId, new LikeCallback() {
                        @Override
                        public void onSuccess(String message) {
                            callback.onStatusChecked(false);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            callback.onError(errorMessage);
                        }
                    });
                } else {
                    UserLikeComment userLikeComment = new UserLikeComment(userId, commentId, true);
                    likeComment(userLikeComment, new LikeCallback() {
                        @Override
                        public void onSuccess(String message) {
                            callback.onStatusChecked(true);
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

    public void listenForLikeCountChanges(int postId, LikeCountCallback callback) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts").child(String.valueOf(postId));
        postRef.child("postLike").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long newLikeCount = dataSnapshot.getValue(Long.class);
                    callback.onLikeCountUpdated(newLikeCount);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError("Error listening for like count changes: " + databaseError.getMessage());
            }
        });
    }

    public void listenForLikeCountChangesComment(int commentId, LikeCountCallback callback) {
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Comments").child(String.valueOf(commentId));
        commentRef.child("commentLike").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long newLikeCount = dataSnapshot.getValue(Long.class);
                    callback.onLikeCountUpdated(newLikeCount);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError("Error listening for like count changes: " + databaseError.getMessage());
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

    public interface LikeCountCallback {
        void onLikeCountUpdated(long newLikeCount);
        void onError(String errorMessage);
    }
}
