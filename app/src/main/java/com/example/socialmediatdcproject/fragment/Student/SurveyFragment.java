package com.example.socialmediatdcproject.fragment.Student;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.EventAPI;
import com.example.socialmediatdcproject.API.FilterPostsAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.MessageAPI;
import com.example.socialmediatdcproject.API.RatingAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.SurveyAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.MessengerActivity;
import com.example.socialmediatdcproject.activity.SearchFriendActivity;
import com.example.socialmediatdcproject.activity.SharedActivity;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.dataModels.Message;
import com.example.socialmediatdcproject.fragment.Admin.HomeAdminFragment;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.AnswerSurvey;
import com.example.socialmediatdcproject.model.Event;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.QuestionSurvey;
import com.example.socialmediatdcproject.model.Rating;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.Survey;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SurveyFragment extends Fragment {
    TextView addQ, submitSurvey, adjustSurvey, review;
    RatingBar ratingBar;
    int adminId, eventId;
    LinearLayout layoutSurvey;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for this fragment
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_survey_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Intent intent = requireActivity().getIntent();
        eventId = intent.getIntExtra("eventId", -1);
        adminId = intent.getIntExtra("adminId", -1);

        layoutSurvey = view.findViewById(R.id.linearLayout2);

        addQ = view.findViewById(R.id.button_add_question);
        submitSurvey = view.findViewById(R.id.button_submit);
        adjustSurvey = view.findViewById(R.id.button_adjust);

        ratingBar = view.findViewById(R.id.ratingBar);
        review = view.findViewById(R.id.review);

        submitSurvey.setVisibility(View.GONE);
        addQ.setVisibility(View.GONE);
        adjustSurvey.setVisibility(View.GONE);

        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        adminDepartmentAPI.getAdminDepartmentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                EventAPI eventAPI = new EventAPI();
                eventAPI.getEventById(adminId, eventId, new EventAPI.EventCallback() {
                    @Override
                    public void onEventReceived(Event event) {
                        SurveyAPI surveyAPI = new SurveyAPI();
                        surveyAPI.getSurveyByAdminAndEvent(adminId, eventId, new SurveyAPI.SurveyCallback() {
                            @Override
                            public void onSuccess(Survey survey) {
                                RatingAPI ratingAPI = new RatingAPI();
                                ratingAPI.getAllRatingRealtime(adminId, eventId, survey.getSurveyId(), new RatingAPI.AllRatingsCallback() {
                                    @Override
                                    public void onSuccess(List<Rating> ratings) {
                                        double average = 0;
                                        int sum = 0;

                                        if (ratings.size() != 0) {
                                            for (Rating r : ratings) {
                                                sum = sum + r.getRatingStar();
                                            }

                                            average = sum / ratings.size();
                                        }
                                        else {
                                            average = 5;
                                        }

                                        ratingBar.setRating((int) average);
                                        switch ((int) average){
                                            case 1:
                                                review.setText("Rất tệ");
                                                break;
                                            case 2:
                                                review.setText("Không tốt");
                                                break;
                                            case 3:
                                                review.setText("Bình thường");
                                                break;
                                            case 4:
                                                review.setText("Khá ổn");
                                                break;
                                            case 5:
                                                review.setText("Rất tuyệt");
                                                break;
                                        }
                                        ratingBar.setEnabled(false);
                                    }

                                    @Override
                                    public void onFailure(Exception e) {

                                    }
                                });

                                if (survey.getQuestionSurveys() != null) {
                                    adjustSurvey.setVisibility(View.GONE);
                                    if (survey.getQuestionSurveys() != null) {
                                        for (QuestionSurvey q : survey.getQuestionSurveys()) {
                                            // Inflate layout mới từ XML cho câu hỏi và câu trả lời
                                            View questionView = LayoutInflater.from(getContext()).inflate(R.layout.item_question_survey, layoutSurvey, false);
                                            layoutSurvey.addView(questionView);

                                            TextView question = questionView.findViewById(R.id.items_question_survey);
                                            EditText answer = questionView.findViewById(R.id.items_answer_survey);
                                            TextView remove = questionView.findViewById(R.id.button_remove_question);

                                            question.setText(q.getQuestionContent());
                                            answer.setEnabled(false);
                                            remove.setVisibility(View.GONE);
                                        }
                                    }
                                }
                                else {
                                    adjustSurvey.setVisibility(View.VISIBLE);
                                    adjustSurvey.setOnClickListener(v -> {
                                        adjustSurvey.setVisibility(View.GONE);
                                        submitSurvey.setVisibility(View.VISIBLE);
                                        addQ.setVisibility(View.VISIBLE);

                                        // Inflate layout mới từ XML cho câu hỏi và câu trả lời
                                        final View[] questionView = {LayoutInflater.from(getContext()).inflate(R.layout.item_question_survey, layoutSurvey, false)};
                                        layoutSurvey.addView(questionView[0]);

                                        // Lặp qua tất cả các câu hỏi hiện tại và điều chỉnh nút "remove"
                                        for (int i = 0; i < layoutSurvey.getChildCount(); i++) {
                                            View childView = layoutSurvey.getChildAt(i);
                                            TextView removeBtn = childView.findViewById(R.id.button_remove_question);
                                            if (removeBtn != null) {
                                                if (i == 0) {
                                                    removeBtn.setVisibility(View.GONE); // Ẩn nút "remove" nếu là câu hỏi đầu tiên
                                                } else {
                                                    removeBtn.setVisibility(View.VISIBLE); // Hiển thị nút "remove" nếu không phải câu hỏi đầu tiên
                                                }
                                            }
                                        }

                                        addQ.setOnClickListener(b -> {
                                            // Kiểm tra tất cả các câu hỏi đã nhập
                                            boolean hasEmptyQuestion = false;

                                            // Lặp qua tất cả các câu hỏi hiện tại để kiểm tra nhập dữ liệu
                                            for (int i = 0; i < layoutSurvey.getChildCount(); i++) {
                                                View childView = layoutSurvey.getChildAt(i);
                                                EditText editText = childView.findViewById(R.id.items_answer_survey);
                                                if (editText != null) {
                                                    String questionText = editText.getText().toString().trim();
                                                    if (questionText.isEmpty()) {
                                                        hasEmptyQuestion = true; // Có câu hỏi chưa nhập
                                                        editText.setError("Câu hỏi không được để trống!");
                                                    }
                                                }
                                            }

                                            if (hasEmptyQuestion) {
                                                // Nếu có câu hỏi chưa nhập, không thêm câu hỏi mới
                                                return;
                                            }

                                            // Thêm câu hỏi mới
                                            questionView[0] = LayoutInflater.from(getContext()).inflate(R.layout.item_question_survey, layoutSurvey, false);
                                            layoutSurvey.addView(questionView[0]);

                                            // Lặp qua tất cả các câu hỏi hiện tại và điều chỉnh nút "remove"
                                            for (int i = 0; i < layoutSurvey.getChildCount(); i++) {
                                                View childView = layoutSurvey.getChildAt(i);
                                                TextView removeBtn = childView.findViewById(R.id.button_remove_question);
                                                if (removeBtn != null) {
                                                    if (i == 0) {
                                                        removeBtn.setVisibility(View.GONE); // Ẩn nút "remove" nếu là câu hỏi đầu tiên
                                                    } else {
                                                        removeBtn.setVisibility(View.VISIBLE); // Hiển thị nút "remove" nếu không phải câu hỏi đầu tiên
                                                    }
                                                }
                                            }

                                            TextView remove = questionView[0].findViewById(R.id.button_remove_question);
                                            remove.setOnClickListener(v1 -> {
                                                if (layoutSurvey.getChildCount() == 1) {
                                                    Toast.makeText(getContext(), "Bạn phải tạo ít nhất 1 câu hỏi!", Toast.LENGTH_SHORT).show();
                                                }
                                                // Tìm View cha chứa nút "Xóa" được nhấn
                                                View parentView = (View) v1.getParent();
                                                // Xóa View cha khỏi layoutSurvey
                                                layoutSurvey.removeView(parentView);
                                            });
                                        });

                                        submitSurvey.setOnClickListener(n -> {
                                            SurveyAPI surveyAPI = new SurveyAPI();
                                            List<QuestionSurvey> questionList = new ArrayList<>();
                                            boolean hasEmptyQuestion = false;  // Biến kiểm tra câu hỏi rỗng

                                            for (int i = 0; i < layoutSurvey.getChildCount(); i++) {
                                                View childView = layoutSurvey.getChildAt(i);
                                                TextView remove = childView.findViewById(R.id.button_remove_question);
                                                EditText editText = childView.findViewById(R.id.items_answer_survey);

                                                // Kiểm tra xem EditText có rỗng không
                                                if (editText != null && editText.getText().toString().trim().isEmpty()) {
                                                    hasEmptyQuestion = true;  // Nếu có câu hỏi rỗng, đánh dấu biến này là true
                                                } else {
                                                    remove.setVisibility(View.GONE);
                                                    String questionText = editText.getText().toString().trim();
                                                    if (!questionText.isEmpty()) {
                                                        QuestionSurvey question = new QuestionSurvey();
                                                        question.setQuestionId(questionList.size());
                                                        question.setAnswerSurveys(new ArrayList<>());
                                                        question.setQuestionContent(questionText);

                                                        questionList.add(question);
                                                    }
                                                }
                                            }

                                            // Nếu có câu hỏi rỗng, không cho phép submit
                                            if (hasEmptyQuestion) {
                                                Toast.makeText(getContext(), "Câu hỏi không được để trống!", Toast.LENGTH_SHORT).show();
                                                return; // Dừng việc submit khảo sát
                                            }

                                            submitSurvey.setVisibility(View.GONE);
                                            addQ.setVisibility(View.GONE); // Ẩn nút thêm câu hỏi

                                            // Tắt khả năng chỉnh sửa cho tất cả các EditText
                                            for (int i = 0; i < layoutSurvey.getChildCount(); i++) {
                                                View childView = layoutSurvey.getChildAt(i);
                                                EditText editText = childView.findViewById(R.id.items_answer_survey);
                                                if (editText != null) {
                                                    editText.setEnabled(false); // Tắt khả năng chỉnh sửa
                                                }
                                            }

                                            // Tạo Dialog
                                            Dialog loadingDialog = new Dialog(getContext());
                                            loadingDialog.setContentView(R.layout.dialog_loading);
                                            loadingDialog.setCancelable(false); // Không cho phép người dùng tắt dialog bằng cách bấm ngoài
                                            TextView load = loadingDialog.findViewById(R.id.textLoading);
                                            load.setText("Đang khởi tạo bài khảo sát...");

                                            // Hiển thị Dialog
                                            loadingDialog.show();

                                            surveyAPI.getSurveyByAdminAndEvent(adminId, eventId, new SurveyAPI.SurveyCallback() {
                                                @Override
                                                public void onSuccess(Survey surveyF) {
                                                    if (surveyF != null) {
                                                        surveyF.setQuestionSurveys(questionList); // Gán danh sách câu hỏi

                                                        adjustSurvey.setVisibility(View.GONE);
                                                        surveyAPI.updateSurvey(adminId, eventId, surveyF.getSurveyId(), surveyF);
                                                        loadingDialog.dismiss();
                                                        Toast.makeText(getContext(), "Thêm khảo sát thành công!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Exception e) {

                                                }
                                            });
                                        });
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {

                            }
                        });
                    }

                    @Override
                    public void onEventsReceived(List<Event> events) {

                    }
                });
            }

            @Override
            public void onUsersReceived(List<AdminDepartment> adminDepartment) {

            }

            @Override
            public void onError(String s) {

            }
        });

        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                adjustSurvey.setVisibility(View.VISIBLE);
                adjustSurvey.setText("Xác nhận");

                // Lấy danh sách câu hỏi từ API
                loadSurveyQuestions(student);
            }

            @Override
            public void onStudentsReceived(List<Student> students) {
                // Không sử dụng
            }
        });
    }

    private void loadSurveyQuestions(Student student) {
        SurveyAPI surveyAPI = new SurveyAPI();
        surveyAPI.getAllQuestionsByAdminAndEvent(adminId, eventId, new SurveyAPI.QuestionSurveyListCallback() {
            @Override
            public void onSuccess(List<QuestionSurvey> questionList) {
                boolean isSurveyDone = checkIfSurveyDone(questionList, student.getUserId());
                Log.d("MKM", "onSuccess: " + isSurveyDone);
                setupRatingBar(review);

                surveyAPI.getSurveyByAdminAndEvent(adminId, eventId, new SurveyAPI.SurveyCallback() {
                    @Override
                    public void onSuccess(Survey survey) {
                        handleSurvey(survey, student, isSurveyDone);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Xử lý lỗi
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                // Xử lý lỗi
            }
        });
    }

    private boolean checkIfSurveyDone(List<QuestionSurvey> questionList, int userId) {
        if (questionList == null) return false;
        for (QuestionSurvey qe : questionList) {
            if (qe.getAnswerSurveys() != null) {
                for (AnswerSurvey a : qe.getAnswerSurveys()) {
                    if (a.getUserId() == userId) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void setupRatingBar(TextView review) {
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            String[] reviews = {"Tồi tệ", "Không thích", "Bình thường", "Hài lòng", "Tuyệt vời"};
            if (rating >= 1 && rating <= 5) {
                review.setText(reviews[(int) rating - 1]);
            }
        });
    }

    private void handleSurvey(Survey survey, Student student, boolean isSurveyDone) {
        RatingAPI ratingAPI = new RatingAPI();
        ratingAPI.getAllRating(survey.getAdminId(), survey.getEventId(), survey.getSurveyId(), new RatingAPI.AllRatingsCallback() {
            @Override
            public void onSuccess(List<Rating> ratings) {
                boolean isRating = false;
                Rating rating = new Rating();
                for (Rating r : ratings) {
                    if (r.getUserId() == student.getUserId()) {
                        isRating = true;

                        rating = r;
                    }
                }

                if (isSurveyDone && isRating) {
                    Log.d("MLML", "onSuccess: 1" );
                    displayCompletedSurvey(survey, student, rating);
                } else {
                    Log.d("MLML", "onSuccess: 2" );
                    displayPendingSurvey(survey, student);
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void displayCompletedSurvey(Survey survey, Student student, Rating rating) {
        adjustSurvey.setVisibility(View.INVISIBLE);
        ratingBar.setEnabled(false);
        ratingBar.setRating(rating.getRatingStar());

        for (QuestionSurvey q : survey.getQuestionSurveys()) {
            View questionView = LayoutInflater.from(getContext()).inflate(R.layout.item_question_survey, layoutSurvey, false);
            layoutSurvey.addView(questionView);

            TextView question = questionView.findViewById(R.id.items_question_survey);
            EditText answer = questionView.findViewById(R.id.items_answer_survey);
            TextView remove = questionView.findViewById(R.id.button_remove_question);

            question.setText(q.getQuestionContent());
            if (q.getAnswerSurveys() != null) {
                for (AnswerSurvey answerSurvey : q.getAnswerSurveys()) {
                    if (answerSurvey.getUserId() == student.getUserId()) {
                        answer.setText(answerSurvey.getAnswerContent());
                    }
                }
            }
            answer.setEnabled(false);
            remove.setVisibility(View.GONE);
        }
    }

    private void displayPendingSurvey(Survey survey, Student student) {
        for (QuestionSurvey q : survey.getQuestionSurveys()) {
            View questionView = LayoutInflater.from(getContext()).inflate(R.layout.item_question_survey, layoutSurvey, false);
            layoutSurvey.addView(questionView);

            TextView question = questionView.findViewById(R.id.items_question_survey);
            EditText answer = questionView.findViewById(R.id.items_answer_survey);
            TextView remove = questionView.findViewById(R.id.button_remove_question);

            question.setText(q.getQuestionContent());
            remove.setVisibility(View.GONE);
        }

        adjustSurvey.setOnClickListener(v -> submitSurvey(survey, student));
    }

    private void submitSurvey(Survey survey, Student student) {
        Rating ratings = new Rating();
        ratings.setRatingStar((int) ratingBar.getRating());
        ratings.setUserId(student.getUserId());

        boolean allAnswersFilled = true; // Biến để kiểm tra xem tất cả câu trả lời đã đầy đủ chưa

        for (int i = 0; i < layoutSurvey.getChildCount(); i++) {
            View childView = layoutSurvey.getChildAt(i);
            TextView questionS = childView.findViewById(R.id.items_question_survey);
            EditText answerEditText = childView.findViewById(R.id.items_answer_survey);

            if (answerEditText != null) {
                String answerText = answerEditText.getText().toString().trim();
                if (!answerText.isEmpty()) {
                    saveAnswerToSurvey(survey, questionS.getText().toString(), answerText, student);
                } else {
                    answerEditText.setError("Câu trả lời không được để trống!");
                    allAnswersFilled = false; // Nếu có câu trả lời trống, thay đổi trạng thái
                }
            }
        }

        // Kiểm tra nếu tất cả câu trả lời đều đầy đủ mới thực hiện submit
        if (allAnswersFilled) {
            RatingAPI ratingAPI = new RatingAPI();
            ratingAPI.addRating(survey.getAdminId(), survey.getEventId(), survey.getSurveyId(), ratings);

            ratingBar.setEnabled(false);

            disableAnswers();
            Toast.makeText(getContext(), "Cảm ơn bạn đã dành thời gian cho sự kiện.", Toast.LENGTH_SHORT).show();

            adjustSurvey.setVisibility(View.INVISIBLE);
        } else {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ các câu trả lời.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAnswerToSurvey(Survey survey, String questionContent, String answerText, Student student) {
        SurveyAPI surveyAPI = new SurveyAPI();
        surveyAPI.findQuestionByContent(adminId, eventId, questionContent, new SurveyAPI.QuestionSurveyCallback() {
            @Override
            public void onSuccess(QuestionSurvey question) {
                AnswerSurvey answerSurvey = new AnswerSurvey();
                answerSurvey.setAnswerId(question.getAnswerSurveys() != null ? question.getAnswerSurveys().size() : 0);
                answerSurvey.setAnswerContent(answerText);
                answerSurvey.setQuestionId(question.getQuestionId());
                answerSurvey.setUserId(student.getUserId());

                List<AnswerSurvey> answerSurveys = question.getAnswerSurveys();
                if (answerSurveys == null) {
                    answerSurveys = new ArrayList<>();
                }
                answerSurveys.add(answerSurvey);

                question.setAnswerSurveys(answerSurveys);
                surveyAPI.updateQuestionInSurvey(adminId, eventId, survey.getSurveyId(), question.getQuestionId(), question);
            }

            @Override
            public void onFailure(Exception e) {
                // Xử lý lỗi
            }
        });
    }

    private void disableAnswers() {
        for (int i = 0; i < layoutSurvey.getChildCount(); i++) {
            View childView = layoutSurvey.getChildAt(i);
            EditText answerEditText = childView.findViewById(R.id.items_answer_survey);
            if (answerEditText != null) {
                answerEditText.setEnabled(false);
                answerEditText.setFocusable(false);
                answerEditText.setCursorVisible(false);
            }
        }
    }
}