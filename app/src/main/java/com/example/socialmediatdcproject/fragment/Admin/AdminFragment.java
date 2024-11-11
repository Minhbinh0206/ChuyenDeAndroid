package com.example.socialmediatdcproject.fragment.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AdminFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.admin_department_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView avatar = view.findViewById(R.id.admin_department_avatar);
        TextView name = view.findViewById(R.id.admin_department_name);
        ConstraintLayout constraintLayout = view.findViewById(R.id.admin_department_background);

        AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
        AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
        AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();
        adminDepartmentAPI.getAdminDepartmentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
            @Override
            public void onUserReceived(AdminDepartment adminDepartment) {
                // Kiểm tra nếu fragment đã được đính kèm vào context
                if (isAdded() && adminDepartment.getDepartmentId() != -1) {
                    Glide.with(requireContext())
                            .load(adminDepartment.getAvatar())
                            .circleCrop()
                            .into(avatar);

                    name.setText(adminDepartment.getFullName());
                }
            }

            @Override
            public void onUsersReceived(List<AdminDepartment> adminDepartment) {
                // Xử lý khi nhận được danh sách AdminDepartment
            }

            @Override
            public void onError(String s) {
                // Xử lý lỗi
            }
        });
        adminBusinessAPI.getAdminBusinessByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminBusinessAPI.AdminBusinessCallBack() {
            @Override
            public void onUserReceived(AdminBusiness adminBusiness) {
                // Kiểm tra nếu fragment đã được đính kèm vào context
                if (isAdded() && adminBusiness.getBusinessId() != -1) {
                    Glide.with(requireContext())
                            .load(adminBusiness.getAvatar())
                            .circleCrop()
                            .into(avatar);

                    name.setText(adminBusiness.getFullName());
                }
            }

            @Override
            public void onUsersReceived(List<AdminBusiness> adminBusiness) {

            }

            @Override
            public void onError(String s) {

            }
        });
        adminDefaultAPI.getAdminDefaultByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDefaultAPI.AdminDefaultCallBack() {
            @Override
            public void onUserReceived(AdminDefault adminDefault) {
                // Kiểm tra nếu fragment đã được đính kèm vào context
                if (isAdded() && adminDefault.getUserId() != -1) {
                    Glide.with(requireContext())
                            .load(adminDefault.getAvatar())
                            .circleCrop()
                            .into(avatar);

                    name.setText(adminDefault.getFullName());
                }
            }

            @Override
            public void onUsersReceived(List<AdminDefault> adminDefault) {

            }
        });
    }
}
