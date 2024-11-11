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
        String uniqueKey = String.valueOf(answer.getAnswerId());  // Lấy khóa của câu trả lời từ answerId

        // Xóa câu trả lời khỏi Firebase theo khóa (uniqueKey)
        answerDatabase.child(uniqueKey).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                    } else {
                        // Xử lý khi có lỗi xảy ra
                    }
                });
    }


    // Thêm một câu hỏi mới vào database
    public void addAnswer(Answer answer) {
        String uniqueKey = String.valueOf(answer.getAnswerId());

        // Thêm câu hỏi vào Firebase với uniqueKey
        answerDatabase.child(uniqueKey).setValue(answer)
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
                for (DataSnapshot answerSnapshot : snapshot.getChildren()) {
                    Answer answer = answerSnapshot.getValue(Answer.class);
                    if (answer != null) {
                        answerList.add(answer);  // Thêm câu hỏi vào danh sách
                    }
                }
                callback.onAnswersReceived(answerList);  // Trả về danh sách câu hỏi
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }

    // Lấy câu hỏi theo groupId
    public void getAnswersByQuestionId(int id, final AnswersCallback callback) {
        answerDatabase.orderByChild("questionId").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Answer> answerList = new ArrayList<>();
                for (DataSnapshot questionSnapshot : snapshot.getChildren()) {
                    Answer answer = questionSnapshot.getValue(Answer.class);
                    if (answer != null) {
                        answerList.add(answer);  // Thêm câu hỏi vào danh sách
                    }
                }
                callback.onAnswersReceived(answerList);  // Trả về danh sách câu hỏi
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
