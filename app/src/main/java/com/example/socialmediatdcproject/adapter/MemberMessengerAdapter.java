package com.example.socialmediatdcproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.example.socialmediatdcproject.API.MessageAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.MessengerDetailActivity;
import com.example.socialmediatdcproject.dataModels.Message;
import com.example.socialmediatdcproject.model.Lecturer;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;

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

            StudentAPI studentAPI = new StudentAPI();
            studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                @Override
                public void onStudentReceived(Student myStudent) {
                    MessageAPI messageAPI = new MessageAPI();
                    messageAPI.getMessagesByType(myStudent.getUserId(), new MessageAPI.MessageListCallback() {
                        @Override
                        public void onMessageListReceived(List<Message> messageList) {
                            messageAPI.listenForMessages(myStudent.getUserId(), new MessageAPI.MessageCallback() {
                                @Override
                                public void onMessagesReceived(List<Message> messages) {
                                    int count = 0;

                                    for (Message m: messages) {
                                        if (m.getYourUserId() == student.getUserId()) {
                                            // Cắt chuỗi nội dung nếu nó dài hơn 30 ký tự
                                            String content = m.getContent();
                                            if (content.length() > 30) {
                                                content = content.substring(0, 25) + "..."; // Thêm dấu ba chấm
                                            }

                                            if (m.getMessageType().equals("Send")) {
                                                holder.memberRecent.setText("Bạn: " + content);
                                                holder.light.setVisibility(View.GONE);
                                            }
                                            else {
                                                holder.memberRecent.setText(content);
                                                if (m.isRead()) {
                                                    holder.memberRecent.setTypeface(null, Typeface.NORMAL);
                                                }
                                                else {
                                                    count++;
                                                    holder.memberRecent.setTypeface(null, Typeface.BOLD);
                                                }
                                            }

                                        }
                                    }

                                    holder.light.setText(count + "");

                                    if (count == 0) {
                                        holder.light.setVisibility(View.GONE);
                                    }
                                    else {
                                        holder.light.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onMessageAdded(Message message) {

                                }
                            });
                        }

                        @Override
                        public void onError(String errorMessage) {

                        }
                    });
                }

                @Override
                public void onStudentsReceived(List<Student> students) {

                }
            });

            studentAPI.listenForOnlineStatus(student.getUserId(), new StudentAPI.OnlineStatusCallback() {
                @Override
                public void onOnlineStatusReceived(boolean isOnline) {
                    if (isOnline) {
                        holder.online.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.online.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            Log.e("MemberAdapter", "User at position " + position + " is null");
        }

        holder.messengerDetail.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MessengerDetailActivity.class);
            MessageAPI messageAPI = new MessageAPI();
            StudentAPI studentAPI = new StudentAPI();
            studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                @Override
                public void onStudentReceived(Student me) {
                    messageAPI.getAllMessages(me.getUserId(), new MessageAPI.MessageCallback() {
                        @Override
                        public void onMessagesReceived(List<Message> messages) {
                            for (Message m : messages) {
                                if (m.getYourUserId() == student.getUserId()) {
                                    messageAPI.setMessageRead(me.getUserId(), m.getMessageId());
                                }
                            }
                            intent.putExtra("studentId", student.getUserId());
                            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                            v.getContext().startActivity(intent);
                        }

                        @Override
                        public void onMessageAdded(Message message) {

                        }
                    });
                }

                @Override
                public void onStudentsReceived(List<Student> students) {

                }
            });
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
        TextView light;
        TextView online;
        TextView offline;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            memberAvatar = itemView.findViewById(R.id.member_avatar);
            memberName = itemView.findViewById(R.id.member_name);
            memberRecent = itemView.findViewById(R.id.member_messenger_recent);
            messengerDetail = itemView.findViewById(R.id.message_detail);
            light = itemView.findViewById(R.id.light_message);
            online = itemView.findViewById(R.id.online);
            offline = itemView.findViewById(R.id.offline);
        }
    }
}
