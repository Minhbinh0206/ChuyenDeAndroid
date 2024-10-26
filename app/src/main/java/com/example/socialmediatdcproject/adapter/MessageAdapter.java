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
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.GroupDetaiActivity;
import com.example.socialmediatdcproject.activity.SharedActivity;
import com.example.socialmediatdcproject.dataModels.Message;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.GroupViewHolder> {

    private List<Message> messageList;
    private Context context;

    // Constructor
    public MessageAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nếu viewType là 1, sử dụng layout item_my_message, ngược lại sử dụng item_your_message
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_message, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_your_message, parent, false);
        }
        return new GroupViewHolder(view);
    }

    // Bind dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (message != null) {
            holder.textView.setText(message.getContent());
        }
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        // Kiểm tra thuộc tính send hoặc receive để xác định viewType
        if ("Send".equals(message.getMessageType())) {
            return 1; // Layout cho tin nhắn "send"
        } else {
            return 2; // Layout cho tin nhắn "receive"
        }
    }

    // Lớp ViewHolder
    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.content_message_detail);
        }
    }
}
