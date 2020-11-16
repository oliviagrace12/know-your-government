package com.oliviarojas.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private OfficeViewAdapter adapter;
    private RecyclerView recyclerView;
    private List<Official> officials = new ArrayList<>();
    private String location = "60623";
    private TextView locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new OfficeViewAdapter(officials, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        locationTextView = findViewById(R.id.userLocation);
        locationTextView.setText(location);

        new Thread(new InfoRetriever(location, this)).start();

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.about == item.getItemId()) {
            Toast.makeText(this, "Clicked on about", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Search for location", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (officials.isEmpty()) {
            return;
        }
        int position = recyclerView.getChildLayoutPosition(v);
        Official official = officials.get(position);

        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("Location", location);
        intent.putExtra("Official", official);
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        return true;
    }

    public void setLocation(String location) {
        this.location = location;
        locationTextView.setText(location);
    }

    public void addOfficials(List<Official> newOfficials) {
        officials.addAll(newOfficials);
        adapter.notifyDataSetChanged();
    }

}