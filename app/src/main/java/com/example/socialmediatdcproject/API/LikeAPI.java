package com.example.socialmediatdcproject.API;

import com.example.socialmediatdcproject.dataModels.UserLikeComment;
import com.example.socialmediatdcproject.dataModels.UserLikePost;
import com.example.socialmediatdcproject.model.Comment;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
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

    public void likePost(UserLikePost userLikePost, Post post, LikeCallback callback) {
        String key = "PostLikes/" + post.getGroupId() + "/" + userLikePost.getPostId() + "/" + userLikePost.getUserId();
        likeDatabase.child(key).setValue(userLikePost).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts").child(String.valueOf(post.getGroupId())).child(String.valueOf(userLikePost.getPostId()));
                postRef.child("postLike").setValue(ServerValue.increment(1));
                callback.onSuccess("Liked successfully");
            } else {
                callback.onError("Error liking post: " + task.getException().getMessage());
            }
        });
    }

    public void unlikePost(int userId, Post post, LikeCallback callback) {
        String key = "PostLikes/" + post.getGroupId() + "/" + post.getPostId() + "/" + userId;
        likeDatabase.child(key).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts").child(String.valueOf(post.getGroupId())).child(String.valueOf(post.getPostId()));
                postRef.child("postLike").setValue(ServerValue.increment(-1));
                callback.onSuccess("Unliked successfully");
            } else {
                callback.onError("Error unliking post: " + task.getException().getMessage());
            }
        });
    }

    public void likeComment(UserLikeComment userLikeComment, Comment comment ,LikeCallback callback) {
        String key = "CommentLikes/" + comment.getGroupId() + "/" + comment.getPostId() + "/" + userLikeComment.getCommentId() + "/" + userLikeComment.getUserId();
        likeDatabase.child(key).setValue(userLikeComment).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Comments").child(String.valueOf(comment.getGroupId())).child(String.valueOf(comment.getPostId())).child(String.valueOf(userLikeComment.getCommentId()));
                commentRef.child("commentLike").setValue(ServerValue.increment(1));
                callback.onSuccess("Liked comment successfully");
            } else {
                callback.onError("Error liking comment: " + task.getException().getMessage());
            }
        });
    }

    public void unlikeComment(int userId, Comment comment, LikeCallback callback) {
        String key = "CommentLikes/" + comment.getGroupId() + "/" + comment.getPostId() + "/" + comment.getId() + "/" + userId;
        likeDatabase.child(key).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Comments").child(String.valueOf(comment.getGroupId())).child(String.valueOf(comment.getPostId())).child(String.valueOf(comment.getId()));
                commentRef.child("commentLike").setValue(ServerValue.increment(-1));
                callback.onSuccess("Unliked comment successfully");
            } else {
                callback.onError("Error unliking comment: " + task.getException().getMessage());
            }
        });
    }

    public void checkLikeStatus(int userId, Post post, LikeStatusCallback callback) {
        String key = "PostLikes/" + post.getGroupId() + "/" + post.getPostId() + "/" + userId;
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

    public void checkLikeComment(int userId, Comment comment, LikeStatusCallback callback) {
        String key = "CommentLikes/" + comment.getGroupId() + "/" + comment.getPostId() + "/" + comment.getId() + "/" + userId;
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

    public void toggleLikeStatus(int userId, Post post, LikeStatusCallback callback) {
        String key = "PostLikes/" + post.getGroupId() + "/" + post.getPostId() + "/" + userId;
        likeDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    unlikePost(userId, post, new LikeCallback() {
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
                    UserLikePost userLikePost = new UserLikePost(userId, post.getPostId(), true);
                    likePost(userLikePost, post, new LikeCallback() {
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

    public void toggleLikeComment(int userId, Comment comment, LikeStatusCallback callback) {
        String key = "CommentLikes/" + comment.getGroupId() + "/" + comment.getPostId() + "/" + comment.getId() + "/" + userId;
        likeDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    unlikeComment(userId, comment, new LikeCallback() {
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
                    UserLikeComment userLikeComment = new UserLikeComment(userId, comment.getId(), true);
                    likeComment(userLikeComment, comment, new LikeCallback() {
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

    public void listenForLikeCountChanges(Post post, LikeCountCallback callback) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts").child(String.valueOf(post.getGroupId())).child(String.valueOf(post.getPostId()));
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

    public void listenForLikeCountChangesComment(Comment comment, LikeCountCallback callback) {
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Comments").child(String.valueOf(comment.getGroupId())).child(String.valueOf(comment.getPostId())).child(String.valueOf(comment.getId()));
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
