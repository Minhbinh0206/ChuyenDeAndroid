package com.example.socialmediatdcproject.fragment.Student;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.BusinessAPI;
import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.FilterNotifyAPI;
import com.example.socialmediatdcproject.API.FilterPostsAPI;
import com.example.socialmediatdcproject.API.NotifyAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R; // Import đúng package chứa R
import com.example.socialmediatdcproject.adapter.NotifyAdapter;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Business;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Notify;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotifyFragment extends Fragment {
    FrameLayout frameLayout;
    RecyclerView recyclerView;
    NotifyAdapter notifyAdapter;
    ArrayList<Notify> notifyList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for this fragment
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_get_notify_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        frameLayout = requireActivity().findViewById(R.id.third_content_fragment);
        frameLayout.setVisibility(view.GONE);

        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        recyclerView.setVisibility(View.VISIBLE);
        notifyList = new ArrayList<>();
        // Khởi tạo NotifyAdapter với danh sách dữ liệu rỗng
        notifyAdapter = new NotifyAdapter(notifyList, requireContext());
        String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Gán NotifyAdapter cho RecyclerView
        recyclerView.setAdapter(notifyAdapter);

        ImageButton filterNotify = view.findViewById(R.id.icon_filter_notify);
        Spinner spinnerFilterNotify = view.findViewById(R.id.spinner_filter_notify);
        RadioGroup radioGroup = view.findViewById(R.id.radio_group);

        filterNotify.setOnClickListener(v -> {
            spinnerFilterNotify.performClick();
        });

        // Tạo adapter cho spinner
        List<String> filterOptionsDefault = new ArrayList<>();
        String s1 = "Toàn bộ thông báo";
        String s2 = "Trường";
        String s3 = "Khoa";
        String s4 = "Doanh nghiệp";
        filterOptionsDefault.add(s1);
        filterOptionsDefault.add(s2);
        filterOptionsDefault.add(s3);
        filterOptionsDefault.add(s4);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                filterOptionsDefault
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterNotify.setAdapter(spinnerAdapter);

        spinnerFilterNotify.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = filterOptionsDefault.get(position);

                if (selected.equals(filterOptionsDefault.get(0))) {
                    notifyList.clear();
                    notifyAdapter.notifyDataSetChanged();

                    loadAllNotify();
                } else if (selected.equals(filterOptionsDefault.get(1))) {
                    notifyList.clear();
                    notifyAdapter.notifyDataSetChanged();

                    loadNotifyByListIdAminDefault();
                } else if (selected.equals(filterOptionsDefault.get(2))) {
                    notifyList.clear();
                    notifyAdapter.notifyDataSetChanged();

                    loadNotifyByListIdAdminDepartment();
                } else if (selected.equals(filterOptionsDefault.get(3))) {
                    notifyList.clear();
                    notifyAdapter.notifyDataSetChanged();

                    loadNotifyByListIdAdminBusiness();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_default) {
                spinnerFilterNotify.setAdapter(spinnerAdapter);

                spinnerFilterNotify.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selected = filterOptionsDefault.get(position);

                        if (selected.equals(filterOptionsDefault.get(0))) {
                            notifyList.clear();
                            notifyAdapter.notifyDataSetChanged();

                            loadAllNotify();
                        } else if (selected.equals(filterOptionsDefault.get(1))) {
                            notifyList.clear();
                            notifyAdapter.notifyDataSetChanged();

                            loadNotifyByListIdAminDefault();
                        } else if (selected.equals(filterOptionsDefault.get(2))) {
                            notifyList.clear();
                            notifyAdapter.notifyDataSetChanged();

                            loadNotifyByListIdAdminDepartment();
                        } else if (selected.equals(filterOptionsDefault.get(3))) {
                            notifyList.clear();
                            notifyAdapter.notifyDataSetChanged();

                            loadNotifyByListIdAdminBusiness();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } else if (checkedId == R.id.radio_school) {
                // Tạo adapter cho spinner
                List<String> filterOptionsSchool = new ArrayList<>();
                String s00 = "Toàn bộ";
                String s01 = "Phòng đào tạo";
                String s02 = "Phòng CTCT - HSSV";
                String s03 = "Hội sinh viên";
                String s04 = "Đoàn thanh niên";
                filterOptionsSchool.add(s00);
                filterOptionsSchool.add(s01);
                filterOptionsSchool.add(s02);
                filterOptionsSchool.add(s04);
                filterOptionsSchool.add(s03);

                ArrayAdapter<String> spinnerAdapterSchool = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        filterOptionsSchool
                );

                spinnerAdapterSchool.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFilterNotify.setAdapter(spinnerAdapterSchool);

                spinnerFilterNotify.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selected = filterOptionsSchool.get(position);

                        if (selected.equals(filterOptionsSchool.get(0))) {
                            notifyList.clear();
                            notifyAdapter.notifyDataSetChanged();

                            loadNotifyByListIdAminDefault();
                        } else if (selected.equals(filterOptionsSchool.get(1))) {
                            notifyList.clear();
                            notifyAdapter.notifyDataSetChanged();

                            loadNotifyByOneIdAdmin(0);
                        } else if (selected.equals(filterOptionsSchool.get(2))) {
                            notifyList.clear();
                            notifyAdapter.notifyDataSetChanged();

                            loadNotifyByOneIdAdmin(1);
                        } else if (selected.equals(filterOptionsSchool.get(3))) {
                            notifyList.clear();
                            notifyAdapter.notifyDataSetChanged();

                            loadNotifyByOneIdAdmin(2);
                        } else if (selected.equals(filterOptionsSchool.get(4))) {
                            notifyList.clear();
                            notifyAdapter.notifyDataSetChanged();

                            loadNotifyByOneIdAdmin(3);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } else if (checkedId == R.id.radio_department) {
                List<String> filterOptionsDepartment = new ArrayList<>();
                String s01 = "Toàn bộ";
                filterOptionsDepartment.add(s01);

                DepartmentAPI departmentAPI = new DepartmentAPI();
                departmentAPI.getAllDepartments(new DepartmentAPI.DepartmentCallback() {
                    @Override
                    public void onDepartmentReceived(Department department) {
                        // Xử lý khi chỉ nhận được một department (nếu cần)
                    }

                    @Override
                    public void onDepartmentsReceived(List<Department> departments) {
                        for (Department d : departments) {
                            filterOptionsDepartment.add(d.getDepartmentName());
                        }

                        // Sau khi thêm xong, cập nhật adapter
                        ArrayAdapter<String> spinnerAdapterDepartment = new ArrayAdapter<>(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                filterOptionsDepartment
                        );

                        spinnerAdapterDepartment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerFilterNotify.setAdapter(spinnerAdapterDepartment);

                        spinnerFilterNotify.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String selected = filterOptionsDepartment.get(position);

                                if (selected.equals(filterOptionsDepartment.get(0))) {
                                    notifyList.clear();
                                    notifyAdapter.notifyDataSetChanged();

                                    loadNotifyByListIdAdminDepartment();
                                }
                                else {
                                    departmentAPI.getDepartmentByName(selected, new DepartmentAPI.DepartmentCallback() {
                                        @Override
                                        public void onDepartmentReceived(Department department) {
                                            AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
                                            adminDepartmentAPI.getAdminDepartmentByDepartmentId(department.getDepartmentId(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
                                                @Override
                                                public void onUserReceived(AdminDepartment adminDepartment) {
                                                    notifyList.clear();
                                                    notifyAdapter.notifyDataSetChanged();

                                                    loadNotifyByOneIdAdmin(adminDepartment.getUserId());
                                                }

                                                @Override
                                                public void onUsersReceived(List<AdminDepartment> adminDepartment) {

                                                }

                                                @Override
                                                public void onError(String s) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onDepartmentsReceived(List<Department> departments) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });
            } else if (checkedId == R.id.radio_business) {
                List<String> filterOptionsBusiness = new ArrayList<>();
                String s01 = "Toàn bộ";
                filterOptionsBusiness.add(s01);

                BusinessAPI businessAPI = new BusinessAPI();
                businessAPI.getAllBusinesses(new BusinessAPI.BusinessCallback() {
                    @Override
                    public void onBusinessReceived(Business business) {

                    }

                    @Override
                    public void onBusinessesReceived(List<Business> businesses) {
                        for (Business b : businesses) {
                            filterOptionsBusiness.add(b.getBusinessName());
                        }

                        ArrayAdapter<String> spinnerAdapterBusiness = new ArrayAdapter<>(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                filterOptionsBusiness
                        );

                        spinnerAdapterBusiness.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerFilterNotify.setAdapter(spinnerAdapterBusiness);

                        spinnerFilterNotify.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String selected = filterOptionsBusiness.get(position);

                                if (selected.equals(filterOptionsBusiness.get(0))) {
                                    notifyList.clear();
                                    notifyAdapter.notifyDataSetChanged();

                                    loadNotifyByListIdAdminBusiness();
                                }
                                else {
                                    businessAPI.getBusinessByName(selected, new BusinessAPI.BusinessCallback() {
                                        @Override
                                        public void onBusinessReceived(Business business) {
                                            AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
                                            adminBusinessAPI.getAdminBusinessByBusinessId(business.getBusinessId(), new AdminBusinessAPI.AdminBusinessCallBack() {
                                                @Override
                                                public void onUserReceived(AdminBusiness adminBusiness) {
                                                    notifyList.clear();
                                                    notifyAdapter.notifyDataSetChanged();

                                                    loadNotifyByOneIdAdmin(adminBusiness.getUserId());
                                                }

                                                @Override
                                                public void onUsersReceived(List<AdminBusiness> adminBusiness) {

                                                }

                                                @Override
                                                public void onError(String s) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onBusinessesReceived(List<Business> businesses) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });
            }
        });

    }

    private void handleNotifyAddition(Notify notify) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        try {
            Date date1 = format.parse(notify.getCreateAt());

            // Kiểm tra nếu date1 hợp lệ
            if (date1 == null) {
                return;
            }

            final boolean[] isAdded = {false};
            int insertPosition = -1; // Vị trí để thêm bài viết

            // Duyệt qua từng bài viết trong postList để so sánh với bài viết mới
            for (int i = 0; i < notifyList.size(); i++) {
                Notify n = notifyList.get(i);
                Date date2 = format.parse(n.getCreateAt());

                // Kiểm tra nếu date2 hợp lệ
                if (date2 == null) {
                    continue;
                }

                // So sánh ngày
                if (date1.after(date2)) {
                    // Nếu bài viết mới hơn bài viết hiện tại
                    insertPosition = i; // Ghi nhận vị trí cần chèn bài viết mới
                    break; // Dừng lại khi tìm được vị trí thích hợp
                }
            }

            // Nếu không tìm thấy vị trí thích hợp, bài viết mới nhất sẽ được thêm vào cuối danh sách
            if (insertPosition == -1) {
                insertPosition = notifyList.size(); // Thêm vào cuối danh sách
            }

            // Nếu bài viết không cần lọc
            if (!notify.isFilter()) {
                notifyList.add(insertPosition, notify); // Thêm bài viết vào vị trí thích hợp
                isAdded[0] = true;
            } else {
                // Nếu bài viết cần lọc
                StudentAPI studentAPI = new StudentAPI();
                studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                    @Override
                    public void onStudentReceived(Student student) {
                        FilterNotifyAPI filterNotifyAPI = new FilterNotifyAPI();
                        filterNotifyAPI.findUserInReceive(notify, student.getUserId(), new FilterNotifyAPI.UserInReceiveCallback() {
                            @Override
                            public void onResult(boolean isFound) {
                                if (isFound) {
                                    notifyList.add(0, notify); // Thêm bài viết vào đầu danh sách nếu được lọc
                                    notifyAdapter.notifyItemInserted(0); // Thông báo RecyclerView có phần tử mới ở đầu
                                    recyclerView.scrollToPosition(0); // Cuộn lên đầu để hiển thị bài viết mới

                                    isAdded[0] = true;
                                }
                            }
                        });
                    }

                    @Override
                    public void onStudentsReceived(List<Student> students) {
                    }
                });

                AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();
                adminDefaultAPI.getAdminDefaultByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDefaultAPI.AdminDefaultCallBack() {
                    @Override
                    public void onUserReceived(AdminDefault adminDefault) {
                        FilterNotifyAPI filterNotifyAPI = new FilterNotifyAPI();
                        filterNotifyAPI.findUserInReceive(notify, adminDefault.getUserId(), new FilterNotifyAPI.UserInReceiveCallback() {
                            @Override
                            public void onResult(boolean isFound) {
                                if (isFound) {
                                    notifyList.add(0, notify); // Thêm bài viết vào đầu danh sách nếu được lọc
                                    notifyAdapter.notifyItemInserted(0); // Thông báo RecyclerView có phần tử mới ở đầu
                                    recyclerView.scrollToPosition(0); // Cuộn lên đầu để hiển thị bài viết mới

                                    isAdded[0] = true;
                                }
                            }
                        });
                    }

                    @Override
                    public void onUsersReceived(List<AdminDefault> adminDefault) {

                    }
                });

                AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
                adminDepartmentAPI.getAdminDepartmentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
                    @Override
                    public void onUserReceived(AdminDepartment adminDepartment) {
                        FilterNotifyAPI filterNotifyAPI = new FilterNotifyAPI();
                        filterNotifyAPI.findUserInReceive(notify, adminDepartment.getUserId(), new FilterNotifyAPI.UserInReceiveCallback() {
                            @Override
                            public void onResult(boolean isFound) {
                                if (isFound) {
                                    notifyList.add(0, notify); // Thêm bài viết vào đầu danh sách nếu được lọc
                                    notifyAdapter.notifyItemInserted(0); // Thông báo RecyclerView có phần tử mới ở đầu
                                    recyclerView.scrollToPosition(0); // Cuộn lên đầu để hiển thị bài viết mới

                                    isAdded[0] = true;
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

                AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
                adminBusinessAPI.getAdminBusinessByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminBusinessAPI.AdminBusinessCallBack() {
                    @Override
                    public void onUserReceived(AdminBusiness adminBusiness) {
                        FilterNotifyAPI filterNotifyAPI = new FilterNotifyAPI();
                        filterNotifyAPI.findUserInReceive(notify, adminBusiness.getUserId(), new FilterNotifyAPI.UserInReceiveCallback() {
                            @Override
                            public void onResult(boolean isFound) {
                                if (isFound) {
                                    notifyList.add(0, notify); // Thêm bài viết vào đầu danh sách nếu được lọc
                                    notifyAdapter.notifyItemInserted(0); // Thông báo RecyclerView có phần tử mới ở đầu
                                    recyclerView.scrollToPosition(0); // Cuộn lên đầu để hiển thị bài viết mới

                                    isAdded[0] = true;
                                }
                            }
                        });
                    }

                    @Override
                    public void onUsersReceived(List<AdminBusiness> adminBusiness) {

                    }

                    @Override
                    public void onError(String s) {

                    }
                });
            }

            // Cập nhật RecyclerView nếu bài viết được thêm vào danh sách
            if (isAdded[0]) {
                notifyAdapter.notifyItemInserted(insertPosition); // Thông báo RecyclerView về sự thay đổi
                recyclerView.scrollToPosition(insertPosition); // Cuộn đến vị trí bài viết mới thêm
            }

        } catch (ParseException e) {
            Log.e("DateParse", "Error parsing date: " + e.getMessage());
        }
    }

    private void loadAllNotify(){
        notifyList.clear();
        notifyAdapter.notifyDataSetChanged();

        UserAPI userAPI = new UserAPI();
        userAPI.getAllUsers(new UserAPI.UserCallback() {
            @Override
            public void onUserReceived(User user) {

            }

            @Override
            public void onUsersReceived(List<User> users) {
                notifyList.clear();

                for (User u : users) {
                    if (u.getRoleId() == 2 || u.getRoleId() == 3 || u.getRoleId() == 4 || u.getRoleId() == 5) {
                        loadNotifyByOneIdAdmin(u.getUserId());
                    }
                }
            }
        });
    }

    private void loadNotifyByOneIdAdmin(int id){
        DatabaseReference notifiesReference = FirebaseDatabase.getInstance()
                .getReference("Notifies")
                .child(String.valueOf(id));

        // Lắng nghe sự kiện cho bài viết của nhóm
        notifiesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Notify notify = snapshot.getValue(Notify.class);
                Log.d("NotifyDebug", "Notify received: " + notify.getNotifyTitle());
                if (notify != null) {
                    handleNotifyAddition(notify);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Không cần thiết trong trường hợp này
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostAPI", "Error listening to post changes: " + error.getMessage());
            }
        });
    }

    private void loadNotifyByListIdAminDefault(){
        notifyList.clear();
        notifyAdapter.notifyDataSetChanged();

        UserAPI userAPI = new UserAPI();
        userAPI.getAllUsers(new UserAPI.UserCallback() {
            @Override
            public void onUserReceived(User user) {

            }

            @Override
            public void onUsersReceived(List<User> users) {
                for (User u : users) {
                    if (u.getRoleId() == 4 || u.getRoleId() == 5) {
                        loadNotifyByOneIdAdmin(u.getUserId());
                    }
                }
            }
        });
    }

    private void loadNotifyByListIdAdminBusiness(){
        notifyList.clear();
        notifyAdapter.notifyDataSetChanged();

        BusinessAPI businessAPI = new BusinessAPI();
        businessAPI.getAllBusinesses(new BusinessAPI.BusinessCallback() {
            @Override
            public void onBusinessReceived(Business business) {

            }

            @Override
            public void onBusinessesReceived(List<Business> businesses) {
                for (Business b : businesses) {
                    AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
                    adminBusinessAPI.getAdminBusinessByBusinessId(b.getBusinessId(), new AdminBusinessAPI.AdminBusinessCallBack() {
                        @Override
                        public void onUserReceived(AdminBusiness adminBusiness) {
                            loadNotifyByOneIdAdmin(adminBusiness.getUserId());
                        }

                        @Override
                        public void onUsersReceived(List<AdminBusiness> adminBusiness) {

                        }

                        @Override
                        public void onError(String s) {

                        }
                    });
                }
            }
        });
    }

    private void loadNotifyByListIdAdminDepartment(){
        notifyList.clear();
        notifyAdapter.notifyDataSetChanged();

        DepartmentAPI departmentAPI = new DepartmentAPI();
        departmentAPI.getAllDepartments(new DepartmentAPI.DepartmentCallback() {
            @Override
            public void onDepartmentReceived(Department department) {

            }

            @Override
            public void onDepartmentsReceived(List<Department> departments) {
                for (Department d : departments) {
                    AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
                    adminDepartmentAPI.getAdminDepartmentByDepartmentId(d.getDepartmentId(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
                        @Override
                        public void onUserReceived(AdminDepartment adminDepartment) {
                            loadNotifyByOneIdAdmin(adminDepartment.getUserId());
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
        });
    }
}
