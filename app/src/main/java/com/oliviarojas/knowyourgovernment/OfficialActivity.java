package com.oliviarojas.knowyourgovernment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class OfficialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        TextView location = findViewById(R.id.locationOfficial);
        TextView title = findViewById(R.id.titleOfficial);
        TextView name = findViewById(R.id.nameOfficial);
        TextView party = findViewById(R.id.partyOfficial);
        TextView address = findViewById(R.id.address);
        TextView phone = findViewById(R.id.phone);
        TextView website = findViewById(R.id.website);

        Intent intent = getIntent();
        if (intent.hasExtra("Location")) {
            location.setText(intent.getStringExtra("Location"));
        }
        if (intent.hasExtra("Official")) {
            Official official = (Official) intent.getSerializableExtra("Official");
            title.setText(official.getTitle());
            name.setText(official.getName());
            party.setText(official.getParty());
            address.setText(official.getAddress());
            phone.setText(official.getPhone());
            website.setText(official.getUrl());
        }
    }
}
