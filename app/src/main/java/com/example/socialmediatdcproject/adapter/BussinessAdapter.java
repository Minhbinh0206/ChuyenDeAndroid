package com.example.socialmediatdcproject.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.database.UserDatabase;
import com.example.socialmediatdcproject.model.Bussiness;
import com.example.socialmediatdcproject.model.Lecturer;
import com.example.socialmediatdcproject.model.Bussiness;
import com.example.socialmediatdcproject.model.User;

import java.util.List;

public class BussinessAdapter extends RecyclerView.Adapter<BussinessAdapter.BussinessViewHolder> {

    private List<Bussiness> bussinessList;

    // Constructor
    public BussinessAdapter(List<Bussiness> bussinessList) {
        this.bussinessList = bussinessList;
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
        Bussiness bussiness = bussinessList.get(position);
        UserDatabase userDatabase = new UserDatabase();
        if (bussiness != null) {
            // Set dữ liệu cho các view
            holder.bussinessName.setText(bussiness.getBussinessName());
            holder.bussinessAddress.setText(bussiness.getAddress());
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
        ImageView groupAvatar;
        TextView bussinessName;
        TextView bussinessAddress;

        public BussinessViewHolder(@NonNull View itemView) {
            super(itemView);
            bussinessName = itemView.findViewById(R.id.bussiness_name);
            bussinessAddress = itemView.findViewById(R.id.bussiness_address);
        }
    }
}
