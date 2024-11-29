package com.example.socialmediatdcproject.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AnswerAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.NotifyQuicklyAPI;
import com.example.socialmediatdcproject.API.QuestionAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.GroupDetaiActivity;
import com.example.socialmediatdcproject.activity.SharedActivity;
import com.example.socialmediatdcproject.dataModels.Answer;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.dataModels.NotifyQuickly;
import com.example.socialmediatdcproject.dataModels.Question;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.GroupViewHolder> {

    private List<Answer> answerList;
    private Context context;

    // Constructor
    public AnswerAdapter(List<Answer> memberList, Context context) {
        this.answerList = memberList;
        this.context = context;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer_question, parent, false);
        return new GroupViewHolder(view);
    }

    // Bind dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Answer answer = answerList.get(position);

        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentById(answer.getUserId(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                Glide.with(context)
                        .load(student.getAvatar())
                        .circleCrop()
                        .into(holder.avatarUser);

                holder.nameUser.setText(student.getFullName());
            }

            @Override
            public void onStudentsReceived(List<Student> students) {

            }
        });

        holder.contentAnswer.setText('"' + answer.getAnswerContent() + '"');

        holder.denied.setOnClickListener(v -> {
            Log.d("Click", "Denied");

            AnswerAPI answerAPI = new AnswerAPI();
            answerAPI.deleteAnswer(answer.getQuestionId(), answer.getUserId());

            answerList.remove(position);
            notifyItemRemoved(position);

            NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();
            NotifyQuickly notifyQuickly = new NotifyQuickly();
            notifyQuicklyAPI.getAllNotifications(new NotifyQuicklyAPI.NotificationCallback() {
                @Override
                public void onNotificationsReceived(List<NotifyQuickly> notifications) {
                    studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                        @Override
                        public void onStudentReceived(Student student) {
                            QuestionAPI questionAPI = new QuestionAPI();
                            questionAPI.getQuestionById(answer.getQuestionId(), new QuestionAPI.QuestionCallback() {
                                @Override
                                public void onQuestionReceived(Question question) {
                                    GroupAPI groupAPI = new GroupAPI();
                                    groupAPI.getGroupById(question.getGroupId(), new GroupAPI.GroupCallback() {
                                        @Override
                                        public void onGroupReceived(Group group) {
                                            notifyQuickly.setNotifyId(notifications.size());
                                            notifyQuickly.setUserSendId(student.getUserId());
                                            notifyQuickly.setUserGetId(answer.getUserId());
                                            notifyQuickly.setContent("Đơn xin gia nhập nhóm " + group.getGroupName() + " đã bị từ chối.");

                                            notifyQuicklyAPI.addNotification(notifyQuickly);

                                            Toast.makeText(v.getContext(), "Đã từ chối người này vào nhóm", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onGroupsReceived(List<Group> groups) {

                                        }
                                    });
                                }

                                @Override
                                public void onQuestionsReceived(List<Question> questions) {

                                }
                            });
                        }

                        @Override
                        public void onStudentsReceived(List<Student> students) {

                        }
                    });
                }
            });
        });

        holder.submit.setOnClickListener(v -> {
            Log.d("Click", "Submit");
            studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                @Override
                public void onStudentReceived(Student student) {
                    QuestionAPI questionAPI = new QuestionAPI();
                    questionAPI.getQuestionById(answer.getQuestionId(), new QuestionAPI.QuestionCallback() {
                        @Override
                        public void onQuestionReceived(Question question) {
                            GroupAPI groupAPI = new GroupAPI();
                            groupAPI.getGroupById(question.getGroupId(), new GroupAPI.GroupCallback() {
                                @Override
                                public void onGroupReceived(Group group) {
                                    GroupUserAPI groupUserAPI = new GroupUserAPI();
                                    GroupUser groupUser = new GroupUser();
                                    groupUser.setGroupId(group.getGroupId());
                                    groupUser.setUserId(answer.getUserId());

                                    groupUserAPI.addGroupUser(groupUser);

                                    NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();
                                    NotifyQuickly notifyQuickly = new NotifyQuickly();
                                    notifyQuicklyAPI.getAllNotifications(new NotifyQuicklyAPI.NotificationCallback() {
                                        @Override
                                        public void onNotificationsReceived(List<NotifyQuickly> notifications) {
                                            notifyQuickly.setNotifyId(notifications.size());
                                            notifyQuickly.setUserSendId(student.getUserId());
                                            notifyQuickly.setUserGetId(answer.getUserId());
                                            notifyQuickly.setContent("Đơn xin gia nhập nhóm "+group.getGroupName()+" đã được duyệt.");

                                            notifyQuicklyAPI.addNotification(notifyQuickly);
                                        }
                                    });

                                    AnswerAPI answerAPI = new AnswerAPI();
                                    answerAPI.deleteAnswer(answer.getQuestionId(), answer.getUserId());

                                    answerList.remove(position);  // position là vị trí của item trong answerList
                                    notifyItemRemoved(position);

                                    Toast.makeText(v.getContext(), "Đã duyệt vào nhóm", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onGroupsReceived(List<Group> groups) {

                                }
                            });
                        }

                        @Override
                        public void onQuestionsReceived(List<Question> questions) {

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
        return answerList != null ? answerList.size() : 0 ;
    }


    // Lớp ViewHolder
    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView contentAnswer;
        ImageView avatarUser;
        TextView nameUser;
        TextView submit;
        TextView denied;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarUser = itemView.findViewById(R.id.imageView);
            nameUser = itemView.findViewById(R.id.name_user);
            contentAnswer = itemView.findViewById(R.id.content_answer);
            submit = itemView.findViewById(R.id.button_accept);
            denied = itemView.findViewById(R.id.button_denied);
        }
    }
}
