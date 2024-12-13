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
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.GroupDetaiActivity;
import com.example.socialmediatdcproject.model.Business;
import com.example.socialmediatdcproject.model.Group;
import com.example.socialmediatdcproject.model.User;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.BussinessViewHolder> {

    private List<User> users;

    // Constructor
    public AccountAdapter(List<User> bussinessList) {
        this.users = bussinessList;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public BussinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new BussinessViewHolder(view);
    }

    // Bind dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull BussinessViewHolder holder, int position) {
        User user = users.get(position);
        if (user.getRoleId() == 2 || user.getRoleId() == 3) {
            holder.name.setText(user.getFullName());
            holder.login.setText("Tài khoản: " + user.getEmail());
            holder.pass.setText("Mật khẩu: " + user.getPassword());
        }
    }


    @Override
    public int getItemCount() {
        return users.size();
    }


    // Lớp ViewHolder
    public static class BussinessViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView login;
        TextView pass;

        public BussinessViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.account_name);
            login = itemView.findViewById(R.id.account_email);
            pass = itemView.findViewById(R.id.account_password);

        }
    }


}
