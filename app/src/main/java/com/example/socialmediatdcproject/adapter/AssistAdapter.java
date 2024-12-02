package com.example.socialmediatdcproject.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.AnswerAPI;
import com.example.socialmediatdcproject.API.EventAPI;
import com.example.socialmediatdcproject.API.QuestionAPI;
import com.example.socialmediatdcproject.API.RollCallAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.EventDetailActivity;
import com.example.socialmediatdcproject.activity.GroupDetaiActivity;
import com.example.socialmediatdcproject.activity.SharedActivity;
import com.example.socialmediatdcproject.dataModels.Answer;
import com.example.socialmediatdcproject.dataModels.Question;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Assist;
import com.example.socialmediatdcproject.model.Event;
import com.example.socialmediatdcproject.model.Assist;
import com.example.socialmediatdcproject.model.Assist;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssistAdapter extends RecyclerView.Adapter<AssistAdapter.GroupViewHolder> {

    private List<Assist> assistList;
    private Context context;

    // Constructor
    public AssistAdapter(List<Assist> assistList, Context context) {
        this.assistList = assistList;
        this.context = context;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assist, parent, false);
        return new GroupViewHolder(view);
    }

    // Bind dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Assist assist = assistList.get(position);

        Intent intent = ((Activity) context).getIntent();
        int eventId = intent.getIntExtra("eventId", -1);
        int adminId = intent.getIntExtra("adminId", -1);

        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentById(assist.getUserId(), new StudentAPI.StudentCallback() {
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

                    holder.sendCode.setVisibility(View.VISIBLE);

                    if (assist.isAssist()) {
                        holder.sendCode.setVisibility(View.INVISIBLE);
                    } else {
                        holder.sendCode.setVisibility(View.VISIBLE);

                        holder.sendCode.setText("Duyệt");

                        holder.sendCode.setOnClickListener(v -> {
                            EventAPI eventAPI = new EventAPI();
                            eventAPI.updateUserAssistStatus(eventId, adminId, student.getUserId(), true, new EventAPI.UpdateCallback() {
                                @Override
                                public void onUpdateSuccess() {
                                    assist.setAssist(true); // Cập nhật trạng thái assist
                                    holder.sendCode.setVisibility(View.INVISIBLE); // Ẩn nút "Duyệt"
                                    assistList.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(context, "Đã chỉ định " + student.getFullName() + " là người hỗ trợ", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onUpdateFailed() {
                                    Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                    }
                }
            }

            @Override
            public void onStudentsReceived(List<Student> students) {
                // Handle if needed
            }
        });
    }

    @Override
    public int getItemCount() {
        return assistList != null ? assistList.size() : 0 ;
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

    public void changeColorButtonActive(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.defaultBlue));
        btn.setTextColor(ContextCompat.getColorStateList(context, R.color.white));
    }
}
