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
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.model.Lecturer;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private List<Student> memberList;
    private Context context;

    // Constructor
    public MemberAdapter(List<Student> memberList, Context context) {
        this.memberList = memberList;
        this.context = context;
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
        Student student = memberList.get(position);

        if (student != null) {
            // Set dữ liệu cho các view
            holder.memberName.setText(student.getFullName());
            holder.memberPositionJob.setText("Chức vụ: " + getPositionJob(student));
            holder.memberEmail.setText(student.getEmail());
            Glide.with(context)
                    .load(student.getAvatar())
                    .circleCrop()
                    .into(holder.memberAvatar);
        } else {
            Log.e("MemberAdapter", "User at position " + position + " is null");
        }
    }


    @Override
    public int getItemCount() {
        return memberList.size();
    }

    // Hàm lấy chức vụ
    public String getPositionJob(Student student) {
        if (student.getRoleId() == 0) {
            return "Học sinh";
        } else if (student.getRoleId() == 1){
            return "Giảng viên";
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
