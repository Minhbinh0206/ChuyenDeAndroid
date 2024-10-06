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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.database.DepartmentDatabase;
import com.example.socialmediatdcproject.database.MajorDatabase;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Major;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile_upload_layout);

        // Xử lý spinner các ngành và khoa
        Spinner department = findViewById(R.id.department_infomation);
        Spinner major = findViewById(R.id.major_infomation);

        // Danh sách các tùy chọn
        DepartmentDatabase departmentDatabase = new DepartmentDatabase();
        MajorDatabase majorDatabase = new MajorDatabase();

        List<String> optionsDepartment = new ArrayList<>();
        List<String> optionsMajor = new ArrayList<>();

        // Lấy danh sách tên các khoa
        for (Department d: departmentDatabase.dataDepartments()) {
            optionsDepartment.add(d.getDepartmentName());
        }

        // Tạo ArrayAdapter cho Khoa
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, optionsDepartment);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        department.setAdapter(departmentAdapter);

        // Xử lý khi người dùng chọn Khoa
        department.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedKhoa = optionsDepartment.get(position);

                // Làm rỗng danh sách ngành trước khi thêm mới
                optionsMajor.clear();

                for (int majorId : departmentDatabase.getDepartmentByName(selectedKhoa, departmentDatabase.dataDepartments()).getMajorId()) {
                    Major major1 = majorDatabase.getMajorById(majorId, majorDatabase.dataMajors());
                    optionsMajor.add(major1.getMajorName());
                }

                // Tạo ArrayAdapter cho Ngành
                ArrayAdapter<String> majorAdapter = new ArrayAdapter<>(UploadProfileActivity.this, android.R.layout.simple_spinner_item, optionsMajor);
                majorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                major.setAdapter(majorAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                optionsMajor.clear();
            }
        });


        // Xử lý chọn năm tối đa
        EditText yearInput = findViewById(R.id.year_born_info);
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
                            Toast.makeText(getApplicationContext(), "Năm bạn nhập vào không hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Xử lý chọn tháng và ngày
        EditText monthInput = findViewById(R.id.month_born_info);
        EditText dayInput = findViewById(R.id.day_born_info);

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
                            // Cập nhật số ngày tối đa dựa trên tháng
                            updateMaxDay(enteredMonth, dayInput, yearInput);
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
                        // Kiểm tra số ngày hợp lệ
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

        // Xử lý chuyển trang qua trang home
        Button buttonUploadProfile = findViewById(R.id.button_upload_profile);
        buttonUploadProfile.setOnClickListener(v -> {
            Intent intent = new Intent(UploadProfileActivity.this, SharedActivity.class);
            startActivity(intent);
        });
    }

    // Hàm kiểm tra xem năm có phải năm nhuận hay không
    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    // Hàm trả về số ngày tối đa của tháng dựa trên tháng và năm
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

    // Cập nhật số ngày tối đa dựa trên tháng và năm
    private void updateMaxDay(int month, EditText dayInput, EditText yearInput) {
        int year = Integer.parseInt(yearInput.getText().toString());
        int maxDay = getMaxDayForMonth(month, year);
        int enteredDay = Integer.parseInt(dayInput.getText().toString().isEmpty() ? "1" : dayInput.getText().toString());
        if (enteredDay > maxDay) {
            dayInput.setText(String.valueOf(maxDay));
        }
    }

    //Cập nhật hình ảnh
}
