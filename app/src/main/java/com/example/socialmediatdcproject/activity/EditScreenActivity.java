package com.example.socialmediatdcproject.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private ImageButton btnBack;
    private String userId;
//    private Student student;  // Khai báo student ở đây

    private static final int MY_CAMERA_REQUEST_CODE = 110;

    //Kiểm tra và yêu cầu quyền camera
    private void onClickRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
    }
    //Mở camera
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mActivityResultLauncher.launch(cameraIntent);
    }


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
//        editTextDescription = findViewById(R.id.editTextMota);
        buttonUpdate = findViewById(R.id.button_club_post);
         btnBack = findViewById(R.id.icon_back);

        // Thiết lập sự kiện nhấn
        btnBack.setOnClickListener(v -> finish()); // Đóng Activity khi nhấn nút

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
//                    editTextDescription.setText(student.getDescription());

                    // Hiển thị ảnh sử dụng Glide
                    Glide.with(EditScreenActivity.this)
                            .load(student.getAvatar())
                            .circleCrop()
                            .into(imageEditPersonal);

                    Glide.with(EditScreenActivity.this)
                            .load(student.getAvatar())
                            .circleCrop()
                            .into(imageEditPersonalSmall);

                    // Sự kiện click cập nhật thông tin
                    buttonUpdate.setOnClickListener(v -> {
                        String name = editTextName.getText().toString().trim();
                        String mssv = editTextMSSV.getText().toString().trim();
                        String className = editTextClass.getText().toString().trim();
                        String department = editTextDepartment.getText().toString().trim();
//                        String description = editTextDescription.getText().toString().trim();

                        if (name.isEmpty() || mssv.isEmpty() || className.isEmpty() || department.isEmpty()) {
                            Toast.makeText(EditScreenActivity.this, "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                        } else {
                            student.setFullName(name);
                            student.setDepartmentId(Integer.parseInt(department));
//                            student.setDescription(description);
                            student.setStudentNumber(mssv);
                            student.setStudentClass(className);

                            studentAPI.updateStudent(student, new StudentAPI.StudentCallback() {
                                @Override
                                public void onStudentReceived(Student student) {
                                    // Khoong lam gi
                                }

                                @Override
                                public void onStudentsReceived(List<Student> students) {

                                }

                                @Override
                                public void onError(String errorMessage) {

                                }

                                @Override
                                public void onStudentDeleted(int studentId) {

                                }
                            });

                            // Nếu người dùng đã chọn ảnh mới
                            if (selectedImageUri != null) {
                                uploadImageToFirebaseStorage(selectedImageUri, student);

                            } else {
                                // Cập nhật thông tin người dùng mà không cần đổi ảnh
                                saveStudentDataToDatabase(student);
                            }
                        }
                        //Hàm xử lý ảnh từ cam
                      //  uploadImageFromCamera( bitmap, student);
                    });
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
        imageEditPersonalSmall.setOnClickListener(v -> {
            showImageSourceDialog();
        });

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


//Xử lí ảnh từ thư viện
    private void uploadImageToFirebaseStorage(Uri filePath, Student student) {
        if (filePath != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            String imageName = "avatar_" + student.getStudentNumber() + ".jpg";
            StorageReference avatarRef = storageRef.child("avatar/" + imageName);

            // Xóa ảnh cũ nếu tồn tại trước khi upload ảnh mới
            avatarRef.delete().addOnSuccessListener(aVoid -> {
                // Sau khi ảnh cũ đã bị xóa, tải ảnh mới lên
                avatarRef.putFile(filePath)
                        .addOnSuccessListener(taskSnapshot -> {
                            avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                student.setAvatar(uri.toString());  // Cập nhật URL ảnh mới vào student object
                                saveStudentDataToDatabase(student);
                            });
                        })
                        .addOnFailureListener(exception -> {
                            Toast.makeText(EditScreenActivity.this, "Upload Failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }).addOnFailureListener(exception -> {
                // Nếu không có ảnh cũ hoặc xóa ảnh cũ thất bại, tiến hành upload ảnh mới luôn
                avatarRef.putFile(filePath)
                        .addOnSuccessListener(taskSnapshot -> {
                            avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                student.setAvatar(uri.toString());  // Cập nhật URL ảnh mới vào student object
                                saveStudentDataToDatabase(student);
                            });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(EditScreenActivity.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });
        }
    }

    //Xử lí ảnh từu cam
    private void uploadImageFromCamera(Bitmap bitmap, Student student) {
        // Lưu tạm ảnh vào bộ nhớ và chuyển thành Uri
        Uri tempUri = getImageUriFromBitmap(this, bitmap);

        // Gọi hàm upload lên Firebase Storage
        if (tempUri != null) {
            uploadImageToFirebaseStorage(tempUri, student);
        } else {
            Toast.makeText(EditScreenActivity.this, "Failed to get image URI from camera", Toast.LENGTH_SHORT).show();
        }
    }

    // Chuyển Bitmap thành Uri tạm
    private Uri getImageUriFromBitmap(Context context, Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "CameraImage", null);
        return Uri.parse(path);
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
            showPermissionDialog();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            showPermissionDialog();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUEST_CODE);
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
                Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }

        // Xử lý quyền camera
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //hiển thị thông báo cấp quyền
    // Hàm hiển thị bảng thông báo
    private void showPermissionDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_permission)
                .setCancelable(false) // Không cho phép đóng bằng cách chạm ra ngoài
                .create();

        dialog.show();

        Button btnAllow = dialog.findViewById(R.id.btn_allow);
        Button btnDeny = dialog.findViewById(R.id.btn_deny);

        btnAllow.setOnClickListener(v -> {
            // Nếu người dùng chọn cho phép, mở thư viện ảnh
            openGallery();
            dialog.dismiss();
        });

        btnDeny.setOnClickListener(v -> {
            // Nếu người dùng chọn không cho phép, hiển thị thông báo
            Toast.makeText(this, "Permission not granted. You can't access the gallery.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

//Mở thư viện ảnh
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    // Hàm xử lý kết quả từ camera hoặc gallery
    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            // Kiểm tra nếu ảnh đến từ camera
                            if (data.hasExtra("data")) {
                                // Nhận ảnh từ camera
                                Bundle extras = data.getExtras();
                                Bitmap imageBitmap = (Bitmap) extras.get("data");
                                imageEditPersonalSmall.setImageBitmap(imageBitmap);
                                imageEditPersonal.setImageBitmap(imageBitmap);

                                // Upload ảnh từ camera lên Firebase Storage
                               // uploadImageFromCamera(imageBitmap, imageBitmap);  // Sử dụng student
                            } else {
                                // Nhận ảnh từ gallery
                                selectedImageUri = data.getData();
                                try {
                                    // Hiển thị ảnh chọn từ Gallery
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                                    imageEditPersonalSmall.setImageBitmap(bitmap);
                                    imageEditPersonal.setImageBitmap(bitmap);

                                    // Upload ảnh từ gallery lên Firebase Storage
                                  //  uploadImageToFirebaseStorage(selectedImageUri, student);  // Sử dụng student
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

            }
    );

}
