package com.example.socialmediatdcproject.fragment.Student;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.EventAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.RollCallAdapter;
import com.example.socialmediatdcproject.model.Event;
import com.example.socialmediatdcproject.model.RollCall;
import com.example.socialmediatdcproject.model.Student;

import java.util.ArrayList;
import java.util.List;

public class RollCallAssistFragment extends Fragment {
    RecyclerView recyclerView;
    private List<RollCall> rollCallList = new ArrayList<>();
    private List<RollCall> filteredList = new ArrayList<>();
    RollCallAdapter rollCallAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.roll_call_assist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Intent intent = requireActivity().getIntent();
        int eventId = intent.getIntExtra("eventId", -1);

        EditText search = view.findViewById(R.id.search_roll_call);
        recyclerView = view.findViewById(R.id.recycle_assist);

        rollCallList = new ArrayList<>();
        EventAPI eventAPI = new EventAPI();
        eventAPI.getEventById(eventId, new EventAPI.EventCallback() {
            @Override
            public void onEventReceived(Event event) {
                if (event.getUserJoin() != null) {
                    for (RollCall r : event.getUserJoin()) {
                        if (r.getIsVerify() == 0) {
                            rollCallList.add(r);
                        }
                    }
                }

                filteredList = new ArrayList<>(rollCallList); // Khởi tạo filteredList

                rollCallAdapter = new RollCallAdapter(filteredList, getContext());
                recyclerView.setAdapter(rollCallAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        rollCallAdapter.filter(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }

            @Override
            public void onEventsReceived(List<Event> events) {}
        });
    }
}
