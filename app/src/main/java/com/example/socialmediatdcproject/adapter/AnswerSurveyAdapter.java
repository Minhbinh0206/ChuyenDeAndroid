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
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.CommentAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.LikeAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.AnswerSurvey;
import com.example.socialmediatdcproject.model.Comment;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AnswerSurveyAdapter extends RecyclerView.Adapter<AnswerSurveyAdapter.CommentViewHolder> {

    private List<AnswerSurvey> answerSurveys;
    private Context context;

    // Constructor
    public AnswerSurveyAdapter(List<AnswerSurvey> answerSurveys, Context context) {
        this.answerSurveys = answerSurveys;
        this.context = context;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer_survey_list, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        AnswerSurvey answerSurvey = answerSurveys.get(position);
        StudentAPI studentAPI = new StudentAPI();

        studentAPI.getStudentById(answerSurvey.getUserId(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                Glide.with(context)
                        .load(student.getAvatar())
                        .circleCrop()
                        .into(holder.avatar);

                holder.name.setText(student.getFullName());

                holder.content.setText('"' + answerSurvey.getAnswerContent() + '"');
            }

            @Override
            public void onStudentsReceived(List<Student> students) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return answerSurveys.size();
    }

    // Lớp ViewHolder
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name;
        TextView content;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.comment_avatar);
            name = itemView.findViewById(R.id.comment_user_name);
            content = itemView.findViewById(R.id.comment_content);

        }

    }


}
