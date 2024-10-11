package com.example.socialmediatdcproject.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.model.Lecturer;
import com.example.socialmediatdcproject.model.Notify;
import com.example.socialmediatdcproject.model.User;

import java.util.List;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.NotifyViewHolder> {

    private List<Notify> notifyList;

    // Constructor
    public NotifyAdapter(List<Notify> memberList) {
        this.notifyList = memberList;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public NotifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_get_notify, parent, false);
        return new NotifyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifyAdapter.NotifyViewHolder holder, int position) {
        Notify notify = notifyList.get(position);
        UserAPI userAPI = new UserAPI();
        if (notify != null) {
            // Set dữ liệu cho các view
            userAPI.getAllUsers(new UserAPI.UserCallback() {
                @Override
                public void onUserReceived(User user) {

                }

                @Override
                public void onUsersReceived(List<User> users) {
                    for (User u: users) {
                        if (u.getUserId() == notify.getUserSendId()){
                            holder.userSendID.setText(u.getFullName());
                            break;
                        }
                    }
                }
            });
            holder.notifyTitle.setText(notify.getNotifyTitle());
            holder.notifyContent.setText(notify.getNotifyContent());
        } else {
            Log.e("MemberAdapter", "User at position " + position + " is null");
        }
    }

    @Override
    public int getItemCount() {
        return notifyList.size();
    }

    // Hàm lấy chức vụ
    public String getPositionJob(User user) {
        String s = "";

        if (user.getRoleId() == User.ROLE_STUDENT) {
            s = "Sinh viên";
        } else if (user.getRoleId() == User.ROLE_LECTURER){
            s = "Giảng viên";
        }
        return s;
    }


    // Lớp ViewHolder
    public static class NotifyViewHolder extends RecyclerView.ViewHolder {
        TextView userSendID;
        TextView notifyTitle;
        TextView notifyContent;

        public NotifyViewHolder(@NonNull View itemView) {
            super(itemView);
            userSendID = itemView.findViewById(R.id.notify_user_send);
            notifyTitle = itemView.findViewById(R.id.notify_title);
            notifyContent = itemView.findViewById(R.id.notify_content);
        }
    }
}
