package com.example.socialmediatdcproject.fragment.Student;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.EditScreenActivity;
import com.example.socialmediatdcproject.adapter.GroupAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class PersonalScreenFragment extends Fragment {

    private static final String ARG_USER_ID = "userId";

    public static PersonalScreenFragment newInstance(int userId) {
        PersonalScreenFragment fragment = new PersonalScreenFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    private FirebaseAuth mAuth;

    private RecyclerView recyclerView; // RecyclerView để hiển thị bài viết và doanh nghiệp
    private ArrayList<Post> postsUser = new ArrayList<>(); // Danh sách bài viết
    private ArrayList<Group> groupList = new ArrayList<>(); // Danh sách doanh nghiệp
    private PostAdapter postAdapter; // Adapter cho bài viết
    private FrameLayout frameLayout;
    Fragment friendsFragment;
    GroupAdapter groupAdapter;
    FragmentTransaction fragmentTransaction;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.fragment_personal_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo RecyclerView
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        frameLayout = requireActivity().findViewById(R.id.third_content_fragment);
        frameLayout.setVisibility(view.getVisibility());
        postAdapter = new PostAdapter(postsUser, requireContext());

        recyclerView.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth

        // Tìm các nút
        Button personalFriends = view.findViewById(R.id.personal_friends);
        Button personalMyGroup = view.findViewById(R.id.personal_mygroup);
        ImageButton personalUpdate = view.findViewById(R.id.personal_user_update);

        //tìm ảnh
        ImageView imageUser = view.findViewById(R.id.logo_personal_user_image);
        TextView nameUser = view.findViewById(R.id.name_personnal_user);

        TextView des = view.findViewById(R.id.personal_description);

        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(key, new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                if (isAdded() && student != null && student.getAvatar() != null) {
                    String name = student.getFullName();
                    nameUser.setText(name);
                    Glide.with(requireContext())
                            .load(student.getAvatar())
                            .circleCrop()
                            .into(imageUser);

                    des.setText(student.getDescription());
                }
            }

            @Override
            public void onStudentsReceived(List<Student> students) {

            }
        });
        frameLayout.setVisibility(View.VISIBLE);
        friendsFragment = new ListFriendFragment();
        fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.third_content_fragment, friendsFragment);
        fragmentTransaction.commit();
        recyclerView.setVisibility(View.VISIBLE);

        // Set màu mặc định cho nút "Bài viết"
        updateButtonColorsActive(personalFriends);
        updateButtonColorsNormal(personalMyGroup);

        // Sự kiện khi nhấn vào nút bạn bè
        personalFriends.setOnClickListener(v -> {
            frameLayout.setVisibility(View.VISIBLE);
            friendsFragment = new ListFriendFragment();
            fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.third_content_fragment, friendsFragment);
            fragmentTransaction.commit();
            recyclerView.setVisibility(View.VISIBLE);

            // Cập nhật màu cho các nút
            updateButtonColorsActive(personalFriends);
            updateButtonColorsNormal(personalMyGroup);
        });

        // Sự kiện khi nhấn vào nút group
        personalMyGroup.setOnClickListener(v -> {
            frameLayout.setVisibility(View.VISIBLE);
            friendsFragment = new ListEventAndGroupFragment();
            fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.third_content_fragment, friendsFragment);
            fragmentTransaction.commit();

            // Cập nhật màu cho các nút
            updateButtonColorsActive(personalMyGroup);
            updateButtonColorsNormal(personalFriends);
        });


        // Sự kiện khi nhấn vào nút update
        personalUpdate.setOnClickListener(v -> {
            // Chuyển sang màn hình EditScreenActivity (Activity)
            Intent intent = new Intent(requireContext(), EditScreenActivity.class);
            startActivity(intent); // Bắt đầu EditScreenActivity
        });
    }


    //Set sự kiện màu cho các nút
    private void updateButtonColorsActive(Button activeButton){
        // Cập nhật nút đang hoạt động
        activeButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        activeButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
    }

    //Set sự kiện màu cho các nút
    private void updateButtonColorsNormal(Button button){
        // Cập nhật nút đang hoạt động
        button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        button.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
    }

}