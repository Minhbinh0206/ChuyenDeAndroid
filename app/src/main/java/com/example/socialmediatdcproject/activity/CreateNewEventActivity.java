package com.example.socialmediatdcproject.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.socialmediatdcproject.API.EventAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.model.Event;
import com.example.socialmediatdcproject.model.Group;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CreateNewEventActivity extends AppCompatActivity {
    private Uri selectedImageUri;
    private static final int MY_REQUEST_CODE = 10;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private ImageView imgFromGallery;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.create_event_layout);

        Intent intent = getIntent();
        int adminId = intent.getIntExtra("adminId", -1);

        Button cancle = findViewById(R.id.cancle_create);
        Button submit = findViewById(R.id.submit_create);

        imgFromGallery = findViewById(R.id.content_avatar_event);

        // Khi nhấn vào ảnh, chọn ảnh từ Gallery hoặc chụp ảnh bằng Camera
        imgFromGallery.setOnClickListener(v -> showImageSourceDialog());

        EditText titleEvent = findViewById(R.id.title_event);
        EditText contentEvent = findViewById(R.id.content_event);
        EditText point = findViewById(R.id.point);

        LinearLayout startButton = findViewById(R.id.start_button);
        LinearLayout finishButton = findViewById(R.id.finish_button);

        TextView startAt = findViewById(R.id.start_at);
        TextView finishAt = findViewById(R.id.finish_at);

        startButton.setOnClickListener(v -> showDateTimePicker(startAt));
        finishButton.setOnClickListener(v -> showDateTimePicker(finishAt));

        cancle.setOnClickListener(v -> finish());

        submit.setOnClickListener(v -> {
            String title = titleEvent.getText().toString();
            String content = contentEvent.getText().toString();
            String des = "+ " + point.getText().toString() + " điểm rèn luyện";

            // Lấy thời gian hiện tại
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
            String currentTime = sdf.format(new Date());

            String startTime = startAt.getText().toString();
            String finishTime = finishAt.getText().toString();

            submit.setEnabled(false);

            if (title.isEmpty() || content.isEmpty() || point.getText().toString().isEmpty() || startTime.isEmpty() || finishTime.isEmpty()) {
                // Hiển thị Toast nếu title hoặc content rỗng
                Toast.makeText(getApplicationContext(), "Vui lòng điền đầy đủ các dữ liệu trên", Toast.LENGTH_SHORT).show();

                // Bật lại nút submit để người dùng có thể thử lại
                submit.setEnabled(true);

                return; // Dừng tiếp tục xử lý nếu title hoặc content rỗng
            }

            // Tạo Dialog
            Dialog loadingDialog = new Dialog(CreateNewEventActivity.this);
            loadingDialog.setContentView(R.layout.dialog_loading);
            loadingDialog.setCancelable(false); // Không cho phép người dùng tắt dialog bằng cách bấm ngoài

            // Thêm ProgressBar vào layout của Dialog (layout: dialog_loading.xml)
            ProgressBar progressBar = loadingDialog.findViewById(R.id.progressBar);
            TextView textView = loadingDialog.findViewById(R.id.textLoading);
            textView.setText("Đang khởi tạo sự kiện ...");

            // Hiển thị Dialog
            loadingDialog.show();

            EventAPI eventAPI = new EventAPI();
            Event e = new Event();

            final boolean[] isEventAdded = {false};

            uploadImageToFirebase(selectedImageUri ,e);

            eventAPI.getAllEvents(new EventAPI.EventCallback() {
                @Override
                public void onEventReceived(Event event) {

                }

                @Override
                public void onEventsReceived(List<Event> events) {
                    if (!isEventAdded[0]) {
                        e.setEventId(events.size());
                        e.setAdminEventId(adminId);
                        e.setTitleEvent(title);
                        e.setContentEvent(content);
                        e.setDescription(des);
                        e.setCreateAt(currentTime);
                        e.setBeginAt(startTime);
                        e.setFinishAt(finishTime);
                        e.setStatus(0);

                        eventAPI.addEvent(e);

                        isEventAdded[0] = true;

                        Toast.makeText(getApplicationContext(), "Tạo sự kiện thành công", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

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

                                // Đặt ảnh làm background cho imgFromGallery
                                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                                imgFromGallery.setBackground(drawable);

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
                                    // Đặt ảnh làm background cho imgFromGallery
                                    Drawable drawable = new BitmapDrawable(getResources(), imageBitmap);
                                    imgFromGallery.setBackground(drawable);

                                    // Chuyển Bitmap thành Uri
                                    selectedImageUri = getImageUriFromBitmap(CreateNewEventActivity.this, imageBitmap);
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

            openGallery();

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

    // 3. Upload ảnh lên Firebase Storage
    private void uploadImageToFirebase(Uri imageUri, Event event) {
        if (imageUri == null) {
            // Lấy Uri của ảnh mặc định từ drawable
            imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.event_image_default);
        }

        // Tạo một tên file duy nhất
        String fileName = "eventImages/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(fileName);

        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String avatarUrl = uri.toString();

                event.setImageEvent(avatarUrl);

                EventAPI eventAPI = new EventAPI();
                eventAPI.updateEvent(event);

                // Chuyển sang GroupDetailActivity với groupId
                Intent intent = new Intent(CreateNewEventActivity.this, HomeAdminActivity.class);
                startActivity(intent);
                finish();

            }).addOnFailureListener(e -> {
                // Xử lý lỗi khi lấy URL
            });
        }).addOnFailureListener(e -> {
            // Xử lý lỗi khi upload ảnh
        });
    }

    private void showDateTimePicker(TextView targetTextView) {
        Calendar calendar = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        // Hiển thị DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    // Lưu ngày đã chọn
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Kiểm tra nếu ngày đã chọn là ngày quá khứ
                    if (calendar.before(now)) {
                        Toast.makeText(this, "Ngày bạn vừa chọn không hợp lệ", Toast.LENGTH_SHORT).show();
                        return; // Không tiếp tục hiển thị TimePickerDialog
                    }

                    // Hiển thị TimePickerDialog sau khi chọn ngày
                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (view1, hourOfDay, minute) -> {
                                // Lưu giờ đã chọn
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                // Kiểm tra nếu thời gian đã chọn là quá khứ
                                if (calendar.before(now)) {
                                    Toast.makeText(this, "Thời gian bạn chọn không hợp lệ", Toast.LENGTH_SHORT).show();
                                    return; // Không hiển thị thời gian đã chọn
                                }

                                // Hiển thị ngày và giờ đã chọn lên TextView
                                @SuppressLint("DefaultLocale")
                                String dateTime = String.format(
                                        "%02d-%02d-%d %02d:%02d",
                                        calendar.get(Calendar.DAY_OF_MONTH),
                                        calendar.get(Calendar.MONTH) + 1,
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE)
                                );

                                targetTextView.setText(dateTime);
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true);

                    // Nếu ngày đã chọn là ngày hiện tại, chặn giờ trong quá khứ
                    if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                            calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                            calendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)) {
                        timePickerDialog.updateTime(
                                Math.max(now.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.HOUR_OF_DAY)),
                                now.get(Calendar.MINUTE)
                        );
                    }
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        // Chặn ngày trong quá khứ
        datePickerDialog.getDatePicker().setMinDate(now.getTimeInMillis());
        datePickerDialog.show();
    }
}
