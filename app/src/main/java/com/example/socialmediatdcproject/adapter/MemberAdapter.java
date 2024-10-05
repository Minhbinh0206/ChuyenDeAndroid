package com.example.socialmediatdcproject.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.database.UserDatabase;
import com.example.socialmediatdcproject.model.Lecturer;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private List<User> memberList;

    // Constructor
    public MemberAdapter(List<User> memberList) {
        this.memberList = memberList;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_list, parent, false);
        return new MemberViewHolder(view);
    }

    // Bind dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        User user = memberList.get(position);

        if (user != null) {
            // Set dữ liệu cho các view
            holder.memberName.setText(user.getFullName());
            holder.memberPositionJob.setText("Chức vụ: " + getPositionJob(user));
            holder.memberEmail.setText(user.getEmail());
        } else {
            Log.e("MemberAdapter", "User at position " + position + " is null");
        }
    }


    @Override
    public int getItemCount() {
        return memberList.size();
    }

    // Hàm lấy chức vụ
    public String getPositionJob(User user) {
        if (user instanceof Lecturer) {
            Lecturer lecturer = (Lecturer) user;
            if (lecturer != null && lecturer.getDescription() != null) {
                return lecturer.getDescription();
            } else {
                return "No description available";  // Trả về một chuỗi mặc định nếu không có mô tả
            }
        } else if (user instanceof Student) {
            return "Học sinh";
        }
        return "Unknown position";  // Xử lý trường hợp không phải Lecturer hoặc Student
    }



    // Lớp ViewHolder
    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        ImageView memberAvatar;
        TextView memberName;
        TextView memberPositionJob;
        TextView memberEmail;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            memberAvatar = itemView.findViewById(R.id.member_avatar);
            memberName = itemView.findViewById(R.id.member_name);
            memberPositionJob = itemView.findViewById(R.id.member_position_job);
            memberEmail = itemView.findViewById(R.id.member_email);
        }
    }
}
