package com.example.socialmediatdcproject.fragment.Admin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.BusinessAPI;
import com.example.socialmediatdcproject.API.ClassAPI;
import com.example.socialmediatdcproject.API.CollabAPI;
import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.FilterPostsAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R; // Import đúng package chứa R
import com.example.socialmediatdcproject.activity.CreateNewEventActivity;
import com.example.socialmediatdcproject.activity.SendNotificationActivity;
import com.example.socialmediatdcproject.adapter.ItemFilterAdapter;
import com.example.socialmediatdcproject.dataModels.Collab;
import com.example.socialmediatdcproject.dataModels.FilterPost;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Business;
import com.example.socialmediatdcproject.model.Class;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainFeatureFragment extends Fragment {
    private List<String> optionsOfAdminDepartment = new ArrayList<>();
    private static final int MY_REQUEST_CODE = 10;
    private static final int MY_CAMERA_REQUEST_CODE = 110;
    RecyclerView recyclerView;
    ItemFilterAdapter itemFilterAdapter;
    private Uri selectedImageUri;
    private ImageView showImagePost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for this fragment
        setRetainInstance(true);
        return inflater.inflate(R.layout.admin_fragment_notify_and_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button postBtn = view.findViewById(R.id.admin_create_post);
        Button noticeBtn = view.findViewById(R.id.admin_create_notify);
        Button eventBtn = view.findViewById(R.id.admin_create_event);
        showImagePost = view.findViewById(R.id.post_image_filter);

        changeColorButtonNormal(postBtn);
        changeColorButtonNormal(noticeBtn);
        changeColorButtonNormal(eventBtn);

        postBtn.setOnClickListener(v -> {
            showCustomDialog();
        });

        noticeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SendNotificationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);
        });

        eventBtn.setOnClickListener(v -> {
            String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Intent intent = new Intent(v.getContext(), CreateNewEventActivity.class);

            AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();
            AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
            AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
            adminDepartmentAPI.getAdminDepartmentByKey(userKey, new AdminDepartmentAPI.AdminDepartmentCallBack() {
                @Override
                public void onUserReceived(AdminDepartment adminDepartment) {
                    intent.putExtra("adminId", adminDepartment.getUserId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    startActivity(intent);
                }

                @Override
                public void onUsersReceived(List<AdminDepartment> adminDepartment) {

                }

                @Override
                public void onError(String s) {

                }
            });
            adminBusinessAPI.getAdminBusinessByKey(userKey, new AdminBusinessAPI.AdminBusinessCallBack() {
                @Override
                public void onUserReceived(AdminBusiness adminBusiness) {
                    intent.putExtra("adminId", adminBusiness.getUserId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    startActivity(intent);
                }

                @Override
                public void onUsersReceived(List<AdminBusiness> adminBusiness) {

                }

                @Override
                public void onError(String s) {

                }
            });
            adminDefaultAPI.getAdminDefaultByKey(userKey, new AdminDefaultAPI.AdminDefaultCallBack() {
                @Override
                public void onUserReceived(AdminDefault adminDefault) {
                    intent.putExtra("adminId", adminDefault.getUserId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    startActivity(intent);
                }

                @Override
                public void onUsersReceived(List<AdminDefault> adminDefault) {

                }
            });

        });
    }

    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            showImagePost.setVisibility(View.VISIBLE);

                            if (data.hasExtra("data")) {
                                // Nhận ảnh từ Camera
                                Bundle extras = data.getExtras();
                                if (extras != null) {
                                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                                    if (imageBitmap != null && isAdded()) {
                                        // Đặt ảnh làm background cho imgFromGallery
                                        Drawable drawable = new BitmapDrawable(getResources(), imageBitmap);
                                        showImagePost.setBackground(drawable);

                                        // Chuyển Bitmap thành Uri
                                        selectedImageUri = getImageUriFromBitmap(requireContext(), imageBitmap);
                                    }
                                }
                            } else {
                                // Nhận ảnh từ Gallery
                                selectedImageUri = data.getData();
                                if (selectedImageUri != null && isAdded()) {
                                    try {
                                        // Đọc ảnh từ Uri
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
                                        if (bitmap != null) {
                                            // Đặt ảnh làm background cho imgFromGallery
                                            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                                            showImagePost.setBackground(drawable);

                                            // Chuyển Bitmap thành Uri
                                            selectedImageUri = getImageUriFromBitmap(requireContext(), bitmap);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
    );

    // Phương thức helper để chuyển Bitmap thành Uri
    private Uri getImageUriFromBitmap(Context context, Bitmap bitmap) {
        // Lưu bitmap thành file và trả về Uri
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "temp_image", null);
        return Uri.parse(path);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showCustomDialog() {
        // Tạo Dialog
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.post_create_filter_layout);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.comment_custom));

        int marginInDp = (int) (10 * getResources().getDisplayMetrics().density);
        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.horizontalMargin = marginInDp / (float) getResources().getDisplayMetrics().widthPixels;
            dialog.getWindow().setAttributes(params);
        }

        showImagePost = dialog.findViewById(R.id.post_image_filter);
        showImagePost.setVisibility(View.GONE);

        dialog.show();

        // Tìm các view bên trong Dialog
        Button postButtonCreate = dialog.findViewById(R.id.button_post_user_create_new);
        Button postButtonCancle = dialog.findViewById(R.id.button_post_user_create_cancle);
        EditText postContent = dialog.findViewById(R.id.post_content);
        Spinner filterMain = dialog.findViewById(R.id.filter_main);
        recyclerView = dialog.findViewById(R.id.filter_recycle);

        ImageButton addImage = dialog.findViewById(R.id.post_add_image);
        ImageButton changeBanckground = dialog.findViewById(R.id.post_change_background);
        ImageButton addSurvey = dialog.findViewById(R.id.post_icon_survey);

        // Xử lý sự kiện khi nhấn vào nút addImage
        addImage.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Chọn ảnh")
                    .setItems(new CharSequence[]{"Chọn từ thư viện", "Chụp ảnh mới"}, (dialogInterface, which) -> {
                        if (which == 0) {
                            onClickRequestGalleryPermission();

                        } else {
                            onClickRequestCameraPermission();
                        }
                    })
                    .show();
        });

        ImageView imageViewAvatar = dialog.findViewById(R.id.avatar_user_create_post);

        changeColorButtonNormal(postButtonCancle);
        changeColorButtonNormal(postButtonCreate);

        optionsOfAdminDepartment.clear();

        final int[] isAdminType = {-1};

        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
        AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();

        adminDepartmentAPI.getAdminDepartmentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                Glide.with(getContext())
                        .load(adminDepartment.getAvatar())
                        .circleCrop()
                        .into(imageViewAvatar);

                // Các tùy chọn cho Spinner
                optionsOfAdminDepartment.add("Toàn bộ học sinh");
                optionsOfAdminDepartment.add("Doanh nghiệp liên kết với khoa");
                optionsOfAdminDepartment.add("Học sinh thuộc lớp");
                optionsOfAdminDepartment.add("Các cá nhân cụ thể");

                // Set adapter cho Spinner
                ArrayAdapter<String> filterMainAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, optionsOfAdminDepartment);
                filterMainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                filterMain.setAdapter(filterMainAdapter);

                isAdminType[0] = 1;
            }

            @Override
            public void onUsersReceived(List<AdminDepartment> adminDepartment) {
            }

            @Override
            public void onError(String s) {
            }
        });

        adminBusinessAPI.getAdminBusinessByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminBusinessAPI.AdminBusinessCallBack() {
            @Override
            public void onUserReceived(AdminBusiness adminBusiness) {
                Glide.with(getContext())
                        .load(adminBusiness.getAvatar())
                        .circleCrop()
                        .into(imageViewAvatar);

                // Các tùy chọn cho Spinner
                optionsOfAdminDepartment.add("Học sinh toàn trường");
                optionsOfAdminDepartment.add("Khoa cụ thể");
                optionsOfAdminDepartment.add("Học sinh thuộc Khoa liên kết");

                // Set adapter cho Spinner
                ArrayAdapter<String> filterMainAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, optionsOfAdminDepartment);
                filterMainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                filterMain.setAdapter(filterMainAdapter);

                isAdminType[0] = 2;
            }

            @Override
            public void onUsersReceived(List<AdminBusiness> adminBusiness) {

            }

            @Override
            public void onError(String s) {

            }
        });

        adminDefaultAPI.getAdminDefaultByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDefaultAPI.AdminDefaultCallBack() {
            @Override
            public void onUserReceived(AdminDefault adminDefault) {
                if (!adminDefault.getAdminType().equals("Super")) {
                    Glide.with(getContext())
                            .load(adminDefault.getAvatar())
                            .circleCrop()
                            .into(imageViewAvatar);

                    // Các tùy chọn cho Spinner
                    optionsOfAdminDepartment.add("Học sinh toàn trường");
                    optionsOfAdminDepartment.add("Các quản lý Khoa cụ thể");
                    optionsOfAdminDepartment.add("Các Doanh nghiệp cụ thể");
                    optionsOfAdminDepartment.add("Học sinh thuộc Khoa");
                    optionsOfAdminDepartment.add("Học sinh thuộc Lớp");
                    optionsOfAdminDepartment.add("Các cá nhân học sinh");

                    // Set adapter cho Spinner
                    ArrayAdapter<String> filterMainAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, optionsOfAdminDepartment);
                    filterMainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    filterMain.setAdapter(filterMainAdapter);

                    isAdminType[0] = 3;
                }
            }

            @Override
            public void onUsersReceived(List<AdminDefault> adminDefault) {

            }
        });

        recyclerView.setVisibility(View.GONE);
        List<Integer> receivePostUser = new ArrayList<>();

        final boolean[] isFilterPost = {false};
        final boolean[] isSendAdminDepartment = {false};
        // Gán sự kiện cho Spinner khi người dùng chọn item
        filterMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = optionsOfAdminDepartment.get(position);
                if (isAdminType[0] == 1) {
                    // Admin Department
                    if (selected.equals(optionsOfAdminDepartment.get(0))) {
                        recyclerView.setVisibility(View.GONE);
                        isFilterPost[0] = false;

                    } else if (selected.equals(optionsOfAdminDepartment.get(1))) {
                        recyclerView.setVisibility(View.VISIBLE);
                        loadBusinessFilterCollabsDepartment();
                        isFilterPost[0] = true;


                    } else if (selected.equals(optionsOfAdminDepartment.get(2))) {
                        recyclerView.setVisibility(View.VISIBLE);
                        loadClassFilterByDepartment();
                        isFilterPost[0] = true;

                    } else {
                        loadStudentInDepartmentFilter();
                        recyclerView.setVisibility(View.VISIBLE);
                        isFilterPost[0] = true;

                    }
                } else if (isAdminType[0] == 2){
                    // Admin business
                    if (selected.equals(optionsOfAdminDepartment.get(0))) {
                        recyclerView.setVisibility(View.GONE);
                        isFilterPost[0] = false;

                    } else if (selected.equals(optionsOfAdminDepartment.get(1))) {
                        recyclerView.setVisibility(View.VISIBLE);
                        loadDepartmentFilter();
                        isFilterPost[0] = true;
                        isSendAdminDepartment[0] = true;

                        Log.d("AdminBusiness", "onItemSelected: " + "Admin Khoa");

                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        loadDepartmentFilter();
                        isFilterPost[0] = true;
                        isSendAdminDepartment[0] = false;

                        Log.d("AdminBusiness", "onItemSelected: " + "Học sinh thuộc khoa");
                    }
                } else if (isAdminType[0] == 3){
                    // Admin Phòng đào tạo và Admin Đoàn thanh niên
                    if (selected.equals(optionsOfAdminDepartment.get(0))) {
                        // Học sinh toàn trường
                        recyclerView.setVisibility(View.GONE);
                        isFilterPost[0] = false;

                    } else if (selected.equals(optionsOfAdminDepartment.get(1))) {
                        // Admin Khoa
                        recyclerView.setVisibility(View.VISIBLE);
                        loadAllDepartment();
                        isFilterPost[0] = true;
                        isSendAdminDepartment[0] = true;

                    } else if (selected.equals(optionsOfAdminDepartment.get(2))) {
                        // Admin doanh nghiệp
                        recyclerView.setVisibility(View.VISIBLE);
                        loadAllBusiness();
                        isFilterPost[0] = true;
                    } else if (selected.equals(optionsOfAdminDepartment.get(3))) {
                        // Học sinh thuộc khoa
                        recyclerView.setVisibility(View.VISIBLE);
                        loadAllDepartment();
                        isFilterPost[0] = true;
                        isSendAdminDepartment[0] = false;
                    } else if (selected.equals(optionsOfAdminDepartment.get(4))) {
                        // Học sinh thuộc lớp
                        recyclerView.setVisibility(View.VISIBLE);
                        loadAllClass();
                        isFilterPost[0] = true;
                    } else if (selected.equals(optionsOfAdminDepartment.get(5))) {
                        // Cá nhân học sinh
                        recyclerView.setVisibility(View.VISIBLE);
                        loadAllStudent();
                        isFilterPost[0] = true;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        postButtonCreate.setOnClickListener(v -> {
            // Vô hiệu hóa nút để tránh nhấn nhiều lần
            postButtonCreate.setEnabled(false);

            String content = postContent.getText().toString();

            // Kiểm tra xem title hoặc content có null hoặc rỗng không
            if (content.isEmpty()) {
                // Hiển thị Toast nếu title hoặc content rỗng
                Toast.makeText(v.getContext(), "Vui lòng điền đầy đủ các dữ liệu trên", Toast.LENGTH_SHORT).show();

                // Bật lại nút submit để người dùng có thể thử lại
                postButtonCreate.setEnabled(true);

                return; // Dừng tiếp tục xử lý nếu title hoặc content rỗng
            }

            if (isFilterPost[0]) {
                // Xử lý các bộ lọc như đã đề cập ở trên
                receivePostUser.clear(); // Đảm bảo danh sách rỗng trước khi thêm

                List<String> selectedFilter = itemFilterAdapter.getSelectedFilters();

                Log.d("AdminBusiness", "Lựa chọn đầu: " + selectedFilter.get(0));
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
                    processCreatePost(content, isFilterPost[0], receivePostUser);
                    dialog.dismiss();
                }
                else if (selectedFilter.get(0).substring(0, 2).equals("CD")) {
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
                    processCreatePost(content, isFilterPost[0], receivePostUser);
                    dialog.dismiss(); // Đóng dialog sau khi processCreatePost hoàn tất
                }
                else if (selectedFilter.get(0).substring(0, 4).equals("Khoa")) {
                    if (isSendAdminDepartment[0]) {
                        for (String s : selectedFilter) {
                            GroupAPI groupAPI = new GroupAPI();
                            groupAPI.getGroupByName(s, new GroupAPI.GroupCallback() {
                                @Override
                                public void onGroupReceived(Group group) {

                                }

                                @Override
                                public void onGroupsReceived(List<Group> groups) {
                                    Group group = groups.get(0);
                                    receivePostUser.add(group.getAdminUserId());
                                    Log.d("LKDU", "onGroupReceived: " + receivePostUser.size());
                                    processCreatePost(content, isFilterPost[0], receivePostUser);
                                }
                            });
                        }
                    }else {
                        for (String s : selectedFilter) {
                            GroupAPI groupAPI = new GroupAPI();
                            groupAPI.getGroupByName(s, new GroupAPI.GroupCallback() {
                                @Override
                                public void onGroupReceived(Group group) {

                                }

                                @Override
                                public void onGroupsReceived(List<Group> groups) {
                                    Group group = groups.get(0);
                                    GroupUserAPI groupUserAPI = new GroupUserAPI();
                                    groupUserAPI.getGroupUserByIdGroup(group.getGroupId(), new GroupUserAPI.GroupUserCallback() {
                                        @Override
                                        public void onGroupUsersReceived(List<GroupUser> groupUsers) {
                                            for (GroupUser g : groupUsers) {
                                                receivePostUser.add(g.getUserId());
                                            }
                                        }
                                    });
                                    processCreatePost(content, isFilterPost[0], receivePostUser);
                                }
                            });
                        }
                    }
                    dialog.dismiss(); // Đóng dialog sau khi processCreatePost hoàn tất
                }
                else {
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
                    processCreatePost(content, isFilterPost[0], receivePostUser);
                    dialog.dismiss(); // Đóng dialog sau khi processCreatePost hoàn tất
                }
            } else {
                // Nếu không dùng bộ lọc, chỉ gọi processCreatePost một lần
                processCreatePost(content, isFilterPost[0], receivePostUser);
                dialog.dismiss(); // Đóng dialog
            }

            // Bật lại nút sau khi hoàn thành
            postButtonCreate.setEnabled(true);

        });

        postButtonCancle.setOnClickListener(v -> dialog.dismiss());
    }

    // Phương thức cập nhật RecyclerView sau khi có đủ dữ liệu
    private void updateRecyclerView(ArrayList<String> filterList) {
        if (recyclerView == null) {
            recyclerView = getView().findViewById(R.id.filter_recycle);
        }

        if (recyclerView != null) {
            itemFilterAdapter = new ItemFilterAdapter(filterList, requireContext());
            itemFilterAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(itemFilterAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        } else {
            Log.e("MainFeatureFragment", "RecyclerView is null, cannot update RecyclerView.");
        }
    }

    public void changeColorButtonNormal(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
    }

    private void loadBusinessFilterCollabsDepartment() {
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
        FilterPostsAPI filterPostsAPI = new FilterPostsAPI();
        FilterPost filterPost = new FilterPost();
        filterPost.setPostId(id);
        filterPost.setListUserGet(users);
        filterPostsAPI.addReceivePost(filterPost);
    }

    private void loadClassFilterByDepartment() {
        ClassAPI classAPI = new ClassAPI();
        ArrayList<String> filterList = new ArrayList<>();
        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        adminDepartmentAPI.getAdminDepartmentByKey(key, new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                classAPI.getClassesByDepartmentId(adminDepartment.getDepartmentId(), new ClassAPI.ClassCallback() {
                    @Override
                    public void onClassReceived(Class classItem) {

                    }

                    @Override
                    public void onClassesReceived(List<Class> classList) {
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

    private void loadAllDepartment(){
        DepartmentAPI departmentAPI = new DepartmentAPI();
        ArrayList<String> filterList = new ArrayList<>();
        departmentAPI.getAllDepartments(new DepartmentAPI.DepartmentCallback() {
            @Override
            public void onDepartmentReceived(Department department) {

            }

            @Override
            public void onDepartmentsReceived(List<Department> departments) {
                for (Department d : departments) {
                    GroupAPI groupAPI = new GroupAPI();
                    groupAPI.getGroupById(d.getGroupId(), new GroupAPI.GroupCallback() {
                        @Override
                        public void onGroupReceived(Group group) {
                            filterList.add(group.getGroupName());
                            updateRecyclerView(filterList);
                        }

                        @Override
                        public void onGroupsReceived(List<Group> groups) {

                        }
                    });
                }
            }
        });
    }

    private void loadAllBusiness(){
        BusinessAPI businessAPI = new BusinessAPI();
        ArrayList<String> filterList = new ArrayList<>();
        businessAPI.getAllBusinesses(new BusinessAPI.BusinessCallback() {
            @Override
            public void onBusinessReceived(Business business) {

            }

            @Override
            public void onBusinessesReceived(List<Business> businesses) {
                for (Business b : businesses) {
                    filterList.add(b.getBussinessName());
                }
                updateRecyclerView(filterList);
            }
        });
    }

    private void loadAllClass(){
        ClassAPI classAPI = new ClassAPI();
        ArrayList<String> filterList = new ArrayList<>();
        classAPI.getAllClasses(new ClassAPI.ClassCallback() {
            @Override
            public void onClassReceived(Class classItem) {

            }

            @Override
            public void onClassesReceived(List<Class> classList) {
                for (Class c : classList) {
                    filterList.add(c.getClassName());
                }
                updateRecyclerView(filterList);
            }
        });
    }

    private void loadAllStudent(){
        StudentAPI studentAPI = new StudentAPI();
        ArrayList<String> filterList = new ArrayList<>();
        studentAPI.getAllStudents(new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {

            }

            @Override
            public void onStudentsReceived(List<Student> students) {
                for (Student s : students) {
                    filterList.add(s.getStudentNumber());
                }
                updateRecyclerView(filterList);
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

    private void loadDepartmentFilter(){
        CollabAPI collabAPI = new CollabAPI();
        ArrayList<String> filterList = new ArrayList<>();
        AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
        adminBusinessAPI.getAdminBusinessByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminBusinessAPI.AdminBusinessCallBack() {
            @Override
            public void onUserReceived(AdminBusiness adminBusiness) {
                collabAPI.getCollabsByBusinessId(adminBusiness.getBusinessId(), new CollabAPI.CollabCallback() {
                    @Override
                    public void onCollabReceived(List<Collab> collabList) {
                        for (Collab c : collabList) {
                            DepartmentAPI departmentAPI = new DepartmentAPI();
                            departmentAPI.getDepartmentById(c.getDepartmentId(), new DepartmentAPI.DepartmentCallback() {
                                @Override
                                public void onDepartmentReceived(Department department) {
                                    GroupAPI groupAPI = new GroupAPI();
                                    groupAPI.getGroupById(department.getGroupId(), new GroupAPI.GroupCallback() {
                                        @Override
                                        public void onGroupReceived(Group group) {
                                            filterList.add(group.getGroupName());
                                            updateRecyclerView(filterList);
                                        }

                                        @Override
                                        public void onGroupsReceived(List<Group> groups) {

                                        }
                                    });
                                }

                                @Override
                                public void onDepartmentsReceived(List<Department> departments) {

                                }
                            });
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

    private void processCreatePost(String content, boolean filter, List<Integer> postsReceive) {
        PostAPI postAPI = new PostAPI();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
        AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        // Tạo Dialog
        Dialog loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false); // Không cho phép người dùng tắt dialog bằng cách bấm ngoài

        // Thêm ProgressBar vào layout của Dialog (layout: dialog_loading.xml)
        ProgressBar progressBar = loadingDialog.findViewById(R.id.progressBar);
        TextView textView = loadingDialog.findViewById(R.id.textLoading);
        textView.setText("Đang đăng bài...");

        // Hiển thị Dialog
        loadingDialog.show();

        // Cờ để kiểm tra chỉ gọi addPost một lần
        final boolean[] isPostAdded = {false};
        Post newPost = new Post();
        uploadImageToFirebaseStorage(selectedImageUri, newPost, loadingDialog);
        adminDepartmentAPI.getAdminDepartmentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                postAPI.getAllPosts(new PostAPI.PostCallback() {
                    @Override
                    public void onPostReceived(Post post) {
                        // Không cần xử lý ở đây
                    }

                    @Override
                    public void onPostsReceived(List<Post> posts) {
                        GroupAPI groupAPI = new GroupAPI();
                        DepartmentAPI departmentAPI = new DepartmentAPI();

                        departmentAPI.getDepartmentById(adminDepartment.getDepartmentId(), new DepartmentAPI.DepartmentCallback() {
                            @Override
                            public void onDepartmentReceived(Department department) {
                                groupAPI.getGroupById(department.getGroupId(), new GroupAPI.GroupCallback() {
                                    @Override
                                    public void onGroupReceived(Group group) {
                                        // Kiểm tra nếu là Admin của group và bài viết chưa được thêm
                                        if (adminDepartment.getUserId() == group.getAdminUserId() && !isPostAdded[0]) {
                                            isPostAdded[0] = true; // Đánh dấu là đã thêm bài viết
                                            newPost.setPostId(posts.size());
                                            newPost.setUserId(adminDepartment.getUserId());
                                            newPost.setPostLike(0);
                                            newPost.setContent(content);
                                            newPost.setStatus(Post.APPROVED);
                                            newPost.setFilter(filter);
                                            newPost.setGroupId(department.getGroupId());
                                            newPost.setCreatedAt(sdf.format(new Date()));

                                            if (postsReceive.size() != 0) {
                                                processAdditional(newPost.getPostId(), postsReceive);
                                            }

                                            postAPI.addPost(newPost);

                                            // Hiển thị Toast thông báo thành công
                                            Toast.makeText(getContext(), "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onGroupsReceived(List<Group> groups) {
                                        // Không cần xử lý ở đây
                                    }
                                });
                            }

                            @Override
                            public void onDepartmentsReceived(List<Department> departments) {
                                // Không cần xử lý ở đây
                            }
                        });
                    }
                });
            }

            @Override
            public void onUsersReceived(List<AdminDepartment> adminDepartment) {
                // Không cần xử lý ở đây
            }

            @Override
            public void onError(String s) {
                // Xử lý lỗi nếu cần
            }
        });
        adminBusinessAPI.getAdminBusinessByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminBusinessAPI.AdminBusinessCallBack() {
            @Override
            public void onUserReceived(AdminBusiness adminBusiness) {
                postAPI.getAllPosts(new PostAPI.PostCallback() {
                    @Override
                    public void onPostReceived(Post post) {
                        // Không cần xử lý ở đây
                    }

                    @Override
                    public void onPostsReceived(List<Post> posts) {
                        GroupAPI groupAPI = new GroupAPI();
                        BusinessAPI businessAPI = new BusinessAPI();

                        businessAPI.getBusinessById(adminBusiness.getBusinessId(), new BusinessAPI.BusinessCallback() {
                            @Override
                            public void onBusinessReceived(Business business) {
                                groupAPI.getGroupById(business.getGroupId(), new GroupAPI.GroupCallback() {
                                    @Override
                                    public void onGroupReceived(Group group) {
                                        // Kiểm tra nếu là Admin của group và bài viết chưa được thêm
                                        if (!isPostAdded[0]) {
                                            isPostAdded[0] = true; // Đánh dấu là đã thêm bài viết

                                            newPost.setPostId(posts.size());
                                            newPost.setUserId(adminBusiness.getUserId());
                                            newPost.setPostLike(0);
                                            newPost.setContent(content);
                                            newPost.setStatus(Post.APPROVED);
                                            newPost.setFilter(filter);
                                            newPost.setGroupId(business.getGroupId());
                                            newPost.setCreatedAt(sdf.format(new Date()));

                                            if (postsReceive.size() != 0) {
                                                processAdditional(newPost.getPostId(), postsReceive);
                                            }

                                            postAPI.addPost(newPost);

                                            // Hiển thị Toast thông báo thành công
                                            Toast.makeText(getContext(), "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onGroupsReceived(List<Group> groups) {
                                        // Không cần xử lý ở đây
                                    }
                                });
                            }

                            @Override
                            public void onBusinessesReceived(List<Business> businesses) {

                            }
                        });
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
        adminDefaultAPI.getAdminDefaultByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDefaultAPI.AdminDefaultCallBack() {
            @Override
            public void onUserReceived(AdminDefault adminDefault) {
                if (!adminDefault.getAdminType().equals("Super")) {
                    postAPI.getAllPosts(new PostAPI.PostCallback() {
                        @Override
                        public void onPostReceived(Post post) {
                            // Không cần xử lý ở đây
                        }

                        @Override
                        public void onPostsReceived(List<Post> posts) {
                            GroupAPI groupAPI = new GroupAPI();
                            groupAPI.getGroupById(adminDefault.getUserId(), new GroupAPI.GroupCallback() {
                                @Override
                                public void onGroupReceived(Group group) {
                                    // Kiểm tra nếu là Admin của group và bài viết chưa được thêm
                                    if (!isPostAdded[0]) {
                                        isPostAdded[0] = true; // Đánh dấu là đã thêm bài viết

                                        newPost.setPostId(posts.size());
                                        newPost.setUserId(adminDefault.getUserId());
                                        newPost.setPostLike(0);
                                        newPost.setContent(content);
                                        newPost.setStatus(Post.APPROVED);
                                        newPost.setFilter(filter);
                                        newPost.setGroupId(adminDefault.getUserId());
                                        newPost.setCreatedAt(sdf.format(new Date()));

                                        if (postsReceive.size() != 0) {
                                            processAdditional(newPost.getPostId(), postsReceive);
                                        }

                                        postAPI.addPost(newPost);

                                        // Hiển thị Toast thông báo thành công
                                        Toast.makeText(getContext(), "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onGroupsReceived(List<Group> groups) {
                                    // Không cần xử lý ở đây
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onUsersReceived(List<AdminDefault> adminDefault) {

            }
        });
    }

    // Tải ảnh lên Firebase và lưu URL vào post
    private void uploadImageToFirebaseStorage(Uri filePath, Post post, Dialog loadingDialog) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Đặt tên ảnh theo postId
        String imageName = FirebaseDatabase.getInstance().getReference().push().getKey();
        StorageReference postImageRef = storageRef.child("postImages/" + imageName);

        if (filePath != null) {
            postImageRef.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        postImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            post.setPostImage(downloadUrl);

                            PostAPI postAPI = new PostAPI();
                            postAPI.updatePost(post);

                            // Dismiss dialog sau khi bài viết được thêm
                            loadingDialog.dismiss();
                        });
                    })
                    .addOnFailureListener(exception -> {
                        Toast.makeText(requireContext(), "Tải ảnh thất bại: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
        else {
            post.setPostImage("");
            PostAPI postAPI = new PostAPI();
            postAPI.updatePost(post);
        }

    }
    // Camera
    private void onClickRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
    }

    // Mở thư viện ảnh
    private void onClickRequestGalleryPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.CUR_DEVELOPMENT ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Xử lý quyền đọc bộ nhớ
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(getContext(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }

        // Xử lý quyền camera
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(getContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Mở cam
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mActivityResultLauncher.launch(cameraIntent);
    }

    //Mở Gallery
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }
}
