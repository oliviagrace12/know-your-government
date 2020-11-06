package com.oliviarojas.knowyourgovernment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OfficeViewHolder extends RecyclerView.ViewHolder {

    TextView officeTitle;
    TextView officialName;

    public OfficeViewHolder(@NonNull View itemView) {
        super(itemView);
        officeTitle = itemView.findViewById(R.id.officeTitle);
        officialName = itemView.findViewById(R.id.officialName);
    }
}
