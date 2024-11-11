package com.example.socialmediatdcproject.API;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.dataModels.Question;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuestionAPI {
    private DatabaseReference questionDatabase;

    public QuestionAPI() {
        questionDatabase = FirebaseDatabase.getInstance().getReference("Questions");
    }

    // Thêm một câu hỏi mới vào database
    public void addQuestion(Question question) {
        String uniqueKey = String.valueOf(question.getGroupId());

        // Thêm câu hỏi vào Firebase với uniqueKey
        questionDatabase.child(uniqueKey).setValue(question)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                    } else {
                        // Xử lý khi có lỗi (nếu cần)
                    }
                });
    }

    // Lấy tất cả câu hỏi
    public void getAllQuestions(final QuestionCallback callback) {
        questionDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Question> questionList = new ArrayList<>();
                for (DataSnapshot questionSnapshot : snapshot.getChildren()) {
                    Question question = questionSnapshot.getValue(Question.class);
                    if (question != null) {
                        questionList.add(question);  // Thêm câu hỏi vào danh sách
                    }
                }
                callback.onQuestionsReceived(questionList);  // Trả về danh sách câu hỏi
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }

    // Lấy câu hỏi theo groupId (trả về 1 câu hỏi duy nhất)
    public void getQuestionByGroupId(int groupId, final QuestionCallback callback) {
        questionDatabase.orderByChild("groupId").equalTo(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Kiểm tra nếu có câu hỏi nào trả về
                if (snapshot.exists()) {
                    // Lấy câu hỏi đầu tiên tìm thấy
                    Question question = null;
                    for (DataSnapshot questionSnapshot : snapshot.getChildren()) {
                        question = questionSnapshot.getValue(Question.class);
                        break;  // Dừng vòng lặp sau khi lấy câu hỏi đầu tiên
                    }
                    if (question != null) {
                        callback.onQuestionReceived(question);  // Trả về câu hỏi
                    } else {
                        // Trường hợp không tìm thấy câu hỏi (nếu có thể xảy ra)
                        callback.onQuestionReceived(null);
                    }
                } else {
                    // Trường hợp không có câu hỏi nào cho groupId này
                    callback.onQuestionReceived(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }


    public void getQuestionById(int questionId, final QuestionCallback callback) {
        questionDatabase.orderByChild("questionId").equalTo(questionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Kiểm tra nếu có câu hỏi nào trả về
                if (snapshot.exists()) {
                    // Lấy câu hỏi đầu tiên tìm thấy
                    Question question = null;
                    for (DataSnapshot questionSnapshot : snapshot.getChildren()) {
                        question = questionSnapshot.getValue(Question.class);
                        break;  // Dừng vòng lặp sau khi lấy câu hỏi đầu tiên
                    }
                    if (question != null) {
                        callback.onQuestionReceived(question);  // Trả về câu hỏi
                    } else {
                        // Trường hợp không tìm thấy câu hỏi (nếu có thể xảy ra)
                        callback.onQuestionReceived(null);
                    }
                } else {
                    // Trường hợp không có câu hỏi nào cho groupId này
                    callback.onQuestionReceived(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }

    // Cập nhật thông tin câu hỏi
    public void updateQuestion(Question question, final QuestionCallback callback) {
        String uniqueKey = questionDatabase.push().getKey(); // Lấy uniqueKey từ Firebase

        // Cập nhật câu hỏi trong Firebase với uniqueKey
        questionDatabase.child(uniqueKey).setValue(question)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onQuestionReceived(question);  // Trả về câu hỏi đã cập nhật
                    } else {
                        // Xử lý khi có lỗi xảy ra
                    }
                });
    }

    // Định nghĩa interface QuestionCallback
    public interface QuestionCallback {
        void onQuestionReceived(Question question);  // Callback khi nhận được 1 câu hỏi
        void onQuestionsReceived(List<Question> questions);  // Callback khi nhận được danh sách câu hỏi
    }
}
