package com.example.socialmediatdcproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

import java.util.ArrayList;
import java.util.List;

public class ItemFilterAdapter extends RecyclerView.Adapter<ItemFilterAdapter.BussinessViewHolder> {
    private List<String> filterList;
    private List<String> selectedFilters = new ArrayList<>();
    private Context context;

    // Constructor
    public ItemFilterAdapter(List<String> filterList , Context context) {
        this.filterList = filterList;
        this.context = context;
    }

    public List<String> getSelectedFilters() {
        return selectedFilters;
    }

    @NonNull
    @Override
    public BussinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_checkbox, parent, false);
        return new BussinessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BussinessViewHolder holder, int position) {
        String filterName = filterList.get(position);
        holder.checkBox.setText(filterName);

        // Đặt trạng thái checkbox
        holder.checkBox.setChecked(selectedFilters.contains(filterName));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedFilters.contains(filterName)) {
                    selectedFilters.add(filterName);
                    Log.d("TAG", "onBindViewHolder: " + selectedFilters.size());
                }
            } else {
                selectedFilters.remove(filterName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    public static class BussinessViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView nameFilter;

        public BussinessViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            nameFilter = itemView.findViewById(R.id.nameCheckBox);
        }
    }
}