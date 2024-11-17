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
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.shareViewModels.SharedViewModel;

import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private List<Student> memberList;
    private List<Student> filteredMemberList;
    private Context context;
    private OnMemberClickListener onMemberClickListener;
    private boolean isEditMode;
    private SharedViewModel sharedViewModel;
    private int groupId;

    public MemberAdapter(List<Student> memberList, Context context, SharedViewModel sharedViewModel) {
        this.memberList = memberList;
        this.context = context;
        this.sharedViewModel = sharedViewModel;
    }

    public MemberAdapter(List<Student> memberList, Context context, SharedViewModel sharedViewModel, int groupId) {
        this.memberList = memberList;
        this.context = context;
        this.sharedViewModel = sharedViewModel;
        this.groupId = groupId; // Gán giá trị cho groupId
    }
    // Constructor
    public MemberAdapter(List<Student> memberList, Context context) {
        this.memberList = memberList;
        this.context = context;
    }

    public MemberAdapter(List<Student> studentList) {
    }

    public interface OnMemberClickListener {
        void onMemberClick(Student student);
    }
    public void setOnMemberClickListener(OnMemberClickListener listener) {
        this.onMemberClickListener = listener;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_list_remove, parent, false);
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

            // Kiểm tra quyền isAdmin trực tiếp từ SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);

            if(isAdmin){
                holder.cardView.setOnClickListener(v -> {
                    //Nothing here
                });
            }else{
                holder.cardView.setOnClickListener(v -> {
                    openPersonalPage(student.getUserId());
                });
            }


            Log.e("LecturerAdapter", "isEditMode: " + isEditMode );

            holder.removeButton.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
            changeColorButtonRemove(holder.removeButton);

            holder.removeButton.setOnClickListener(view -> {
                // Gọi phương thức xóa trong ViewModel và truyền vào groupId và studentId
                sharedViewModel.removeStudentFromGroup(groupId, student.getUserId());
                    Toast.makeText(context,"Xóa học sinh thành công khỏi nhóm", Toast.LENGTH_SHORT).show();
                // Cập nhật danh sách hiển thị trong Adapter
                memberList.remove(position);
                notifyItemRemoved(position);
            });


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
        return "Học sinh";
    }



    // Lớp ViewHolder
    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        ImageView memberAvatar;
        TextView memberName;
        TextView memberPositionJob;
        TextView memberEmail;
        CardView cardView;
        Button removeButton;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            memberAvatar = itemView.findViewById(R.id.member_avatar);
            memberName = itemView.findViewById(R.id.member_name);
            memberPositionJob = itemView.findViewById(R.id.member_position_job);
            memberEmail = itemView.findViewById(R.id.member_email);
            cardView = itemView.findViewById(R.id.item_member);
            removeButton = itemView.findViewById(R.id.button_remove);
        }
    }

    private void openPersonalPage(int userId) {
        Intent intent = new Intent(context, SharedActivity.class);
        intent.putExtra("studentId", userId); // Chuyển userId qua trang cá nhân
        context.startActivity(intent);
    }

    // Cập nhật chế độ chỉnh sửa và làm mới giao diện
    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        notifyDataSetChanged();
    }

    public void changeColorButtonRemove(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.defaultBlue));
        btn.setTextColor(ContextCompat.getColorStateList(context, R.color.black));
    }

    public void filterMember(String keyword) {
//        memberList.clear();
        if (keyword.isEmpty()) {
            filteredMemberList = new ArrayList<>(memberList);
        } else {
            List<Student> filteredList = new ArrayList<>();
            for (Student student : memberList) {
                if (student.getFullName().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(student);
                }
            }
            filteredMemberList = filteredList;
        }
        notifyDataSetChanged();
    }

//    public void changeColorButtonNormal(Button btn){
//        btn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
//        btn.setTextColor(ContextCompat.getColorStateList(context, R.color.defaultBlue));
//    }


}
