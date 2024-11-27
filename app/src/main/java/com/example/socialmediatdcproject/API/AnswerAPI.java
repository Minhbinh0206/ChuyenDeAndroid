package com.example.socialmediatdcproject.API;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.dataModels.Answer;
import com.example.socialmediatdcproject.dataModels.Question;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AnswerAPI {
    private DatabaseReference answerDatabase;

    public AnswerAPI() {
        answerDatabase = FirebaseDatabase.getInstance().getReference("Answers");
    }

    public void deleteAnswer(Answer answer) {
        answerDatabase.child(String.valueOf(answer.getQuestionId())).child(String.valueOf(answer.getAnswerId())).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                    } else {
                        // Xử lý khi có lỗi xảy ra
                    }
                });
    }


    // Thêm một câu hỏi mới vào database
    public void addAnswer(Answer answer) {
        answerDatabase.child(String.valueOf(answer.getQuestionId())).child(String.valueOf(answer.getAnswerId())).setValue(answer)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                    } else {
                        // Xử lý khi có lỗi (nếu cần)
                    }
                });
    }

    // Lấy tất cả câu hỏi
    public void getAllAnswers(final AnswersCallback callback) {
        answerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Answer> answerList = new ArrayList<>();
                // Duyệt qua tất cả các câu trả lời
                for (DataSnapshot questionSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot answerSnapshot : questionSnapshot.getChildren()) {
                        Answer answer = answerSnapshot.getValue(Answer.class);
                        if (answer != null) {
                            answerList.add(answer);  // Thêm câu trả lời vào danh sách
                        }
                    }
                }
                callback.onAnswersReceived(answerList);  // Trả về danh sách câu trả lời
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }

    // Lấy câu hỏi theo groupId
    public void getAnswersByQuestionId(int id, final AnswersCallback callback) {
        answerDatabase.child(String.valueOf(id)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Answer> answerList = new ArrayList<>();
                // Duyệt qua các câu trả lời cho câu hỏi với questionId = id
                for (DataSnapshot answerSnapshot : snapshot.getChildren()) {
                    Answer answer = answerSnapshot.getValue(Answer.class);
                    if (answer != null) {
                        answerList.add(answer);  // Thêm câu trả lời vào danh sách
                    }
                }
                callback.onAnswersReceived(answerList);  // Trả về danh sách câu trả lời
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }

    public interface AnswerDeleteCallback {
        void onAnswerDeleted(int answerId);  // Callback khi câu trả lời bị xóa thành công
        void onDeleteFailed(String errorMessage);  // Callback khi có lỗi xảy ra trong quá trình xóa
    }


    // Định nghĩa interface QuestionCallback
    public interface AnswersCallback {
        void onAnswerReceived(Answer answer);  // Callback khi nhận được 1 câu hỏi
        void onAnswersReceived(List<Answer> answers);  // Callback khi nhận được danh sách câu hỏi
    }
}
