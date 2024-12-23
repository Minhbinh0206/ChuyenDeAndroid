package com.example.socialmediatdcproject.fragment.Student;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AnswerAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.NotifyQuicklyAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.QuestionAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.dataModels.Answer;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.dataModels.NotifyQuickly;
import com.example.socialmediatdcproject.dataModels.Question;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupNotFollowFragment extends Fragment {
    FrameLayout frameLayout;
    RecyclerView recyclerView;
    boolean isWaiting;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_user_fit_tdc, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);

        ImageView avatarGroup = view.findViewById(R.id.image_group_user_follow);
        TextView nameGroup = view.findViewById(R.id.name_group_user_follow);
        Button button = view.findViewById(R.id.button_group_user_follow);
        ImageView iconPublic = view.findViewById(R.id.icon_public_or_private);
        TextView textPublic = view.findViewById(R.id.text_public);
        TextView textAdmin = view.findViewById(R.id.text_admin);

        isWaiting = false;

        int groupId;

        changeColorButtonActive(button);

        Intent intent = requireActivity().getIntent();
        groupId = intent.getIntExtra("groupId", -1);

        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getGroupById(groupId, new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {
                UserAPI userAPI = new UserAPI();
                userAPI.getUserById(group.getAdminUserId(), new UserAPI.UserCallback() {
                    @Override
                    public void onUserReceived(User user) {
                        textAdmin.setText("Nhóm của " + user.getFullName());
                    }

                    @Override
                    public void onUsersReceived(List<User> users) {

                    }
                });

                if (group.getAvatar() != null) {
                    Glide.with(view)
                            .load(group.getAvatar())
                            .circleCrop()
                            .into(avatarGroup);
                }
                else {

                }

                loadPostFromFirebase(group.getGroupId());

                if (group.isPrivate()) {
                    iconPublic.setBackground(requireContext().getResources().getDrawable(R.drawable.icon_private));
                    textPublic.setText("Nhóm riêng tư");
                } else {
                    iconPublic.setBackground(requireContext().getResources().getDrawable(R.drawable.icon_public));
                    textPublic.setText("Nhóm công khai");
                }

                nameGroup.setText(group.getGroupName());

                if (group.isPrivate()) {
                    StudentAPI studentAPI = new StudentAPI();
                    studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                        @Override
                        public void onStudentReceived(Student student) {
                            QuestionAPI questionAPI = new QuestionAPI();
                            questionAPI.getQuestionByGroupId(groupId, new QuestionAPI.QuestionCallback() {
                                @Override
                                public void onQuestionReceived(Question question) {
                                    AnswerAPI answerAPI = new AnswerAPI();
                                    answerAPI.getAnswersByQuestionId(question.getQuestionId(), new AnswerAPI.AnswersCallback() {
                                        @Override
                                        public void onAnswerReceived(Answer answer) {

                                        }

                                        @Override
                                        public void onAnswersReceived(List<Answer> answers) {
                                            for (Answer a : answers) {
                                                if (a.getUserId() == student.getUserId()) {
                                                    isWaiting = true;
                                                }
                                            }

                                            // Lắng nghe người dùng mới tham gia nhóm
                                            GroupUserAPI groupUserAPI = new GroupUserAPI();
                                            groupUserAPI.listenForNewUsersInGroup(groupId, new GroupUserAPI.StudentCallback() {
                                                @Override
                                                public void onStudentReceived(Integer newUserId) {
                                                    Log.d("HII", "onStudentReceived: " + newUserId);

                                                    // Kiểm tra nếu userId của người tham gia là của sinh viên hiện tại
                                                    if (newUserId == student.getUserId()) {
                                                        // Nếu đúng, thay thế Fragment sang GroupFollowedFragment
                                                        Fragment searchGroupFragment = new GroupFollowedFragment();
                                                        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                                        fragmentTransaction.replace(R.id.first_content_fragment, searchGroupFragment);
                                                        fragmentTransaction.commit();
                                                    }
                                                }
                                            });

                                            if (isWaiting) {

                                                button.setText("Đang chờ duyệt");

                                                button.setEnabled(false);

                                            }else {
                                                button.setText("Tham gia");

                                                button.setOnClickListener(v -> {
                                                    groupAPI.getGroupById(groupId, new GroupAPI.GroupCallback() {
                                                        @Override
                                                        public void onGroupReceived(Group group) {
                                                            showCustomDialog(groupId, button);
                                                        }

                                                        @Override
                                                        public void onGroupsReceived(List<Group> groups) {

                                                        }
                                                    });
                                                });
                                            }
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
                } else {
                    button.setText("Tham gia");

                    button.setOnClickListener(v -> {
                        groupAPI.getGroupById(groupId, new GroupAPI.GroupCallback() {
                            @Override
                            public void onGroupReceived(Group group) {
                                StudentAPI studentAPI = new StudentAPI();
                                studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                                    @Override
                                    public void onStudentReceived(Student student) {
                                        GroupUserAPI groupUserAPI = new GroupUserAPI();
                                        GroupUser groupUser = new GroupUser();
                                        groupUser.setGroupId(group.getGroupId());
                                        groupUser.setUserId(student.getUserId());

                                        groupUserAPI.addGroupUser(groupUser);

                                        Fragment searchGroupFragment = new GroupFollowedFragment();
                                        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                        fragmentTransaction.replace(R.id.first_content_fragment, searchGroupFragment);
                                        fragmentTransaction.commit();
                                    }

                                    @Override
                                    public void onStudentsReceived(List<Student> students) {

                                    }
                                });
                            }

                            @Override
                            public void onGroupsReceived(List<Group> groups) {

                            }
                        });
                    });
                }
            }

            @Override
            public void onGroupsReceived(List<Group> groups) {

            }
        });
    }
    public void changeColorButtonActive(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
    }

    public void changeColorButtonNormal(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
    }

    public void loadPostFromFirebase(int id) {
        ArrayList<Post> postsList = new ArrayList<>(); // Danh sách bài viết

        // Tạo instance của PostAPI
        PostAPI postAPI = new PostAPI();

        // Lấy bài viết theo groupId
        postAPI.getPostsByGroupId(id, new PostAPI.PostCallback() {
            @Override
            public void onPostReceived(Post post) {

            }

            @Override
            public void onPostsReceived(List<Post> posts) {
                TextView textView = requireActivity().findViewById(R.id.null_content_notify);
                // Kiểm tra nếu có bài viết
                if (posts.size() > 0) {
                    textView.setVisibility(View.GONE);

                    for (Post p : posts) {
                        if (p != null) {
                            postsList.add(p); // Thêm bài viết vào danh sách
                        }
                    }

                    // Cập nhật RecyclerView với dữ liệu bài viết
                    PostAdapter postAdapter = new PostAdapter(postsList, requireContext());
                    recyclerView.setAdapter(postAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                } else {

                    textView.setVisibility(View.VISIBLE);
                    textView.setText("Bạn chưa tham gia nhóm này, Hãy tham gia nhóm để cập nhật những thông tin mới nhất!");
                }
            }
        });
    }

    private void showCustomDialog(int id, Button button) {
        // Tạo Dialog
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_question_custom);

        // Tìm các view bên trong Dialog
        Button submitButton = dialog.findViewById(R.id.submit_button);
        Button cancleButton = dialog.findViewById(R.id.cancle_button);
        TextView questionTitle = dialog.findViewById(R.id.question_title);
        EditText questionContentAnswer = dialog.findViewById(R.id.question_aswer);

        changeColorButtonActive(submitButton);
        changeColorButtonActive(cancleButton);

        final boolean[] isAnswersAdded = {false};

        QuestionAPI questionAPI = new QuestionAPI();
        questionAPI.getAllQuestions(new QuestionAPI.QuestionCallback() {
            @Override
            public void onQuestionReceived(Question question) {

            }

            @Override
            public void onQuestionsReceived(List<Question> questions) {
                for (Question question : questions) {
                    if (question.getGroupId() == id) {
                        Log.d("LOG", "onQuestionReceived: " + question.getGroupQuestion());

                        questionTitle.setText(question.getGroupQuestion());

                        submitButton.setOnClickListener(v -> {
                            button.setEnabled(false);
                            // Tiến hành các bước xử lý sau khi submit
                            AnswerAPI answerAPI = new AnswerAPI();
                            if (!isAnswersAdded[0]) {
                                StudentAPI studentAPI = new StudentAPI();
                                studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                                    @Override
                                    public void onStudentReceived(Student student) {
                                        Answer answer = new Answer();
                                        answer.setUserId(student.getUserId());
                                        answer.setQuestionId(question.getQuestionId());
                                        answer.setAnswerContent(questionContentAnswer.getText().toString());

                                        // Thêm câu trả lời vào Firebase
                                        answerAPI.addAnswer(answer);

                                        // Cập nhật trạng thái isWaiting và thay đổi nội dung nút
                                        isWaiting = true; // Đặt trạng thái là "đang chờ duyệt"
                                        button.setText("Đang chờ duyệt");

                                        // Tạo thông báo
                                        NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();
                                        notifyQuicklyAPI.getAllNotifications(new NotifyQuicklyAPI.NotificationCallback() {
                                            @Override
                                            public void onNotificationsReceived(List<NotifyQuickly> notifications) {
                                                GroupAPI groupAPI = new GroupAPI();
                                                groupAPI.getGroupById(question.getGroupId(), new GroupAPI.GroupCallback() {
                                                    @Override
                                                    public void onGroupReceived(Group group) {
                                                        NotifyQuickly notifyQuickly = new NotifyQuickly();
                                                        notifyQuickly.setNotifyId(notifications.size());
                                                        notifyQuickly.setUserSendId(student.getUserId());
                                                        notifyQuickly.setUserGetId(group.getAdminUserId());
                                                        notifyQuickly.setContent(student.getFullName() + " vừa gửi đơn xin gia nhập nhóm " + group.getGroupName() + " của bạn, xem ngay!");

                                                        // Thêm thông báo vào Firebase
                                                        notifyQuicklyAPI.addNotification(notifyQuickly);
                                                    }

                                                    @Override
                                                    public void onGroupsReceived(List<Group> groups) {
                                                    }
                                                });
                                            }
                                        });

                                        // Thông báo cho người dùng rằng câu trả lời của họ đã được ghi nhận
                                        Toast.makeText(requireContext(), "Câu trả lời của bạn đã được ghi nhận!", Toast.LENGTH_SHORT).show();

                                        // Đánh dấu đã thêm câu trả lời
                                        isAnswersAdded[0] = true;
                                    }

                                    @Override
                                    public void onStudentsReceived(List<Student> students) {
                                    }
                                });
                            }

                            // Đóng dialog sau khi gửi câu trả lời
                            dialog.dismiss();
                        });

                    }
                }
            }
        });

        cancleButton.setOnClickListener(v -> {
            dialog.dismiss();
        });


        // Cài đặt kích thước cho Dialog
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.comment_custom));

        // Thiết lập marginHorizontal 10dp cho Dialog
        int marginInDp = (int) (10 * getResources().getDisplayMetrics().density);
        if (dialog.getWindow() != null) {
            // Lấy WindowManager.LayoutParams
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.horizontalMargin = marginInDp / (float) getResources().getDisplayMetrics().widthPixels; // margin dưới dạng tỉ lệ so với chiều rộng màn hình

            dialog.getWindow().setAttributes(params);
        }

        // Hiển thị Dialog
        dialog.show();
    }
}
