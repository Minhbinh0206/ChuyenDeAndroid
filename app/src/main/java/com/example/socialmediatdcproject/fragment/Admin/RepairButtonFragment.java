package com.example.socialmediatdcproject.fragment.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.LecturerAdapter;

public class RepairButtonFragment extends Fragment {

    private Runnable editAction;
    private boolean showRemoveButton = false;
    private LecturerAdapter lecturerAdapter; // Tham chiếu đến LecturerAdapter

    public void setEditAction(Runnable action) {
        this.editAction = action;
    }

    public void setLecturerAdapter(LecturerAdapter adapter) {
        this.lecturerAdapter = adapter; // Cập nhật tham chiếu đến adapter
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

        Button btnEdit = view.findViewById(R.id.button_edit);
        btnEdit.setOnClickListener(v -> {
            if (editAction != null) {
                editAction.run(); // Thực thi hành động khi nhấn nút
            }
            // Cập nhật showRemoveButton thành true
//            if (lecturerAdapter != null) {
//                lecturerAdapter.setShowRemoveButton(true);
//            }
        });
    }
}
