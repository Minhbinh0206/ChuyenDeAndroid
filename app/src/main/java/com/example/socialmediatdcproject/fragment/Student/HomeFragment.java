package com.example.socialmediatdcproject.fragment.Student;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.MessageAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R; // Import đúng package chứa R
import com.example.socialmediatdcproject.activity.MessengerActivity;
import com.example.socialmediatdcproject.activity.SearchFriendActivity;
import com.example.socialmediatdcproject.dataModels.Message;
import com.example.socialmediatdcproject.fragment.Admin.HomeAdminFragment;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class HomeFragment extends Fragment {
    FrameLayout frameLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for this fragment
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_home_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton searchFriends = view.findViewById(R.id.search_friends);

        RecyclerView recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        recyclerView.setVisibility(View.VISIBLE);

        // Gán fragment home là mặc định
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.third_content_fragment, new HomeAdminFragment());

        fragmentTransaction.commit();

        ImageButton mes = view.findViewById(R.id.icon_mess);

        mes.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MessengerActivity.class);
            startActivity(intent);
        });

        searchFriends.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SearchFriendActivity.class);
            startActivity(intent);
        });
        TextView textView = view.findViewById(R.id.count_message);

        StudentAPI studentAPI = new StudentAPI();
        studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
            @Override
            public void onStudentReceived(Student student) {
                loadCountMessage(student.getUserId(), textView);
            }

            @Override
            public void onStudentsReceived(List<Student> students) {

            }
        });
    }

    private void loadCountMessage(int id, TextView textView){
        MessageAPI messageAPI = new MessageAPI();
        messageAPI.getAllMessagesRealtime(id, new MessageAPI.MessageCallback() {
            @Override
            public void onMessagesReceived(List<Message> messages) {
                int count = 0;

                for (Message m : messages) {
                    if (!m.isRead()) {
                        count++;
                    }
                }

                textView.setText(count + "");

                if (count == 0) {
                    textView.setVisibility(View.GONE);
                }
                else {
                    textView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onMessageAdded(Message message) {

            }
        });
    }
}
