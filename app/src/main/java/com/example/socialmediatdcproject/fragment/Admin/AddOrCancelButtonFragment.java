package com.example.socialmediatdcproject.fragment.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.LecturerAdapter;
import com.example.socialmediatdcproject.shareViewModels.SharedViewModel;

public class AddOrCancelButtonFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private LecturerAdapter lecturerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.department_detail_admin_fixing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo SharedViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Kiểm tra nếu lecturerAdapter là null
        if (lecturerAdapter == null) {
            // Nếu là null, khởi tạo nó
            lecturerAdapter = new LecturerAdapter();
        }
        Log.d("RepairButtonFragment" , "LecturerAdapter: " + lecturerAdapter);

        // Khởi tạo các nút
        Button btnAdd = view.findViewById(R.id.button_add);
        Button btnCancel = view.findViewById(R.id.button_cancel);

        sharedViewModel.getIsEditMode().observe(getViewLifecycleOwner(), isEditMode -> {
            if (lecturerAdapter != null) {
                lecturerAdapter.setEditMode(isEditMode); // Cập nhật chế độ chỉnh sửa cho adapter
            }
        });

        btnCancel.setOnClickListener(v -> {
            sharedViewModel.setEditMode(false);
            // Quay lại RepairButtonFragment
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });
    }
}
