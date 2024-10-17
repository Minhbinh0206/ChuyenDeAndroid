package com.example.socialmediatdcproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socialmediatdcproject.R;

public class EditScreenFragment extends Fragment {

    private EditText editTextName;
    private EditText editTextMSSV;
    private EditText editTextClass;
    private EditText editTextDepartment;
    private EditText editTextDescription;
    private Button buttonUpdate;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.edit_screen_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tìm các EditText và Button
        editTextName = view.findViewById(R.id.editTextName);
        editTextMSSV = view.findViewById(R.id.editTextMSSV);
        editTextClass = view.findViewById(R.id.editTextLop);
        editTextDepartment = view.findViewById(R.id.editTextKhoa);
        editTextDescription = view.findViewById(R.id.editTextMota);
        buttonUpdate = view.findViewById(R.id.button_club_post);

        // Sự kiện click cho nút Cập nhật
        buttonUpdate.setOnClickListener(v -> {
            // Lấy dữ liệu từ các EditText
            String name = editTextName.getText().toString().trim();
            String mssv = editTextMSSV.getText().toString().trim();
            String className = editTextClass.getText().toString().trim();
            String department = editTextDepartment.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();

            // Kiểm tra dữ liệu (có thể điều chỉnh theo yêu cầu của bạn)
            if (name.isEmpty() || mssv.isEmpty() || className.isEmpty() || department.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            } else {
                // Thực hiện cập nhật dữ liệu ở đây (ví dụ: gửi dữ liệu đến server hoặc Firebase)
                Toast.makeText(requireContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                // Quay trở lại fragment trước đó (nếu cần)
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}
