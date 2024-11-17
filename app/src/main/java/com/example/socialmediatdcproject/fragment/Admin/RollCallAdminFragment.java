package com.example.socialmediatdcproject.fragment.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.EventAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.AssistAdapter;
import com.example.socialmediatdcproject.adapter.RollCallAdapter;
import com.example.socialmediatdcproject.model.Assist;
import com.example.socialmediatdcproject.model.Event;
import com.example.socialmediatdcproject.model.RollCall;

import java.util.ArrayList;
import java.util.List;

public class RollCallAdminFragment extends Fragment {
    RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.roll_call_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Intent intent = requireActivity().getIntent();
        int eventId = intent.getIntExtra("eventId", -1);

        recyclerView = view.findViewById(R.id.reycleview_admin);

        Button listAssist = view.findViewById(R.id.list_assistant);
        Button listApprove = view.findViewById(R.id.list_apply_for_assist);
        
        changeColorButtonActive(listAssist);
        changeColorButtonNormal(listApprove);

        loadListAssist(eventId);

        listAssist.setOnClickListener(v -> {
            loadListAssist(eventId);

            changeColorButtonActive(listAssist);
            changeColorButtonNormal(listApprove);
        });

        listApprove.setOnClickListener(v -> {
            loadListAssistApprove(eventId);

            changeColorButtonActive(listApprove);
            changeColorButtonNormal(listAssist);
        });
    }

    private void loadListAssist(int eventId){
        EventAPI eventAPI = new EventAPI();
        List<Assist> assists = new ArrayList<>();
        eventAPI.getEventById(eventId, new EventAPI.EventCallback() {
            @Override
            public void onEventReceived(Event event) {
                for (Assist a : event.getUserAssist()) {
                    if (a.isAssist()) {
                        assists.add(a);
                    }
                    // Cập nhật RecyclerView sau khi thêm tất cả member
                    AssistAdapter assistAdapter = new AssistAdapter(assists, requireContext());
                    recyclerView.removeAllViews();
                    recyclerView.setAdapter(assistAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                }


            }

            @Override
            public void onEventsReceived(List<Event> events) {

            }
        });
    }

    private void loadListAssistApprove(int eventId){
        EventAPI eventAPI = new EventAPI();
        List<Assist> assists = new ArrayList<>();
        eventAPI.getEventById(eventId, new EventAPI.EventCallback() {
            @Override
            public void onEventReceived(Event event) {
                for (Assist a : event.getUserAssist()) {
                    if (!a.isAssist()) {
                        assists.add(a);
                    }
                    // Cập nhật RecyclerView sau khi thêm tất cả member
                    AssistAdapter assistAdapter = new AssistAdapter(assists, requireContext());
                    recyclerView.removeAllViews();
                    recyclerView.setAdapter(assistAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                }
            }

            @Override
            public void onEventsReceived(List<Event> events) {

            }
        });
    }

    private void changeColorButtonActive(Button btn) {
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
        btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
    }
    
    private void changeColorButtonNormal(Button btn) {
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.defaultBlue));
    }
    
}
