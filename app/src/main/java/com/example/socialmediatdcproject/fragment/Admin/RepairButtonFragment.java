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
import androidx.lifecycle.ViewModelProvider;

import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.HomeAdminActivity;
import com.example.socialmediatdcproject.adapter.LecturerAdapter;
import com.example.socialmediatdcproject.adapter.MemberAdapter;
import com.example.socialmediatdcproject.shareViewModels.SharedViewModel;

public class RepairButtonFragment extends Fragment {

    private Runnable editAction;
//    private boolean showRemoveButton = false;
    private SharedViewModel sharedViewModel;

    private LecturerAdapter lecturerAdapter; // Tham chiếu đến LecturerAdapter
    private MemberAdapter memberAdapter; // Tham chiếu đến MemberAdapter

    public void setEditAction(Runnable editAction) {
        this.editAction = editAction;
    }

    public void setLecturerAdapter(LecturerAdapter adapter) {
        this.lecturerAdapter = adapter; // Cập nhật tham chiếu đến adapter
    }
    public void setMemberAdapter(MemberAdapter adapter) {
        this.memberAdapter = adapter; // Cập nhật tham chiếu đến adapter
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.department_detail_admin_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Kiểm tra nếu lecturerAdapter là null
        if (lecturerAdapter == null) {
            // Nếu là null, khởi tạo nó
            lecturerAdapter = new LecturerAdapter();
        }
        Log.d("RepairButtonFragement" , "LecturerAdapter: " + lecturerAdapter);
        // Khởi tạo SharedViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        Button btnEdit = view.findViewById(R.id.button_edit);


        sharedViewModel.getIsEditMode().observe(getViewLifecycleOwner(), isEditMode -> {
            if (lecturerAdapter != null) {
                lecturerAdapter.setEditMode(isEditMode); // Cập nhật chế độ chỉnh sửa cho adapter
            }
        });

        btnEdit.setOnClickListener(v -> {
            if (editAction != null) {
                editAction.run(); // Thực thi hành động khi nhấn nút
            }
            sharedViewModel.setEditMode(true);

//            if (lecturerAdapter != null) {
//                lecturerAdapter.setEditMode(true); // Cập nhật chế độ chỉnh sửa cho adapter
//                lecturerAdapter.notifyDataSetChanged();
//                Log.d("Repair" , "Đã chỉnh sửa nút lại thành true");
//            }
        }); 
    }
}
