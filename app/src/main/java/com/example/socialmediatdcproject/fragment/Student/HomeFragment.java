package com.example.socialmediatdcproject.fragment.Student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.FilterPostsAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.MessageAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R; // Import đúng package chứa R
import com.example.socialmediatdcproject.activity.MessengerActivity;
import com.example.socialmediatdcproject.activity.SearchFriendActivity;
import com.example.socialmediatdcproject.activity.SharedActivity;
import com.example.socialmediatdcproject.adapter.PostAdapter;
import com.example.socialmediatdcproject.dataModels.Message;
import com.example.socialmediatdcproject.fragment.Admin.HomeAdminFragment;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    FrameLayout frameLayout;
    ArrayList<Post> postList;
    PostAdapter postAdapter;
    RecyclerView recyclerView;
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

        recyclerView = requireActivity().findViewById(R.id.second_content_fragment);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, requireContext());
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
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

        loadPostsFromFirebase();

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

    private void sortPostsByDate() {
        Collections.sort(postList, (post1, post2) -> {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            try {
                Date date1 = format.parse(post1.getCreatedAt());
                Date date2 = format.parse(post2.getCreatedAt());
                return date2.compareTo(date1); // Sắp xếp giảm dần
            } catch (ParseException e) {
                Log.e("PostAdapter", "Date parsing error for postId: " + post1.getPostId() + " or " + post2.getPostId());
                e.printStackTrace();
                return 0; // Không đổi vị trí nếu xảy ra lỗi
            }
        });
    }

    private void loadPostsFromFirebase() {
        GroupAPI groupAPI = new GroupAPI();
        groupAPI.getAllGroups(new GroupAPI.GroupCallback() {
            @Override
            public void onGroupReceived(Group group) {
            }

            @Override
            public void onGroupsReceived(List<Group> groups) {
                for (Group g : groups) {
                    if (g.isGroupDefault()) {
                        DatabaseReference postReference = FirebaseDatabase.getInstance()
                                .getReference("Posts")
                                .child(String.valueOf(g.getGroupId()));

                        postReference.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                Post post = snapshot.getValue(Post.class);
                                if (post != null) {
                                    handlePostAddition(post);
                                }
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                // Nếu cần lắng nghe bài viết bị chỉnh sửa
                                Post updatedPost = snapshot.getValue(Post.class);
                                if (updatedPost != null) {
                                    handlePostUpdate(updatedPost);
                                }
                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                                // Nếu cần lắng nghe bài viết bị xóa
                                Post removedPost = snapshot.getValue(Post.class);
                                if (removedPost != null) {
                                    handlePostRemoval(removedPost);
                                }
                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                // Không cần thiết trong trường hợp này
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("PostAPI", "Error listening to post changes: " + error.getMessage());
                            }
                        });
                    }
                }
            }
        });
    }

    private void handlePostAddition(Post post) {
        if (!post.isFilter()) {
            // Nếu bài viết không cần lọc
            postList.add(post);
            sortPostsByDate();
            postAdapter.notifyDataSetChanged();
        } else {
            // Nếu bài viết cần lọc
            StudentAPI studentAPI = new StudentAPI();
            studentAPI.getStudentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new StudentAPI.StudentCallback() {
                @Override
                public void onStudentReceived(Student student) {
                    FilterPostsAPI filterPostsAPI = new FilterPostsAPI();
                    filterPostsAPI.findUserInReceive(post, student.getUserId(), new FilterPostsAPI.UserInReceiveCallback() {
                        @Override
                        public void onResult(boolean isFound) {
                            if (isFound) {
                                postList.add(post);
                                sortPostsByDate();
                                postAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }

                @Override
                public void onStudentsReceived(List<Student> students) {
                }
            });
        }
    }

    private void handlePostUpdate(Post updatedPost) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getPostId() == updatedPost.getPostId()) {
                postList.set(i, updatedPost);
                postAdapter.notifyItemChanged(i);
                break;
            }
        }
    }
    private void handlePostRemoval(Post removedPost) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getPostId() == removedPost.getPostId()) {
                postList.remove(i);
                postAdapter.notifyItemRemoved(i);
                break;
            }
        }
    }
}
