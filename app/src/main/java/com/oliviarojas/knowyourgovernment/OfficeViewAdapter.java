package com.oliviarojas.knowyourgovernment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfficeViewAdapter extends RecyclerView.Adapter<OfficeViewHolder> {

    private List<Office> offices;
    private MainActivity mainActivity;

    public OfficeViewAdapter(List<Office> offices, MainActivity mainActivity) {
        this.offices = offices;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public OfficeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.office_recycler_item, parent, false);
        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new OfficeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficeViewHolder holder, int position) {
        Office office = offices.get(position);
        holder.officeTitle.setText(office.getOfficeTitle());
        holder.officialName.setText(office.getOfficialName());
    }

    @Override
    public int getItemCount() {
        return offices.size();
    }
}
