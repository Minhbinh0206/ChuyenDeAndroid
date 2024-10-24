package com.example.socialmediatdcproject.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.MajorAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Major;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadProfileActivity extends AppCompatActivity {

    private Spinner departmentSpinner;
    private Spinner majorSpinner;
    private EditText yearInput, monthInput, dayInput;
    private List<String> optionsDepartment = new ArrayList<>();
    private List<String> optionsMajor = new ArrayList<>();
    private DepartmentAPI departmentAPI = new DepartmentAPI();
    private MajorAPI majorAPI = new MajorAPI();
    public static final String TAG = UploadProfileActivity.class.getName();
    private static final int MY_REQUEST_CODE = 10;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 2 ;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private ImageView imgFromGallery;
    private Button btnSelectImage;
    private TextView studentClass;
    private EditText studentNumberEditText;
    private EditText fullNameText;
    private EditText phoneNumber;
    String fullName = "";
    private int userId;
    String email;
    String password;
    private Uri selectedImageUri; // Declare this variable to store the selected image URI
    private Student student;

    //Kiểm tra quyền camera
    private void onClickRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
    }
    // Hàm hiển thị hộp thoại để chọn nguồn hình ảnh
    private void showImageSourceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Select Image Source")
                .setItems(new String[]{"Gallery", "Camera"}, (dialog, which) -> {
                    if (which == 0) {
                        onClickRequestPermission(); // Gọi hàm chọn ảnh từ thư viện
                    } else {
                        onClickRequestCameraPermission(); // Gọi hàm mở camera
                    }
                })
                .show();
    }
    // Hàm xử lý kết quả từ camera hoặc gallery
    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();


                        // Nếu chọn ảnh từ Gallery
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            try {
                                // Hiển thị ảnh chọn từ Gallery
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                                imgFromGallery.setImageBitmap(bitmap);

                                // Upload ảnh lên Firebase Storage
//                                uploadImageToFirebaseStorage(selectedImageUri, userId, student); // Gọi hàm upload ảnh với userId và student
                            } catch (IOException e) {
                                e.printStackTrace();

                            }
                        }
                        // Nếu chụp ảnh từ Camera
                        else {
                            Bundle extras = data != null ? data.getExtras() : null;
                            if (extras != null) {
                                Bitmap imageBitmap = (Bitmap) extras.get("data");
                                if (imageBitmap != null) {
                                    imgFromGallery.setImageBitmap(imageBitmap);

                                    // Chuyển Bitmap thành Uri
                                    selectedImageUri = getImageUriFromBitmap(UploadProfileActivity.this, imageBitmap);

                                    // Upload ảnh lên Firebase Storage
//                                    uploadImageToFirebaseStorage(imageUri, userId, student); // Gọi hàm upload ảnh với userId và student
                                }
                            }
                        }
                    }
                }
            }
    );


    //Hàm khởi tạo giá trị ánh xạ
    private void initUi() {
        imgFromGallery = findViewById(R.id.pfofileImages);
        btnSelectImage = findViewById(R.id.button_upload_image);
    }


    //Upload Profile
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile_upload_layout);

        Intent intent = getIntent();

        userId = intent.getIntExtra("userId", userId);
        if (userId == -1) {
            Toast.makeText(this, "User ID not found!", Toast.LENGTH_SHORT).show();
            // Có thể thoát khỏi Activity hoặc xử lý lỗi ở đây
            finish();
        }
        UserAPI userAPI = new UserAPI();
        userAPI.getUserById(userId, new UserAPI.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                fullName = user.getFullName();
                email = user.getEmail();
                password = user.getPassword();
            }

            @Override
            public void onUsersReceived(List<User> users) {

            }
        });

        // Khởi tạo các view
        fullNameText = findViewById(R.id.fullname_infomation);
        studentClass = findViewById(R.id.class_infomation);
        departmentSpinner = findViewById(R.id.department_infomation);
        majorSpinner = findViewById(R.id.major_infomation);
        yearInput = findViewById(R.id.year_born_info);
        monthInput = findViewById(R.id.month_born_info);
        dayInput = findViewById(R.id.day_born_info);
        studentNumberEditText = findViewById(R.id.student_number_infomation);
        phoneNumber = findViewById(R.id.phone_infomation);
        Button buttonUploadProfile = findViewById(R.id.button_upload_profile);

        //Gọi lại hàm ánh xạ initUi ở trên
        initUi();
        //checkAndRequestPermissions();

        //Nhấn vào nút chọn và mở quyền
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSourceDialog();
            }
        });


        // Xử lý spinner các ngành và khoa
        Spinner department = findViewById(R.id.department_infomation);
        Spinner major = findViewById(R.id.major_infomation);

        // Lấy danh sách phòng ban từ API
        loadDepartments();

        // Xử lý khi người dùng chọn Khoa
        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedDepartmentName = optionsDepartment.get(position);
                loadMajorsForDepartment(selectedDepartmentName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                optionsMajor.clear();
            }
        });

        // Xử lý chọn năm sinh
        setupYearInput();

        // Xử lý chọn tháng và ngày
        setupDateInput();

        // Xử lý chuyển trang qua trang home
        buttonUploadProfile.setOnClickListener(v -> {
            // Lấy dữ liệu từ các trường
            String studentNumber = studentNumberEditText.getText().toString().trim(); // Thêm EditText cho số sinh viên
            String birthday = yearInput.getText().toString() + "-" + monthInput.getText().toString() + "-" + dayInput.getText().toString();

            // Lấy ID từ spinner
            int departmentId = departmentSpinner.getSelectedItemPosition(); // Thay đổi nếu bạn cần lấy ID thực sự từ đối tượng Department
            int majorId = majorSpinner.getSelectedItemPosition(); // Tương tự
            String classId = studentClass.getText().toString();
            String phoneNumberInfo = phoneNumber.getText().toString();
            int roleId = User.ROLE_STUDENT;
            String fullnameStudent = fullNameText.getText().toString();
            String description = ""; // Thêm mô tả nếu cần
            String avatarUrl = ""; // Thêm ảnh

            // Kiểm tra xem người dùng đã nhập đầy đủ thông tin chưa
            if (studentNumber.isEmpty() || birthday.isEmpty()) {
                Toast.makeText(UploadProfileActivity.this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng Student

            student = new Student(userId, email, password, fullnameStudent, avatarUrl, phoneNumberInfo, roleId , studentNumber, birthday, departmentId, majorId, classId, description);

            GroupUserAPI groupUserAPI = new GroupUserAPI();
            groupUserAPI.getAllGroupUsers(new GroupUserAPI.GroupUserCallback() {
                @Override
                public void onGroupUsersReceived(List<GroupUser> groupUsers) {
                    int lastId = groupUsers.size();
                    departmentAPI.getDepartmentById(departmentId, new DepartmentAPI.DepartmentCallback() {
                        @Override
                        public void onDepartmentReceived(Department department) {
                            GroupAPI groupAPI = new GroupAPI();
                            String name = "Khoa "+ department.getDepartmentName();
                            groupAPI.getGroupByName(name, new GroupAPI.GroupCallback() {
                                @Override
                                public void onGroupReceived(Group group) {
                                    GroupUser groupUser = new GroupUser(group.getGroupId(), userId);
                                    GroupUser groupUser1 = new GroupUser(User.ID_ADMIN_PHONGDAOTAO, userId);
                                    GroupUser groupUser2 = new GroupUser(User.ID_ADMIN_DOANTHANHNIEN, userId);

                                    groupUserAPI.addGroupUser(groupUser);
                                    groupUserAPI.addGroupUser(groupUser2);
                                    groupUserAPI.addGroupUser(groupUser1);
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
            });

            uploadImageToFirebaseStorage(selectedImageUri, userId, student);

        });
    }

    // Sử dụng trong UploadProfileActivity
    private void loadDepartments() {
        departmentAPI.getAllDepartments(new DepartmentAPI.DepartmentCallback() {
            @Override
            public void onDepartmentReceived(Department department) {

            }

            @Override
            public void onDepartmentsReceived(List<Department> departments) {
                for (Department department : departments) {
                    if (department != null) {
                        optionsDepartment.add(department.getDepartmentName());
                    }
                }
                ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(UploadProfileActivity.this, android.R.layout.simple_spinner_item, optionsDepartment);
                departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                departmentSpinner.setAdapter(departmentAdapter);
            }
        });
    }

    // Sử dụng trong UploadProfileActivity
    private void loadMajors(int departmentId) {
        optionsMajor.clear();
        optionsMajor.add(0, "Chọn ngành");

        majorAPI.getAllMajors(new MajorAPI.MajorCallback() {
            @Override
            public void onMajorReceived(Major major) {

            }

            @Override
            public void onMajorsReceived(List<Major> majors) {
                for (Major major : majors) {
                    if (major != null && major.getDepartmentId() == departmentId) {
                        optionsMajor.add(major.getMajorName());
                    }
                }

                // Cập nhật adapter cho spinner major
                ArrayAdapter<String> majorAdapter = new ArrayAdapter<>(UploadProfileActivity.this, android.R.layout.simple_spinner_item, optionsMajor);
                majorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                majorSpinner.setAdapter(majorAdapter);

            }
        });
    }

    private void loadMajorsForDepartment(String name) {
        optionsMajor.clear();
        optionsMajor.add(0, "Chọn ngành"); // Thêm lựa chọn mặc định

        // Lấy thông tin department theo tên
        departmentAPI.getAllDepartments(new DepartmentAPI.DepartmentCallback() {
            @Override
            public void onDepartmentReceived(Department department) {

            }

            @Override
            public void onDepartmentsReceived(List<Department> departments) {
                for (Department department : departments) {
                    if (department != null && department.getDepartmentName().equals(name)) {
                        // Nếu tên khoa trùng khớp, lấy departmentId
                        int departmentId = department.getDepartmentId();
                        loadMajors(departmentId); // Gọi phương thức để lấy ngành
                    }
                }
            }
        });
    }

    private void setupYearInput() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    try {
                        int enteredYear = Integer.parseInt(s.toString());
                        if (enteredYear > currentYear) {
                            yearInput.setText(String.valueOf(currentYear));
                            yearInput.setSelection(yearInput.getText().length());
                            Toast.makeText(getApplicationContext(), "Năm không hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupDateInput() {
        monthInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    try {
                        int enteredMonth = Integer.parseInt(s.toString());
                        if (enteredMonth < 1 || enteredMonth > 12) {
                            monthInput.setText(String.valueOf(12));
                            monthInput.setSelection(monthInput.getText().length());
                            Toast.makeText(getApplicationContext(), "Tháng không hợp lệ", Toast.LENGTH_SHORT).show();
                        } else {
                            updateMaxDay(enteredMonth);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dayInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    try {
                        int enteredDay = Integer.parseInt(s.toString());
                        int maxDay = getMaxDayForMonth(Integer.parseInt(monthInput.getText().toString()), Integer.parseInt(yearInput.getText().toString()));
                        if (enteredDay < 1 || enteredDay > maxDay) {
                            dayInput.setText(String.valueOf(maxDay));
                            dayInput.setSelection(dayInput.getText().length());
                            Toast.makeText(getApplicationContext(), "Ngày không hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    // Hàm kiểm tra xem năm có phải năm nhuận hay không
    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    private int getMaxDayForMonth(int month, int year) {
        switch (month) {
            case 2:
                return isLeapYear(year) ? 29 : 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }

    private void updateMaxDay(int month) {
        int year = Integer.parseInt(yearInput.getText().toString());
        int maxDay = getMaxDayForMonth(month, year);
        int enteredDay = Integer.parseInt(dayInput.getText().toString().isEmpty() ? "1" : dayInput.getText().toString());
        if (enteredDay > maxDay) {
            dayInput.setText(String.valueOf(maxDay));
        }
    }

    // Cấp quyền mở file ảnh trong thiết bị và camera
    private void onClickRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.CUR_DEVELOPMENT) {

            showImageSourceDialog();

            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showImageSourceDialog();
        } else {
            // Cấp quyền yêu cầu
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    },
                    MY_REQUEST_CODE);
        }
    }
    // Lắng nghe người dùng cho phép hay từ chối

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            // Hiển thị lý do tại sao ứng dụng cần quyền này
            Toast.makeText(this, "This app needs storage and camera permissions to upload images.", Toast.LENGTH_SHORT).show();
        }

        // Kiểm tra mã yêu cầu quyền
        if (requestCode == MY_REQUEST_CODE) {
            // Kiểm tra xem có quyền nào đã được cấp hay không
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Nếu quyền đã được cấp, hiển thị dialog để chọn nguồn ảnh
                showImageSourceDialog();
            } else {
                // Nếu quyền bị từ chối, hiển thị thông báo cho người dùng
                Toast.makeText(this, "Permission denied to read your External storage or use the camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Hàm chọn ảnh từ Gallery
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    // Hàm chụp ảnh từ Camera
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mActivityResultLauncher.launch(intent);
    }

    private Uri getImageUriFromBitmap(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }


    // Hàm upload hình ảnh lên Storage Firebase ( Đưa ảnh lên Storage + cập nhật thông tin Student)
    private void uploadImageToFirebaseStorage(Uri filePath, int userId, Student student) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Kiểm tra nếu không chọn ảnh (filePath = null) thì gán ảnh mặc định từ drawable
        if (filePath == null) {
            // Lấy ảnh mặc định từ drawable
            int defaultImageResId = R.drawable.avatar_macdinh;
            Uri defaultImageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + getResources().getResourcePackageName(defaultImageResId) +
                    '/' + getResources().getResourceTypeName(defaultImageResId) +
                    '/' + getResources().getResourceEntryName(defaultImageResId));
            /*
            String imageName = "avatar_" + System.currentTimeMillis() + ".jpg";
            StorageReference avatarRef = storageRef.child("avatar/" + imageName);
            */
            StorageReference avatarRef = storageRef.child("avatarUser/" + userId + ",jpg");

            avatarRef.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            // Save the avatar URL to the Student object
                            student.setAvatar(downloadUrl); // Assuming you have a method in Student class
                            saveStudentToDatabase(student); // Save the student data including avatar URL
                        });
                    })
                    .addOnFailureListener(exception -> {
                        Toast.makeText(this, "Upload Failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            filePath = defaultImageUri; // Gán ảnh mặc định vào filePath
        }

        String imageName = "avatar_" + student.getStudentNumber() + ".jpg";

        StorageReference avatarRef = storageRef.child("avatar/" + imageName);

        avatarRef.putFile(filePath)
                .addOnSuccessListener(taskSnapshot -> {
                    avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        // Save the avatar URL to the Student object
                        student.setAvatar(downloadUrl); // Assuming you have a method in Student class
                        saveStudentToDatabase(student); // Save the student data including avatar URL
                    });
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(this, "Upload Failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // New method to save the Student object
    private void saveStudentToDatabase(Student student) {
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.addStudent(student, new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                Toast.makeText(getApplicationContext(), "Student profile uploaded successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UploadProfileActivity.this, SharedActivity.class));
            }

            @Override
            public void onStudentsReceived(List<Student> students) {

            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getApplicationContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStudentDeleted(int studentId) {

            }
        });
    }
}