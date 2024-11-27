package com.example.socialmediatdcproject.fragment.Student;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.GroupUserAPI;
import com.example.socialmediatdcproject.API.PostAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.GroupAdapter;
import com.example.socialmediatdcproject.adapter.MemberAdapter;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.dataModels.GroupUser;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;

import java.util.ArrayList;
import java.util.List;

public class YouthFragment extends Fragment {
    private RecyclerView recyclerView; // RecyclerView để hiển thị danh sách người dùng
    FrameLayout frameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.fragment_youth_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo danh sách người dùng và RecyclerView
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        frameLayout = requireActivity().findViewById(R.id.third_content_fragment);
        frameLayout.setVisibility(view.GONE);

        RecyclerView recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        recyclerView.setVisibility(View.VISIBLE);

        FrameLayout frameLayout = requireActivity().findViewById(R.id.third_content_fragment);
        frameLayout.setVisibility(View.GONE);

        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getGroupById(User.ID_ADMIN_DOANTHANHNIEN, new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {
                if (group != null) {
                    TextView nameTraining = getView().findViewById(R.id.name_department);
                    ImageView avatarTraining = getView().findViewById(R.id.logo_department);
                    nameTraining.setText(group.getGroupName());

                    Glide.with(requireContext())
                            .load(group.getAvatar())
                            .into(avatarTraining);
                }
            }

            @Override
            public void onGroupsReceived(List<Group> groups) {

            }
        });

        loadPostFromFirebase(User.ID_ADMIN_DOANTHANHNIEN);

        // Tìm các nút
        Button postButton = view.findViewById(R.id.button_youth_post);
        Button memberButton = view.findViewById(R.id.button_youth_member);
        Button groupButton = view.findViewById(R.id.button_youth_group);

        // Set màu mặc định cho nút "Bài viết"
        changeColorButtonActive(postButton);
        changeColorButtonNormal(memberButton);
        changeColorButtonNormal(groupButton);

        // Sự kiện khi nhấn vào nút memberButton
        memberButton.setOnClickListener(v -> {
            loadUsersByGroupId(User.ID_ADMIN_DOANTHANHNIEN);

            // Cập nhật màu cho các nút
            changeColorButtonActive(memberButton);
            changeColorButtonNormal(postButton);
            changeColorButtonNormal(groupButton);
        });

        // Sự kiện khi nhấn vào nút postButton
        postButton.setOnClickListener(v -> {
            loadPostFromFirebase(User.ID_ADMIN_DOANTHANHNIEN);

            // Cập nhật màu cho các nút
            changeColorButtonActive(postButton);
            changeColorButtonNormal(memberButton);
            changeColorButtonNormal(groupButton);
        });

        groupButton.setOnClickListener(v -> {
            loadGroupByUserId(User.ID_ADMIN_DOANTHANHNIEN);

            changeColorButtonActive(groupButton);
            changeColorButtonNormal(memberButton);
            changeColorButtonNormal(postButton);
        });
    }

    public void loadPostFromFirebase(int id) {
        ArrayList<Post> postsList = new ArrayList<>();

        // Tạo instance của PostAPI
        PostAPI postAPI = new PostAPI();

        // Lấy bài viết theo groupId
        postAPI.getPostsByGroupId(id, new PostAPI.PostCallback() {
            @Override
            public void onPostReceived(Post post) {

            }

            @Override
            public void onPostsReceived(List<Post> posts) {
                // Kiểm tra nếu có bài viết
                for (Post p : posts) {
                    if (p != null) {
                        postsList.add(p); // Thêm bài viết vào danh sách
                    }
                }

                // Cập nhật RecyclerView với dữ liệu bài viết
                PostAdapter postAdapter = new PostAdapter(postsList, requireContext());
                recyclerView.setAdapter(postAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

            }
        });
    }

    public void loadUsersByGroupId(int groupId){
        ArrayList<Student> memberList = new ArrayList<>();

        // Cập nhật RecyclerView với dữ liệu bài viết
        MemberAdapter memberAdapter = new MemberAdapter(memberList, requireContext());
        recyclerView.setAdapter(memberAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        GroupUserAPI groupUserAPI = new GroupUserAPI();
        groupUserAPI.getAllUsersInGroup(groupId, new GroupUserAPI.GroupUsersCallback() {
            @Override
            public void onUsersReceived(List<Integer> userIds) {
                for (Integer i : userIds) {
                    StudentAPI studentAPI = new StudentAPI();
                    studentAPI.getStudentById(i, new StudentAPI.StudentCallback() {
                        @Override
                        public void onStudentReceived(Student student) {
                            memberList.add(student);
                            memberAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onStudentsReceived(List<Student> students) {

                        }
                    });
                }
            }
        });
    }

    public void loadGroupByUserId(int adminId){
        ArrayList<Group> groupList = new ArrayList<>();

        // Cập nhật RecyclerView với dữ liệu bài viết
        GroupAdapter groupAdapter = new GroupAdapter(groupList, requireContext());
        recyclerView.setAdapter(groupAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getAllGroupsByAdminId(adminId, new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {

            }

            @Override
            public void onGroupsReceived(List<Group> groups) {
                for (Group g : groups) {
                    groupList.add(g);
                    groupAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void changeColorButtonActive(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
    }

    public void changeColorButtonNormal(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
    }
    public void openPersonalPageFragment(int userId) {
        // Chuyển sang màn hình PersonalScreenFragment với userId được truyền vào
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        PersonalScreenFragment personalScreenFragment = PersonalScreenFragment.newInstance(userId);
        fragmentTransaction.replace(R.id.first_content_fragment, personalScreenFragment);
        fragmentTransaction.addToBackStack(null); // Thêm vào back stack để có thể quay lại
        fragmentTransaction.commit();
    }
}
