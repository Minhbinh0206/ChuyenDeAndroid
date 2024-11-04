package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.BusinessAPI;
import com.example.socialmediatdcproject.API.ClassAPI;
import com.example.socialmediatdcproject.API.CollabAPI;
import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.FilterNotifyAPI;
import com.example.socialmediatdcproject.API.FilterPostsAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.NotifyAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.ItemFilterAdapter;
import com.example.socialmediatdcproject.dataModels.Collab;
import com.example.socialmediatdcproject.dataModels.FilterNotify;
import com.example.socialmediatdcproject.dataModels.FilterPost;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Business;
import com.example.socialmediatdcproject.model.Class;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Notify;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SendNotificationActivity extends AppCompatActivity {
    private List<String> optionsOfAdminDepartment = new ArrayList<>();
    RecyclerView recyclerViewFilterNotice;
    ItemFilterAdapter itemFilterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.admin_send_notify_layout);

        // Tìm các phần tử
        ImageButton iconBack = findViewById(R.id.icon_back);
        ImageView avatar = findViewById(R.id.imageAvatar);
        TextView nameUserSend = findViewById(R.id.name_user_send_notice);
        EditText titleNotice = findViewById(R.id.title_notice);
        EditText contentNotice = findViewById(R.id.content_notice);
        TextView createAt = findViewById(R.id.create_at_notice);
        Spinner firstChoice = findViewById(R.id.spinner_choice);
        recyclerViewFilterNotice = findViewById(R.id.recyclerView);
        Button buttonSubmit = findViewById(R.id.button_submit_send);

        iconBack.setOnClickListener(v -> finish());

        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        adminDepartmentAPI.getAdminDepartmentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                Glide.with(SendNotificationActivity.this)
                        .load(adminDepartment.getAvatar())
                        .circleCrop()
                        .into(avatar);

                nameUserSend.setText(adminDepartment.getFullName());
            }

            @Override
            public void onUsersReceived(List<AdminDepartment> adminDepartment) {

            }

            @Override
            public void onError(String s) {

            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("'Ngày' dd 'Tháng' MM 'Năm' yyyy");
        createAt.setText(sdf.format(new Date()));

        changeColorButtonActive(buttonSubmit);

        spinnerAdmin(firstChoice);

        recyclerViewFilterNotice.setVisibility(View.GONE);
        List<Integer> receivePostUser = new ArrayList<>();

        final boolean[] isFilterNotify = {false};
        // Gán sự kiện cho Spinner khi người dùng chọn item
        firstChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = optionsOfAdminDepartment.get(position);
                if (selected.equals(optionsOfAdminDepartment.get(0))) {
                    recyclerViewFilterNotice.setVisibility(View.GONE);
                    isFilterNotify[0] = false;

                } else if (selected.equals(optionsOfAdminDepartment.get(1))) {
                    recyclerViewFilterNotice.setVisibility(View.VISIBLE);
                    loadBusinessFilter();
                    isFilterNotify[0] = true;


                } else if (selected.equals(optionsOfAdminDepartment.get(2))) {
                    recyclerViewFilterNotice.setVisibility(View.VISIBLE);
                    loadClassFilter();
                    isFilterNotify[0] = true;

                } else {
                    loadStudentInDepartmentFilter();
                    recyclerViewFilterNotice.setVisibility(View.VISIBLE);
                    isFilterNotify[0] = true;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        buttonSubmit.setOnClickListener(v -> {
            // Vô hiệu hóa nút để tránh nhấn nhiều lần
            buttonSubmit.setEnabled(false);

            String title = titleNotice.getText().toString();
            String content = contentNotice.getText().toString();

            if (isFilterNotify[0]) {
                // Xử lý các bộ lọc như đã đề cập ở trên
                receivePostUser.clear(); // Đảm bảo danh sách rỗng trước khi thêm

                List<String> selectedFilter = itemFilterAdapter.getSelectedFilters();
                if (selectedFilter.get(0).substring(0, 5).equals("Doanh")) {
                    // Xử lý các phần tử là doanh nghiệp
                    for (String s : selectedFilter) {
                        BusinessAPI businessAPI = new BusinessAPI();
                        businessAPI.getAllBusinesses(new BusinessAPI.BusinessCallback() {
                            @Override
                            public void onBusinessReceived(Business business) {

                            }

                            @Override
                            public void onBusinessesReceived(List<Business> businesses) {
                                for (Business b : businesses) {
                                    if (s.equals(b.getBussinessName())) {
                                        receivePostUser.add(b.getBusinessAdminId());
                                    }
                                }
                            }
                        });
                    }
                    processCreatePostAdminDepartment(title, content, isFilterNotify[0], receivePostUser);

                    finish();
                } else if (selectedFilter.get(0).substring(0, 2).equals("CD")) {
                    for (String s : selectedFilter) {
                        ClassAPI classAPI = new ClassAPI();
                        classAPI.getAllClasses(new ClassAPI.ClassCallback() {
                            @Override
                            public void onClassReceived(Class classItem) {
                                // Không cần xử lý ở đây
                            }

                            @Override
                            public void onClassesReceived(List<Class> classList) {
                                for (Class c : classList) {
                                    Log.d("Class", "Class Name: " + c.getClassName());
                                    if (s.equals(c.getClassName())) {
                                        StudentAPI studentAPI = new StudentAPI();
                                        studentAPI.getAllStudents(new StudentAPI.StudentCallback() {
                                            @Override
                                            public void onStudentReceived(Student student) {
                                                // Không cần xử lý ở đây
                                            }

                                            @Override
                                            public void onStudentsReceived(List<Student> students) {
                                                for (Student student : students) {
                                                    if (student.getClassId() == c.getId()) {
                                                        receivePostUser.add(student.getUserId());
                                                    }
                                                }

                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                    processCreatePostAdminDepartment(title, content, isFilterNotify[0], receivePostUser);

                    finish();
                } else {
                    // Xử lý trường hợp học sinh cụ thể
                    for (String s : selectedFilter) {
                        StudentAPI studentAPI = new StudentAPI();
                        studentAPI.getStudentByStudentNumber(s, new StudentAPI.StudentCallback() {
                            @Override
                            public void onStudentReceived(Student student) {
                                receivePostUser.add(student.getUserId());
                            }

                            @Override
                            public void onStudentsReceived(List<Student> students) {

                            }
                        });
                    }
                    processCreatePostAdminDepartment(title, content, isFilterNotify[0], receivePostUser);

                    finish();
                }

            } else {
                // Nếu không dùng bộ lọc, chỉ gọi processCreatePost một lần
                processCreatePostAdminDepartment(title, content, isFilterNotify[0], receivePostUser);
                finish();
            }
        });
    }

    public void spinnerAdmin(Spinner spinner){
        optionsOfAdminDepartment.clear();

        // Các tùy chọn cho Spinner
        optionsOfAdminDepartment.add("Toàn bộ học sinh");
        optionsOfAdminDepartment.add("Doanh nghiệp liên kết với khoa");
        optionsOfAdminDepartment.add("Học sinh thuộc lớp");
        optionsOfAdminDepartment.add("Các cá nhân cụ thể");

        // Set adapter cho Spinner
        ArrayAdapter<String> filterMainAdapter = new ArrayAdapter<>(SendNotificationActivity.this, android.R.layout.simple_spinner_item, optionsOfAdminDepartment);
        filterMainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(filterMainAdapter);
    }

    public void changeColorButtonActive(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(SendNotificationActivity.this, R.color.defaultBlue));
        btn.setTextColor(ContextCompat.getColorStateList(SendNotificationActivity.this, R.color.white));
    }

    private void loadBusinessFilter() {
        CollabAPI collabAPI = new CollabAPI();
        BusinessAPI businessAPI = new BusinessAPI();
        ArrayList<String> filterList = new ArrayList<>();
        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        adminDepartmentAPI.getAdminDepartmentByKey(key, new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                collabAPI.getCollabsByDepartmentId(adminDepartment.getDepartmentId(), new CollabAPI.CollabCallback() {
                    @Override
                    public void onCollabReceived(List<Collab> collabList) {
                        // Nếu collabList rỗng, cập nhật RecyclerView ngay lập tức
                        if (collabList.isEmpty()) {
                            updateRecyclerView(filterList);
                            return;
                        }

                        int totalCollabs = collabList.size();
                        final int[] completedCount = {0}; // Bộ đếm hoàn thành

                        for (Collab c : collabList) {
                            businessAPI.getBusinessById(c.getBusinessId(), new BusinessAPI.BusinessCallback() {
                                @Override
                                public void onBusinessReceived(Business business) {
                                    filterList.add(business.getBussinessName());
                                    Log.d("NAM", "onBusinessReceived: " + business.getBussinessName());

                                    // Kiểm tra khi đã hoàn thành tất cả các yêu cầu business
                                    completedCount[0]++;
                                    if (completedCount[0] == totalCollabs) {
                                        updateRecyclerView(filterList); // Cập nhật RecyclerView khi đã đủ dữ liệu
                                    }
                                }

                                @Override
                                public void onBusinessesReceived(List<Business> businesses) { }
                            });
                        }
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
    }

    private void processAdditional(int id, List<Integer> users){
        FilterNotifyAPI filterNotifyAPI = new FilterNotifyAPI();
        FilterNotify filterNotify = new FilterNotify();
        filterNotify.setNotifyId(id);
        filterNotify.setListUserGet(users);
        filterNotifyAPI.addReceiveNotify(filterNotify);
    }

    private void loadClassFilter() {
        ClassAPI classAPI = new ClassAPI();
        ArrayList<String> filterList = new ArrayList<>();
        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        adminDepartmentAPI.getAdminDepartmentByKey(key, new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                classAPI.getClassesByDepartmentId(adminDepartment.getDepartmentId(), new ClassAPI.ClassCallback() {
                    @Override
                    public void onClassReceived(com.example.socialmediatdcproject.model.Class classItem) {

                    }

                    @Override
                    public void onClassesReceived(List<com.example.socialmediatdcproject.model.Class> classList) {
                        for (Class c : classList) {
                            filterList.add(c.getClassName());
                        }
                        updateRecyclerView(filterList);
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
    }

    private void loadStudentInDepartmentFilter() {
        ArrayList<String> filterList = new ArrayList<>();
        StudentAPI studentAPI = new StudentAPI();
        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        adminDepartmentAPI.getAdminDepartmentByKey(key, new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                studentAPI.getAllStudents(new StudentAPI.StudentCallback() {
                    @Override
                    public void onStudentReceived(Student student) {

                    }

                    @Override
                    public void onStudentsReceived(List<Student> students) {
                        for (Student student: students) {
                            if (student.getDepartmentId() == adminDepartment.getDepartmentId()) {
                                filterList.add(student.getStudentNumber());

                            }
                        }
                        updateRecyclerView(filterList);

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
    }

    private void updateRecyclerView(ArrayList<String> filterList) {
        if (recyclerViewFilterNotice == null) {
            recyclerViewFilterNotice = findViewById(R.id.recyclerView);
        }

        if (recyclerViewFilterNotice != null) {
            itemFilterAdapter = new ItemFilterAdapter(filterList, SendNotificationActivity.this);
            itemFilterAdapter.notifyDataSetChanged();
            recyclerViewFilterNotice.setAdapter(itemFilterAdapter);
            recyclerViewFilterNotice.setLayoutManager(new GridLayoutManager(SendNotificationActivity.this, 3));
        } else {
            Log.e("MainFeatureFragment", "RecyclerView is null, cannot update RecyclerView.");
        }
    }

    private void processCreatePostAdminDepartment(String title, String content, boolean isFilter, List<Integer> notifyReceive) {
        NotifyAPI notifyAPI = new NotifyAPI();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        // Cờ để kiểm tra chỉ gọi addPost một lần
        final boolean[] isPostAdded = {false};

        adminDepartmentAPI.getAdminDepartmentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                notifyAPI.getAllNotifications(new NotifyAPI.NotificationCallback() {
                    @Override
                    public void onNotificationReceived(Notify notify) {

                    }

                    @Override
                    public void onNotificationsReceived(List<Notify> notifications) {
                        if (!isPostAdded[0]) {
                            isPostAdded[0] = true; // Đánh dấu là đã thêm bài viết
                            Notify notify = new Notify();
                            notify.setNotifyId(notifications.size());
                            notify.setUserSendId(adminDepartment.getUserId());
                            notify.setNotifyTitle(title);
                            notify.setNotifyContent(content);
                            notify.setFilter(isFilter);
                            notify.setIsRead(0);
                            notify.setCreateAt(sdf.format(new Date()));

                            if (notifyReceive.size() != 0) {
                                processAdditional(notify.getNotifyId(), notifyReceive);
                            }

                            notifyAPI.addNotification(notify);
                            Toast.makeText(SendNotificationActivity.this, "Gửi thông báo thành công", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {

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
    }
}
