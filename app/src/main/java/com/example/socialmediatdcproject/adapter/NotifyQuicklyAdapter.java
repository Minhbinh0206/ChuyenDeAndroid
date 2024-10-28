package com.example.socialmediatdcproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    }

    @Override
    public int getItemCount() {
        return notifyList.size(); // Trả về số lượng thông báo
    }

    // Lớp ViewHolder
    public static class NotifyViewHolder extends RecyclerView.ViewHolder {
        TextView notifyContent;

        public NotifyViewHolder(@NonNull View itemView) {
            super(itemView);
            notifyContent = itemView.findViewById(R.id.content_notify_quickly);
        }
    }
}
