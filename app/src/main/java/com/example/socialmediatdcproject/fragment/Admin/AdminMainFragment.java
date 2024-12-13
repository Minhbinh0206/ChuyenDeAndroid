package com.example.socialmediatdcproject.fragment.Admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.EventAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.ListEventActivity;
import com.example.socialmediatdcproject.adapter.EventAdapter;
import com.example.socialmediatdcproject.model.Event;

import java.util.ArrayList;
import java.util.List;

public class AdminMainFragment extends Fragment {
    private RecyclerView recyclerView; // RecyclerView để hiển thị danh sách người dùng

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.fragment_home_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.frame_event);

        ImageButton iconThreeDot = view.findViewById(R.id.detail_event_list);
        iconThreeDot.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ListEventActivity.class);
            startActivity(intent);
        });

        loadEventsFromFirebase();
    }

    private void loadEventsFromFirebase() {
        TextView textView = getView().findViewById(R.id.null_event);
        textView.setVisibility(View.GONE);
        EventAPI eventAPI = new EventAPI();
        ArrayList<Event> eventList = new ArrayList<>();
        eventAPI.listenForNewEvent(new EventAPI.OnNewEventListener() {
            @Override
            public void onNewEventAdded(Event event) {
                if (event.getStatus() != 2) {
                    eventList.add(event);
                }

                if (eventList.isEmpty()) {
                    textView.setVisibility(View.VISIBLE);
                } else {
                    textView.setVisibility(View.GONE);
                }

                // Setup RecyclerView với Adapter
                EventAdapter eventAdapter = new EventAdapter(eventList, getContext());
                recyclerView.setAdapter(eventAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);

                // Tự động cuộn
                autoScrollRecyclerView(recyclerView, eventList.size());

                // Đổi alpha khi cuộn
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    private boolean isScrolling = false; // Biến flag để kiểm tra trạng thái cuộn

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        if (dx != 0 || dy != 0) {
                            if (!isScrolling) {
                                isScrolling = true; // Đánh dấu là đang cuộn
                            }
                        } else {
                            isScrolling = false; // Nếu không có cuộn thì không làm gì
                        }
                    }
                });
            }
        });
    }

    private void autoScrollRecyclerView(RecyclerView recyclerView, int itemCount) {
        final Handler handler = new Handler();
        final int delay = 5000; // 5 giây
        final int[] currentIndex = {0}; // Vị trí hiện tại
        final boolean[] isReversing = {false}; // Xác định chiều di chuyển

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (itemCount > 0) {
                    if (!isReversing[0]) {
                        // Lướt tới
                        currentIndex[0]++;
                        if (currentIndex[0] >= itemCount - 1) {
                            isReversing[0] = true; // Đổi chiều khi đến phần tử cuối
                        }
                    } else {
                        // Lướt lùi
                        currentIndex[0]--;
                        if (currentIndex[0] <= 0) {
                            isReversing[0] = false; // Đổi chiều khi về phần tử đầu tiên
                        }
                    }

                    // Cuộn RecyclerView đến vị trí bằng cách sử dụng LinearSmoothScroller
                    AdminMainFragment.CustomScroller customScroller = new AdminMainFragment.CustomScroller(recyclerView.getContext());
                    customScroller.setTargetPosition(currentIndex[0]);
                    recyclerView.getLayoutManager().startSmoothScroll(customScroller);
                }

                // Lặp lại
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private static class CustomScroller extends LinearSmoothScroller {
        public CustomScroller(Context context) {
            super(context);
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return 0.3f; // Điều chỉnh tốc độ cuộn ở đây
        }
    }

}
