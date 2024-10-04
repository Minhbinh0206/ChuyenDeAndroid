package com.example.socialmediatdcproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.socialmediatdcproject.R;

public class MemberFragment extends Fragment {

    private static final String ARG_POST_TEXT = "post_text";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_member, container, false);

        // Lấy dữ liệu từ arguments
        String postText = getArguments() != null ? getArguments().getString(ARG_POST_TEXT) : "No Post";

        // Hiển thị nội dung post
        TextView memberName = view.findViewById(R.id.member_name);
        TextView memberPosition = view.findViewById(R.id.member_position_job);
        TextView memberEmail = view.findViewById(R.id.member_email);
        ImageView memberImage = view.findViewById(R.id.member_avatar);

        memberName.setText("Nguyễn Huy Hoàng");
        memberPosition.setText("Trưởng bộ môn Công nghệ phần mềm");
        memberEmail.setText("hoanghn@example.com");
        return view;
    }
}
