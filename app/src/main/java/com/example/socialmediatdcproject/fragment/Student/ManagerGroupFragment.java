package com.example.socialmediatdcproject.fragment.Student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.AnswerAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.QuestionAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.CreateNewGroupActivity;
import com.example.socialmediatdcproject.adapter.AnswerAdapter;
import com.example.socialmediatdcproject.adapter.PostApproveAdapter;
import com.example.socialmediatdcproject.dataModels.Answer;
import com.example.socialmediatdcproject.dataModels.Question;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManagerGroupFragment extends Fragment {

    RecyclerView recyclerView;
    FrameLayout frameLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two_button_manager, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Intent intent = requireActivity().getIntent();
        int groupId = intent.getIntExtra("groupId" , -1);


        Button buttonUser = view.findViewById(R.id.button_apply_for_group);
        Button buttonPost = view.findViewById(R.id.button_approve_post_group);
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        frameLayout = requireActivity().findViewById(R.id.third_content_fragment);

        changeColorButtonActive(buttonUser);
        changeColorButtonNormal(buttonPost);

        TextView textView = requireActivity().findViewById(R.id.null_content_notify);

        loadUserApply(groupId, textView);

        buttonUser.setOnClickListener(v -> {
            loadUserApply(groupId, textView);

            changeColorButtonActive(buttonUser);
            changeColorButtonNormal(buttonPost);
        });

        buttonPost.setOnClickListener(v -> {
            loadPostApproveFromFirebase(groupId, textView);

            changeColorButtonActive(buttonPost);
            changeColorButtonNormal(buttonUser);
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

    public void loadPostApproveFromFirebase(int id, TextView textView) {
        ArrayList<Post> postsApprove = new ArrayList<>();
        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getGroupById(id ,new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {
                PostAPI postAPI = new PostAPI();
                postAPI.getPostsByGroupId(group.getGroupId(), new PostAPI.PostCallback() {
                    @Override
                    public void onPostReceived(Post post) {

                    }

                    @Override
                    public void onPostsReceived(List<Post> posts) {
                        postsApprove.clear();

                        for (Post p : posts) {
                            if (p.getStatus() == 0) {
                                postsApprove.add(p);
                            }
                        }

                        if (postsApprove.isEmpty()) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText("Chưa có bài viết nào cần duyệt");
                        }
                        else {
                            textView.setVisibility(View.GONE);
                        }

                        PostApproveAdapter postMyselfAdapter = new PostApproveAdapter(postsApprove, requireContext());
                        recyclerView.setAdapter(postMyselfAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    }
                });
            }

            @Override
            public void onGroupsReceived(List<Group> groups) {

            }
        });
    }

    public void loadUserApply(int groupId, TextView textView){
        ArrayList<Answer> answerList = new ArrayList<>();
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
                        answerList.clear();

                        for (Answer a : answers) {
                            if (a.getQuestionId() == question.getQuestionId()) {
                                answerList.add(a);
                            }
                        }

                        if (answerList.isEmpty()) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText("Hiện chưa có đơn xin tham gia nào");
                        }
                        else {
                            textView.setVisibility(View.GONE);
                        }

                        // Cập nhật RecyclerView với dữ liệu bài viết
                        AnswerAdapter answerAdapter = new AnswerAdapter(answerList, requireContext());
                        recyclerView.setAdapter(answerAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    }
                });

            }

            @Override
            public void onQuestionsReceived(List<Question> questions) {

            }
        });

    }
}

