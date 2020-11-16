package com.oliviarojas.knowyourgovernment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfficeViewAdapter extends RecyclerView.Adapter<OfficeViewHolder> {

    private List<Official> offices;
    private MainActivity mainActivity;

    public OfficeViewAdapter(List<Official> offices, MainActivity mainActivity) {
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
        Official official = offices.get(position);
        holder.officeTitle.setText(official.getTitle());
        String nameAndParty = official.getName() + " " + official.getParty();
        holder.officialName.setText(nameAndParty);
    }

    @Override
    public int getItemCount() {
        return offices.size();
    }
}
