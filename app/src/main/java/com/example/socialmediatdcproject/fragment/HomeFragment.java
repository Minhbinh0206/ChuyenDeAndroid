package com.example.socialmediatdcproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socialmediatdcproject.R; // Import đúng package chứa R
import com.example.socialmediatdcproject.activity.SearchFriendActivity;

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

        frameLayout = requireActivity().findViewById(R.id.third_content_fragment);
        frameLayout.setVisibility(view.GONE);

        ImageButton searchFriends = view.findViewById(R.id.search_friends);

        searchFriends.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SearchFriendActivity.class);
            startActivity(intent);
        });
    }
}
