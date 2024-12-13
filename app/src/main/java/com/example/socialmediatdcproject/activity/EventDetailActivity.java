package com.example.socialmediatdcproject.activity;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.EventAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.SurveyAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.AnswerAdapter;
import com.example.socialmediatdcproject.adapter.AnswerSurveyAdapter;
import com.example.socialmediatdcproject.adapter.AssistAdapter;
import com.example.socialmediatdcproject.adapter.CommentAdapter;
import com.example.socialmediatdcproject.fragment.Admin.HomeAdminFragment;
import com.example.socialmediatdcproject.fragment.Student.HomeFragment;
import com.example.socialmediatdcproject.fragment.Student.SurveyFragment;
import com.example.socialmediatdcproject.model.Assist;
import com.example.socialmediatdcproject.model.Event;
import com.example.socialmediatdcproject.model.QuestionSurvey;
import com.example.socialmediatdcproject.model.RollCall;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.Survey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class EventDetailActivity extends AppCompatActivity {
    private TextView content, title, textStatus, position;
    private ImageView imageEvent;
    private static final int QR_REQUEST_CODE = 1;
    private ImageButton iconBackEvent, iconAssistEvent, iconActionEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_event_layout);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.survey_content, new SurveyFragment());
        fragmentTransaction.commit();

        iconBackEvent = findViewById(R.id.icon_back_event);
        iconAssistEvent = findViewById(R.id.icon_assist_event);
        iconActionEvent = findViewById(R.id.icon_action_event);
        title = findViewById(R.id.detail_title_event);
        content = findViewById(R.id.detail_content_event);
        imageEvent = findViewById(R.id.detail_image_event);
        NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);
        LinearLayout layout = findViewById(R.id.navComment);
        position = findViewById(R.id.position);
        position.setVisibility(View.INVISIBLE);

        // Lắng nghe sự kiện cuộn
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Nếu cuộn xuống (scrollY > oldScrollY), đổi màu nền thành màu xanh
                if (scrollY > oldScrollY) {
                    position.setVisibility(View.VISIBLE);
                    animateBackgroundColor(layout, Color.TRANSPARENT, getResources().getColor(R.color.defaultBlue));
                }
                // Nếu cuộn lên (scrollY < oldScrollY), đặt lại màu nền
                else if (scrollY < oldScrollY) {
                    position.setVisibility(View.INVISIBLE);
                    animateBackgroundColor(layout, getResources().getColor(R.color.defaultBlue), Color.TRANSPARENT);
                }
            }
        });

        // Set back button listener
        iconBackEvent.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get event ID and type of joining from the intent
        Intent intent = getIntent();
        int eventId = intent.getIntExtra("eventId", -1);
        int typeJoin = intent.getIntExtra("typeJoin", -1);
        int adminId = intent.getIntExtra("adminId", -1);

        EventAPI eventAPI = new EventAPI();
        eventAPI.getEventById(eventId, adminId, new EventAPI.EventCallback() {
            @Override
            public void onEventReceived(Event event) {
                Glide.with(EventDetailActivity.this)
                        .load(event.getImageEvent())
                        .centerCrop()
                        .into(imageEvent);

                title.setText(event.getTitleEvent());

                content.setText(event.getContentEvent());

                if (event.getStatus() == 1 || event.getStatus() == 0) {
                    if (typeJoin == 3) {
                        // Admin
                        iconAssistEvent.setBackground(ContextCompat.getDrawable(EventDetailActivity.this, R.drawable.icon_assist));

                        iconAssistEvent.setOnClickListener(v -> {
                            adminShowPopupAssist(EventDetailActivity.this, event);
                        });

                        position.setText("Admin sự kiện");
                    }
                    else if (typeJoin == 2) {
                        eventAPI.listenForEventStatusChange(event, new EventAPI.EventStatusCallback() {
                            @Override
                            public void onEventStatusChanged(Event event) {
                                if (event.getStatus() == 2) {
                                    // Hiển thị popup yêu cầu người dùng rời đi
                                    showExitPopup();
                                }
                            }
                        });

                        // Người hỗ trợ
                        iconAssistEvent.setBackground(ContextCompat.getDrawable(EventDetailActivity.this, R.drawable.icon_qrcode));

                        iconAssistEvent.setOnClickListener(v -> {
                            assistShowPopupQR(EventDetailActivity.this, event);
                        });

                        position.setText("Người hỗ trợ");

                    }
                    else {
                        eventAPI.listenForEventStatusChange(event, new EventAPI.EventStatusCallback() {
                            @Override
                            public void onEventStatusChanged(Event event) {
                                if (event.getStatus() == 2) {
                                    // Hiển thị popup yêu cầu người dùng rời đi
                                    showExitPopup();
                                }
                            }
                        });

                        // Sinh viên
                        iconAssistEvent.setBackground(ContextCompat.getDrawable(EventDetailActivity.this, R.drawable.icon_qrcode));

                        iconAssistEvent.setOnClickListener(v -> {
                            StudentAPI studentAPI = new StudentAPI();
                            studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                                @Override
                                public void onStudentReceived(Student student) {
                                    List<RollCall> rollCalls = event.getUserJoin();

                                    boolean isDone = false;

                                    if (rollCalls != null) {
                                        for (RollCall r : rollCalls) {
                                            if (r.getStudentNumber().equals(student.getStudentNumber())) {
                                                isDone = true;
                                                Toast.makeText(EventDetailActivity.this, "Bạn đã điểm danh rồi.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }

                                        if (!isDone) {
                                            // Bắt đầu quét QR nếu chưa điểm danh
                                            Intent scanIntent = new Intent(EventDetailActivity.this, CaptureActivity.class); // Dùng CaptureActivity mặc định
                                            startActivityForResult(scanIntent, QR_REQUEST_CODE);
                                        }
                                    }
                                    else {
                                        // Bắt đầu quét QR nếu chưa điểm danh
                                        Intent scanIntent = new Intent(EventDetailActivity.this, CaptureActivity.class); // Dùng CaptureActivity mặc định
                                        startActivityForResult(scanIntent, QR_REQUEST_CODE);
                                    }
                                }

                                @Override
                                public void onStudentsReceived(List<Student> students) {

                                }
                            });
                        });

                        position.setText("Sinh viên");
                    }
                } else if (event.getStatus() == 2) {
                    if (typeJoin == 3) {
                        iconAssistEvent.setEnabled(true);

                        // Admin
                        iconAssistEvent.setBackground(ContextCompat.getDrawable(EventDetailActivity.this, R.drawable.icon_survey_white));

                        iconAssistEvent.setOnClickListener(v -> {
                            SurveyAPI surveyAPI = new SurveyAPI();
                            surveyAPI.getSurveyByAdminAndEvent(event.getAdminEventId(), event.getEventId(), new SurveyAPI.SurveyCallback() {
                                @Override
                                public void onSuccess(Survey survey) {
                                    if (survey.getQuestionSurveys() != null) {
                                        adminShowPopupSurvey(EventDetailActivity.this, event);
                                    }
                                    else {
                                        Toast.makeText(EventDetailActivity.this, "Không có khảo sát nào được tạo", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
                        });

                        position.setText("Admin sự kiện");
                    }
                    else if (typeJoin == 2) {
                        iconAssistEvent.setEnabled(false);

                        // Người hỗ trợ
                        iconAssistEvent.setBackground(ContextCompat.getDrawable(EventDetailActivity.this, R.drawable.icon_qrcode));

                        position.setText("Người hỗ trợ");

                    }
                    else {
                        iconAssistEvent.setEnabled(false);

                        // Sinh viên
                        iconAssistEvent.setBackground(ContextCompat.getDrawable(EventDetailActivity.this, R.drawable.icon_qrcode));

                        position.setText("Sinh viên");
                    }
                }

            }

            @Override
            public void onEventsReceived(List<Event> events) {

            }
        });
    }

    private void adminShowPopupAssist(Context context, Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.roll_call_admin, null);
        builder.setView(view);

        Button listAssist = view.findViewById(R.id.list_assistant);
        Button listApply = view.findViewById(R.id.list_apply_for_assist);
        RecyclerView recyclerView = view.findViewById(R.id.reycleview_admin);
        Button cancel = view.findViewById(R.id.cancel_manager);

        changeColorButtonActive(listAssist);
        changeColorButtonNormal(listApply);
        changeColorButtonActive(cancel);

        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.comment_custom));

        cancel.setOnClickListener(v -> dialog.dismiss());

        loadListAssist(event, recyclerView);

        listAssist.setOnClickListener(v -> {
            loadListAssist(event, recyclerView);

            changeColorButtonActive(listAssist);
            changeColorButtonNormal(listApply);
        });

        listApply.setOnClickListener(v -> {
            loadListApply(event, recyclerView);

            changeColorButtonActive(listApply);
            changeColorButtonNormal(listAssist);
        });

        dialog.show();
    }

    private void adminShowPopupSurvey(Context context, Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.survey_admin, null);
        builder.setView(view);

        LinearLayout linearLayout = view.findViewById(R.id.layout_contain_item);
        Button cancel = view.findViewById(R.id.cancel_manager);

        changeColorButtonActive(cancel);

        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.comment_custom));

        cancel.setOnClickListener(v -> dialog.dismiss());

        SurveyAPI surveyAPI = new SurveyAPI();
        surveyAPI.getSurveyByAdminAndEvent(event.getAdminEventId(), event.getEventId(), new SurveyAPI.SurveyCallback() {
            @Override
            public void onSuccess(Survey survey) {
                if (survey.getQuestionSurveys() != null) {
                    for (QuestionSurvey questionSurvey : survey.getQuestionSurveys()) {
                        // Inflate the question layout
                        View questionView = LayoutInflater.from(context).inflate(R.layout.item_list_question_survey, null);

                        // Set the question text
                        TextView questionTitle = questionView.findViewById(R.id.question_title);
                        TextView nullAnswer = questionView.findViewById(R.id.null_answer);
                        questionTitle.setText((questionSurvey.getQuestionId() + 1) + ". " + questionSurvey.getQuestionContent());

                        // Set up the RecyclerView for answers
                        RecyclerView recyclerView = questionView.findViewById(R.id.items_answer_survey_list);

                        if (questionSurvey.getAnswerSurveys() != null) {
                            nullAnswer.setVisibility(View.GONE);

                            GridLayoutManager gridLayoutManager = new GridLayoutManager(EventDetailActivity.this, 1, RecyclerView.HORIZONTAL, false);
                            recyclerView.setLayoutManager(gridLayoutManager);
                            recyclerView.setAdapter(new AnswerSurveyAdapter(questionSurvey.getAnswerSurveys(), EventDetailActivity.this));
                        }
                        else {
                            nullAnswer.setVisibility(View.VISIBLE);
                        }

                        // Add the inflated layout to the LinearLayout
                        linearLayout.addView(questionView);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Handle the failure case
                Toast.makeText(context, "Failed to load survey", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void assistShowPopupQR(Context context, Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.roll_call_assist, null);
        builder.setView(view);

        ImageView rollCollCode = view.findViewById(R.id.qr_code);
        ImageButton cancel = view.findViewById(R.id.icon_cancel);

        EventAPI eventAPI = new EventAPI();
        eventAPI.getEventById(event.getEventId(), event.getAdminEventId(), new EventAPI.EventCallback() {
            @Override
            public void onEventReceived(Event event) {
                // Lấy chuỗi QR code từ event
                String qrCodeString = event.getCurrentQrCode();

                // Kiểm tra nếu chuỗi không rỗng hoặc null
                if (!TextUtils.isEmpty(qrCodeString)) {
                    // Tạo mã QR và hiển thị trên ImageView
                    Bitmap qrCodeBitmap = generateQRCode(qrCodeString);
                    rollCollCode.setImageBitmap(qrCodeBitmap);
                }
            }

            @Override
            public void onEventsReceived(List<Event> events) {

            }
        });

        eventAPI.listenForCodeChange(event.getEventId(), event.getAdminEventId(), new EventAPI.EventCallback() {
            @Override
            public void onEventReceived(Event event) {
                // Lấy chuỗi QR code từ event
                String qrCodeString = event.getCurrentQrCode();

                // Kiểm tra nếu chuỗi không rỗng hoặc null
                if (!TextUtils.isEmpty(qrCodeString)) {
                    // Tạo mã QR và hiển thị trên ImageView
                    Bitmap qrCodeBitmap = generateQRCode(qrCodeString);
                    rollCollCode.setImageBitmap(qrCodeBitmap);
                }
            }

            @Override
            public void onEventsReceived(List<Event> events) {}
        });

        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.comment_custom));

        cancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Hàm tạo mã QR từ chuỗi
    private Bitmap generateQRCode(String data) {
        MultiFormatWriter writer = new MultiFormatWriter();
        // Tạo mã QR dưới dạng ma trận bit
        com.google.zxing.common.BitMatrix bitMatrix = null;
        try {
            bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        // Chuyển ma trận bit thành Bitmap
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        return bitmap;
    }

    private void animateBackgroundColor(View view, int fromColor, int toColor) {
        ValueAnimator colorAnimation = ValueAnimator.ofArgb(fromColor, toColor);
        colorAnimation.setDuration(0); // Thời gian chuyển đổi màu (300ms)
        colorAnimation.addUpdateListener(animator -> {
            view.setBackgroundColor((int) animator.getAnimatedValue());
        });
        colorAnimation.start();
    }

    public void changeColorButtonActive(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(EventDetailActivity.this, R.color.defaultBlue));
        btn.setTextColor(ContextCompat.getColorStateList(EventDetailActivity.this, R.color.white));
    }

    public void changeColorButtonNormal(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(EventDetailActivity.this, R.color.buttonDefault));
        btn.setTextColor(ContextCompat.getColorStateList(EventDetailActivity.this, R.color.black));
    }

    private void loadListAssist(Event event, RecyclerView recyclerViewA){
        EventAPI eventAPI = new EventAPI();
        eventAPI.getEventById(event.getEventId(), event.getAdminEventId(), new EventAPI.EventCallback() {
            @Override
            public void onEventReceived(Event event) {
                List<Assist> assists = new ArrayList<>();
                if (event.getUserAssist() != null) {
                    for (Assist a : event.getUserAssist()) {
                        if (a.isAssist()) {
                            assists.add(a);
                        }
                    }

                    AssistAdapter assistAdapter = new AssistAdapter(assists, EventDetailActivity.this);
                    recyclerViewA.setAdapter(assistAdapter);
                    recyclerViewA.setLayoutManager(new LinearLayoutManager(EventDetailActivity.this, LinearLayoutManager.VERTICAL, false));
                }
            }

            @Override
            public void onEventsReceived(List<Event> events) {

            }
        });
    }

    private void loadListApply(Event event, RecyclerView recyclerViewA){
        List<Assist> assists = new ArrayList<>();

        EventAPI eventAPI = new EventAPI();
        eventAPI.listenForNewUserAssist(event, new EventAPI.OnNewUserAddedListener() {
            @Override
            public void onNewUserAdded(Assist assist) {
                if (!assist.isAssist()) {
                    assists.add(assist);
                }

                AssistAdapter assistAdapter = new AssistAdapter(assists, EventDetailActivity.this);
                recyclerViewA.setAdapter(assistAdapter);
                recyclerViewA.setLayoutManager(new LinearLayoutManager(EventDetailActivity.this, LinearLayoutManager.VERTICAL, false));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Kiểm tra mã yêu cầu để xác định kết quả quét QR
        if (requestCode == QR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Lấy dữ liệu quét được từ Intent trả về
                String scannedData = data.getStringExtra("SCAN_RESULT");

                // Xử lý dữ liệu quét được (ví dụ: lưu vào cơ sở dữ liệu hoặc thực hiện kiểm tra)
                if (scannedData != null && !scannedData.isEmpty()) {
                    // Ví dụ, gọi API để xác nhận QR hoặc xử lý thêm
                    handleScannedData(scannedData);
                }
            } else {
                Toast.makeText(this, "Quét QR thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleScannedData(String scannedData) {
        Intent intent = getIntent();
        int eventId = intent.getIntExtra("eventId", -1);
        int adminId = intent.getIntExtra("adminId", -1);

        EventAPI eventAPI = new EventAPI();
        eventAPI.getEventById(eventId, adminId, new EventAPI.EventCallback() {
            @Override
            public void onEventReceived(Event event) {
                StudentAPI studentAPI = new StudentAPI();
                studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                    @Override
                    public void onStudentReceived(Student student) {
                        List<RollCall> rollCalls = event.getUserJoin();
                        if (rollCalls == null) {
                            rollCalls = new ArrayList<>();
                        }

                        if (event.getCurrentQrCode().equals(scannedData)) {
                            for (RollCall r : rollCalls) {
                                if (r.getStudentNumber().equals(student.getStudentNumber())) {
                                    Toast.makeText(EventDetailActivity.this, "Bạn đã điểm danh rồi.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            RollCall rollCall = new RollCall();
                            rollCall.setCodeRollCall(scannedData);
                            rollCall.setStudentNumber(student.getStudentNumber());

                            rollCalls.add(rollCall);

                            event.setCurrentQrCode(UUID.randomUUID().toString().substring(0, 8));
                            event.setUserJoin(rollCalls);

                            eventAPI.updateEvent(event);

                            Toast.makeText(EventDetailActivity.this, "Điểm danh thành công", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(EventDetailActivity.this, "Mã QR không hợp lệ hoặc đã được sử dụng", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onStudentsReceived(List<Student> students) {

                    }
                });
            }

            @Override
            public void onEventsReceived(List<Event> events) {

            }
        });
    }

    private void showExitPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Sự kiện đã kết thúc. Bạn có muốn rời đi không?");

        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Thực hiện hành động khi người dùng chọn "Có" (rời đi)
                finish(); // Đóng Activity hiện tại (hoặc thực hiện hành động rời đi khác)
            }
        });

        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Đóng hộp thoại khi người dùng chọn "Không"
                dialog.dismiss();
            }
        });

        // Hiển thị popup
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
