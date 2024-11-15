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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.department_detail_admin_fixing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Khởi tạo các nút
        Button btnAdd = view.findViewById(R.id.button_add);
        Button btnCancel = view.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(v -> {
            sharedViewModel.setEditMode(false);
            // Quay lại RepairButtonFragment
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });
        btnAdd.setOnClickListener(v -> {
            // Chưa xử lý
        });
    }
}
