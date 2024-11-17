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

public class EventPersonalAdapter extends RecyclerView.Adapter<EventPersonalAdapter.GroupViewHolder> {

    private List<Event> eventList;
    private Context context;

    // Constructor
    public EventPersonalAdapter(List<Event> eventList, Context context) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_point, parent, false);
        return new GroupViewHolder(view);
    }

    // Bind dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Event event = eventList.get(position);


        Glide.with(context)
                .load(event.getImageEvent())
                .centerCrop()
                .into(holder.imageEvent);
        // Cắt content xuống tối đa 50 ký tự
        String shortContent = getShortenedContent(event.getTitleEvent(), 50);
        holder.titleEvent.setText(shortContent);

        holder.point.setText(event.getDescription());
    }


    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0 ;
    }


    // Lớp ViewHolder
    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView point;
        ImageView imageEvent;
        TextView titleEvent;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            point = itemView.findViewById(R.id.point);
            imageEvent = itemView.findViewById(R.id.image_event);
            titleEvent = itemView.findViewById(R.id.title_event);
        }
    }

    public void changeColorButtonActive(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.defaultBlue));
        btn.setTextColor(ContextCompat.getColorStateList(context, R.color.white));
    }
}
