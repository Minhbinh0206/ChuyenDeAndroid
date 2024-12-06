package com.example.socialmediatdcproject.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.BusinessAPI;
import com.example.socialmediatdcproject.API.DepartmentAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.AccountAdapter;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Business;
import com.example.socialmediatdcproject.model.Department;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class SuperAdminActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.super_admin_layout);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        TextView create = findViewById(R.id.button_create_account);
        create.setOnClickListener(v -> {
            showPopupCreate();
        });

        ImageButton imageButton = findViewById(R.id.icon_logout);
        imageButton.setOnClickListener(v -> {
            // Đăng xuất người dùng và chuyển đến LoginActivity
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();

            SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor =  sharedPreferences.edit();
            editor.putBoolean("isRegistering", false);
            editor.putBoolean("isAdmin", false);
            editor.apply();

            Intent intent = new Intent(SuperAdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);
            finish(); // Đóng SharedActivity
        });

        loadDepartment();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.filter_department) {
                loadDepartment();
            } else if (checkedId == R.id.filter_business) {
                loadBusiness();
            }
        });

        recyclerView = findViewById(R.id.recycle_view_account);

    }

    private void loadDepartment(){
        ArrayList<User> userList = new ArrayList<>();
        UserAPI userAPI = new UserAPI();
        userAPI.getAllUsers(new UserAPI.UserCallback() {
            @Override
            public void onUserReceived(User user) {

            }

            @Override
            public void onUsersReceived(List<User> users) {
                for (User u : users) {
                    if (u.getRoleId() == 2) {
                        userList.add(u);
                    }
                }
                AccountAdapter adapter = new AccountAdapter(userList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(SuperAdminActivity.this));
            }
        });
    }

    private void loadBusiness(){
        ArrayList<User> userList = new ArrayList<>();
        UserAPI userAPI = new UserAPI();
        userAPI.getAllUsers(new UserAPI.UserCallback() {
            @Override
            public void onUserReceived(User user) {

            }

            @Override
            public void onUsersReceived(List<User> users) {
                for (User u : users) {
                    if (u.getRoleId() == 3) {
                        userList.add(u);
                    }
                }
                AccountAdapter adapter = new AccountAdapter(userList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(SuperAdminActivity.this));
            }
        });
    }

    private void showPopupCreate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SuperAdminActivity.this);
        View customView = getLayoutInflater().inflate(R.layout.create_new_account_layout, null);
        builder.setView(customView);

        EditText edtUsername = customView.findViewById(R.id.name_account);
        EditText edtEmail = customView.findViewById(R.id.email_account);
        EditText edtPassword = customView.findViewById(R.id.pass_account);
        Spinner spinner = customView.findViewById(R.id.spinner_role);
        Button submit = customView.findViewById(R.id.submit_create);
        Button cancel = customView.findViewById(R.id.cancle_create);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Phòng ban", "Doanh nghiệp"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        AlertDialog dialog = builder.create();

        submit.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String selectedRole = (String) spinner.getSelectedItem();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SuperAdminActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else {
                int roleId = "Phòng ban".equals(selectedRole) ? 2 : 3;

                // Hiển thị dialog loading
                ProgressDialog progressDialog = new ProgressDialog(SuperAdminActivity.this);
                progressDialog.setMessage("Đang xử lý...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String uid = auth.getCurrentUser().getUid();
                                saveUserToDatabase(username, email, password, roleId);

                                if (roleId == 2) {
                                    saveDepartment(uid, username, email, password, roleId);
                                } else {
                                    saveBusiness(uid, username, email, password, roleId);
                                }

                                Toast.makeText(SuperAdminActivity.this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(SuperAdminActivity.this, "Lỗi Firebase: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            // Ẩn dialog loading
                            progressDialog.dismiss();
                        });
            }
        });

        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void saveUserToDatabase(String username, String email, String pass, int roleId) {
        UserAPI userAPI = new UserAPI();
        userAPI.getAllUsers(new UserAPI.UserCallback() {
            @Override
            public void onUserReceived(User user) {

            }

            @Override
            public void onUsersReceived(List<User> users) {
                User newUser = new User();
                if (users != null) {
                    newUser.setUserId(users.get(users.size() - 1).getUserId() + 1); // Hoặc giữ Firebase UID
                }
                else {
                    newUser.setUserId(0);
                }
                newUser.setAvatar(""); // Avatar mặc định
                newUser.setEmail(email);
                newUser.setFullName(username);
                newUser.setPassword(pass);
                newUser.setRoleId(roleId);
                newUser.setPhoneNumber(""); // Trường này có thể được cập nhật sau nếu cần

                userAPI.addUser(newUser);
            }
        });
    }

    private void saveDepartment(String uid, String username, String email, String pass, int roleId) {
        UserAPI userAPI = new UserAPI();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        AdminDepartment adminDepartment = new AdminDepartment();
        DepartmentAPI departmentAPI = new DepartmentAPI();
        departmentAPI.getAllDepartments(new DepartmentAPI.DepartmentCallback() {
            @Override
            public void onDepartmentReceived(Department department) {

            }

            @Override
            public void onDepartmentsReceived(List<Department> departments) {
                userAPI.getAllUsers(new UserAPI.UserCallback() {
                    @Override
                    public void onUserReceived(User user) {

                    }

                    @Override
                    public void onUsersReceived(List<User> users) {
                        adminDepartment.setDepartmentId(departments.size());
                        adminDepartment.setAvatar("");
                        if (users != null) {
                            adminDepartment.setUserId(users.get(users.size() - 1).getUserId()); // Hoặc giữ Firebase UID
                        }
                        else {
                            adminDepartment.setUserId(-2);
                        }
                        adminDepartment.setEmail(email);
                        adminDepartment.setFullName("Khoa " + username);
                        adminDepartment.setPassword(pass);
                        adminDepartment.setBackground("");
                        adminDepartment.setRoleId(roleId);
                        adminDepartment.setPhoneNumber("");

                        adminDepartmentAPI.addAdminDepartment(uid, adminDepartment);

                        GroupAPI groupAPI = new GroupAPI();
                        groupAPI.getAllGroups(new GroupAPI.GroupCallback() {
                            @Override
                            public void onGroupReceived(Group group) {

                            }

                            @Override
                            public void onGroupsReceived(List<Group> groups) {
                                Group group = new Group();
                                group.setGroupId(groups.size());
                                group.setAvatar("");
                                group.setPrivate(false);
                                group.setGroupDefault(true);
                                group.setBackground("");
                                group.setGroupName("Khoa " + username);
                                group.setAdminUserId(users.get(users.size() - 1).getUserId());

                                groupAPI.addGroup(group);

                                Department department = new Department();
                                department.setDepartmentId(departments.size());
                                department.setDepartmentName("Khoa " + username);
                                department.setDepartmentInfo("Chưa cập nhật");
                                department.setGroupId(groups.size());

                                departmentAPI.addDepartment(department);
                            }
                        });
                    }
                });
            }
        });

    }

    private void saveBusiness(String uid, String username, String email, String pass, int roleId) {
        UserAPI userAPI = new UserAPI();
        AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
        AdminBusiness adminBusiness = new AdminBusiness();
        BusinessAPI businessAPI = new BusinessAPI();
        businessAPI.getAllBusinesses(new BusinessAPI.BusinessCallback() {
            @Override
            public void onBusinessReceived(Business business) {

            }

            @Override
            public void onBusinessesReceived(List<Business> businesses) {
                userAPI.getAllUsers(new UserAPI.UserCallback() {
                    @Override
                    public void onUserReceived(User user) {

                    }

                    @Override
                    public void onUsersReceived(List<User> users) {
                        adminBusiness.setBusinessId(businesses.size());
                        adminBusiness.setAvatar("");
                        if (users != null) {
                            adminBusiness.setUserId(users.get(users.size() - 1).getUserId()); // Hoặc giữ Firebase UID
                        }
                        else {
                            adminBusiness.setUserId(-2);
                        }
                        adminBusiness.setEmail(email);
                        adminBusiness.setFullName("Doanh nghiệp " + username);
                        adminBusiness.setPassword(pass);
                        adminBusiness.setRoleId(roleId);
                        adminBusiness.setPhoneNumber("");

                        adminBusinessAPI.addAdminBusiness(uid, adminBusiness);

                        GroupAPI groupAPI = new GroupAPI();
                        groupAPI.getAllGroups(new GroupAPI.GroupCallback() {
                            @Override
                            public void onGroupReceived(Group group) {

                            }

                            @Override
                            public void onGroupsReceived(List<Group> groups) {
                                Group group = new Group();
                                group.setGroupId(groups.size());
                                group.setAvatar("");
                                group.setPrivate(false);
                                group.setGroupDefault(true);
                                group.setBackground("");
                                group.setGroupName("Doanh nghiệp " + username);
                                group.setAdminUserId(users.get(users.size() - 1).getUserId());

                                groupAPI.addGroup(group);

                                Business business = new Business();
                                business.setBusinessId(businesses.size());
                                business.setBusinessName("Doanh nghiệp " + username);
                                business.setAddress("Chưa cập nhật");
                                business.setGroupId(groups.size());
                                business.setAvatar("");
                                business.setBusinessAdminId(users.get(users.size() - 1).getUserId() + 1);

                                businessAPI.addBusiness(business);
                            }
                        });
                    }
                });
            }
        });

    }
}
