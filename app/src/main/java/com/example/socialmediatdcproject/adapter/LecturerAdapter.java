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
import com.example.socialmediatdcproject.activity.SharedActivity;
import com.example.socialmediatdcproject.model.Lecturer;

import java.util.List;

public class LecturerAdapter extends RecyclerView.Adapter<LecturerAdapter.LecturerViewHolder> {

    private List<Lecturer> lecturerList;
    private Context context;
    private OnLecturerClickListener onLecturerClickListener;

    // Constructor
    public LecturerAdapter(List<Lecturer> lecturerList, Context context) {
        this.lecturerList = lecturerList;
        this.context = context;
    }

    public interface OnLecturerClickListener {
        void onLecturerClick(Lecturer lecturer);
    }

    public void setOnLecturerClickListener(OnLecturerClickListener listener) {
        this.onLecturerClickListener = listener;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public LecturerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_list, parent, false);
        return new LecturerViewHolder(view);
    }

    // Bind dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull LecturerViewHolder holder, int position) {
        Lecturer lecturer = lecturerList.get(position);

        if (lecturer != null) {
            // Set dữ liệu cho các view
            holder.lecturerName.setText(lecturer.getFullName());
            holder.lecturerPositionJob.setText("Chức vụ: " + getPositionJob(lecturer));
            holder.lecturerEmail.setText(lecturer.getEmail());
            Glide.with(context)
                    .load(lecturer.getAvatar())
                    .circleCrop()
                    .into(holder.lecturerAvatar);

            holder.cardView.setOnClickListener(v -> openPersonalPage(lecturer.getUserId()));
        } else {
            Log.e("LecturerAdapter", "Lecturer at position " + position + " is null");
        }
    }

    @Override
    public int getItemCount() {
        return lecturerList.size();
    }

    // Hàm lấy chức vụ
    public String getPositionJob(Lecturer lecturer) {
        return "Giảng viên - Nhân Viên";
    }

    // Lớp ViewHolder
    public static class LecturerViewHolder extends RecyclerView.ViewHolder {
        ImageView lecturerAvatar;
        TextView lecturerName;
        TextView lecturerPositionJob;
        TextView lecturerEmail;
        CardView cardView;

        public LecturerViewHolder(@NonNull View itemView) {
            super(itemView);
            lecturerAvatar = itemView.findViewById(R.id.member_avatar);
            lecturerName = itemView.findViewById(R.id.member_name);
            lecturerPositionJob = itemView.findViewById(R.id.member_position_job);
            lecturerEmail = itemView.findViewById(R.id.member_email);
            cardView = itemView.findViewById(R.id.item_member);
        }
    }

    private void openPersonalPage(int userId) {
        Intent intent = new Intent(context, SharedActivity.class);
        intent.putExtra("lecturerId", userId); // Chuyển userId qua trang cá nhân
        context.startActivity(intent);
    }
}
