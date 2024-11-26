package com.example.socialmediatdcproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.GroupAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.GroupDetaiActivity;
import com.example.socialmediatdcproject.model.Business;
import com.example.socialmediatdcproject.model.Group;

import java.util.List;

public class BussinessAdapter extends RecyclerView.Adapter<BussinessAdapter.BussinessViewHolder> {

    private List<Business> bussinessList;
    private Context context;

    // Constructor
    public BussinessAdapter(List<Business> bussinessList , Context context) {
        this.bussinessList = bussinessList;
        this.context = context;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public BussinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bussiness_list, parent, false);
        return new BussinessViewHolder(view);
    }

    // Bind dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull BussinessViewHolder holder, int position) {
        Business bussiness = bussinessList.get(position);
        if (bussiness != null) {
            // Set dữ liệu cho các view
            holder.bussinessName.setText(bussiness.getBusinessName());
            holder.bussinessAddress.setText(bussiness.getAddress());

            if (context != null && holder.bussinessImage != null) {
                Glide.with(context)
                        .load(bussiness.getAvatar())
                        .circleCrop()
                        .into(holder.bussinessImage);
            }

            holder.cardView.setOnClickListener(v -> {
                GroupAPI groupAPI = new GroupAPI();
                groupAPI.getGroupById(bussiness.getGroupId(), new GroupAPI.GroupCallback() {
                    @Override
                    public void onGroupReceived(Group group) {
                        Intent intent = new Intent(v.getContext(), GroupDetaiActivity.class);
                        intent.putExtra("groupId", group.getGroupId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        v.getContext().startActivity(intent);
                    }

                    @Override
                    public void onGroupsReceived(List<Group> groups) {

                    }
                });
            });
        } else {
            Log.e("BusinessAdapter", "Bussiness at position " + position + " is null");
        }
    }


    @Override
    public int getItemCount() {
        return bussinessList.size();
    }


    // Lớp ViewHolder
    public static class BussinessViewHolder extends RecyclerView.ViewHolder {
        ImageView bussinessImage;
        TextView bussinessName;
        TextView bussinessAddress;
        CardView cardView;

        public BussinessViewHolder(@NonNull View itemView) {
            super(itemView);
            bussinessName = itemView.findViewById(R.id.bussiness_name);
            bussinessAddress = itemView.findViewById(R.id.bussiness_address);
            bussinessImage = itemView.findViewById(R.id.bussiness_avatar);
            cardView = itemView.findViewById(R.id.item_business);
        }
    }


}
