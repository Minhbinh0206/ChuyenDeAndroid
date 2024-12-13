package com.example.socialmediatdcproject.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.EventAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.adapter.EventAdapter;
import com.example.socialmediatdcproject.model.Event;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ListEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_event_layout);

        RecyclerView coming = findViewById(R.id.recycler_coming);
        RecyclerView doing = findViewById(R.id.recycler_doing);
        RecyclerView done = findViewById(R.id.recycler_done);

        TextView nullComing, nullDoing;
        nullComing = findViewById(R.id.null_coming);
        nullDoing = findViewById(R.id.null_doing);

        ImageButton back = findViewById(R.id.icon_back_event);

        back.setOnClickListener(v -> finish());

        ArrayList<Event> e1 = new ArrayList<>();
        ArrayList<Event> e2 = new ArrayList<>();
        ArrayList<Event> e3 = new ArrayList<>();

        EventAPI eventAPI = new EventAPI();
        eventAPI.getAllEvents(new EventAPI.EventCallback() {
            @Override
            public void onEventReceived(Event event) {

            }

            @Override
            public void onEventsReceived(List<Event> events) {
                for (Event e : events) {
                    if (e.getStatus() == 0) {
                        e1.add(e);
                    }
                    else if (e.getStatus() == 1) {
                        e2.add(e);
                    }
                    else {
                        e3.add(e);
                    }
                }

                EventAdapter eventAdapter1 = new EventAdapter(e1, ListEventActivity.this);
                EventAdapter eventAdapter2 = new EventAdapter(e2, ListEventActivity.this);
                EventAdapter eventAdapter3 = new EventAdapter(e3, ListEventActivity.this);

                if (e1.isEmpty()) {
                    nullComing.setVisibility(View.VISIBLE);
                    nullComing.setText("Hiện tại chưa có sự kiện nào");
                }else {
                    nullComing.setVisibility(View.INVISIBLE);
                }

                if (e2.isEmpty()) {
                    nullDoing.setVisibility(View.VISIBLE);
                    nullDoing.setText("Hiện tại chưa có sự kiện nào");
                }else {
                    nullDoing.setVisibility(View.INVISIBLE);
                }

                coming.setAdapter(eventAdapter1);
                coming.setLayoutManager(new LinearLayoutManager(ListEventActivity.this, LinearLayoutManager.HORIZONTAL, false));

                doing.setAdapter(eventAdapter2);
                doing.setLayoutManager(new LinearLayoutManager(ListEventActivity.this, LinearLayoutManager.HORIZONTAL, false));

                done.setAdapter(eventAdapter3);
                done.setLayoutManager(new LinearLayoutManager(ListEventActivity.this, LinearLayoutManager.HORIZONTAL, false));
            }
        });

    }
}
