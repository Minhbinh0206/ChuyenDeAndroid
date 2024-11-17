package com.example.socialmediatdcproject.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.example.socialmediatdcproject.API.EventAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.model.RollCall;
import com.example.socialmediatdcproject.model.Student;

import java.util.ArrayList;
import java.util.List;

public class RollCallAdapter extends RecyclerView.Adapter<RollCallAdapter.GroupViewHolder> {

    private List<RollCall> rollCallList; // Đảm bảo tên biến nhất quán
    private List<RollCall> rollCallListFull; // Danh sách đầy đủ để lọc
    private Context context;

    // Constructor
    public RollCallAdapter(List<RollCall> rollCallList, Context context) {
        this.rollCallList = rollCallList;
        this.rollCallListFull = new ArrayList<>(rollCallList); // Lưu danh sách đầy đủ
        this.context = context;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_roll_call, parent, false);
        return new GroupViewHolder(view);
    }

    // Bind dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        RollCall rollCall = rollCallList.get(position); // Sử dụng tên biến chính xác

        Intent intent = ((Activity) context).getIntent();
        int eventId = intent.getIntExtra("eventId", -1);

        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByStudentNumber(rollCall.getStudentNumber(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                if (student != null) {
                    Glide.with(context)
                            .load(student.getAvatar())
                            .circleCrop()
                            .into(holder.imageUserRollCall);

                    changeColorButtonActive(holder.sendCode);

                    holder.nameUserRollCall.setText(student.getFullName());
                    holder.studentNumberRollCall.setText(student.getStudentNumber());

                    holder.sendCode.setOnClickListener(v -> {
                        EventAPI eventAPI = new EventAPI();
                        eventAPI.updateUserJoinVerification(eventId, student.getStudentNumber(), true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        Toast.makeText(context, "Đã gửi mã điểm danh cho " + student.getFullName(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onStudentsReceived(List<Student> students) {
                // Không cần xử lý gì ở đây
            }
        });
    }

    @Override
    public int getItemCount() {
        return rollCallList != null ? rollCallList.size() : 0; // Sử dụng tên biến chính xác
    }

    public void filter(String text) {
        rollCallList.clear();
        if (text.isEmpty()) {
            rollCallList.addAll(rollCallListFull);
        } else {
            text = text.toLowerCase();
            for (RollCall item : rollCallListFull) {
                if (item.getStudentNumber().toLowerCase().contains(text)) {
                    rollCallList.add(item);
                }
            }
        }
        notifyDataSetChanged();

    }

    // Lớp ViewHolder
    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView nameUserRollCall;
        ImageView imageUserRollCall;
        TextView studentNumberRollCall;
        Button sendCode;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            imageUserRollCall = itemView.findViewById(R.id.avatar_user_rollcall);
            nameUserRollCall = itemView.findViewById(R.id.name_user_rollcall);
            studentNumberRollCall = itemView.findViewById(R.id.student_number_user_rollcall);
            sendCode = itemView.findViewById(R.id.submit_send_rollcall);
        }
    }

    public void changeColorButtonActive(Button btn) {
        btn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.defaultBlue));
        btn.setTextColor(ContextCompat.getColorStateList(context, R.color.white));
    }
}
