package com.example.socialmediatdcproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.FriendAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.SharedActivity;
import com.example.socialmediatdcproject.dataModels.Friends;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class SearchFriendAdapter extends RecyclerView.Adapter<SearchFriendAdapter.FriendViewHolder> {

    private List<Student> friendList;
    private Context context;

    // Constructor
    public SearchFriendAdapter(List<Student> friendList, Context context) {
        this.friendList = friendList;
        this.context = context;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_friends, parent, false);
        return new FriendViewHolder(view);
    }

    // Bind dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Student friend = friendList.get(position);
        if (friend != null) {
            // Set dữ liệu cho các view
            holder.friendName.setText(friend.getFullName());
            Glide.with(context)
                    .load(friend.getAvatar())
                    .circleCrop()
                    .into(holder.friendAvatar);

            holder.button.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.defaultBlue));
            holder.button.setTextColor(ContextCompat.getColorStateList(context, R.color.white));

            holder.button.setOnClickListener(v -> {
                FriendAPI friendAPI = new FriendAPI();
                Friends friendsfirst = new Friends();
                Friends friendsSecond = new Friends();
                String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
                StudentAPI studentAPI = new StudentAPI();

                studentAPI.getStudentByKey(key, new StudentAPI.StudentCallback() {
                    @Override
                    public void onStudentReceived(Student student) {
                        friendAPI.getAllFriends(new FriendAPI.FriendCallback() {
                            @Override
                            public void onFriendsReceived(List<Friends> friendsList) {
                                boolean alreadyFriends = false;

                                // Kiểm tra xem đã là bạn hay chưa
                                for (Friends f : friendsList) {
                                    // Nếu đã có mối quan hệ bạn bè
                                    if ((student.getUserId() == f.getMyUserId() && friend.getUserId() == f.getYourUserId()) ||
                                            (friend.getUserId() == f.getMyUserId() && student.getUserId() == f.getYourUserId())) {
                                        alreadyFriends = true; // Cờ đã có bạn
                                        break; // Thoát khỏi vòng lặp
                                    }
                                }

                                // Nếu chưa có mối quan hệ bạn bè, thêm mới
                                if (!alreadyFriends) {
                                    friendsfirst.setMyUserId(student.getUserId());
                                    friendsfirst.setYourUserId(friend.getUserId());
                                    friendsfirst.setStatus(0);

                                    friendsSecond.setMyUserId(friend.getUserId());
                                    friendsSecond.setYourUserId(student.getUserId());
                                    friendsSecond.setStatus(0);

                                    // Thêm bạn
                                    friendAPI.addFriend(friendsfirst);
                                    friendAPI.addFriend(friendsSecond);
                                } else {
                                    Log.d("SearchFriendAdapter", "Friend already exists: " + friend.getFullName());
                                }
                            }
                        });
                    }

                    @Override
                    public void onStudentsReceived(List<Student> students) {
                        // Xử lý nếu cần
                    }
                });
                Intent intent = new Intent(v.getContext(), SharedActivity.class);
                intent.putExtra("studentId", friend.getUserId());
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                v.getContext().startActivity(intent);
            });

        } else {
            Log.e("SearchFriendAdapter", "Friend at position " + position + " is null");
        }
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    // Hàm lấy trạng thái bạn bè
    private String getFriendStatus(Friends friend) {
        int status = friend.getStatus();
        switch (status) {
            case 0:
                return "Chưa kết bạn";
            case 1:
                return "Đang chờ kết bạn";
            case 2:
                return "Đã kết bạn";
            default:
                return "Trạng thái không xác định";
        }
    }

    // Lớp ViewHolder
    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView friendAvatar;
        TextView friendName;
        Button button;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friendAvatar = itemView.findViewById(R.id.search_friend_avatar);
            friendName = itemView.findViewById(R.id.search_friend_name);
            button = itemView.findViewById(R.id.search_friend_button_submit);
        }
    }
}
