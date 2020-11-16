package com.oliviarojas.knowyourgovernment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        TextView location = findViewById(R.id.locationPhoto);
        TextView title = findViewById(R.id.titlePhoto);
        TextView name = findViewById(R.id.namePhoto);

        Intent intent = getIntent();
        if (intent.hasExtra("Location")) {
            location.setText(intent.getStringExtra("Location"));
        }
        if (intent.hasExtra("Official")) {
            Official official = (Official) intent.getSerializableExtra("Official");
            title.setText(official.getTitle());
            name.setText(official.getName());
        }
    }

}
