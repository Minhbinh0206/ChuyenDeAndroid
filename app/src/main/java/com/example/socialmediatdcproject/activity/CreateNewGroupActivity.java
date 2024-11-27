package com.example.socialmediatdcproject.activity;

import android.Manifest;
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
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
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

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.QuestionAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.dataModels.Question;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CreateNewGroupActivity extends AppCompatActivity {
    private int adminUserId;
    private static final int MY_REQUEST_CODE = 10;
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
                                    selectedImageUri = getImageUriFromBitmap(CreateNewGroupActivity.this, imageBitmap);
                                }
                            }
                        }
                    }
                }
            }
    );


    // 3. Upload ảnh lên Firebase Storage
    private void uploadImageToFirebase(Uri imageUri, Group group) {
        if (imageUri == null) {
            // Lấy Uri của ảnh mặc định từ drawable
            imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.avatar_group_default);
        }

        // Tạo một tên file duy nhất
        String fileName = "groupAvatars/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(fileName);

        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String avatarUrl = uri.toString();

                group.setAvatar(avatarUrl);

                GroupAPI groupAPI = new GroupAPI();
                groupAPI.updateGroup(group.getGroupId(), group);

                // Chuyển sang GroupDetailActivity với groupId
                Intent intent = new Intent(CreateNewGroupActivity.this, GroupDetaiActivity.class);
                intent.putExtra("groupId", group.getGroupId());
                startActivity(intent);
                finish();

            }).addOnFailureListener(e -> {
                // Xử lý lỗi khi lấy URL
            });
        }).addOnFailureListener(e -> {
            // Xử lý lỗi khi upload ảnh
        });
    }

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.create_new_group_layout);

        LinearLayout linearLayout = findViewById(R.id.parent_survey_group);
        ImageButton swapQuestion = findViewById(R.id.button_change_question);
        EditText fieldQuestion = findViewById(R.id.content_question_group);
        Button cancleAction = findViewById(R.id.cancle_create);
        Button submitAction = findViewById(R.id.submit_create);
        EditText fieldNameGroup = findViewById(R.id.content_name_group);
        Switch fieldIsPrivate = findViewById(R.id.content_private_group);
        imgFromGallery = findViewById(R.id.content_avatar_group); // Ánh xạ hình ảnh từ layout

        // Khi nhấn vào ảnh, chọn ảnh từ Gallery hoặc chụp ảnh bằng Camera
        imgFromGallery.setOnClickListener(v -> showImageSourceDialog());

        ArrayList<String> optionQuestionDefault = new ArrayList<>();
        String q1 = "Bạn biết đến nhóm này từ đâu ?";
        String q2 = "Mục đích bạn muốn tham gia nhóm này là gì ?";
        String q3 = "Bạn có kinh nghiệm hoặc kiến thức gì liên quan đến chủ đề của nhóm không ?";
        String q4 = "Bạn có đồng ý tuân thủ các quy định của nhóm không ?";
        String q5 = "Bạn có tham gia nhóm hoặc cộng đồng trực tuyến nào khác tương tự không? Nếu có, là nhóm nào?";
        String q6 = "Bạn sẽ đóng góp như thế nào vào nhóm nếu được chấp nhận ?";
        String q7 = "Bạn có thể giữ sự riêng tư và bảo mật trong nhóm không ?";
        String q8 = "Bạn có từng vi phạm các quy định trong các nhóm trước đây không ?Nếu có, bạn có thể chia sẻ lý do ?";
        String q9 = "Bạn sẵn sàng tham gia các cuộc thảo luận và hoạt động nhóm không ?";
        String q10 = "Bạn có câu hỏi hoặc điều gì cần làm rõ về nhóm này không ?";
        optionQuestionDefault.add(q1);
        optionQuestionDefault.add(q2);
        optionQuestionDefault.add(q3);
        optionQuestionDefault.add(q4);
        optionQuestionDefault.add(q5);
        optionQuestionDefault.add(q6);
        optionQuestionDefault.add(q7);
        optionQuestionDefault.add(q8);
        optionQuestionDefault.add(q9);
        optionQuestionDefault.add(q10);

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
        });

        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        adminDepartmentAPI.getAdminDepartmentByKey(key, new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                adminUserId = adminDepartment.getUserId();
            }

            @Override
            public void onUsersReceived(List<AdminDepartment> adminDepartment) {

            }

            @Override
            public void onError(String s) {

            }
        });

        AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
        adminBusinessAPI.getAdminBusinessByKey(key, new AdminBusinessAPI.AdminBusinessCallBack() {
            @Override
            public void onUserReceived(AdminBusiness adminBusiness) {
                adminUserId = adminBusiness.getUserId();
            }

            @Override
            public void onUsersReceived(List<AdminBusiness> adminBusiness) {

            }

            @Override
            public void onError(String s) {

            }
        });

        AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();
        adminDefaultAPI.getAdminDefaultByKey(key, new AdminDefaultAPI.AdminDefaultCallBack() {
            @Override
            public void onUserReceived(AdminDefault adminDefault) {
                adminUserId = adminDefault.getUserId();
            }

            @Override
            public void onUsersReceived(List<AdminDefault> adminDefault) {

            }
        });


        fieldIsPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    linearLayout.setVisibility(View.VISIBLE);
                }
                else {
                    linearLayout.setVisibility(View.GONE);
                }
            }
        });

        // Hủy tạo nhóm
        cancleAction.setOnClickListener(v -> finish());

        swapQuestion.setOnClickListener(v -> {
            fieldQuestion.setText(optionQuestionDefault.get(getRandomNumber()));
        });

        // Xác nhận tạo nhóm
        submitAction.setOnClickListener(v -> {
            String nameGroup = fieldNameGroup.getText().toString();
            boolean isPrivate = fieldIsPrivate.isChecked();

            if (nameGroup.isEmpty()) {
                // Hiển thị Toast nếu title hoặc content rỗng
                Toast.makeText(getApplicationContext(), "Vui lòng điền đầy đủ các dữ liệu trên", Toast.LENGTH_SHORT).show();

                // Bật lại nút submit để người dùng có thể thử lại
                submitAction.setEnabled(true);

                return; // Dừng tiếp tục xử lý nếu title hoặc content rỗng
            }else if (isPrivate){
                if (fieldQuestion.getText().toString().isEmpty()) {
                    // Hiển thị Toast nếu title hoặc content rỗng
                    Toast.makeText(getApplicationContext(), "Vui lòng điền đầy đủ các dữ liệu trên", Toast.LENGTH_SHORT).show();

                    // Bật lại nút submit để người dùng có thể thử lại
                    submitAction.setEnabled(true);

                    return; // Dừng tiếp tục xử lý nếu title hoặc content rỗng
                }
            }

            // Tạo Dialog
            Dialog loadingDialog = new Dialog(CreateNewGroupActivity.this);
            loadingDialog.setContentView(R.layout.dialog_loading);
            loadingDialog.setCancelable(false); // Không cho phép người dùng tắt dialog bằng cách bấm ngoài

            // Thêm ProgressBar vào layout của Dialog (layout: dialog_loading.xml)
            ProgressBar progressBar = loadingDialog.findViewById(R.id.progressBar);
            TextView textView = loadingDialog.findViewById(R.id.textLoading);
            textView.setText("Đang khởi tạo nhóm ...");

            // Hiển thị Dialog
            loadingDialog.show();

            submitAction.setEnabled(false);


            GroupAPI groupAPI = new GroupAPI();
            Group g = new Group();

            uploadImageToFirebase(selectedImageUri ,g);

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
                    g.setGroupDefault(false);
                    g.setPrivate(isPrivate);

                    // Thêm nhóm vào cơ sở dữ liệu
                    groupAPI.addGroup(g);

                    if (isPrivate){
                        Question question = new Question();
                        QuestionAPI questionAPI = new QuestionAPI();
                        questionAPI.getAllQuestions(new QuestionAPI.QuestionCallback() {
                            @Override
                            public void onQuestionReceived(Question question) {

                            }

                            @Override
                            public void onQuestionsReceived(List<Question> questions) {
                                question.setQuestionId(questions.size());
                                question.setGroupId(g.getGroupId());
                                question.setGroupQuestion(fieldQuestion.getText().toString());

                                questionAPI.addQuestion(question);
                            }
                        });
                    }

                    groupUser.setUserId(adminUserId);
                    groupUser.setGroupId(g.getGroupId());
                    groupUserAPI.addGroupUser(groupUser);
                }
            });
        });
    }
    public static int getRandomNumber() {
        Random random = new Random();
        // nextInt(10) sẽ trả về giá trị ngẫu nhiên trong khoảng từ 0 đến 9
        return random.nextInt(10);
    }

}



