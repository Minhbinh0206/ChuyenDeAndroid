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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class CreateNewGroupActivity extends AppCompatActivity {
    private int adminUserId;
    private static final int MY_REQUEST_CODE = 10;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 2 ;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private ImageView imgFromGallery;
    private ImageView selectImage;
    private Uri selectedImageUri;
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
                                    selectedImageUri = getImageUriFromBitmap(CreateNewGroupActivity.this, imageBitmap);

                                    // Upload ảnh lên Firebase Storage
//                                    uploadImageToFirebaseStorage(imageUri, userId, student); // Gọi hàm upload ảnh với userId và student
                                }
                            }
                        }
                    }
                }
            }
    );

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



    ///////////
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.create_new_group_layout);

        Button cancleAction = findViewById(R.id.cancle_create);
        Button submitAction = findViewById(R.id.submit_create);
        EditText fieldNameGroup = findViewById(R.id.content_name_group);
        Switch fieldIsPrivate = findViewById(R.id.content_private_group);
        imgFromGallery = findViewById(R.id.content_avatar_group); // Ánh xạ hình ảnh từ layout

        // Khi nhấn vào ảnh, chọn ảnh từ Gallery hoặc chụp ảnh bằng Camera
        imgFromGallery.setOnClickListener(v -> showImageSourceDialog());

        // Lấy user từ Firebase
        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(key, new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                adminUserId = student.getUserId();
            }

            @Override
            public void onStudentsReceived(List<Student> students) {}

            @Override
            public void onError(String errorMessage) {}

            @Override
            public void onStudentDeleted(int studentId) {}
        });

        // Hủy tạo nhóm
        cancleAction.setOnClickListener(v -> finish());

        // Xác nhận tạo nhóm
        submitAction.setOnClickListener(v -> {
            String nameGroup = fieldNameGroup.getText().toString();
            String avatar = selectedImageUri != null ? selectedImageUri.toString() : ""; // Lấy URI của ảnh được chọn
            boolean isPrivate = fieldIsPrivate.isChecked();

            GroupAPI groupAPI = new GroupAPI();
            Group g = new Group();

            GroupUserAPI groupUserAPI = new GroupUserAPI();
            GroupUser groupUser = new GroupUser();

            // Lấy danh sách nhóm để lấy groupId mới
            groupAPI.getAllGroups(new GroupAPI.GroupCallback() {
                @Override
                public void onGroupReceived(Group group) {}

                @Override
                public void onGroupsReceived(List<Group> groups) {
                    int lastId = groups.size();
                    g.setGroupId(lastId);
                    g.setGroupName(nameGroup);
                    g.setAdminUserId(adminUserId);
                    g.setPrivate(isPrivate);
                    g.setAvatar(avatar); // Đặt URI ảnh làm avatar của nhóm

                    // Thêm nhóm vào cơ sở dữ liệu
                    groupAPI.addGroup(g);
                    groupUser.setUserId(adminUserId);
                    groupUser.setGroupId(g.getGroupId());
                    groupUserAPI.addGroupUser(groupUser);

                    boolean isJoin = true;

                    // Chuyển sang GroupDetailActivity với groupId
                    Intent intent = new Intent(CreateNewGroupActivity.this, SharedActivity.class);
                    intent.putExtra("groupId", g.getGroupId());
                    intent.putExtra("isJoin", isJoin);
                    startActivity(intent);
                }
            });
        });
    }

}



