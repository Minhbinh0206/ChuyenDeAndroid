package com.example.socialmediatdcproject.fragment.Student;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.EventAPI;
import com.example.socialmediatdcproject.API.FriendAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R; // Import đúng package chứa R
import com.example.socialmediatdcproject.adapter.EventPersonalAdapter;
import com.example.socialmediatdcproject.adapter.FriendPersonalAdapter;
import com.example.socialmediatdcproject.adapter.GroupAdapter;
import com.example.socialmediatdcproject.dataModels.Friends;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.model.Assist;
import com.example.socialmediatdcproject.model.Event;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.RollCall;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ListEventAndGroupFragment extends Fragment {
    FrameLayout frameLayout;
    FriendPersonalAdapter friendPersonalAdapter;
    RecyclerView recyclerView2;
    RecyclerView recyclerView;
    int myId = -1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for this fragment
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_activity_button, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button listEvent = view.findViewById(R.id.button_personal_event_friends);
        Button listGroup = view.findViewById(R.id.button_personal_group_invitation);

        recyclerView2 = view.findViewById(R.id.activity_recycle);

        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        recyclerView.setVisibility(View.GONE);

        TextView detail = view.findViewById(R.id.detail_information);

        TextView textView = requireActivity().findViewById(R.id.null_content_notify);

        updateButtonColorsActive(listEvent);
        updateButtonColorsNormal(listGroup);

        loadEventUsedToJoin(textView, recyclerView2);

        listEvent.setOnClickListener(v -> {
            recyclerView2.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            detail.setVisibility(View.VISIBLE);

            loadEventUsedToJoin(textView, recyclerView2);

            updateButtonColorsActive(listEvent);
            updateButtonColorsNormal(listGroup);
        });

        detail.setOnClickListener(m -> showCustomPopup());

        listGroup.setOnClickListener(v -> {
            recyclerView2.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            detail.setVisibility(View.GONE);
            loadGroupFromFirebase();

            updateButtonColorsActive(listGroup);
            updateButtonColorsNormal(listEvent);
        });
    }

    //Set sự kiện màu cho các nút
    private void updateButtonColorsActive(Button activeButton){
        // Cập nhật nút đang hoạt động
        activeButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        activeButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
    }

    //Set sự kiện màu cho các nút
    private void updateButtonColorsNormal(Button button){
        // Cập nhật nút đang hoạt động
        button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        button.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
    }

    private void loadEventUsedToJoin(TextView textView, RecyclerView r){
        EventAPI eventAPI = new EventAPI();
        StudentAPI studentAPI = new StudentAPI();
        ArrayList<Event> eventList = new ArrayList<>();
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                eventAPI.getAllEvents(new EventAPI.EventCallback() {
                    @Override
                    public void onEventReceived(Event event) {

                    }

                    @Override
                    public void onEventsReceived(List<Event> events) {
                        for (Event e : events) {
                            if (e.getUserJoin() != null) {
                                for (RollCall r : e.getUserJoin()) {
                                    if (r.getStudentNumber().equals(student.getStudentNumber()) && r.getIsVerify() == 2) {
                                        eventList.add(e);
                                    }
                                }
                            }
                            if (e.getUserAssist() != null) {
                                for (Assist a : e.getUserAssist()) {
                                    if (a.getUserId() == student.getUserId() && a.isAssist()) {
                                        eventList.add(e);
                                    }
                                }
                            }
                        }

                        if (eventList.isEmpty()) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText("Bạn chưa tham gia sự kiện nào");
                        } else {
                            textView.setVisibility(View.GONE);
                        }

                        EventPersonalAdapter eventPersonalAdapter = new EventPersonalAdapter(eventList, requireContext());
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
                        r.setLayoutManager(linearLayoutManager);
                        r.setAdapter(eventPersonalAdapter);
                        eventPersonalAdapter.notifyDataSetChanged();

                    }

                });
            }

            @Override
            public void onStudentsReceived(List<Student> students) {

            }
        });
    }

    public void loadGroupFromFirebase() {
        ArrayList<Group> groupList = new ArrayList<>();
        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(key, new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                GroupAPI groupAPI = new GroupAPI();
                GroupUserAPI groupUserAPI = new GroupUserAPI();
                groupUserAPI.getGroupUserByIdUser(student.getUserId(), new GroupUserAPI.GroupUserCallback() {
                    @Override
                    public void onGroupUsersReceived(List<GroupUser> groupUsers) {
                        // Sử dụng CountDownLatch để đợi cho tất cả các nhóm được tải xong
                        CountDownLatch latch = new CountDownLatch(groupUsers.size());
                        for (GroupUser gu : groupUsers) {
                            groupAPI.getGroupById(gu.getGroupId(), new GroupAPI.GroupCallback() {
                                @Override
                                public void onGroupReceived(Group group) {
                                    groupList.add(group);
                                    latch.countDown(); // Giảm số đếm khi tải xong một nhóm
                                }

                                @Override
                                public void onGroupsReceived(List<Group> groups) {
                                    // Không sử dụng trong trường hợp này
                                }
                            });
                        }

                        // Đợi cho đến khi tất cả các nhóm được thêm vào danh sách
                        new Thread(() -> {
                            try {
                                latch.await(); // Đợi cho đến khi countDownLatch đếm đến 0
                                requireActivity().runOnUiThread(() -> {
                                    GroupAdapter groupAdapter = new GroupAdapter(groupList, requireContext());
                                    recyclerView.setAdapter(groupAdapter);
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
                                    recyclerView.setLayoutManager(linearLayoutManager);
                                    groupAdapter.notifyDataSetChanged();
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                });
            }

            @Override
            public void onStudentsReceived(List<Student> students) {
                // Không sử dụng trong trường hợp này
            }
        });
    }

    private void showCustomPopup() {
        // Inflate layout custom cho popup
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View popupView = inflater.inflate(R.layout.popup_detail_event, null);

        // Tạo AlertDialog
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setView(popupView);

        // Lấy các view trong layout popup
        RecyclerView popupRecycle = popupView.findViewById(R.id.event_item_detail);
        Spinner spinner = popupView.findViewById(R.id.filter_join);
        ImageButton filterIcon = popupView.findViewById(R.id.filter_icon);
        Button closeButton = popupView.findViewById(R.id.popup_button_close);
        TextView textView = popupView.findViewById(R.id.null_event);

        // Tạo adapter cho spinner
        String[] filterOptions = {"Hỗ trợ", "Tham gia"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                filterOptions
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        filterIcon.setOnClickListener(v -> {
            spinner.performClick();
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lấy nội dung của mục được chọn
                String selectedItem = parent.getItemAtPosition(position).toString();

                // Thực hiện hành động dựa trên mục được chọn
                switch (selectedItem) {
                    case "Hỗ trợ":
                        // Gọi hàm xử lý cho mục "Người hỗ trợ"
                        loadEventAssistantPopup(popupRecycle);
                        break;
                    case "Tham gia":
                        // Gọi hàm xử lý cho mục "Tham gia"
                        loadEventJoinPopup(popupRecycle);
                        break;
                    default:
                        loadEventUsedToJoin(textView, popupRecycle);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý trường hợp không có gì được chọn
            }
        });

        // Tạo dialog và hiển thị
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Xử lý sự kiện nút đóng
        closeButton.setOnClickListener(v -> alertDialog.dismiss());
    }

    private void loadEventJoinPopup(RecyclerView recyclerViewPopup){
        EventAPI eventAPI = new EventAPI();
        StudentAPI studentAPI = new StudentAPI();
        ArrayList<Event> eventList = new ArrayList<>();
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                eventAPI.getAllEvents(new EventAPI.EventCallback() {
                    @Override
                    public void onEventReceived(Event event) {

                    }

                    @Override
                    public void onEventsReceived(List<Event> events) {
                        for (Event e : events) {
                            if (e.getUserJoin() != null) {
                                for (RollCall r : e.getUserJoin()) {
                                    if (r.getStudentNumber().equals(student.getStudentNumber()) && r.getIsVerify() == 2) {
                                        eventList.add(e);
                                    }
                                }
                            }
                        }

                        EventPersonalAdapter eventPersonalAdapter = new EventPersonalAdapter(eventList, requireContext());
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
                        recyclerViewPopup.setLayoutManager(linearLayoutManager);
                        recyclerViewPopup.setAdapter(eventPersonalAdapter);
                        eventPersonalAdapter.notifyDataSetChanged();

                    }

                });
            }

            @Override
            public void onStudentsReceived(List<Student> students) {

            }
        });
    }

    private void loadEventAssistantPopup(RecyclerView recyclerViewPopup){
        EventAPI eventAPI = new EventAPI();
        StudentAPI studentAPI = new StudentAPI();
        ArrayList<Event> eventList = new ArrayList<>();
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                eventAPI.getAllEvents(new EventAPI.EventCallback() {
                    @Override
                    public void onEventReceived(Event event) {

                    }

                    @Override
                    public void onEventsReceived(List<Event> events) {
                        for (Event e : events) {
                            if (e.getUserAssist() != null) {
                                for (Assist a : e.getUserAssist()) {
                                    if (a.getUserId() == student.getUserId() && a.isAssist()) {
                                        eventList.add(e);
                                    }
                                }
                            }
                        }

                        EventPersonalAdapter eventPersonalAdapter = new EventPersonalAdapter(eventList, requireContext());
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
                        recyclerViewPopup.setLayoutManager(linearLayoutManager);
                        recyclerViewPopup.setAdapter(eventPersonalAdapter);
                        eventPersonalAdapter.notifyDataSetChanged();

                    }

                });
            }

            @Override
            public void onStudentsReceived(List<Student> students) {

            }
        });
    }

}
