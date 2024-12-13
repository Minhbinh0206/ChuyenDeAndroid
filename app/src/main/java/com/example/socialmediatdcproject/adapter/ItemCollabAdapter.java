package com.example.socialmediatdcproject.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.CollabAPI;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.NotifyQuicklyAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.GroupDetaiActivity;
import com.example.socialmediatdcproject.dataModels.Collab;
import com.example.socialmediatdcproject.dataModels.NotifyQuickly;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Business;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ItemCollabAdapter extends RecyclerView.Adapter<ItemCollabAdapter.BussinessViewHolder> {

    private List<Business> users;
    private Context context;

    // Constructor
    public ItemCollabAdapter(List<Business> bussinessList, Context context) {
        this.users = bussinessList;
        this.context = context;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public BussinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collab, parent, false);
        return new BussinessViewHolder(view);
    }

    // Bind dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull BussinessViewHolder holder, int position) {
        Business business = users.get(position);

        holder.name.setText(business.getBusinessName());
        Glide.with(context)
                .load(business.getAvatar())
                .circleCrop()
                .into(holder.avatar);

        holder.submit.setOnClickListener(v -> {
            CollabAPI collabAPI = new CollabAPI();
            Collab collab = new Collab();

            AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
            adminDepartmentAPI.getAdminDepartmentByKey(FirebaseAuth.getInstance().getCurrentUser().getUid(), new AdminDepartmentAPI.AdminDepartmentCallBack() {
                @Override
                public void onUserReceived(AdminDepartment adminDepartment) {
                    collab.setDepartmentId(adminDepartment.getDepartmentId());
                    collab.setBusinessId(business.getBusinessId());

                    collabAPI.addCollab(collab);

                    // Xóa phần tử khỏi danh sách users trước khi gọi notifyItemRemoved
                    users.remove(position);
                    notifyItemRemoved(position); // Cập nhật giao diện RecyclerView

                    NotifyQuicklyAPI notifyQuicklyAPI = new NotifyQuicklyAPI();
                    notifyQuicklyAPI.getAllNotifications(new NotifyQuicklyAPI.NotificationCallback() {
                        @Override
                        public void onNotificationsReceived(List<NotifyQuickly> notifications) {
                            NotifyQuickly notifyQuickly = new NotifyQuickly();
                            notifyQuickly.setNotifyId(notifications.size());
                            notifyQuickly.setUserGetId(business.getBusinessAdminId());
                            notifyQuickly.setUserSendId(adminDepartment.getUserId());
                            notifyQuickly.setContent("Xin chúc mừng! " + adminDepartment.getFullName() + " vừa xác nhận hợp tác với doanh nghiệp của bạn.");

                            notifyQuicklyAPI.addNotification(notifyQuickly);
                        }
                    });
                }

                @Override
                public void onUsersReceived(List<AdminDepartment> adminDepartment) {

                }

                @Override
                public void onError(String s) {

                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    // Lớp ViewHolder
    public static class BussinessViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView avatar;
        ImageButton submit;

        public BussinessViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_collab);
            avatar = itemView.findViewById(R.id.avatar_collab);
            submit = itemView.findViewById(R.id.submit_collab);
        }
    }


}
