package com.example.socialmediatdcproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.SharedActivity;
import com.example.socialmediatdcproject.model.Lecturer;
import com.example.socialmediatdcproject.shareViewModels.SharedViewModel;

import java.util.ArrayList;
import java.util.List;

public class LecturerAdapter extends RecyclerView.Adapter<LecturerAdapter.LecturerViewHolder> {

    private List<Lecturer> lecturerList;
    private Context context;
    private SharedViewModel sharedViewModel;
    private boolean isEditMode;
    private int groupId;

    public LecturerAdapter(List<Lecturer> lecturerList, Context context, SharedViewModel sharedViewModel , int groupId) {
        this.lecturerList = lecturerList;
        this.context = context;
        this.sharedViewModel = sharedViewModel;
        this.groupId = groupId;
    }
    public LecturerAdapter(List<Lecturer> lecturerList, Context context) {
        this.lecturerList = lecturerList;
        this.context = context;
    }
    public LecturerAdapter() {
        this.lecturerList = null;
        this.context = null;
    }
    public void updateList(List<Lecturer> newLecturers) {
        this.lecturerList = newLecturers;
        notifyDataSetChanged();
    }

    public List<Lecturer> getLecturers() {
        return lecturerList;
    }



    // Cập nhật chế độ chỉnh sửa và làm mới giao diện
    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        notifyDataSetChanged();
    }

//    public void updateData(ArrayList<Lecturer> newLecturerList) {
//        lecturerList.clear();
//        lecturerList.addAll(newLecturerList);
//        notifyDataSetChanged();
//    }

    @NonNull
    @Override
    public LecturerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_list_remove, parent, false);
        return new LecturerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LecturerViewHolder holder, int position) {
        Lecturer lecturer = lecturerList.get(position);

        if (lecturer != null) {
            holder.lecturerName.setText(lecturer.getFullName());
            holder.lecturerPositionJob.setText("Chức vụ: " + getPositionJob(lecturer));
            holder.lecturerEmail.setText(lecturer.getEmail());

            Glide.with(context)
                    .load(lecturer.getAvatar())
                    .circleCrop()
                    .into(holder.lecturerAvatar);

            SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);

            if(isAdmin){
                holder.cardView.setOnClickListener(v -> {
                    sharedViewModel.addLecturerToGroup(groupId , lecturer.getUserId());

                    Toast.makeText(context,"Thêm giảng viên thành công vào nhóm", Toast.LENGTH_SHORT).show();
                    lecturerList.remove(position);
                    notifyItemRemoved(position);
                });
            }else{
                holder.cardView.setOnClickListener(v -> openPersonalPage(lecturer.getUserId()));
            }
            holder.removeButton.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
            changeColorButtonRemove(holder.removeButton);
            holder.removeButton.setOnClickListener(view -> {
                // Gọi phương thức xóa trong ViewModel và truyền vào groupId và studentId
                sharedViewModel.removeLecturerFromGroup(groupId, lecturer.getUserId());
                Toast.makeText(context, "Xóa giảng viên thành công khỏi nhóm", Toast.LENGTH_SHORT).show();
                // Cập nhật danh sách hiển thị trong Adapter
                if (position >= 0 && position < lecturerList.size()) {
                    lecturerList.remove(position);
                    notifyItemRemoved(position);
                }
            });

        } else {
            Log.e("LecturerAdapter", "Lecturer at position " + position + " is null");
        }
    }

    @Override
    public int getItemCount() {
        return lecturerList.size();
    }

    public String getPositionJob(Lecturer lecturer) {
        return "Giảng viên - Nhân Viên";
    }

    public static class LecturerViewHolder extends RecyclerView.ViewHolder {
        ImageView lecturerAvatar;
        TextView lecturerName;
        TextView lecturerPositionJob;
        TextView lecturerEmail;
        CardView cardView;
        Button removeButton;

        public LecturerViewHolder(@NonNull View itemView) {
            super(itemView);
            lecturerAvatar = itemView.findViewById(R.id.member_avatar);
            lecturerName = itemView.findViewById(R.id.member_name);
            lecturerPositionJob = itemView.findViewById(R.id.member_position_job);
            lecturerEmail = itemView.findViewById(R.id.member_email);
            cardView = itemView.findViewById(R.id.item_member);
            removeButton = itemView.findViewById(R.id.button_remove);
        }
    }

    private void openPersonalPage(int userId) {
        Intent intent = new Intent(context, SharedActivity.class);
        intent.putExtra("studentId", userId);
        context.startActivity(intent);
    }

    public void changeColorButtonRemove(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.defaultBlue));
        btn.setTextColor(ContextCompat.getColorStateList(context, R.color.white));
    }

//    public void changeColorButtonNormal(Button btn){
//        btn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
//        btn.setTextColor(ContextCompat.getColorStateList(context, R.color.defaultBlue));
//    }
}
