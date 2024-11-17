package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.EventAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.fragment.Admin.RollCallAdminFragment;
import com.example.socialmediatdcproject.fragment.Student.RollCallAssistFragment;
import com.example.socialmediatdcproject.fragment.Student.RollCallStudentFragment;
import com.example.socialmediatdcproject.model.Assist;
import com.example.socialmediatdcproject.model.Event;
import com.example.socialmediatdcproject.model.RollCall;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class EventDetailActivity extends AppCompatActivity {
    private TextView content, title, textStatus, textHidden;
    private ImageView imageEvent;
    private Button buttonJoin, buttonVolunteer;
    private FrameLayout rollcall;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.detail_event_layout);

        // Initialize UI components
        initializeUIComponents();

        // Get event ID and type of joining from the intent
        Intent intent = getIntent();
        int eventId = intent.getIntExtra("eventId", -1);
        int typeJoin = intent.getIntExtra("typeJoin", -1);

        // Set back button listener
        findViewById(R.id.icon_back_event).setOnClickListener(v -> finish());

        // Set button colors
        changeColorButtonActive(buttonVolunteer);
        changeColorButtonActive(buttonJoin);

        // Fetch event details
        fetchEventDetails(eventId, typeJoin);

        // Set up handler to periodically check event status
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                fetchEventDetails(eventId, typeJoin);
                handler.postDelayed(this, 5000); // 5 seconds
            }
        };
        handler.post(runnable); // Start the periodic check

        EventAPI eventAPI = new EventAPI();
        StudentAPI studentAPI = new StudentAPI();
        eventAPI.getEventById(eventId, new EventAPI.EventCallback() {
            @Override
            public void onEventReceived(Event event) {
                handleJoinType(typeJoin, event);

                studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                    @Override
                    public void onStudentReceived(Student student) {
                        eventAPI.listenForEventStatusChange(eventId, new EventAPI.EventStatusCallback() {
                            @Override
                            public void onEventStatusChanged(Event event) {
                                if (event.getStatus() == 1) {
                                    eventAPI.listenForUserAssistStatusChange(eventId, student.getUserId(), new EventAPI.UserAssistStatusCallback() {
                                        @Override
                                        public void onUserAssistStatusChanged(Assist assist) {
                                            if (assist.isAssist()) {
                                                setupVolunteerView(event);
                                            }
                                        }
                                    });

                                    eventAPI.listenForNewRollCall(eventId, new EventAPI.NewRollCallCallback() {
                                        @Override
                                        public void onNewRollCallAdded(RollCall rollCall) {
                                            if (rollCall.getStudentNumber().equals(student.getStudentNumber())) {
                                                setupJoinedView(event);
                                            }
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
            }

            @Override
            public void onEventsReceived(List<Event> events) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Stop the periodic check when the activity is destroyed
    }

    private void fetchEventDetails(int eventId, int typeJoin) {
        EventAPI eventAPI = new EventAPI();
        eventAPI.getEventById(eventId, new EventAPI.EventCallback() {
            @Override
            public void onEventReceived(Event event) {
                if (event != null) {
                    updateUIWithEventData(event);
                }
            }

            @Override
            public void onEventsReceived(List<Event> events) {
                // Handle if necessary
            }
        });
    }

    private void initializeUIComponents() {
        textStatus = findViewById(R.id.text_status);
        imageEvent = findViewById(R.id.detail_image_event);
        buttonVolunteer = findViewById(R.id.button_volunteer);
        buttonJoin = findViewById(R.id.button_join);
        title = findViewById(R.id.detail_title_event);
        textHidden = findViewById(R.id.text_hidden);
        content = findViewById(R.id.detail_content_event);
        rollcall = findViewById(R.id.roll_call_content);
    }

    private void updateUIWithEventData(Event event) {
        Glide.with(this)
                .load(event.getImageEvent())
                .centerCrop()
                .into(imageEvent);
        title.setText(event.getTitleEvent());
        content.setText(event.getContentEvent());

        EventAPI eventAPI = new EventAPI();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        String currentTimeString = sdf.format(new Date());

        try {
            Date currentTime = sdf.parse(currentTimeString);
            Date beginAt = sdf.parse(event.getBeginAt());
            Date finishAt = sdf.parse(event.getFinishAt());

            if (currentTime.before(beginAt)) {
                setEventStatus("Sắp diễn ra", 0, R.color.defaultBlue, event);
            } else if (currentTime.before(finishAt)) {
                setEventStatus("Đang diễn ra", 1, R.color.warning, event);
            } else {
                setEventStatus("Đã kết thúc", 2, R.color.danger, event);
                showEventEndedPopup(); // Hiện popup khi sự kiện đã kết thúc
            }
            eventAPI.updateEvent(event);
        } catch (ParseException e) {
            e.printStackTrace();
            textStatus.setText("Lỗi định dạng ngày");
        }
    }

    private void setEventStatus(String statusText, int status, int colorResId, Event event) {
        textStatus.setText(statusText);
        textStatus.setBackgroundColor(ContextCompat.getColor(this, colorResId));

        EventAPI eventAPI = new EventAPI();
        eventAPI.getEventById(event.getEventId(), new EventAPI.EventCallback() {
            @Override
            public void onEventReceived(Event event) {
                event.setStatus(status);
                eventAPI.updateEvent(event);
            }

            @Override
            public void onEventsReceived(List<Event> events) {

            }
        });
    }

    private void handleJoinType(int typeJoin, Event event) {
        switch (typeJoin) {
            case 0:
                setupJoinedView(event);
                break;
            case 1:
                setupVolunteerView(event);
                break;
            case 2:
                setupPendingApprovalView();
                break;
            case 3:
                setupAdminView(event);
                break;
            default:
                setupJoinButtons(event);
                break;
        }
    }

    private void setupJoinedView(Event event) {
        textHidden.setText("Đã tham gia");
        hideJoinButtons();
        rollcall.setVisibility(View.VISIBLE);
        if (event.getStatus() == 1) {
            loadFragment(new RollCallStudentFragment());
        }
    }

    private void setupVolunteerView(Event event) {
        textHidden.setText("Người hỗ trợ");
        hideJoinButtons();
        rollcall.setVisibility(View.VISIBLE);
        if (event.getStatus() == 1) {
            loadFragment(new RollCallAssistFragment());
        }
    }

    private void setupPendingApprovalView() {
        textHidden.setText("Đang chờ duyệt");
        hideJoinButtons();
        rollcall.setVisibility(View.GONE);
    }

    private void setupAdminView(Event event) {
        textHidden.setText("Admin sự kiện");
        hideJoinButtons();
        rollcall.setVisibility(View.VISIBLE);
        if (event.getStatus() == 1) {
            loadFragment(new RollCallAdminFragment());
        }
    }

    private void setupJoinButtons(Event event) {
        buttonJoin.setOnClickListener(v -> handleJoin(event));
        buttonVolunteer.setOnClickListener(v -> handleVolunteer(event));
    }

    private void hideJoinButtons() {
        buttonVolunteer.setVisibility(View.GONE);
        buttonJoin.setVisibility(View.GONE);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.roll_call_content, fragment);
        fragmentTransaction.commit();
    }

    private void handleJoin(Event event) {
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                RollCall rollCall = new RollCall();
                rollCall.setStudentNumber(student.getStudentNumber());
                rollCall.setCodeRollCall(generateRandomCode());
                rollCall.setIsVerify(RollCall.JOIN);

                List<RollCall> existingRollCalls = event.getUserJoin() != null ? event.getUserJoin() : new ArrayList<>();
                existingRollCalls.add(rollCall);
                event.setUserJoin(existingRollCalls);
                new EventAPI().updateEvent(event);

                textHidden.setText("Đã tham gia");
                hideJoinButtons();
                Toast.makeText(EventDetailActivity.this, "Đăng kí tham gia thành công!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStudentsReceived(List<Student> students) {
                // Handle if needed
            }
        });
    }

    private void handleVolunteer(Event event) {
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                Assist assist = new Assist();
                assist.setUserId(student.getUserId());
                assist.setAssist(false);

                List<Assist> existingAssists = event.getUserAssist() != null ? event.getUserAssist() : new ArrayList<>();
                existingAssists.add(assist);
                event.setUserAssist(existingAssists);
                new EventAPI().updateEvent(event);

                textHidden.setText("Đang chờ duyệt");
                hideJoinButtons();
                Toast.makeText(EventDetailActivity.this, "Đăng kí làm tình nguyện viên thành công!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStudentsReceived(List<Student> students) {
                // Handle if needed
            }
        });
    }

    private void changeColorButtonActive(Button btn) {
        btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.defaultBlue));
        btn.setTextColor(ContextCompat.getColorStateList(this, R.color.white));
    }

    private String generateRandomCode() {
        return UUID.randomUUID().toString().substring(0, 8); // Get first 8 characters
    }

    private void showEventEndedPopup() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage("Sự kiện này đã kết thúc, Nhấn OK để rời đi.")
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                    finish(); // Đóng Activity khi nhấn Ok
                })
                .setCancelable(false) // Không cho phép người dùng hủy popup bằng cách nhấn ra ngoài
                .show();
    }
}
