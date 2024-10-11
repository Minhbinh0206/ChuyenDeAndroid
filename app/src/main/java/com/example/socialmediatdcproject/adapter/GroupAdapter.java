package com.example.socialmediatdcproject.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.model.Lecturer;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.User;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> memberList;
    private Context context;

    // Constructor
    public GroupAdapter(List<Group> memberList, Context context) {
        this.memberList = memberList;
        this.context = context;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_list, parent, false);
        return new GroupViewHolder(view);
    }

    // Bind dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = memberList.get(position);
        UserAPI userAPI = new UserAPI();
        if (group != null) {
            // Set dữ liệu cho các view
            holder.groupName.setText(group.getGroupName());
            Glide.with(context)
                    .load(group.getAvatar())
                    .circleCrop()
                    .into(holder.groupAvatar);
            userAPI.getAllUsers(new UserAPI.UserCallback() {
                @Override
                public void onUserReceived(User user) {

                }

                @Override
                public void onUsersReceived(List<User> users) {
                    for (User u : users) {
                        if (u.getUserId() == group.getAdminUserId()) {
                            holder.groupAdminName.setText(u.getFullName());
                        }
                    }
                }
            });
        } else {
            Log.e("GroupAdapter", "Group at position " + position + " is null");
        }
    }


    @Override
    public int getItemCount() {
        return memberList.size();
    }


    // Lớp ViewHolder
    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageView groupAvatar;
        TextView groupName;
        TextView groupAdminName;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_name);
            groupAdminName = itemView.findViewById(R.id.group_admin_name);
            groupAvatar = itemView.findViewById(R.id.group_avatar);
        }
    }
}
