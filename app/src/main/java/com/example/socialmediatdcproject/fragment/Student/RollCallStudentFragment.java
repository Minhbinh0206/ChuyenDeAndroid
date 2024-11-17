package com.example.socialmediatdcproject.fragment.Student;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.socialmediatdcproject.API.EventAPI;
import com.example.socialmediatdcproject.API.RollCallAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.model.Event;
import com.example.socialmediatdcproject.model.RollCall;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class RollCallStudentFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.roll_call_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Intent intent = requireActivity().getIntent();
        int eventId = intent.getIntExtra("eventId", -1);

        TextView textRollCall = view.findViewById(R.id.text_roll_call);
        EditText editTextRollCall = view.findViewById(R.id.code_roll_call);
        Button submitRollCall = view.findViewById(R.id.submit_roll_call);

        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                EventAPI eventAPI = new EventAPI();
                eventAPI.getEventById(eventId, new EventAPI.EventCallback() {
                    @Override
                    public void onEventReceived(Event event) {

                        eventAPI.listenForUserJoinVerificationChange(event.getEventId(), student.getStudentNumber(), new EventAPI.RollCallCallback() {
                            @Override
                            public void onRollCallChanged(RollCall rollCall) {
                                if (rollCall.getIsVerify() != 0) {
                                    textRollCall.setText(rollCall.getCodeRollCall());
                                }

                                if (rollCall.getIsVerify() == 2) {
                                    editTextRollCall.setText(rollCall.getCodeRollCall());
                                    editTextRollCall.setEnabled(false);
                                    submitRollCall.setEnabled(false);
                                }
                            }
                        });
                        submitRollCall.setOnClickListener(v -> {
                            String rollCall = editTextRollCall.getText().toString().trim();

                            if (rollCall.isEmpty()) {

                                Toast.makeText(getContext(), "Vui lòng nhập mã điểm danh!", Toast.LENGTH_SHORT).show();

                                return;
                            }

                            // Tạo Dialog
                            Dialog loadingDialog = new Dialog(getContext());
                            loadingDialog.setContentView(R.layout.dialog_loading);
                            loadingDialog.setCancelable(false); // Không cho phép người dùng tắt dialog bằng cách bấm ngoài

                            // Thêm ProgressBar vào layout của Dialog (layout: dialog_loading.xml)
                            ProgressBar progressBar = loadingDialog.findViewById(R.id.progressBar);
                            TextView textView = loadingDialog.findViewById(R.id.textLoading);
                            textView.setText("Đang xác thực ...");

                            // Hiển thị Dialog
                            loadingDialog.show();

                            for (RollCall r: event.getUserJoin()) {
                                if (r.getStudentNumber().equals(student.getStudentNumber())) {
                                    if (rollCall.equals(r.getCodeRollCall())) {
                                        EventAPI eventAPI = new EventAPI();
                                        eventAPI.updateUserJoinVerificationDone(event.getEventId(), student.getStudentNumber());
                                        Toast.makeText(getContext(), "Điểm danh thành công", Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();
                                    }
                                    else {
                                        Toast.makeText(getContext(), "Mã điểm danh không chính xác", Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onEventsReceived(List<Event> events) {

                    }
                });
            }

            @Override
            public void onStudentsReceived(List<Student> students) {

            }
        });
    }
}
