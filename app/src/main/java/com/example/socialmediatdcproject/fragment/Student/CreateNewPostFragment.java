package com.example.socialmediatdcproject.fragment.Student;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.NotifyQuicklyAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.CreateNewEventActivity;
import com.example.socialmediatdcproject.activity.CreateNewGroupActivity;
import com.example.socialmediatdcproject.activity.SharedActivity;
import com.example.socialmediatdcproject.activity.UploadProfileActivity;
import com.example.socialmediatdcproject.dataModels.NotifyQuickly;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CreateNewPostFragment extends Fragment {
    private static final int MY_REQUEST_CODE = 10;
    private static final int MY_CAMERA_REQUEST_CODE = 110;
    private int groupId;
    private Uri selectedImageUri;
    private ImageView showImagePost;
    // Camera
    private void onClickRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
    }

    // Mở thư viện ảnh
    private void onClickRequestGalleryPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.CUR_DEVELOPMENT ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUEST_CODE);
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
                Toast.makeText(getContext(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }

        // Xử lý quyền camera
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(getContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Mở cam
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mActivityResultLauncher.launch(cameraIntent);
    }

    //Mở Gallery
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    // ActivityResultLauncher cho Camera và Gallery
    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            showImagePost.setVisibility(View.VISIBLE);
                            // Kiểm tra nếu ảnh đến từ camera
                            if (data.hasExtra("data")) {
                                Bundle extras = data != null ? data.getExtras() : null;
                                if (extras != null) {
                                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                                    if (imageBitmap != null) {
                                        // Đặt ảnh làm background cho imgFromGallery
                                        Drawable drawable = new BitmapDrawable(getResources(), imageBitmap);
                                        showImagePost.setBackground(drawable);

                                        // Chuyển Bitmap thành Uri
                                        selectedImageUri = getImageUriFromBitmap(requireContext(), imageBitmap);
                                    }
                                }

                            } else {
                                // Nhận ảnh từ gallery
                                selectedImageUri = data.getData();
                                try {
                                    if (isAdded()) {
                                        // Hiển thị ảnh chọn từ Gallery
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);

                                        // Đặt ảnh làm background cho imgFromGallery
                                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                                        showImagePost.setBackground(drawable);

                                        selectedImageUri = getImageUriFromBitmap(requireContext(), bitmap);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

            }
    );

    // Hàm upload ảnh lên Firebase Storage từ URI
    private void uploadImageFromGallery(Uri uri) {
        // Tạo tham chiếu đến Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + System.currentTimeMillis() + ".jpg");

        // Upload ảnh lên Firebase Storage
        storageRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Ảnh đã được upload thành công
                    Toast.makeText(requireContext(), "Upload thành công", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(exception -> {
                    // Xử lý lỗi
                    Toast.makeText(requireContext(), "Upload thất bại: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private Uri getImageUriFromBitmap(Context context, Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "CameraImage", null);
        return Uri.parse(path);
    }

    // Tải ảnh lên Firebase và lưu URL vào post
    private void uploadImageToFirebaseStorage(Uri filePath, Post post, Dialog loadingDialog) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Đặt tên ảnh theo postId
        String imageName = FirebaseDatabase.getInstance().getReference().push().getKey();
        StorageReference postImageRef = storageRef.child("postImages/" + imageName);

        if (filePath != null) {
            postImageRef.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        postImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            post.setPostImage(downloadUrl);

                            PostAPI postAPI = new PostAPI();
                            postAPI.addPost(post);

                            // Hiển thị Toast thông báo thành công
                            Toast.makeText(getContext(), "Đăng bài thành công!", Toast.LENGTH_SHORT).show();

                            // Dismiss dialog sau khi bài viết được thêm
                            loadingDialog.dismiss();
                        });
                    })
                    .addOnFailureListener(exception -> {
                        Toast.makeText(requireContext(), "Tải ảnh thất bại: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
        else {
            post.setPostImage("");
            PostAPI postAPI = new PostAPI();
            postAPI.addPost(post);

            // Hiển thị Toast thông báo thành công
            Toast.makeText(getContext(), "Đăng bài thành công!", Toast.LENGTH_SHORT).show();

            // Dismiss dialog sau khi bài viết được thêm
            loadingDialog.dismiss();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView avatar = view.findViewById(R.id.avatar_user_create_post);
        showImagePost = view.findViewById(R.id.show_image_create_post);

        StudentAPI studentAPI = new StudentAPI();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
        AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();

        adminDepartmentAPI.getAdminDepartmentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                Glide.with(getContext())
                        .load(adminDepartment.getAvatar())
                        .circleCrop()
                        .into(avatar);
            }

            @Override
            public void onUsersReceived(List<AdminDepartment> adminDepartment) {

            }

            @Override
            public void onError(String s) {

            }
        });
        adminDefaultAPI.getAdminDefaultByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDefaultAPI.AdminDefaultCallBack() {
            @Override
            public void onUserReceived(AdminDefault adminDefault) {
                Glide.with(getContext())
                        .load(adminDefault.getAvatar())
                        .circleCrop()
                        .into(avatar);
            }

            @Override
            public void onUsersReceived(List<AdminDefault> adminDefault) {

            }
        });
        adminBusinessAPI.getAdminBusinessByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminBusinessAPI.AdminBusinessCallBack() {
            @Override
            public void onUserReceived(AdminBusiness adminBusiness) {
                Glide.with(getContext())
                        .load(adminBusiness.getAvatar())
                        .circleCrop()
                        .into(avatar);
            }

            @Override
            public void onUsersReceived(List<AdminBusiness> adminBusiness) {

            }

            @Override
            public void onError(String s) {

            }
        });
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                Glide.with(getContext())
                        .load(student.getAvatar())
                        .circleCrop()
                        .into(avatar);
            }

            @Override
            public void onStudentsReceived(List<Student> students) {

            }
        });

        TextView textView = view.findViewById(R.id.create_post_action);
        textView.setOnClickListener(v -> {
            showCustomDialog();
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showCustomDialog() {
        // Tạo Dialog
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.post_create_layout);

        // Tìm các view bên trong Dialog
        Button postButtonCreate = dialog.findViewById(R.id.button_post_user_create_new);
        Button postButtonCancle = dialog.findViewById(R.id.button_post_user_create_cancle);
        EditText postContent = dialog.findViewById(R.id.post_content);
        ToggleButton readonly = dialog.findViewById(R.id.custom_toggle);

        ImageButton addImage = dialog.findViewById(R.id.post_add_image);
        ImageButton changeBanckground = dialog.findViewById(R.id.post_change_background);
        ImageButton addSurvey = dialog.findViewById(R.id.post_icon_survey);

        Intent intent = requireActivity().getIntent();
        groupId = intent.getIntExtra("groupId", -1);

        //view hiển thị ảnh
        showImagePost = dialog.findViewById(R.id.show_image_create_post);
        showImagePost.setVisibility(View.GONE);

        ImageView imageViewAvatar = dialog.findViewById(R.id.avatar_user_create_post);

        // Xử lý sự kiện khi nhấn vào nút addImage
        addImage.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Chọn ảnh")
                    .setItems(new CharSequence[]{"Chọn từ thư viện", "Chụp ảnh mới"}, (dialogInterface, which) -> {
                        if (which == 0) {
                            onClickRequestGalleryPermission();

                        } else {
                            onClickRequestCameraPermission();
                        }
                    })
                    .show();
        });

        StudentAPI studentAPI = new StudentAPI();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
        AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();

        adminDepartmentAPI.getAdminDepartmentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                Glide.with(getContext())
                        .load(adminDepartment.getAvatar())
                        .circleCrop()
                        .into(imageViewAvatar);
                // Gán sự kiện cho nút "Post"
                postButtonCreate.setOnClickListener(v -> {

                    postButtonCreate.setEnabled(false);

                    String content = postContent.getText().toString();

                    // Kiểm tra xem title hoặc content có null hoặc rỗng không
                    if (content.isEmpty()) {
                        // Hiển thị Toast nếu title hoặc content rỗng
                        Toast.makeText(v.getContext(), "Vui lòng điền đầy đủ các dữ liệu trên", Toast.LENGTH_SHORT).show();

                        // Bật lại nút submit để người dùng có thể thử lại
                        postButtonCreate.setEnabled(true);

                        return; // Dừng tiếp tục xử lý nếu title hoặc content rỗng
                    }

                    boolean blockComment = readonly.isChecked();

                    // Tạo Dialog
                    Dialog loadingDialog = new Dialog(getContext());
                    loadingDialog.setContentView(R.layout.dialog_loading);
                    loadingDialog.setCancelable(false); // Không cho phép người dùng tắt dialog bằng cách bấm ngoài

                    // Thêm ProgressBar vào layout của Dialog (layout: dialog_loading.xml)
                    ProgressBar progressBar = loadingDialog.findViewById(R.id.progressBar);
                    TextView textView = loadingDialog.findViewById(R.id.textLoading);
                    textView.setText("Đang đăng bài...");

                    // Hiển thị Dialog
                    loadingDialog.show();

                    PostAPI postAPI = new PostAPI();
                    Post post = new Post();

                    final boolean[] isPostAdded = {false};
                    postAPI.getPostsByGroupId(groupId ,new PostAPI.PostCallback() {
                        @Override
                        public void onPostReceived(Post post) {

                        }

                        @Override
                        public void onPostsReceived(List<Post> posts) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

                            NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();
                            NotifyQuickly notifyQuickly = new NotifyQuickly();
                            notifyQuicklyAPI.getAllNotifications(new NotifyQuicklyAPI.NotificationCallback() {
                                @Override
                                public void onNotificationsReceived(List<NotifyQuickly> notifications) {
                                    GroupAPI groupAPI = new GroupAPI();
                                    groupAPI.getGroupById(groupId, new GroupAPI.GroupCallback() {
                                        @Override
                                        public void onGroupReceived(Group group) {
                                            if (!isPostAdded[0]) {
                                                if (adminDepartment.getUserId() == group.getAdminUserId()) {
                                                    post.setPostId(posts.size());
                                                    post.setUserId(adminDepartment.getUserId());
                                                    post.setPostLike(0);
                                                    post.setCommentAllow(blockComment);
                                                    post.setContent(content);
                                                    post.setStatus(Post.APPROVED);
                                                    post.setGroupId(groupId);
                                                    post.setCreatedAt(sdf.format(new Date()));

                                                } else {
                                                    post.setPostId(posts.size());
                                                    post.setUserId(adminDepartment.getUserId());
                                                    post.setPostLike(0);
                                                    post.setContent(content);
                                                    post.setCommentAllow(blockComment);
                                                    post.setFilter(false);
                                                    post.setStatus(Post.WAITING);
                                                    post.setGroupId(groupId);
                                                    post.setCreatedAt(sdf.format(new Date()));

                                                    notifyQuickly.setNotifyId(notifications.size());
                                                    notifyQuickly.setUserSendId(adminDepartment.getUserId());
                                                    notifyQuickly.setUserGetId(group.getAdminUserId());
                                                    notifyQuickly.setContent(adminDepartment.getFullName() + " vừa đăng bài mới và đang chờ bạn duuyệt!");
                                                    notifyQuicklyAPI.addNotification(notifyQuickly);
                                                }
                                                isPostAdded[0] = true; // Đánh dấu là đã thêm bài viết

                                                uploadImageToFirebaseStorage(selectedImageUri, post, loadingDialog);
                                            }
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onGroupsReceived(List<Group> groups) {

                                        }
                                    });
                                }
                            });
                        }
                    });
                });
                changeColorButtonActive(postButtonCreate);
                changeColorButtonActive(postButtonCancle);


                postButtonCancle.setOnClickListener(v -> {
                    dialog.dismiss();
                });

                // Cài đặt kích thước cho Dialog
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.comment_custom));

                // Thiết lập marginHorizontal 10dp cho Dialog
                int marginInDp = (int) (10 * getResources().getDisplayMetrics().density);
                if (dialog.getWindow() != null) {
                    // Lấy WindowManager.LayoutParams
                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.horizontalMargin = marginInDp / (float) getResources().getDisplayMetrics().widthPixels; // margin dưới dạng tỉ lệ so với chiều rộng màn hình

                    dialog.getWindow().setAttributes(params);
                }

                // Hiển thị Dialog
                dialog.show();
            }

            @Override
            public void onUsersReceived(List<AdminDepartment> adminDepartment) {

            }

            @Override
            public void onError(String s) {

            }
        });
        adminDefaultAPI.getAdminDefaultByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDefaultAPI.AdminDefaultCallBack() {
            @Override
            public void onUserReceived(AdminDefault adminDefault) {
                Glide.with(getContext())
                        .load(adminDefault.getAvatar())
                        .circleCrop()
                        .into(imageViewAvatar);
                // Gán sự kiện cho nút "Post"
                postButtonCreate.setOnClickListener(v -> {

                    postButtonCreate.setEnabled(false);

                    String content = postContent.getText().toString();

                    // Kiểm tra xem title hoặc content có null hoặc rỗng không
                    if (content.isEmpty()) {
                        // Hiển thị Toast nếu title hoặc content rỗng
                        Toast.makeText(v.getContext(), "Vui lòng điền đầy đủ các dữ liệu trên", Toast.LENGTH_SHORT).show();

                        // Bật lại nút submit để người dùng có thể thử lại
                        postButtonCreate.setEnabled(true);

                        return; // Dừng tiếp tục xử lý nếu title hoặc content rỗng
                    }

                    boolean blockComment = readonly.isChecked();

                    // Tạo Dialog
                    Dialog loadingDialog = new Dialog(getContext());
                    loadingDialog.setContentView(R.layout.dialog_loading);
                    loadingDialog.setCancelable(false); // Không cho phép người dùng tắt dialog bằng cách bấm ngoài

                    // Thêm ProgressBar vào layout của Dialog (layout: dialog_loading.xml)
                    ProgressBar progressBar = loadingDialog.findViewById(R.id.progressBar);
                    TextView textView = loadingDialog.findViewById(R.id.textLoading);
                    textView.setText("Đang đăng bài...");

                    // Hiển thị Dialog
                    loadingDialog.show();

                    PostAPI postAPI = new PostAPI();
                    Post post = new Post();

                    final boolean[] isPostAdded = {false};
                    postAPI.getPostsByGroupId(groupId ,new PostAPI.PostCallback() {
                        @Override
                        public void onPostReceived(Post post) {

                        }

                        @Override
                        public void onPostsReceived(List<Post> posts) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

                            NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();
                            NotifyQuickly notifyQuickly = new NotifyQuickly();
                            notifyQuicklyAPI.getAllNotifications(new NotifyQuicklyAPI.NotificationCallback() {
                                @Override
                                public void onNotificationsReceived(List<NotifyQuickly> notifications) {
                                    GroupAPI groupAPI = new GroupAPI();
                                    groupAPI.getGroupById(groupId, new GroupAPI.GroupCallback() {
                                        @Override
                                        public void onGroupReceived(Group group) {
                                            if (!isPostAdded[0]) {
                                                if (adminDefault.getUserId() == group.getAdminUserId()) {
                                                    post.setPostId(posts.size());
                                                    post.setUserId(adminDefault.getUserId());
                                                    post.setPostLike(0);
                                                    post.setCommentAllow(blockComment);
                                                    post.setContent(content);
                                                    post.setStatus(Post.APPROVED);
                                                    post.setGroupId(groupId);
                                                    post.setCreatedAt(sdf.format(new Date()));

                                                } else {
                                                    post.setPostId(posts.size());
                                                    post.setUserId(adminDefault.getUserId());
                                                    post.setPostLike(0);
                                                    post.setContent(content);
                                                    post.setCommentAllow(blockComment);
                                                    post.setFilter(false);
                                                    post.setStatus(Post.WAITING);
                                                    post.setGroupId(groupId);
                                                    post.setCreatedAt(sdf.format(new Date()));

                                                    notifyQuickly.setNotifyId(notifications.size());
                                                    notifyQuickly.setUserSendId(adminDefault.getUserId());
                                                    notifyQuickly.setUserGetId(group.getAdminUserId());
                                                    notifyQuickly.setContent(adminDefault.getFullName() + " vừa đăng bài mới và đang chờ bạn duuyệt!");
                                                    notifyQuicklyAPI.addNotification(notifyQuickly);
                                                }
                                                isPostAdded[0] = true; // Đánh dấu là đã thêm bài viết

                                                uploadImageToFirebaseStorage(selectedImageUri, post, loadingDialog);
                                            }
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onGroupsReceived(List<Group> groups) {

                                        }
                                    });
                                }
                            });
                        }
                    });
                });
                changeColorButtonActive(postButtonCreate);
                changeColorButtonActive(postButtonCancle);


                postButtonCancle.setOnClickListener(v -> {
                    dialog.dismiss();
                });

                // Cài đặt kích thước cho Dialog
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.comment_custom));

                // Thiết lập marginHorizontal 10dp cho Dialog
                int marginInDp = (int) (10 * getResources().getDisplayMetrics().density);
                if (dialog.getWindow() != null) {
                    // Lấy WindowManager.LayoutParams
                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.horizontalMargin = marginInDp / (float) getResources().getDisplayMetrics().widthPixels; // margin dưới dạng tỉ lệ so với chiều rộng màn hình

                    dialog.getWindow().setAttributes(params);
                }

                // Hiển thị Dialog
                dialog.show();
            }

            @Override
            public void onUsersReceived(List<AdminDefault> adminDefault) {

            }
        });
        adminBusinessAPI.getAdminBusinessByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminBusinessAPI.AdminBusinessCallBack() {
            @Override
            public void onUserReceived(AdminBusiness adminBusiness) {
                Glide.with(getContext())
                        .load(adminBusiness.getAvatar())
                        .circleCrop()
                        .into(imageViewAvatar);
                // Gán sự kiện cho nút "Post"
                postButtonCreate.setOnClickListener(v -> {

                    postButtonCreate.setEnabled(false);

                    String content = postContent.getText().toString();

                    // Kiểm tra xem title hoặc content có null hoặc rỗng không
                    if (content.isEmpty()) {
                        // Hiển thị Toast nếu title hoặc content rỗng
                        Toast.makeText(v.getContext(), "Vui lòng điền đầy đủ các dữ liệu trên", Toast.LENGTH_SHORT).show();

                        // Bật lại nút submit để người dùng có thể thử lại
                        postButtonCreate.setEnabled(true);

                        return; // Dừng tiếp tục xử lý nếu title hoặc content rỗng
                    }

                    boolean blockComment = readonly.isChecked();

                    // Tạo Dialog
                    Dialog loadingDialog = new Dialog(getContext());
                    loadingDialog.setContentView(R.layout.dialog_loading);
                    loadingDialog.setCancelable(false); // Không cho phép người dùng tắt dialog bằng cách bấm ngoài

                    // Thêm ProgressBar vào layout của Dialog (layout: dialog_loading.xml)
                    ProgressBar progressBar = loadingDialog.findViewById(R.id.progressBar);
                    TextView textView = loadingDialog.findViewById(R.id.textLoading);
                    textView.setText("Đang đăng bài...");

                    // Hiển thị Dialog
                    loadingDialog.show();

                    PostAPI postAPI = new PostAPI();
                    Post post = new Post();

                    final boolean[] isPostAdded = {false};
                    postAPI.getPostsByGroupId(groupId ,new PostAPI.PostCallback() {
                        @Override
                        public void onPostReceived(Post post) {

                        }

                        @Override
                        public void onPostsReceived(List<Post> posts) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

                            NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();
                            NotifyQuickly notifyQuickly = new NotifyQuickly();
                            notifyQuicklyAPI.getAllNotifications(new NotifyQuicklyAPI.NotificationCallback() {
                                @Override
                                public void onNotificationsReceived(List<NotifyQuickly> notifications) {
                                    GroupAPI groupAPI = new GroupAPI();
                                    groupAPI.getGroupById(groupId, new GroupAPI.GroupCallback() {
                                        @Override
                                        public void onGroupReceived(Group group) {
                                            if (!isPostAdded[0]) {
                                                if (adminBusiness.getUserId() == group.getAdminUserId()) {
                                                    post.setPostId(posts.size());
                                                    post.setUserId(adminBusiness.getUserId());
                                                    post.setPostLike(0);
                                                    post.setCommentAllow(blockComment);
                                                    post.setContent(content);
                                                    post.setStatus(Post.APPROVED);
                                                    post.setGroupId(groupId);
                                                    post.setCreatedAt(sdf.format(new Date()));

                                                } else {
                                                    post.setPostId(posts.size());
                                                    post.setUserId(adminBusiness.getUserId());
                                                    post.setPostLike(0);
                                                    post.setContent(content);
                                                    post.setCommentAllow(blockComment);
                                                    post.setFilter(false);
                                                    post.setStatus(Post.WAITING);
                                                    post.setGroupId(groupId);
                                                    post.setCreatedAt(sdf.format(new Date()));

                                                    notifyQuickly.setNotifyId(notifications.size());
                                                    notifyQuickly.setUserSendId(adminBusiness.getUserId());
                                                    notifyQuickly.setUserGetId(group.getAdminUserId());
                                                    notifyQuickly.setContent(adminBusiness.getFullName() + " vừa đăng bài mới và đang chờ bạn duuyệt!");
                                                    notifyQuicklyAPI.addNotification(notifyQuickly);
                                                }
                                                isPostAdded[0] = true; // Đánh dấu là đã thêm bài viết

                                                uploadImageToFirebaseStorage(selectedImageUri, post, loadingDialog);
                                            }
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onGroupsReceived(List<Group> groups) {

                                        }
                                    });
                                }
                            });
                        }
                    });
                });
                changeColorButtonActive(postButtonCreate);
                changeColorButtonActive(postButtonCancle);


                postButtonCancle.setOnClickListener(v -> {
                    dialog.dismiss();
                });

                // Cài đặt kích thước cho Dialog
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.comment_custom));

                // Thiết lập marginHorizontal 10dp cho Dialog
                int marginInDp = (int) (10 * getResources().getDisplayMetrics().density);
                if (dialog.getWindow() != null) {
                    // Lấy WindowManager.LayoutParams
                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.horizontalMargin = marginInDp / (float) getResources().getDisplayMetrics().widthPixels; // margin dưới dạng tỉ lệ so với chiều rộng màn hình

                    dialog.getWindow().setAttributes(params);
                }

                // Hiển thị Dialog
                dialog.show();
            }

            @Override
            public void onUsersReceived(List<AdminBusiness> adminBusiness) {

            }

            @Override
            public void onError(String s) {

            }
        });
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                Glide.with(getContext())
                        .load(student.getAvatar())
                        .circleCrop()
                        .into(imageViewAvatar);
                // Gán sự kiện cho nút "Post"
                postButtonCreate.setOnClickListener(v -> {

                    postButtonCreate.setEnabled(false);

                    String content = postContent.getText().toString();

                    // Kiểm tra xem title hoặc content có null hoặc rỗng không
                    if (content.isEmpty()) {
                        // Hiển thị Toast nếu title hoặc content rỗng
                        Toast.makeText(v.getContext(), "Vui lòng điền đầy đủ các dữ liệu trên", Toast.LENGTH_SHORT).show();

                        // Bật lại nút submit để người dùng có thể thử lại
                        postButtonCreate.setEnabled(true);

                        return; // Dừng tiếp tục xử lý nếu title hoặc content rỗng
                    }

                    boolean blockComment = readonly.isChecked();

                    // Tạo Dialog
                    Dialog loadingDialog = new Dialog(getContext());
                    loadingDialog.setContentView(R.layout.dialog_loading);
                    loadingDialog.setCancelable(false); // Không cho phép người dùng tắt dialog bằng cách bấm ngoài

                    // Thêm ProgressBar vào layout của Dialog (layout: dialog_loading.xml)
                    ProgressBar progressBar = loadingDialog.findViewById(R.id.progressBar);
                    TextView textView = loadingDialog.findViewById(R.id.textLoading);
                    textView.setText("Đang đăng bài...");

                    // Hiển thị Dialog
                    loadingDialog.show();

                    PostAPI postAPI = new PostAPI();
                    Post post = new Post();

                    final boolean[] isPostAdded = {false};
                    postAPI.getPostsByGroupId(groupId ,new PostAPI.PostCallback() {
                        @Override
                        public void onPostReceived(Post post) {

                        }

                        @Override
                        public void onPostsReceived(List<Post> posts) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

                            NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();
                            NotifyQuickly notifyQuickly = new NotifyQuickly();
                            notifyQuicklyAPI.getAllNotifications(new NotifyQuicklyAPI.NotificationCallback() {
                                @Override
                                public void onNotificationsReceived(List<NotifyQuickly> notifications) {
                                    GroupAPI groupAPI = new GroupAPI();
                                    groupAPI.getGroupById(groupId, new GroupAPI.GroupCallback() {
                                        @Override
                                        public void onGroupReceived(Group group) {
                                            if (!isPostAdded[0]) {
                                                if (student.getUserId() == group.getAdminUserId()) {
                                                    post.setPostId(posts.size());
                                                    post.setUserId(student.getUserId());
                                                    post.setPostLike(0);
                                                    post.setCommentAllow(blockComment);
                                                    post.setContent(content);
                                                    post.setStatus(Post.APPROVED);
                                                    post.setGroupId(groupId);
                                                    post.setCreatedAt(sdf.format(new Date()));

                                                } else {
                                                    post.setPostId(posts.size());
                                                    post.setUserId(student.getUserId());
                                                    post.setPostLike(0);
                                                    post.setContent(content);
                                                    post.setCommentAllow(blockComment);
                                                    post.setFilter(false);
                                                    post.setStatus(Post.WAITING);
                                                    post.setGroupId(groupId);
                                                    post.setCreatedAt(sdf.format(new Date()));

                                                    notifyQuickly.setNotifyId(notifications.size());
                                                    notifyQuickly.setUserSendId(student.getUserId());
                                                    notifyQuickly.setUserGetId(group.getAdminUserId());
                                                    notifyQuickly.setContent(student.getFullName() + " vừa đăng bài mới và đang chờ bạn duuyệt!");
                                                    notifyQuicklyAPI.addNotification(notifyQuickly);
                                                }
                                                isPostAdded[0] = true; // Đánh dấu là đã thêm bài viết

                                                uploadImageToFirebaseStorage(selectedImageUri, post, loadingDialog);
                                            }
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onGroupsReceived(List<Group> groups) {

                                        }
                                    });
                                }
                            });
                        }
                    });
                });
                changeColorButtonActive(postButtonCreate);
                changeColorButtonActive(postButtonCancle);


                postButtonCancle.setOnClickListener(v -> {
                    dialog.dismiss();
                });

                // Cài đặt kích thước cho Dialog
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.comment_custom));

                // Thiết lập marginHorizontal 10dp cho Dialog
                int marginInDp = (int) (10 * getResources().getDisplayMetrics().density);
                if (dialog.getWindow() != null) {
                    // Lấy WindowManager.LayoutParams
                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.horizontalMargin = marginInDp / (float) getResources().getDisplayMetrics().widthPixels; // margin dưới dạng tỉ lệ so với chiều rộng màn hình

                    dialog.getWindow().setAttributes(params);
                }

                // Hiển thị Dialog
                dialog.show();
            }

            @Override
            public void onStudentsReceived(List<Student> students) {

            }
        });
    }

    public void changeColorButtonActive(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
    }

    public void changeColorButtonNormal(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
    }
}
