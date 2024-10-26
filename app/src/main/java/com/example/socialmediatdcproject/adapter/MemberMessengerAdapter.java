package com.example.socialmediatdcproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.MessengerDetailActivity;
import com.example.socialmediatdcproject.model.Lecturer;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;

import java.util.List;

public class MemberMessengerAdapter extends RecyclerView.Adapter<MemberMessengerAdapter.MemberViewHolder> {

    private List<Student> memberList;
    private Context context;

    // Constructor
    public MemberMessengerAdapter(List<Student> memberList, Context context) {
        this.memberList = memberList;
        this.context = context;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_messenger, parent, false);
        return new MemberViewHolder(view);
    }

    // Bind dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Student student = memberList.get(position);

        if (student != null) {
            // Set dữ liệu cho các view
            holder.memberName.setText(student.getFullName());
            Glide.with(context)
                    .load(student.getAvatar())
                    .circleCrop()
                    .into(holder.memberAvatar);

            holder.memberRecent.setText("Chưa cập nhật");
        } else {
            Log.e("MemberAdapter", "User at position " + position + " is null");
        }

        holder.messengerDetail.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MessengerDetailActivity.class);
            intent.putExtra("studentId", student.getUserId());
            v.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return memberList.size();
    }

    // Lớp ViewHolder
    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        ImageView memberAvatar;
        TextView memberName;
        TextView memberRecent;
        CardView messengerDetail;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            memberAvatar = itemView.findViewById(R.id.member_avatar);
            memberName = itemView.findViewById(R.id.member_name);
            memberRecent = itemView.findViewById(R.id.member_messenger_recent);
            messengerDetail = itemView.findViewById(R.id.message_detail);
        }
    }
}
