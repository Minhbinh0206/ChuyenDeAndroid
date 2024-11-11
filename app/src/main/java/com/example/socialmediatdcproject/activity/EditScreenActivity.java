package com.example.socialmediatdcproject.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.NotifyQuicklyAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.NotifyQuicklyAdapter;
import com.example.socialmediatdcproject.dataModels.NotifyQuickly;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class EditScreenActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 10;
    private FirebaseAuth mAuth;
    private EditText editTextName, editTextMSSV, editTextClass, editTextDepartment, editTextDescription, editTextDoB , editTextPhone;
    private Button buttonUpdate;
    private Uri selectedImageUri;
    private Uri selectedBackgroup;
    private ImageView imageEditPersonal, imageEditPersonalSmall, imageEditBackgroupPersonalSmall, imageEditBackgroupPersonalBig ;
    private ImageButton btnBack;
    private String userId;
    private Student student;  // Khai báo student ở đây

    private static final int MY_CAMERA_REQUEST_CODE = 110;
    private static boolean MY_REQUEST_CODE_AVATAR = false;
    private static final int MY_CAMERA_REQUEST_CODE_Backgroud = 110;

    //Kiểm tra và yêu cầu quyền camera
    private void onClickRequestCameraPermission( ) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (MY_REQUEST_CODE_AVATAR) {
                openCameraAvatar();
            }
            else {
                openCameraBackround();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
    }
    //Mở camera
    private void openCameraAvatar() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mActivityResultLauncher.launch(cameraIntent);

    }
    //Mở camera
    private void openCameraBackround() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mActivityResultLauncherBackground.launch(cameraIntent);

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

        imageEditBackgroupPersonalBig = findViewById(R.id.image_anh_nen_big);
        imageEditBackgroupPersonalSmall = findViewById(R.id.image_anh_nen_small);

        editTextName = findViewById(R.id.editTextName);
        editTextMSSV = findViewById(R.id.editTextMSSV);
        editTextDoB = findViewById(R.id.editTextDoB);
        editTextPhone = findViewById(R.id.editTextPhone);

         editTextDescription = findViewById(R.id.editTextDescription);
        buttonUpdate = findViewById(R.id.button_club_post);
         btnBack = findViewById(R.id.icon_back);

        // Thiết lập sự kiện nhấn
        btnBack.setOnClickListener(v -> finish()); // Đóng Activity khi nhấn nút

        // Lấy thông tin người dùng từ Firebase
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(userId, new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                if (student != null) {
                    // Hiển thị dữ liệu lên các EditText
                    editTextName.setText(student.getFullName());
                    editTextMSSV.setText(student.getStudentNumber());
                    editTextDoB.setText(student.getBirthday());
                   editTextDescription.setText(student.getDescription());
                   editTextPhone.setText(student.getPhoneNumber());
                        // Hiển thị ảnh sử dụng Glide
                        Glide.with(EditScreenActivity.this)
                                .load(student.getAvatar())
                                .circleCrop()
                                .into(imageEditPersonal);

                    Log.d("Student", "onStudentReceived: " + student.getAvatar());

                        Glide.with(EditScreenActivity.this)
                                .load(student.getAvatar())
                                .circleCrop()
                                .into(imageEditPersonalSmall);

//                    Glide.with(EditScreenActivity.this)
//                            .load(student.getBackgroup())
//                            .into(imageEditBackgroupPersonalBig);
//
//                    Glide.with(EditScreenActivity.this)
//                            .load(student.getBackgroup())
//                            .circleCrop()
//                            .into(imageEditBackgroupPersonalSmall);

                    // Sự kiện click cập nhật thông tin
                    buttonUpdate.setOnClickListener(v -> {
                        String name = editTextName.getText().toString().trim();
                        String mssv = editTextMSSV.getText().toString().trim();
                        String dob = editTextDoB.getText().toString().trim();
                        String phone = editTextPhone.getText().toString().trim();
                       String description = editTextDescription.getText().toString().trim();

                        if (name.isEmpty() || mssv.isEmpty() || dob.isEmpty() || description.isEmpty() || phone.isEmpty()) {
                            Toast.makeText(EditScreenActivity.this, "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                        } else {
                            student.setFullName(name);
                           student.setDescription(description);
                           student.setBirthday(dob);
                            student.setStudentNumber(mssv);
                            student.setPhoneNumber(phone);

                            studentAPI.updateStudent(student, new StudentAPI.StudentCallback() {
                                @Override
                                public void onStudentReceived(Student student) {
                                    // Khoong lam gi
                                }

                                @Override
                                public void onStudentsReceived(List<Student> students) {

                                }
                            });

                            Log.d("TAG", "onStudentReceived: "  + selectedImageUri);
                            // Nếu người dùng đã chọn ảnh mới
                            if (selectedImageUri != null) {
                                if (MY_REQUEST_CODE_AVATAR) {
                                    uploadImageToFirebaseStorage(selectedImageUri, student);

                                }else {
                                    uploadImageToFirebaseStorageBackground(selectedImageUri, student);
                                }
                            } else {
                                // Cập nhật thông tin người dùng mà không cần đổi ảnh
                                saveStudentDataToDatabase(student);
                            }

                            //Nếu người dùng chon backgroud mới
//                            if (selectedBackgroup != null) {
//                                uploadImageToFirebaseStorage(selectedBackgroup, student);
//
//                            } else {
//                                // Cập nhật thông tin người dùng mà không cần đổi ảnh
//                                saveStudentDataToDatabase(student);
//                            }


                            // Nếu người dùng chọn backgroud mới

                        }
                        //Hàm xử lý ảnh từ cam
                      //  uploadImageFromCamera( bitmap, student);
                    });
                }
            }

            @Override
            public void onStudentsReceived(List<Student> students) {}
        });

        // Sự kiện click chọn ảnh từ thư viện
        imageEditPersonalSmall.setOnClickListener(v -> {
            showImageSourceDialog();
            MY_REQUEST_CODE_AVATAR = true;
        });

        imageEditBackgroupPersonalSmall.setOnClickListener( v -> {
            showImageSourceDialog();
            MY_REQUEST_CODE_AVATAR = false;
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
                                student.setAvatar(uri.toString());
                            //    student.setBackgroup(uri.toString());
                                // Cập nhật URL ảnh mới vào student object
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
                            //    student.setBackgroup(uri.toString());
                                saveStudentDataToDatabase(student);
                            });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(EditScreenActivity.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });
        }
    }

    //
    //Xử lí ảnh từ thư viện
    private void uploadImageToFirebaseStorageBackground(Uri filePath, Student student) {
        if (filePath != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            String imageName = "background" + student.getStudentNumber() + ".jpg";
            StorageReference avatarRef = storageRef.child("background/" + imageName);

            // Xóa ảnh cũ nếu tồn tại trước khi upload ảnh mới
            avatarRef.delete().addOnSuccessListener(aVoid -> {
                // Sau khi ảnh cũ đã bị xóa, tải ảnh mới lên
                avatarRef.putFile(filePath)
                        .addOnSuccessListener(taskSnapshot -> {
                            avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                student.setBackground(uri.toString());
                                //    student.setBackgroup(uri.toString());
                                // Cập nhật URL ảnh mới vào student object
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
                                student.setBackground(uri.toString());  // Cập nhật URL ảnh mới vào student object
                                //    student.setBackgroup(uri.toString());
                                saveStudentDataToDatabase(student);
                            });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(EditScreenActivity.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });
        }
    }

    //Xử lí ảnh từ cam
    private void uploadImageFromCamera(Bitmap bitmap) {
        // Lưu tạm ảnh vào bộ nhớ và chuyển thành Uri
        Uri tempUri = getImageUriFromBitmap(this, bitmap);

        // Gọi hàm upload lên Firebase Storage
        if (tempUri != null) {
            StudentAPI studentAPI = new StudentAPI();
            studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                @Override
                public void onStudentReceived(Student student1) {
                    uploadImageToFirebaseStorage(tempUri, student1);
                }

                @Override
                public void onStudentsReceived(List<Student> students) {

                }
            });
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

        SharedPreferences preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean isPermissionGranted = preferences.getBoolean("GalleryPermissionGranted", false);
        if (isPermissionGranted == true) {
            if (MY_REQUEST_CODE_AVATAR) {
                openGalleryAvatar();
            }
            ///Không dùng else, tạo 1 biến như trên ìf để làm
            else {
                openGalleryBackground();
            }
        }
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.CUR_DEVELOPMENT) {
            showPermissionDialog();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            showPermissionDialog();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUEST_CODE);
        }
    }

    //Hàm xử lý quyền để mở cam và thư viện
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Xử lý quyền đọc bộ nhớ
        if (requestCode == MY_REQUEST_CODE) {
            if (MY_REQUEST_CODE_AVATAR){
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGalleryAvatar();
                } else {
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
            }else {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGalleryBackground();
                } else {
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
            }

        }

        // Xử lý quyền camera
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (MY_REQUEST_CODE_AVATAR) {
                    openCameraAvatar();
                }
                else {
                    openCameraBackround();
                }
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }

        //Xử lí quyền camera cập nhật ảnh cho bâckgroud
    }

    // Hàm hiển thị bảng thông báo
    private void showPermissionDialog( ) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_permission)
                .setCancelable(false) // Không cho phép đóng bằng cách chạm ra ngoài
                .create();

        dialog.show();

        //
        Button btnOneOfTime = dialog.findViewById(R.id.btn_one_of_time);
        Button btnDeny = dialog.findViewById(R.id.btn_deny);
        Button btnAllow = dialog.findViewById(R.id.btn_allow);

        changeColorButtonNormal(btnAllow);
        changeColorButtonNormal(btnDeny);
        changeColorButtonNormal(btnOneOfTime);
        //
        SharedPreferences preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (MY_REQUEST_CODE_AVATAR) {
            btnAllow.setOnClickListener(v -> {
                //Nếu người dùng nhấn nó thì thiết bị sẽ mở quyền gallery và không hỏi lại nữa

                editor.putBoolean("GalleryPermissionGranted", true);
                editor.apply();
                openGalleryAvatar();
                dialog.dismiss();

            });

            btnOneOfTime.setOnClickListener(v -> {
                // Nếu người dùng chọn cho phép, mở thư viện ảnh
                openGalleryAvatar();
                dialog.dismiss();
            });

            btnDeny.setOnClickListener(v -> {
                // Nếu người dùng chọn không cho phép, hiển thị thông báo
                Toast.makeText(this, "Permission not granted. You can't access the gallery.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        }
        else {
            btnAllow.setOnClickListener(v -> {
                //Nếu người dùng nhấn nó thì thiết bị sẽ mở quyền gallery và không hỏi lại nữa

                editor.putBoolean("GalleryPermissionGranted", true);
                editor.apply();
                openGalleryBackground();
                dialog.dismiss();

            });

            btnOneOfTime.setOnClickListener(v -> {
                // Nếu người dùng chọn cho phép, mở thư viện ảnh
                openGalleryBackground();

                dialog.dismiss();
            });

            btnDeny.setOnClickListener(v -> {
                // Nếu người dùng chọn không cho phép, hiển thị thông báo
                Toast.makeText(this, "Permission not granted. You can't access the gallery.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        }

    }


//Mở thư viện ảnh
    private void openGalleryAvatar() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));

    }
    private void openGalleryBackground() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncherBackground.launch(Intent.createChooser(intent, "Select Picture"));
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

                                Glide.with(EditScreenActivity.this)
                                        .load(imageBitmap)
                                        .circleCrop()
                                        .into(imageEditPersonalSmall);

                                Glide.with(EditScreenActivity.this)
                                        .load(imageBitmap)
                                        .circleCrop()
                                        .into(imageEditPersonal);
                                // Chuyển đổi Bitmap thành Uri tạm
                                selectedImageUri = getImageUriFromBitmap(EditScreenActivity.this, imageBitmap);

                                // imageEditBackgroupPersonalBig.setImageBitmap(imageBitmap);
                              //  imageEditBackgroupPersonalSmall.setImageBitmap(imageBitmap);

                            } else {
                                // Nhận ảnh từ gallery
                                selectedImageUri = data.getData();
                              //  selectedBackgroup = data.getData();
                                try {
                                    // Hiển thị ảnh chọn từ Gallery
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
//                                    imageEditPersonalSmall.setImageBitmap(bitmap);
                                    Glide.with(EditScreenActivity.this)
                                            .load(bitmap)
                                            .circleCrop()
                                            .into(imageEditPersonalSmall);

                                    Glide.with(EditScreenActivity.this)
                                            .load(bitmap)
                                            .circleCrop()
                                            .into(imageEditPersonal);


                                    //Hiển thị hình cho Backgroud
//                                    Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedBackgroup);
//                                    imageEditBackgroupPersonalBig.setImageBitmap(bitmap1);
//                                   imageEditBackgroupPersonalSmall.setImageBitmap(bitmap1);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

            }
    );


//
private ActivityResultLauncher<Intent> mActivityResultLauncherBackground = registerForActivityResult(
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

                            Glide.with(EditScreenActivity.this)
                                    .load(imageBitmap)
                                    .circleCrop()
                                    .into(imageEditBackgroupPersonalBig);

                            Glide.with(EditScreenActivity.this)
                                    .load(imageBitmap)
                                    .circleCrop()
                                    .into(imageEditBackgroupPersonalSmall);
                            // Chuyển đổi Bitmap thành Uri tạm
                            selectedImageUri = getImageUriFromBitmap(EditScreenActivity.this, imageBitmap);
                        } else {
                            // Nhận ảnh từ gallery
                            selectedImageUri = data.getData();
                            //  selectedBackgroup = data.getData();
                            try {
                                // Hiển thị ảnh chọn từ Gallery
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
//                                    imageEditPersonalSmall.setImageBitmap(bitmap);
                                Glide.with(EditScreenActivity.this)
                                        .load(bitmap)
                                        .into(imageEditBackgroupPersonalBig);

                                Glide.with(EditScreenActivity.this)
                                        .load(bitmap)
                                        .circleCrop()
                                        .into(imageEditBackgroupPersonalSmall);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

        }
);

    private void showNotifyQuicklyPopup(int id ,List<NotifyQuickly> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return;  // Nếu không có thông báo thì không làm gì
        }

        // Tạo View từ layout của PopupWindow
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.notify_quickly_popup, null);

        // Tạo PopupWindow
        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true); // Thiết lập true để cho phép tắt PopupWindow khi nhấn bên ngoài

        // Thiết lập RecyclerView bên trong PopupWindow
        RecyclerView recyclerView = popupView.findViewById(R.id.recycler_notify_quickly);
        NotifyQuicklyAdapter notifyQuicklyAdapter = new NotifyQuicklyAdapter(new ArrayList<>());
        recyclerView.setAdapter(notifyQuicklyAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(EditScreenActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Hiển thị PopupWindow ở vị trí mong muốn
        View notifyButton = findViewById(R.id.icon_back);
        popupWindow.showAsDropDown(notifyButton, 0, 0);

        // Handler để hiển thị từng thông báo tuần tự
        final Handler handler = new Handler();
        final int[] currentIndex = {0}; // Chỉ mục hiện tại của thông báo

        // Hàm để cập nhật thông báo hiện tại
        Runnable updateNotification = new Runnable() {
            @Override
            public void run() {
                if (currentIndex[0] < notifications.size()) {
                    // Cập nhật danh sách thông báo với thông báo hiện tại
                    NotifyQuickly currentNotification = notifications.get(currentIndex[0]);
                    notifyQuicklyAdapter.clearNotifications(); // Xóa thông báo cũ
                    notifyQuicklyAdapter.addNotification(currentNotification); // Thêm thông báo mới
                    notifyQuicklyAdapter.notifyDataSetChanged();

                    // Gọi API để xóa thông báo khỏi Firebase
                    NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();
                    notifyQuicklyAPI.deleteNotification(id ,currentNotification.getNotifyId()); // Gọi phương thức xóa với ID của thông báo

                    // Tăng chỉ mục lên cho thông báo tiếp theo
                    currentIndex[0]++;

                    // Gọi lại runnable sau 5 giây để hiển thị thông báo tiếp theo
                    handler.postDelayed(this, 5000); // Điều chỉnh thời gian nếu cần
                } else {
                    // Đã hiển thị hết thông báo, đóng PopupWindow
                    popupWindow.dismiss();
                }
            }
        };

        // Bắt đầu hiển thị thông báo đầu tiên sau khi popup được mở
        handler.post(updateNotification);
    }

    public void changeColorButtonNormal(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(EditScreenActivity.this, R.color.white));
        btn.setTextColor(ContextCompat.getColorStateList(EditScreenActivity.this, R.color.defaultBlue));
    }
    @Override
    protected void onResume() {
        super.onResume();
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();

                // Thiết lập listener để theo dõi thông báo cho người dùng hiện tại
                notifyQuicklyAPI.setNotificationListener(student.getUserId(), new NotifyQuicklyAPI.NotificationCallback() {
                    @Override
                    public void onNotificationsReceived(List<NotifyQuickly> notifications) {
                        // Hiển thị thông báo nhanh qua PopupWindow
                        showNotifyQuicklyPopup(student.getUserId(), notifications);
                    }
                });
            }

            @Override
            public void onStudentsReceived(List<Student> students) {}
        });
    }
}
