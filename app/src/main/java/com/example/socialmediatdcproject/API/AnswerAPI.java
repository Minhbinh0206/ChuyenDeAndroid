package com.example.socialmediatdcproject.API;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.dataModels.Answer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AnswerAPI {
    private DatabaseReference answerDatabase;

    public AnswerAPI() {
        answerDatabase = FirebaseDatabase.getInstance().getReference("Answers");
    }

    // Xóa câu trả lời dựa trên questionId và userId
    public void deleteAnswer(int questionId, int userId) {
        answerDatabase.child(String.valueOf(questionId))
                .orderByChild("userId")
                .equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot answerSnapshot : snapshot.getChildren()) {
                                answerSnapshot.getRef().removeValue()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {

                                            } else {

                                            }
                                        });
                            }
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    // Thêm một câu trả lời mới với uniqueAnswerId tự động
    public void addAnswer(Answer answer) {
        String uniqueAnswerId = UUID.randomUUID().toString(); // Tạo ID duy nhất
        answerDatabase.child(String.valueOf(answer.getQuestionId()))
                .child(uniqueAnswerId)
                .setValue(answer)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Xử lý khi thêm thành công (nếu cần)
                    } else {
                        // Xử lý khi có lỗi (nếu cần)
                    }
                });
    }

    // Lấy tất cả câu trả lời trong Firebase
    public void getAllAnswers(final AnswersCallback callback) {
        answerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Answer> answerList = new ArrayList<>();
                for (DataSnapshot questionSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot answerSnapshot : questionSnapshot.getChildren()) {
                        Answer answer = answerSnapshot.getValue(Answer.class);
                        if (answer != null) {
                            answerList.add(answer);
                        }
                    }
                }
                callback.onAnswersReceived(answerList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi
            }
        });
    }

    // Lấy câu trả lời theo questionId
    public void getAnswersByQuestionId(int questionId, final AnswersCallback callback) {
        answerDatabase.child(String.valueOf(questionId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Answer> answerList = new ArrayList<>();
                for (DataSnapshot answerSnapshot : snapshot.getChildren()) {
                    Answer answer = answerSnapshot.getValue(Answer.class);
                    if (answer != null) {
                        answerList.add(answer);
                    }
                }
                callback.onAnswersReceived(answerList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi
            }
        });
    }

    // Interface callback cho việc xử lý câu trả lời
    public interface AnswersCallback {
        void onAnswerReceived(Answer answer);
        void onAnswersReceived(List<Answer> answers);
    }

    // Interface callback cho việc xóa câu trả lời
    public interface AnswerDeleteCallback {
        void onAnswerDeleted(String uniqueAnswerId);
        void onDeleteFailed(String errorMessage);
    }
}
