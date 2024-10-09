package com.example.socialmediatdcproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.database.UserDatabase;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_layout);

        mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth

        // Thiết kế giao diện
        // - Bo tròn logo
        ImageView imageView = findViewById(R.id.image_logo);
        Glide.with(this)
                .load(R.drawable.image_logo_login) // Hoặc URL nếu bạn dùng ảnh từ mạng
                .circleCrop()
                .into(imageView);


        // Xử lý chức năng
        // - Chuyển qua trang đăng ký
        TextView textSignUp = findViewById(R.id.textSignUp);
        textSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // - Chuyển qua trang quên mật khẩu
        TextView textForgotPassword = findViewById(R.id.textForgotPassword);
        textForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // - Chuyển qua trang Home
        Button btnLogin = findViewById(R.id.btnLogin);
        // ****************************************************************************************************

//        btnLogin.setOnClickListener(v -> {
//            Intent intent = new Intent(LoginActivity.this, SharedActivity.class);
//            startActivity(intent);
//        });

        // ****************************************************************************************************

        // xử lý đăng nhập ( Firebase )
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy email và mật khẩu từ EditText
                EditText editTextEmail = findViewById(R.id.email_login);
                EditText editTextPassword = findViewById(R.id.password_login);

                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                // Kiểm tra nếu email hoặc mật khẩu rỗng
                if (email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Xử lý đăng nhập ( bằng Firebase )

                // Đăng nhập với Firebase Authentication
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Đăng nhập thành công, chuyển đến màn hình chính
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null && user.isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, SharedActivity.class);
                                startActivity(intent);
                                finish();
                                }
                            } else {
                                // Đăng nhập thất bại
                                Toast.makeText(LoginActivity.this, "Email chưa được xác minh. Vui lòng kiểm tra email của bạn.", Toast.LENGTH_SHORT).show();
                            }
                        });


                /*
                // đăng nhập bằng mã giả (cũ)
                // Tạo đối tượng
                UserDatabase userDatabase = new UserDatabase();
                // khởi tạo danh sách người dùng
                userDatabase.dataUser();

                // Lấy thông tin người dùng từ cơ sở dữ liệu theo email
                User user = userDatabase.getUserByEmail(email);
                Log.d("Kiem tra" , "Email: " + userDatabase.getUserByEmail(email));

                if ( user!=null && user.getPassword().equals(password)) {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    // Chuyển sang màn hình SharedActivity
                    Intent intent = new Intent(LoginActivity.this, SharedActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }
                */

            }


        });


    }
}
