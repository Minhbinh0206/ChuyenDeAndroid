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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
public class EditScreenActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 10;
    private FirebaseAuth mAuth;
    private EditText editTextName, editTextMSSV, editTextClass, editTextDepartment, editTextDescription;
    private Button buttonUpdate;
    private Uri selectedImageUri;
    private ImageView imageEditPersonal, imageEditPersonalSmall;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_screen_layout);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid(); // Lấy userId dạng chuỗi

        // Tìm các view từ layout
        imageEditPersonal = findViewById(R.id.image_edit_personal);
        imageEditPersonalSmall = findViewById(R.id.image_edit_personal_small);
        editTextName = findViewById(R.id.editTextName);
        editTextMSSV = findViewById(R.id.editTextMSSV);
        editTextClass = findViewById(R.id.editTextLop);
        editTextDepartment = findViewById(R.id.editTextKhoa);
        editTextDescription = findViewById(R.id.editTextMota);
        buttonUpdate = findViewById(R.id.button_club_post);

        // Lấy thông tin người dùng từ Firebase
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(userId, new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                if (student != null && student.getAvatar() != null) {
                    // Hiển thị dữ liệu lên các EditText
                    editTextName.setText(student.getFullName());
                    editTextMSSV.setText(student.getStudentNumber());
                    editTextClass.setText(student.getStudentClass());
                    editTextDepartment.setText(String.valueOf(student.getDepartmentId()));
                    editTextDescription.setText(student.getDescription());

                    // Hiển thị ảnh sử dụng Glide
                    Glide.with(EditScreenActivity.this)
                            .load(student.getAvatar())
                            .circleCrop()
                            .into(imageEditPersonal);

                    Glide.with(EditScreenActivity.this)
                            .load(student.getAvatar())
                            .circleCrop()
                            .into(imageEditPersonalSmall);
                }
            }

            @Override
            public void onStudentsReceived(List<Student> students) {}

            @Override
            public void onError(String errorMessage) {}

            @Override
            public void onStudentDeleted(int studentId) {}
        });

        // Sự kiện click chọn ảnh từ thư viện
        imageEditPersonalSmall.setOnClickListener(v -> onClickRequestPermission());

        // Sự kiện click cập nhật thông tin
        buttonUpdate.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String mssv = editTextMSSV.getText().toString().trim();
            String className = editTextClass.getText().toString().trim();
            String department = editTextDepartment.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();

            if (name.isEmpty() || mssv.isEmpty() || className.isEmpty() || department.isEmpty()) {
                Toast.makeText(EditScreenActivity.this, "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            } else {
                updateStudentInfo(name, mssv, className, department, description);
            }
        });
    }

    private void updateStudentInfo(String name, String mssv, String className, String department, String description) {
        // Tạo đối tượng Student
        Student student = new Student();
        student.setFullName(name);
        student.setStudentNumber(mssv);
        student.setStudentClass(className);
        student.setDepartmentId(Integer.parseInt(department));
        student.setDescription(description);

        // Nếu người dùng đã chọn ảnh mới
        if (selectedImageUri != null) {
            uploadImageToFirebaseStorage(selectedImageUri, student);
        } else {
            // Cập nhật thông tin người dùng mà không cần đổi ảnh
            saveStudentDataToDatabase(student);
        }
    }

    private void uploadImageToFirebaseStorage(Uri filePath, Student student) {
        if (filePath != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            String imageName = "avatar_" + System.currentTimeMillis() + ".jpg";
            StorageReference avatarRef = storageRef.child("avatars/" + imageName);

            avatarRef.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            student.setAvatar(uri.toString());  // Lưu URL ảnh vào student object
                            saveStudentDataToDatabase(student);
                        });
                    })
                    .addOnFailureListener(exception -> {
                        Toast.makeText(EditScreenActivity.this, "Upload Failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveStudentDataToDatabase(Student student) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Students").child(userId);
        userRef.setValue(student)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditScreenActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        finish();  // Quay về màn hình trước đó
                    } else {
                        Toast.makeText(EditScreenActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onClickRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.CUR_DEVELOPMENT) {
            openGallery();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        selectedImageUri = data.getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            imageEditPersonalSmall.setImageBitmap(bitmap);
                            imageEditPersonal.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
}
