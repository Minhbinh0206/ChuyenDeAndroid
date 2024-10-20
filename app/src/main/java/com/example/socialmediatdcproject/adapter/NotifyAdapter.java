package com.example.socialmediatdcproject.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Handler; // Thêm import này
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.NotifyAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.NotifyDetailActivity;
import com.example.socialmediatdcproject.model.Notify;
import com.example.socialmediatdcproject.model.User;

import java.util.List;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.NotifyViewHolder> {

    private List<Notify> notifyList;
    private Handler handler = new Handler(); // Thêm handler để lập lịch
    private Runnable bellRunnable; // Thêm runnable cho chuông

    // Constructor
    public NotifyAdapter(List<Notify> memberList) {
        this.notifyList = memberList;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public NotifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notify_list, parent, false);
        return new NotifyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifyAdapter.NotifyViewHolder holder, int position) {
        Notify notify = notifyList.get(position);
        UserAPI userAPI = new UserAPI();

        if (notify != null) {
            // Set dữ liệu cho các view
            userAPI.getAllUsers(new UserAPI.UserCallback() {
                @Override
                public void onUserReceived(User user) {
                    // Không làm gì ở đây
                }

                @Override
                public void onUsersReceived(List<User> users) {
                    for (User u : users) {
                        if (u.getUserId() == notify.getUserSendId()) {
                            holder.userSendID.setText(u.getFullName());
                            break;
                        }
                    }
                }
            });

            holder.notifyTitle.setText(notify.getNotifyTitle());
            holder.notifyContent.setText(notify.getNotifyContent());

            Log.d("NotifyAdapter", "Notify Title: " + notify.getNotifyTitle() + ", isRead: " + notify.getIsRead());

            // Chỉ rung chuông khi isRead là false
            if (notify.getIsRead() == 0) {
                scheduleBellAnimation(holder.bellIcon);
            } else {
                holder.bellIcon.clearAnimation(); // Dừng mọi animation của chuông nếu đã đọc
            }

            holder.buttonRead.setOnClickListener(v -> {
                // Đánh dấu là đã đọc
                notify.setIsRead(1);
                NotifyAPI notifyAPI = new NotifyAPI();
                notifyAPI.updateNotification(notify, new NotifyAPI.NotificationCallback() {
                    @Override
                    public void onNotificationsReceived(List<Notify> notifications) {
                        // Update thanh cong
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                });

                Intent intent = new Intent(v.getContext(), NotifyDetailActivity.class);
                intent.putExtra("notifyId", notify.getNotifyId());
                v.getContext().startActivity(intent);
            });
        } else {
            Log.e("NotifyAdapter", "Notify at position " + position + " is null");
        }
    }


    @Override
    public int getItemCount() {
        return notifyList.size();
    }

    // Lớp ViewHolder
    public static class NotifyViewHolder extends RecyclerView.ViewHolder {
        TextView userSendID;
        TextView notifyTitle;
        TextView notifyContent;
        ImageView bellIcon;
        Button buttonRead;

        public NotifyViewHolder(@NonNull View itemView) {
            super(itemView);
            userSendID = itemView.findViewById(R.id.notify_user_send);
            notifyTitle = itemView.findViewById(R.id.notify_title);
            notifyContent = itemView.findViewById(R.id.notify_content);
            bellIcon = itemView.findViewById(R.id.bell_icon);
            buttonRead = itemView.findViewById(R.id.button_read_notify);
        }
    }

    private void scheduleBellAnimation(View view) {
        // Lập lịch rung chuông mỗi 3 giây
        bellRunnable = new Runnable() {
            @Override
            public void run() {
                startBellAnimation(view);
                handler.postDelayed(this, 1000); // Lập lịch lại sau 1 giây
            }
        };
        handler.post(bellRunnable); // Bắt đầu lập lịch
    }

    private void startBellAnimation(View view) {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "translationX", 0f, 15f);
        ObjectAnimator animatorXReverse = ObjectAnimator.ofFloat(view, "translationX", 15f, -15f);
        ObjectAnimator animatorXBack = ObjectAnimator.ofFloat(view, "translationX", -15f, 15f);
        ObjectAnimator animatorXReturn = ObjectAnimator.ofFloat(view, "translationX", 15f, 0f);

        animatorX.setDuration(100);
        animatorXReverse.setDuration(100);
        animatorXBack.setDuration(100);
        animatorXReturn.setDuration(100);

        // Tạo AnimatorSet để chạy các animation liên tiếp
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animatorX, animatorXReverse, animatorXReturn);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.start();
    }

    // Đừng quên dừng lại khi adapter không còn sử dụng
    public void stopAnimation() {
        handler.removeCallbacks(bellRunnable); // Dừng lịch
    }
}
