package com.example.socialmediatdcproject.API;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediatdcproject.dataModels.Friends;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class FriendAPI {
    private DatabaseReference friendsDatabase;

    public FriendAPI() {
        // Khởi tạo reference đến nút "Friends" trong Firebase
        friendsDatabase = FirebaseDatabase.getInstance().getReference("Friends");
    }

    // Thêm phương thức để lấy tất cả các mối quan hệ bạn bè
    public void getAllFriends(final FriendCallback callback) {
        friendsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Friends> friendsList = new ArrayList<>();
                for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                    Friends friend = friendSnapshot.getValue(Friends.class);
                    if (friend != null) {
                        friendsList.add(friend);
                    }
                }
                callback.onFriendsReceived(friendsList); // Gửi danh sách bạn bè qua callback
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FriendAPI", "Failed to retrieve all friends.", error.toException());
            }
        });
    }

    private void listenForFriendRequests() {
        String myUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("Friends");

        friendsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Khi có bạn bè mới được thêm vào
                String friendKey = snapshot.getKey();
                if (friendKey != null && friendKey.startsWith(myUserId + "_")) {
                    // Cập nhật giao diện hoặc thông báo người dùng
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Xử lý khi có thay đổi
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Xử lý khi có bạn bè bị xóa
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Không cần xử lý cho trường hợp này
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
            }
        });
    }

    // Thêm phương thức lắng nghe thay đổi trạng thái bạn bè
    public void listenForFriendStatusChanges(String userId, FriendStatusCallback callback) {
        DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference("Friends");

        // Lắng nghe các thay đổi của bạn bè theo key
        friendRef.child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Có bạn bè mới được thêm vào
                Friends friend = snapshot.getValue(Friends.class);
                if (friend != null) {
                    // Gọi callback để cập nhật giao diện
                    callback.onStatusReceived(friend.getStatus());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Xử lý khi trạng thái bạn bè thay đổi
                Friends friend = snapshot.getValue(Friends.class);
                if (friend != null) {
                    // Gọi callback để cập nhật giao diện
                    callback.onStatusReceived(friend.getStatus());
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Xử lý khi có bạn bè bị xóa
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Không cần xử lý cho trường hợp này
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
            }
        });
    }


    // Thêm bạn mới vào Firebase với key là myUserId_yourUserId
    public void addFriend(Friends friend) {
        String key = generateFriendKey(friend.getMyUserId(), friend.getYourUserId());
        friendsDatabase.child(key).setValue(friend)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FriendAPI", "Friend added/updated successfully.");
                    } else {
                        Log.e("FriendAPI", "Failed to add/update friend.", task.getException());
                    }
                });
    }

    // Cập nhật trạng thái bạn bè
    public void updateFriendStatus(Friends friend) {
        String key = generateFriendKey(friend.getMyUserId(), friend.getYourUserId());
        friendsDatabase.child(key).setValue(friend)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FriendAPI", "Friend status updated successfully.");
                    } else {
                        Log.e("FriendAPI", "Failed to update friend status.", task.getException());
                    }
                });
    }

    // Lấy bạn bè theo myUserId và yourUserId
    public void getFriendByIds(int myUserId, int yourUserId, final FriendCallback callback) {
        String key = generateFriendKey(myUserId, yourUserId);
        friendsDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Friends friend = snapshot.getValue(Friends.class);
                if (friend != null) {
                    List<Friends> friendsList = new ArrayList<>();
                    friendsList.add(friend);
                    callback.onFriendsReceived(friendsList);
                } else {
                    callback.onFriendsReceived(new ArrayList<>()); // Trả về danh sách rỗng nếu không tìm thấy
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FriendAPI", "Failed to retrieve friend by IDs.", error.toException());
            }
        });
    }

    // Xóa bạn bè
    public void deleteFriend(int myUserId, int yourUserId) {
        String key = generateFriendKey(myUserId, yourUserId);
        friendsDatabase.child(key).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FriendAPI", "Friend deleted successfully.");
                    } else {
                        Log.e("FriendAPI", "Failed to delete friend.", task.getException());
                    }
                });
    }

    // Kiểm tra trạng thái bạn bè
    public void checkFriendStatus(int myUserId, int yourUserId, final FriendStatusCallback callback) {
        String key = generateFriendKey(myUserId, yourUserId);
        friendsDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Friends friend = snapshot.getValue(Friends.class);
                if (friend != null) {
                    callback.onStatusReceived(friend.getStatus());
                } else {
                    callback.onStatusReceived(0); // Nếu không tồn tại, trả về trạng thái "người lạ"
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FriendAPI", "Failed to check friend status.", error.toException());
            }
        });
    }

    // Hàm hỗ trợ để tạo key với định dạng myUserId_yourUserId
    private String generateFriendKey(int myUserId, int yourUserId) {
        // Tạo key theo format "myUserId_yourUserId"
        return myUserId + "_" + yourUserId;
    }

    // Định nghĩa interface FriendCallback để xử lý callback
    public interface FriendCallback {
        void onFriendsReceived(List<Friends> friendsList);
    }

    // Định nghĩa interface FriendStatusCallback để xử lý trạng thái bạn bè
    public interface FriendStatusCallback {
        void onStatusReceived(int status); // Trả về trạng thái
    }

    // Định nghĩa interface FriendStatusListener để xử lý các thay đổi trạng thái bạn bè
    public interface FriendStatusListener {
        void onFriendStatusChanged(int friendId, int status);
    }
}
