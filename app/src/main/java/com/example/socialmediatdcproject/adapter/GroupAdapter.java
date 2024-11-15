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
import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.AnswerAPI;
import com.example.socialmediatdcproject.API.QuestionAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.GroupDetaiActivity;
import com.example.socialmediatdcproject.activity.SharedActivity;
import com.example.socialmediatdcproject.dataModels.Answer;
import com.example.socialmediatdcproject.dataModels.Question;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> memberList;
    private Context context;

    // Constructor
    public GroupAdapter(List<Group> memberList, Context context) {
        this.memberList = memberList;
        this.context = context;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_list, parent, false);
        return new GroupViewHolder(view);
    }

    // Bind dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = memberList.get(position);
        UserAPI userAPI = new UserAPI();
        if (group != null) {
            // Set dữ liệu cho các view
            holder.groupName.setText(group.getGroupName());
            if (!group.getAvatar().isEmpty()) {
                Glide.with(context)
                        .load(group.getAvatar())
                        .circleCrop()
                        .into(holder.groupAvatar);
            } else {
                Glide.with(context)
                        .load(R.drawable.avatar_group_default)
                        .circleCrop()
                        .into(holder.groupAvatar);
            }

            StudentAPI studentAPI = new StudentAPI();
            studentAPI.getStudentById(group.getAdminUserId(), new StudentAPI.StudentCallback() {
                @Override
                public void onStudentReceived(Student student) {
                    holder.groupAdminName.setText(student.getFullName());
                }

                @Override
                public void onStudentsReceived(List<Student> students) {

                }
            });

            AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
            adminDepartmentAPI.getAdminDepartmentById(group.getAdminUserId(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
                @Override
                public void onUserReceived(AdminDepartment adminDepartment) {
                    holder.groupAdminName.setText(adminDepartment.getFullName());
                }

                @Override
                public void onUsersReceived(List<AdminDepartment> adminDepartment) {

                }

                @Override
                public void onError(String s) {

                }
            });

            AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
            adminBusinessAPI.getAdminBusinessById(group.getAdminUserId(), new AdminBusinessAPI.AdminBusinessCallBack() {
                @Override
                public void onUserReceived(AdminBusiness adminBusiness) {
                    holder.groupAdminName.setText(adminBusiness.getFullName());
                }

                @Override
                public void onUsersReceived(List<AdminBusiness> adminBusiness) {

                }

                @Override
                public void onError(String s) {

                }
            });

            AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();
            adminDefaultAPI.getAdminDefaultById(group.getAdminUserId(), new AdminDefaultAPI.AdminDefaultCallBack() {
                @Override
                public void onUserReceived(AdminDefault adminDefault) {
                    holder.groupAdminName.setText(adminDefault.getFullName());
                }

                @Override
                public void onUsersReceived(List<AdminDefault> adminDefault) {

                }
            });

            holder.groupDetail.setOnClickListener(v -> {
                final boolean[] isWaiting = {false};
                Intent intent = new Intent(v.getContext(), GroupDetaiActivity.class);
                intent.putExtra("groupId", group.getGroupId());
                studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                    @Override
                    public void onStudentReceived(Student student) {
                        AnswerAPI answerAPI = new AnswerAPI();
                        answerAPI.getAllAnswers(new AnswerAPI.AnswersCallback() {
                            @Override
                            public void onAnswerReceived(Answer answer) {

                            }

                            @Override
                            public void onAnswersReceived(List<Answer> answers) {
                                QuestionAPI questionAPI = new QuestionAPI();
                                for (Answer a : answers) {
                                    questionAPI.getQuestionById(a.getQuestionId(), new QuestionAPI.QuestionCallback() {
                                        @Override
                                        public void onQuestionReceived(Question question) {
                                            if (a.getUserId() == student.getUserId() && question.getGroupId() == group.getGroupId()) {
                                                isWaiting[0] = true;
                                            } else {
                                                isWaiting[0] = false;
                                            }
                                            intent.putExtra("isWaiting", isWaiting[0]);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                        }

                                        @Override
                                        public void onQuestionsReceived(List<Question> questions) {

                                        }
                                    });
                                }
                            }
                        });
                    }

                    @Override
                    public void onStudentsReceived(List<Student> students) {

                    }
                });
                v.getContext().startActivity(intent);
            });
        } else {
            Log.e("GroupAdapter", "Group at position " + position + " is null");
        }
    }


    @Override
    public int getItemCount() {
        return memberList != null ? memberList.size() : 0 ;
    }


    // Lớp ViewHolder
    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageView groupAvatar;
        TextView groupName;
        CardView groupDetail;
        TextView groupAdminName;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_name);
            groupAdminName = itemView.findViewById(R.id.group_admin_name);
            groupAvatar = itemView.findViewById(R.id.group_avatar);
            groupDetail = itemView.findViewById(R.id.group_detail);
        }
    }
}
