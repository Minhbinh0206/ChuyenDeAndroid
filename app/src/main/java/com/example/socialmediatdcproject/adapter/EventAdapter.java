package com.example.socialmediatdcproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.AnswerAPI;
import com.example.socialmediatdcproject.API.EventAPI;
import com.example.socialmediatdcproject.API.QuestionAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.EventDetailActivity;
import com.example.socialmediatdcproject.activity.GroupDetaiActivity;
import com.example.socialmediatdcproject.activity.SharedActivity;
import com.example.socialmediatdcproject.dataModels.Answer;
import com.example.socialmediatdcproject.dataModels.Question;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Assist;
import com.example.socialmediatdcproject.model.Event;
import com.example.socialmediatdcproject.model.RollCall;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.GroupViewHolder> {

    private List<Event> eventList;
    private Context context;
    private Handler handler;
    private Runnable runnable;

    // Constructor
    public EventAdapter(List<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }

    private String getShortenedContent(String content, int maxLength) {
        if (content != null && content.length() > maxLength) {
            return content.substring(0, maxLength) + "..."; // Thêm dấu "..." nếu chuỗi bị cắt
        } else {
            return content;
        }
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new GroupViewHolder(view);
    }

    // Bind dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Event event = eventList.get(position);

        // Set up handler to periodically check event status
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                fetchEventDetails(event.getEventId(), holder.status, new FetchEventCallback() {
                    @Override
                    public void onEventEnded(boolean isEnded) {

                    }
                });
                handler.postDelayed(this, 5000); // 5 seconds
            }
        };
        handler.post(runnable); // Start the periodic check

        Glide.with(context)
                .load(event.getImageEvent())
                .centerCrop()
                .into(holder.imageEvent);
        // Cắt content xuống tối đa 50 ký tự
        String shortContent = getShortenedContent(event.getTitleEvent(), 30);
        holder.titleEvent.setText(shortContent + "...");

        changeColorButtonActive(holder.detailEvent);

        holder.detailEvent.setOnClickListener(v -> {
            // Kiểm tra trạng thái sự kiện
            fetchEventDetails(event.getEventId(), holder.status, isEventEnded -> {
                if (isEventEnded) {
                    Toast.makeText(context, "Sự kiện đã kết thúc", Toast.LENGTH_SHORT).show();
                } else {
                    // Mở Activity chi tiết
                    final int[] typeJoin = {-1};
                    Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
                    EventAPI eventAPI = new EventAPI();
                    eventAPI.getEventById(event.getEventId(), new EventAPI.EventCallback() {
                        @Override
                        public void onEventReceived(Event event) {
                            String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            StudentAPI studentAPI = new StudentAPI();
                            studentAPI.getStudentByKey(userKey, new StudentAPI.StudentCallback() {
                                @Override
                                public void onStudentReceived(Student student) {
                                    if (event.getUserJoin() != null) {
                                        for (RollCall r : event.getUserJoin()) {
                                            if (r.getStudentNumber().equals(student.getStudentNumber())){
                                                typeJoin[0] = 0;
                                                intent.putExtra("typeJoin", typeJoin[0]);
                                            }
                                        }
                                    }

                                    if (event.getUserAssist() != null) {
                                        for (Assist a : event.getUserAssist()) {
                                            if (a.getUserId() == student.getUserId()) {
                                                if (a.isAssist()) {
                                                    typeJoin[0] = 1;
                                                    intent.putExtra("typeJoin", typeJoin[0]);
                                                } else {
                                                    typeJoin[0] = 2;
                                                    intent.putExtra("typeJoin", typeJoin[0]);
                                                }
                                            }
                                        }
                                    }

                                    intent.putExtra("eventId", event.getEventId());
                                    v.getContext().startActivity(intent);
                                }

                                @Override
                                public void onStudentsReceived(List<Student> students) {
                                    // Handle if needed
                                }
                            });

                            AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
                            adminDepartmentAPI.getAdminDepartmentByKey(userKey, new AdminDepartmentAPI.AdminDepartmentCallBack() {
                                @Override
                                public void onUserReceived(AdminDepartment adminDepartment) {
                                    if (event.getAdminEventId() == adminDepartment.getUserId()) {
                                        typeJoin[0] = 3;
                                        intent.putExtra("eventId", event.getEventId());
                                        intent.putExtra("typeJoin", typeJoin[0]);
                                        v.getContext().startActivity(intent);
                                    }
                                    else {
                                        Toast.makeText(context, "Sự kiện này không thuộc quản lý của bạn", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onUsersReceived(List<AdminDepartment> adminDepartment) {

                                }

                                @Override
                                public void onError(String s) {

                                }
                            });

                            AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
                            adminBusinessAPI.getAdminBusinessByKey(userKey, new AdminBusinessAPI.AdminBusinessCallBack() {
                                @Override
                                public void onUserReceived(AdminBusiness adminBusiness) {
                                    if (event.getAdminEventId() == adminBusiness.getUserId()) {
                                        typeJoin[0] = 3;
                                        intent.putExtra("eventId", event.getEventId());
                                        intent.putExtra("typeJoin", typeJoin[0]);
                                        v.getContext().startActivity(intent);
                                    }
                                    else {
                                        Toast.makeText(context, "Sự kiện này không thuộc quản lý của bạn", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onUsersReceived(List<AdminBusiness> adminBusiness) {

                                }

                                @Override
                                public void onError(String s) {

                                }
                            });

                            AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();
                            adminDefaultAPI.getAdminDefaultByKey(userKey, new AdminDefaultAPI.AdminDefaultCallBack() {
                                @Override
                                public void onUserReceived(AdminDefault adminDefault) {
                                    if (!adminDefault.getAdminType().equals("Super")) {
                                        if (event.getAdminEventId() == adminDefault.getUserId()) {
                                            typeJoin[0] = 3;
                                            intent.putExtra("eventId", event.getEventId());
                                            intent.putExtra("typeJoin", typeJoin[0]);
                                            v.getContext().startActivity(intent);
                                        }
                                        else {
                                            Toast.makeText(context, "Sự kiện này không thuộc quản lý của bạn", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onUsersReceived(List<AdminDefault> adminDefault) {

                                }
                            });
                        }

                        @Override
                        public void onEventsReceived(List<Event> events) {
                            // Handle if needed
                        }
                    });
                }
            });
        });
    }


    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0 ;
    }


    // Lớp ViewHolder
    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView status;
        ImageView imageEvent;
        TextView titleEvent;
        Button detailEvent;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.status_event);
            imageEvent = itemView.findViewById(R.id.image_event);
            titleEvent = itemView.findViewById(R.id.title_event);
            detailEvent = itemView.findViewById(R.id.event_detail);
        }
    }

    public void fetchEventDetails(int eventId, TextView textView, FetchEventCallback callback) {
        EventAPI eventAPI = new EventAPI();
        eventAPI.getEventById(eventId, new EventAPI.EventCallback() {
            @Override
            public void onEventReceived(Event event) {
                // Lấy thời gian hiện tại
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                String currentTimeString = sdf.format(new Date());

                try {
                    // Chuyển đổi thời gian hiện tại và thời gian của sự kiện sang đối tượng Date
                    Date currentTime = sdf.parse(currentTimeString);
                    Date beginAt = sdf.parse(event.getBeginAt());
                    Date finishAt = sdf.parse(event.getFinishAt());

                    // So sánh thời gian
                    if (currentTime.before(beginAt)) {
                        textView.setText("Sắp diễn ra");
                        callback.onEventEnded(false);
                    } else if (currentTime.before(finishAt)) {
                        textView.setText("Đang diễn ra");
                        callback.onEventEnded(false);
                    } else {
                        textView.setText("Đã kết thúc");
                        callback.onEventEnded(true);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                    textView.setText("Lỗi định dạng ngày");
                    callback.onEventEnded(false);
                }
            }

            @Override
            public void onEventsReceived(List<Event> events) {
                // Handle if needed
            }
        });
    }

    // Interface callback
    public interface FetchEventCallback {
        void onEventEnded(boolean isEnded);
    }

    public void changeColorButtonActive(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.defaultBlue));
        btn.setTextColor(ContextCompat.getColorStateList(context, R.color.white));
    }
}
