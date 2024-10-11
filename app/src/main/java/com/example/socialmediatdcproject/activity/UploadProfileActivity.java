package com.example.socialmediatdcproject.activity;

import android.Manifest;
import android.app.Activity;
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
import com.example.socialmediatdcproject.API.MajorAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Major;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    private ImageView imgFromGallery;
    private Button btnSelectImage;
    private EditText studentNumberEditText;
    private EditText phoneNumber;

    //Hàm chạy một intent để xử lý kết quả trả về là mở Gallery để chọn hình ảnh
    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e(TAG, "onActivityResult");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data == null) {
                            return;
                        }

                        //Dữ liệu ảnh chọn từ Gallery
                        Uri uri = data.getData();
                        // mUri = uri;
                        try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                imgFromGallery.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile_upload_layout);

        Intent intent = getIntent();

        // Nhận dữ liệu từ Intent
        String fullName = intent.getStringExtra("fullName");
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");
        int userId = intent.getIntExtra("userId", -1);

        // Khởi tạo các view
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
        checkAndRequestPermissions();

        //Nhấn vào nút chọn và mở quyền
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRequestPermission();
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
            int classId = 0; // Thay đổi nếu bạn có EditText cho lớp học
            String phoneNumberInfo = phoneNumber.getText().toString();
            int roleId = User.ROLE_STUDENT;
            String description = ""; // Thêm mô tả nếu cần

            // Kiểm tra xem người dùng đã nhập đầy đủ thông tin chưa
            if (studentNumber.isEmpty() || birthday.isEmpty()) {
                Toast.makeText(UploadProfileActivity.this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng Student
            Student student = new Student(userId, email, password, fullName, "", phoneNumberInfo, roleId , studentNumber, birthday, departmentId, majorId, classId, description);

            // Lưu thông tin vào Firebase hoặc cơ sở dữ liệu
            StudentAPI studentAPI = new StudentAPI();
            studentAPI.addStudent(student, new StudentAPI.StudentCallback() {
                @Override
                public void onStudentReceived(Student student) {
                    Toast.makeText(getApplicationContext(), "Sinh viên đã được thêm thành công!", Toast.LENGTH_SHORT).show();

                    // Chuyển đến trang tiếp theo
                    Intent i = new Intent(UploadProfileActivity.this, SharedActivity.class);
                    startActivity(i);
                }

                @Override
                public void onStudentsReceived(List<Student> students) {
                    // Không cần thực hiện ở đây
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(getApplicationContext(), "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStudentDeleted(int studentId) {
                    // Không cần xử lý ở đây khi thêm sinh viên
                }
            });
        });

    }

    // Sử dụng trong UploadProfileActivity
    private void loadDepartments() {
        departmentAPI.getAllDepartments(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot departmentSnapshot : snapshot.getChildren()) {
                        Department department = (Department) departmentSnapshot.getValue(Department.class);
                        if (department != null) {
                            optionsDepartment.add(department.getDepartmentName());
                        }
                    }
                    ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(UploadProfileActivity.this, android.R.layout.simple_spinner_item, optionsDepartment);
                    departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    departmentSpinner.setAdapter(departmentAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UploadProfileActivity", "Error fetching departments: " + error.getMessage());
            }
        });
    }

    // Sử dụng trong UploadProfileActivity
    private void loadMajors(int departmentId) {
        optionsMajor.clear();
        optionsMajor.add(0, "Chọn ngành");

        majorAPI.getAllMajors(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot majorSnapshot) {
                if (majorSnapshot.exists()) {
                    for (DataSnapshot majorData : majorSnapshot.getChildren()) {
                        Major major = majorData.getValue(Major.class);
                        if (major != null && major.getDepartmentId() == departmentId) {
                            optionsMajor.add(major.getMajorName());
                        }
                    }

                    // Cập nhật adapter cho spinner major
                    ArrayAdapter<String> majorAdapter = new ArrayAdapter<>(UploadProfileActivity.this, android.R.layout.simple_spinner_item, optionsMajor);
                    majorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    majorSpinner.setAdapter(majorAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UploadProfileActivity", "Error fetching majors: " + error.getMessage());
            }
        });
    }

    private void loadMajorsForDepartment(String name) {
        optionsMajor.clear();
        optionsMajor.add(0, "Chọn ngành"); // Thêm lựa chọn mặc định

        // Lấy thông tin department theo tên
        departmentAPI.getAllDepartments(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot departmentSnapshot : snapshot.getChildren()) {
                    Department department = departmentSnapshot.getValue(Department.class);
                    if (department != null && department.getDepartmentName().equals(name)) {
                        // Nếu tên khoa trùng khớp, lấy departmentId
                        int departmentId = department.getDepartmentId();
                        loadMajors(departmentId); // Gọi phương thức để lấy ngành
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UploadProfileActivity", "Error fetching departments: " + error.getMessage());
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

    //Cấp quyền mở file ảnh trong thiết bị
    private void onClickRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUEST_CODE);
        }
    }

    //Lắng nghe người dùng cho phép hay từ chối
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Hiển thị lý do tại sao ứng dụng cần quyền này
            Toast.makeText(this, "This app needs storage permission to upload images.", Toast.LENGTH_SHORT).show();
        }

        // Kiểm tra mã yêu cầu quyền
        if (requestCode == MY_REQUEST_CODE) {
            // Kiểm tra xem có quyền nào đã được cấp hay không
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Nếu quyền đã được cấp, mở thư viện ảnh
                openGallery();
            } else {
                // Nếu quyền bị từ chối, hiển thị thông báo cho người dùng
                Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Hàm chọn ảnh từ Gallery
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));

    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED) {
            // Nếu chưa được cấp quyền, yêu cầu quyền
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_STORAGE_PERMISSION
            );
        } else {
            // Nếu đã có quyền, mở thư viện ảnh
            openGallery();
        }
    }

}
