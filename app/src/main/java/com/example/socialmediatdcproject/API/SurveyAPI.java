package com.example.socialmediatdcproject.API;

import android.util.Log;

import com.example.socialmediatdcproject.model.QuestionSurvey;
import com.example.socialmediatdcproject.model.Rating;
import com.example.socialmediatdcproject.model.Survey;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SurveyAPI {

    private final DatabaseReference surveyDatabase;

    public SurveyAPI() {
        // Tham chiếu đến nút "Surveys" trong Firebase
        surveyDatabase = FirebaseDatabase.getInstance().getReference("Surveys");
    }

    // Hàm để thêm hoặc cập nhật một Survey
    public void addSurvey(Survey survey, int idA, int idE) {
        String adminKey = "" + idA;
        String eventKey = "" + idE;
        String surveyKey = "" + survey.getSurveyId();

        surveyDatabase.child(adminKey)
                .child(eventKey)
                .child(surveyKey)
                .setValue(survey)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("SurveyAPI", "Survey saved successfully.");
                    } else {
                        Log.e("SurveyAPI", "Failed to save survey.", task.getException());
                    }
                });
    }

    public void updateSurvey(int adminId, int eventId, int surveyId, Survey updatedSurvey) {
        String adminKey = "" + adminId;
        String eventKey = "" + eventId;
        String surveyKey = "" + surveyId;

        // Tạo một HashMap chứa các trường cần cập nhật
        HashMap<String, Object> surveyUpdates = new HashMap<>();
        surveyUpdates.put("surveyId", updatedSurvey.getSurveyId());
        surveyUpdates.put("questionSurveys", updatedSurvey.getQuestionSurveys());

        // Cập nhật các trường cần thiết vào Survey
        surveyDatabase.child(adminKey)
                .child(eventKey)
                .child(surveyKey)
                .updateChildren(surveyUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("SurveyAPI", "Survey updated successfully.");
                    } else {
                        Log.e("SurveyAPI", "Failed to update survey.", task.getException());
                    }
                });
    }

    public void getSurveyByAdminAndEvent(int adminId, int eventId, final SurveyCallback callback) {
        String adminKey = "" + adminId;
        String eventKey = "" + eventId;

        surveyDatabase.child(adminKey)
                .child(eventKey)
                .child(String.valueOf(0))  // or use the correct surveyKey if needed
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Ensure the data is being deserialized as a Survey object
                        Survey survey = task.getResult().getValue(Survey.class);
                        if (survey != null) {
                            Log.d("SurveyAPI", "Survey fetched successfully.");
                            if (callback != null) callback.onSuccess(survey);
                        } else {
                            Log.w("SurveyAPI", "Survey not found.");
                            if (callback != null) callback.onSuccess(null);
                        }
                    } else {
                        Log.e("SurveyAPI", "Failed to fetch survey.", task.getException());
                        if (callback != null) callback.onFailure(task.getException());
                    }
                });
    }

    // Hàm để cập nhật một câu hỏi cụ thể trong Survey
    public void updateQuestionInSurvey(int adminId, int eventId, int surveyId, int questionIndex, QuestionSurvey updatedQuestion) {
        String adminKey = "" + adminId;
        String eventKey = "" + eventId;
        String surveyKey = "" + surveyId;

        // Truy cập đến Survey cụ thể
        surveyDatabase.child(adminKey)
                .child(eventKey)
                .child(surveyKey)
                .child("questionSurveys")  // Truy cập danh sách câu hỏi
                .child(String.valueOf(questionIndex))  // Truy cập câu hỏi ở vị trí cần cập nhật
                .setValue(updatedQuestion)  // Cập nhật câu hỏi mới
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("SurveyAPI", "Survey question updated successfully.");
                    } else {
                        Log.e("SurveyAPI", "Failed to update survey question.", task.getException());
                    }
                });
    }


    // Hàm để lấy một Survey cụ thể theo adminId, eventId, và surveyId
    public void getSurveyById(int adminId, int eventId, int surveyId, final SurveyCallback callback) {
        String adminKey = "" + adminId;
        String eventKey = "" + eventId;
        String surveyKey = "" + surveyId;

        surveyDatabase.child(adminKey)
                .child(eventKey)
                .child(surveyKey)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Survey survey = task.getResult().getValue(Survey.class);
                        Log.d("SurveyAPI", "Survey fetched successfully.");
                        if (callback != null) callback.onSuccess(survey);
                    } else {
                        Log.e("SurveyAPI", "Failed to fetch survey.", task.getException());
                        if (callback != null) callback.onFailure(task.getException());
                    }
                });
    }

    // Hàm tìm câu hỏi theo nội dung
    public void findQuestionByContent(int adminId, int eventId, String content, final QuestionSurveyCallback callback) {
        getAllQuestionsByAdminAndEvent(adminId, eventId, new QuestionSurveyListCallback() {
            @Override
            public void onSuccess(List<QuestionSurvey> questionList) {
                for (QuestionSurvey question : questionList) {
                    if (question.getQuestionContent() != null && question.getQuestionContent().equalsIgnoreCase(content)) {
                        Log.d("SurveyAPI", "Found question: " + question.getQuestionContent());
                        if (callback != null) {
                            callback.onSuccess(question);  // Trả về câu hỏi tìm được
                            return;  // Thoát khỏi vòng lặp khi đã tìm thấy câu hỏi
                        }
                    }
                }
                Log.d("SurveyAPI", "No question found with content: " + content);
                if (callback != null) {
                    callback.onFailure(new Exception("No question found with the given content"));
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("SurveyAPI", "Failed to fetch questions.", e);
                if (callback != null) {
                    callback.onFailure(e);
                }
            }
        });
    }

    // Callback khi tìm thấy câu hỏi
    public interface QuestionSurveyCallback {
        void onSuccess(QuestionSurvey question);
        void onFailure(Exception e);
    }

    public void getAllRatingsForSurvey(int adminId, int eventId, int surveyId, final RatingsCallback callback) {
        String adminKey = "" + adminId;
        String eventKey = "" + eventId;
        String surveyKey = "" + surveyId;

        // Truy cập vào node ratings của Survey
        surveyDatabase.child(adminKey)
                .child(eventKey)
                .child(surveyKey)
                .child("ratings")  // Truy cập trường ratings
                .addValueEventListener(new ValueEventListener() {  // Thêm listener real-time
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Lấy danh sách các Rating từ Firebase
                            GenericTypeIndicator<HashMap<String, Rating>> genericTypeIndicator =
                                    new GenericTypeIndicator<HashMap<String, Rating>>() {};
                            HashMap<String, Rating> ratingsMap = dataSnapshot.getValue(genericTypeIndicator);

                            if (ratingsMap != null) {
                                Log.d("SurveyAPI", "Ratings fetched successfully.");
                                if (callback != null) {
                                    callback.onSuccess(ratingsMap);  // Trả về HashMap Rating
                                }
                            } else {
                                Log.w("SurveyAPI", "No ratings found.");
                                if (callback != null) {
                                    callback.onFailure(new Exception("No ratings found."));  // Trả về lỗi nếu không tìm thấy Rating
                                }
                            }
                        } else {
                            Log.w("SurveyAPI", "No data found for ratings.");
                            if (callback != null) {
                                callback.onFailure(new Exception("No data found for ratings."));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("SurveyAPI", "Failed to fetch ratings: " + databaseError.getMessage());
                        if (callback != null) {
                            callback.onFailure(databaseError.toException());  // Trả về lỗi nếu có lỗi
                        }
                    }
                });
    }


    // Callback để trả về danh sách Rating
    public interface RatingsCallback {
        void onSuccess(HashMap<String ,Rating> ratings);
        void onFailure(Exception e);
    }

    public void updateRatingAverage(int adminId, int eventId, int surveyId, float newRatingAverage) {
        String adminKey = "" + adminId;
        String eventKey = "" + eventId;
        String surveyKey = "" + surveyId;

        // Tạo một HashMap chứa các trường cần cập nhật
        HashMap<String, Object> ratingUpdate = new HashMap<>();
        ratingUpdate.put("ratingAverage", newRatingAverage);  // Chỉ cập nhật ratingAverage

        // Cập nhật trường ratingAverage trong Survey
        surveyDatabase.child(adminKey)
                .child(eventKey)
                .child(surveyKey)
                .updateChildren(ratingUpdate)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("SurveyAPI", "Rating average updated successfully.");
                    } else {
                        Log.e("SurveyAPI", "Failed to update rating average.", task.getException());
                    }
                });
    }


    // Hàm để lắng nghe sự thay đổi của ratingAverage trong Survey
    public void listenToRatingAverageChanges(int adminId, int eventId, int surveyId, final RatingAverageCallback callback) {
        String adminKey = "" + adminId;
        String eventKey = "" + eventId;
        String surveyKey = "" + surveyId;

        // Truy cập vào Survey cụ thể và lắng nghe sự thay đổi của ratingAverage
        surveyDatabase.child(adminKey)
                .child(eventKey)
                .child(surveyKey)
                .child("ratingAverage")  // Truy cập trường ratingAverage
                .addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Lấy giá trị của ratingAverage
                            float ratingAverage = dataSnapshot.getValue(Float.class);
                            Log.d("SurveyAPI", "Rating average updated: " + ratingAverage);
                            if (callback != null) {
                                callback.onRatingAverageUpdated(ratingAverage);  // Gọi callback để trả về giá trị mới
                            }
                        }
                    }

                    @Override
                    public void onCancelled(com.google.firebase.database.DatabaseError databaseError) {
                        Log.e("SurveyAPI", "Failed to listen for ratingAverage changes.", databaseError.toException());
                    }
                });
    }

    public void addRatingToSurvey(int adminId, int eventId, int surveyId, Rating rating) {
        String adminKey = "" + adminId;
        String eventKey = "" + eventId;
        String surveyKey = "" + surveyId;

        // Tạo một khóa duy nhất cho mỗi Rating dựa trên userId
        String ratingKey = String.valueOf(rating.getUserId());

        // Truy cập trường ratings của Survey và thêm đối tượng Rating
        surveyDatabase.child(adminKey)
                .child(eventKey)
                .child(surveyKey)
                .child("ratings") // Truy cập trường ratings
                .child(ratingKey) // Khóa cho từng Rating
                .setValue(rating) // Lưu đối tượng Rating
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("SurveyAPI", "Rating added successfully.");
                    } else {
                        Log.e("SurveyAPI", "Failed to add rating.", task.getException());
                    }
                });
    }


    // Callback để xử lý sự thay đổi của ratingAverage
    public interface RatingAverageCallback {
        void onRatingAverageUpdated(float ratingAverage);  // Gọi callback khi ratingAverage thay đổi
    }

    // Hàm để lấy tất cả các câu hỏi từ tất cả Survey theo adminId và eventId
    public void getAllQuestionsByAdminAndEvent(int adminId, int eventId, final QuestionSurveyListCallback callback) {
        getSurveyByAdminAndEvent(adminId, eventId, new SurveyCallback() {
            @Override
            public void onSuccess(Survey survey) {
                if (survey != null) {
                    List<QuestionSurvey> allQuestions = new ArrayList<>();
                    if (survey.getQuestionSurveys() != null) {
                        allQuestions.addAll(survey.getQuestionSurveys());
                    }
                    Log.d("SurveyAPI", "All questions fetched successfully.");
                    if (callback != null) callback.onSuccess(allQuestions);
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    // Callback cho các hàm trả về danh sách Survey
    public interface SurveyListCallback {
        void onSuccess(List<Survey> surveyList);
        void onFailure(Exception e);
    }

    // Callback cho hàm trả về danh sách câu hỏi QuestionSurvey
    public interface QuestionSurveyListCallback {
        void onSuccess(List<QuestionSurvey> questionList);
        void onFailure(Exception e);
    }

    // Callback cho hàm trả về một Survey
    public interface SurveyCallback {
        void onSuccess(Survey survey);
        void onFailure(Exception e);
    }
}
