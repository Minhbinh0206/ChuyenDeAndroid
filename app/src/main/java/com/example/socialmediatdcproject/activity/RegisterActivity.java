package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText studentNumber, emailEditText, passwordEditText, passwordConfirmEditText;
    private FirebaseAuth auth;
    private Handler handler;
    private Runnable verificationRunnable;
    private static final int VERIFICATION_CHECK_INTERVAL = 15000;
    private String tdcEmail = "@mail.tdc.edu.vn";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        // Khởi tạo FirebaseAuth
        auth = FirebaseAuth.getInstance();
        handler = new Handler(Looper.getMainLooper());

        // Khai báo Firebase Database reference
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Students");

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isEmailVerificationPending = sharedPreferences.getBoolean("isEmailVerificationPending", false);
        if (isEmailVerificationPending) {
            // Lấy email và studentNumber từ SharedPreferences
            String email = sharedPreferences.getString("registerEmail", "");
            String password = sharedPreferences.getString("registerPassword", "");
            String studentNumber = sharedPreferences.getString("registerStudentNumber", "");

            // Kiểm tra trạng thái xác thực của người dùng
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                showVerificationDialog(user, email, password, studentNumber);
            }
        }
        else{
            initView();

            // Xử lý chức năng đăng ký
            Button buttonRegister = findViewById(R.id.buttonRegister);
            buttonRegister.setOnClickListener(v -> {

                String sNumber = studentNumber.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String passwordConfirm = passwordConfirmEditText.getText().toString().trim();

                // Kiểm tra thông tin đầu vào
                if (sNumber.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(passwordConfirm)) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra mssv đã tồn tại chưa
                database.orderByChild("studentNumber").equalTo(sNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(RegisterActivity.this, "MSSV đã tồn tại", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            FirebaseUser user = auth.getCurrentUser();
                            if(user != null){
                                showVerificationDialog(user, email , password , sNumber);
                            }
                            else {
                                registerUser(email, password , sNumber);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RegisterActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }

    private void startEmailVerificationChecker(String email, String password, String studentNumber) {
        verificationRunnable = () -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                user.reload().addOnCompleteListener(reloadTask -> {
                    if (reloadTask.isSuccessful() && user.isEmailVerified()) {
                        saveUserDataAndProceed(email, password, studentNumber);
                    } else {
                        sendVerificationEmail(user);
//                        Toast.makeText(this, "Email chưa xác thực!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            handler.postDelayed(verificationRunnable, VERIFICATION_CHECK_INTERVAL);  // Tiếp tục kiểm tra sau 30 giây
        };
        handler.post(verificationRunnable);  // Bắt đầu chạy kiểm tra lần đầu tiên
    }


    // Đăng ký người dùng
    private void registerUser(String email, String password , String sNumber) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification().addOnCompleteListener(verificationTask -> {
                                if (verificationTask.isSuccessful()) {
                                    // Hiển thị thông báo hoặc Dialog yêu cầu xác thực email
                                    showVerificationDialog(user, email, password, sNumber);
                                }
                            });
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "Đăng ký không thành công: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDataAndProceed(String email, String password , String sNumber) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("isEmailVerificationPending");
        editor.remove("registerEmail");
        editor.remove("registerPassword");
        editor.remove("registerStudentNumber");

        UserAPI userAPI = new UserAPI();
        userAPI.getAllUsers(new UserAPI.UserCallback() {
            @Override
            public void onUserReceived(User user) {}

            @Override
            public void onUsersReceived(List<User> users) {
                int userId = users.size();  // Đặt userId dựa trên tổng số người dùng hiện có

                // Tạo đối tượng User để lưu vào cơ sở dữ liệu
                User userDTB = new User();
                userDTB.setUserId(userId);
                userDTB.setEmail(email);
                userDTB.setPassword(password);

                // Thêm người dùng vào cơ sở dữ liệu
                userAPI.addUser(userDTB);

                // Chuyển đến UploadProfileActivity
                // Lưu chuyển hướng
                // Đánh dấu đang đăng ký
                editor.putBoolean("isRegistering", true);
                editor.putString("studentNumber" , sNumber);
                editor.putInt("userId" , userId);
                editor.putString("email",email);
                editor.apply();

                Intent intent = new Intent(RegisterActivity.this, UploadProfileActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("email",email);
                intent.putExtra("studentNumber", sNumber);
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showVerificationDialog(FirebaseUser user, String email, String password, String sNumber) {
        // Lưu trạng thái xác thực email vào SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isEmailVerificationPending", true); // Đánh dấu trạng thái chờ xác thực email
        editor.putString("registerEmail", email);
        editor.putString("registerPassword", password);
        editor.putString("registerStudentNumber", sNumber);
        editor.apply();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác thực email");
        builder.setMessage("Vui lòng kiểm tra email của bạn để xác thực tài khoản.");
        builder.setCancelable(false);

        Toast.makeText(RegisterActivity.this,"Email này chưa được xác thực , vui lòng xác thực!" , Toast.LENGTH_SHORT).show();

        startEmailVerificationChecker(email , password , sNumber);

        builder.create().show();
    }

    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RegisterActivity.this, "Đã gửi lại email xác thực. Vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView(){
        // Thiết kế giao diện
        ImageView imageView = findViewById(R.id.image_logo);
        Glide.with(this)
                .load(R.drawable.image_logo_login)
                .circleCrop()
                .into(imageView);

        // Xử lý chức năng
        TextView textLogIn = findViewById(R.id.textLogIn);
        textLogIn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);
            finish();
        });

        // Lấy dữ liệu
        studentNumber = findViewById(R.id.studentNumber_first);
        emailEditText = findViewById(R.id.email_register);
        passwordEditText = findViewById(R.id.password_register);
        passwordConfirmEditText = findViewById(R.id.password_confirm_register);

        studentNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String mssv = s.toString();
                if (!mssv.isEmpty()) {
//                    emailEditText.setText(mssv);
                    emailEditText.setText(mssv + tdcEmail);
                    emailEditText.setEnabled(false);
                    emailEditText.setSelection(emailEditText.getText().length());
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Hủy bỏ runnable khi Activity không còn hiển thị
        handler.removeCallbacks(verificationRunnable);
    }
}