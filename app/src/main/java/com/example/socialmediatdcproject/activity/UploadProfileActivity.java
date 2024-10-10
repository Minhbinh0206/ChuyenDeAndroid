package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.MajorAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Major;
import com.example.socialmediatdcproject.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile_upload_layout);

        // Khởi tạo các view
        departmentSpinner = findViewById(R.id.department_infomation);
        majorSpinner = findViewById(R.id.major_infomation);
        yearInput = findViewById(R.id.year_born_info);
        monthInput = findViewById(R.id.month_born_info);
        dayInput = findViewById(R.id.day_born_info);
        Button buttonUploadProfile = findViewById(R.id.button_upload_profile);

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
            Intent intent = new Intent(UploadProfileActivity.this, SharedActivity.class);
            startActivity(intent);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

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
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupDateInput() {
        monthInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

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
            public void afterTextChanged(Editable s) {}
        });

        dayInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

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
            public void afterTextChanged(Editable s) {}
        });
    }

    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    private int getMaxDayForMonth(int month, int year) {
        switch (month) {
            case 2:
                return isLeapYear(year) ? 29 : 28;
            case 4: case 6: case 9: case 11:
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
}
