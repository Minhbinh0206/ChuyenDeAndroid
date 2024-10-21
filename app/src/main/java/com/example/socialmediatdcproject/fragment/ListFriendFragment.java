package com.example.socialmediatdcproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.FriendAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R; // Import đúng package chứa R
import com.example.socialmediatdcproject.adapter.FriendPersonalAdapter;
import com.example.socialmediatdcproject.dataModels.Friends;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ListFriendFragment extends Fragment {
    FrameLayout frameLayout;
    FriendPersonalAdapter friendPersonalAdapter;
    RecyclerView recyclerView;
    int myId = -1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for this fragment
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_friends_button, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button listFriend = view.findViewById(R.id.button_personal_list_friends);
        Button listInvitation = view.findViewById(R.id.button_personal_list_invitation);
        Button listFollowed = view.findViewById(R.id.button_personal_list_follow);

        displayFriends(3);

        updateButtonColorsActive(listFriend);
        updateButtonColorsNormal(listInvitation);
        updateButtonColorsNormal(listFollowed);

        listFriend.setOnClickListener(v -> {
            displayFriends(3);

            updateButtonColorsActive(listFriend);
            updateButtonColorsNormal(listInvitation);
            updateButtonColorsNormal(listFollowed);
        });

        listInvitation.setOnClickListener(v -> {
            displayFriendsFollow();

            updateButtonColorsActive(listInvitation);
            updateButtonColorsNormal(listFriend);
            updateButtonColorsNormal(listFollowed);
        });

        listFollowed.setOnClickListener(v -> {
            displayFriendsInvitation();

            updateButtonColorsActive(listFollowed);
            updateButtonColorsNormal(listInvitation);
            updateButtonColorsNormal(listFriend);
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

    private void displayFriends(int status){
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        ArrayList<Student> listFriends = new ArrayList<>();
        ArrayList<Student> tempListFriends = new ArrayList<>();

        FriendAPI friendAPI = new FriendAPI();
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                myId = student.getUserId();

                friendAPI.getAllFriends(new FriendAPI.FriendCallback() {
                    @Override
                    public void onFriendsReceived(List<Friends> friendsList) {
                        for (Friends f : friendsList) {
                            if (f.getStatus() == status) {
                                if (f.getMyUserId() == myId) {
                                    studentAPI.getStudentById(f.getYourUserId(), new StudentAPI.StudentCallback() {
                                        @Override
                                        public void onStudentReceived(Student student) {
                                            boolean isAlreadyInList = false;
                                            for (Student fri : listFriends) {
                                                if (fri.getUserId() == student.getUserId()) {
                                                    isAlreadyInList = true;
                                                    break;
                                                }
                                            }

                                            // Chỉ thêm sinh viên nếu chưa tồn tại trong danh sách
                                            if (!isAlreadyInList) {
                                                listFriends.add(student);
                                            }

                                            Log.d("FR", "onStudentReceived: " + listFriends.size());

                                            FriendPersonalAdapter friendPersonalAdapter = new FriendPersonalAdapter(listFriends, requireContext());
                                            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                                            recyclerView.setAdapter(friendPersonalAdapter);
                                            friendPersonalAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onStudentsReceived(List<Student> students) {

                                        }

                                        @Override
                                        public void onError(String errorMessage) {

                                        }

                                        @Override
                                        public void onStudentDeleted(int studentId) {

                                        }
                                    });
                                }
                                else if (f.getYourUserId() == myId){
                                    studentAPI.getStudentById(f.getMyUserId(), new StudentAPI.StudentCallback() {
                                        @Override
                                        public void onStudentReceived(Student student) {
                                            boolean isAlreadyInList = false;
                                            for (Student fri : listFriends) {
                                                if (fri.getUserId() == student.getUserId()) {
                                                    isAlreadyInList = true;
                                                    break;
                                                }
                                            }

                                            // Chỉ thêm sinh viên nếu chưa tồn tại trong danh sách
                                            if (!isAlreadyInList) {
                                                listFriends.add(student);
                                            }

                                            Log.d("FR2", "onStudentReceived: " + listFriends.size());

                                            FriendPersonalAdapter friendPersonalAdapter = new FriendPersonalAdapter(listFriends, requireContext());
                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
                                            recyclerView.setLayoutManager(linearLayoutManager);
                                            recyclerView.setAdapter(friendPersonalAdapter);
                                            friendPersonalAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onStudentsReceived(List<Student> students) {

                                        }

                                        @Override
                                        public void onError(String errorMessage) {

                                        }

                                        @Override
                                        public void onStudentDeleted(int studentId) {

                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void onStudentsReceived(List<Student> students) {

            }

            @Override
            public void onError(String errorMessage) {

            }

            @Override
            public void onStudentDeleted(int studentId) {

            }
        });

        FriendPersonalAdapter friendPersonalAdapter = new FriendPersonalAdapter(listFriends, requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(friendPersonalAdapter);
        friendPersonalAdapter.notifyDataSetChanged();

    }

    private void displayFriendsInvitation(){
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        ArrayList<Student> listFriends = new ArrayList<>();

        FriendAPI friendAPI = new FriendAPI();
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                myId = student.getUserId();

                friendAPI.getAllFriends(new FriendAPI.FriendCallback() {
                    @Override
                    public void onFriendsReceived(List<Friends> friendsList) {
                        for (Friends f : friendsList) {
                            if (f.getStatus() == 1 && f.getMyUserId() == myId) {

                                studentAPI.getStudentById(f.getYourUserId(), new StudentAPI.StudentCallback() {
                                    @Override
                                    public void onStudentReceived(Student student) {
                                        listFriends.add(student);

                                        Log.d("FR", "onStudentReceived: " + listFriends.size());

                                        FriendPersonalAdapter friendPersonalAdapter = new FriendPersonalAdapter(listFriends, requireContext());
                                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                                        recyclerView.setAdapter(friendPersonalAdapter);
                                        friendPersonalAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onStudentsReceived(List<Student> students) {

                                    }

                                    @Override
                                    public void onError(String errorMessage) {

                                    }

                                    @Override
                                    public void onStudentDeleted(int studentId) {

                                    }
                                });
                            }

                        }
                    }
                });
            }

            @Override
            public void onStudentsReceived(List<Student> students) {

            }

            @Override
            public void onError(String errorMessage) {

            }

            @Override
            public void onStudentDeleted(int studentId) {

            }
        });

        FriendPersonalAdapter friendPersonalAdapter = new FriendPersonalAdapter(listFriends, requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(friendPersonalAdapter);
        friendPersonalAdapter.notifyDataSetChanged();

    }

    private void displayFriendsFollow(){
        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        ArrayList<Student> listFriends = new ArrayList<>();

        FriendAPI friendAPI = new FriendAPI();
        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                myId = student.getUserId();

                friendAPI.getAllFriends(new FriendAPI.FriendCallback() {
                    @Override
                    public void onFriendsReceived(List<Friends> friendsList) {
                        for (Friends f : friendsList) {
                            if (f.getStatus() == 1 && f.getYourUserId() == myId) {

                                studentAPI.getStudentById(f.getMyUserId(), new StudentAPI.StudentCallback() {
                                    @Override
                                    public void onStudentReceived(Student student) {
                                        listFriends.add(student);

                                        Log.d("FR", "onStudentReceived: " + listFriends.size());

                                        FriendPersonalAdapter friendPersonalAdapter = new FriendPersonalAdapter(listFriends, requireContext());
                                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                                        recyclerView.setAdapter(friendPersonalAdapter);
                                        friendPersonalAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onStudentsReceived(List<Student> students) {

                                    }

                                    @Override
                                    public void onError(String errorMessage) {

                                    }

                                    @Override
                                    public void onStudentDeleted(int studentId) {

                                    }
                                });
                            }

                        }
                    }
                });
            }

            @Override
            public void onStudentsReceived(List<Student> students) {

            }

            @Override
            public void onError(String errorMessage) {

            }

            @Override
            public void onStudentDeleted(int studentId) {

            }
        });

        FriendPersonalAdapter friendPersonalAdapter = new FriendPersonalAdapter(listFriends, requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(friendPersonalAdapter);
        friendPersonalAdapter.notifyDataSetChanged();

    }
}
