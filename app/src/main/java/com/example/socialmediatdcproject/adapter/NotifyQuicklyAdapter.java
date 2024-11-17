package com.example.socialmediatdcproject.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediatdcproject.API.NotifyQuicklyAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.dataModels.NotifyQuickly;
import java.util.List;

public class NotifyQuicklyAdapter extends RecyclerView.Adapter<NotifyQuicklyAdapter.NotifyViewHolder> {

    private List<NotifyQuickly> notifyList;
    private int selectedIndex = -1; // Khởi tạo với giá trị không hợp lệ
    private Handler handler = new Handler(); // Thêm handler để lập lịch
    private Runnable bellRunnable; // Thêm runnable cho chuông

    // Constructor
    public NotifyQuicklyAdapter(List<NotifyQuickly> notifyList) {
        this.notifyList = notifyList;
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index; // Cập nhật chỉ mục hiện tại
        notifyDataSetChanged(); // Cập nhật giao diện
    }

    public void addNotification(NotifyQuickly notification) {
        notifyList.add(notification); // Sử dụng notifyList thay vì notifyQuicklies
        notifyItemInserted(notifyList.size() - 1); // Thông báo cho RecyclerView có mục mới
    }

    public void clearNotifications() {
        notifyList.clear(); // Xóa tất cả thông báo
        notifyDataSetChanged(); // Cập nhật giao diện
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public NotifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popup_invitation_layout, parent, false);
        return new NotifyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifyViewHolder holder, int position) {
        NotifyQuickly notification = notifyList.get(position); // Lấy thông báo theo chỉ mục

        // Hiển thị nội dung thông báo
        holder.notifyContent.setText(notification.getContent());

        scheduleBellAnimation(holder.bellIcon);
    }

    @Override
    public int getItemCount() {
        return notifyList.size(); // Trả về số lượng thông báo
    }

    // Lớp ViewHolder
    public static class NotifyViewHolder extends RecyclerView.ViewHolder {
        TextView notifyContent;
        ImageView bellIcon;

        public NotifyViewHolder(@NonNull View itemView) {
            super(itemView);
            notifyContent = itemView.findViewById(R.id.content_notify_quickly);
            bellIcon = itemView.findViewById(R.id.bell_icon);
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
}
