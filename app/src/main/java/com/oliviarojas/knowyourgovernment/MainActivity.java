package com.oliviarojas.knowyourgovernment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private OfficeViewAdapter adapter;
    private RecyclerView recyclerView;
    private List<Official> officials = new ArrayList<>();
    private String location = "";
    private TextView locationTextView;
    private static int MY_LOCATION_REQUEST_CODE_ID = 111;
    private LocationManager locationManager;
    private Criteria criteria;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new OfficeViewAdapter(officials, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        locationTextView = findViewById(R.id.userLocation);

        if (!isConnectedToNetwork()) {
            showNoNetworkDialogue("New data cannot be retrieved");
            loadSavedOfficialsAndLocation();
        } else {
            location = getLocationFromGPS();
            updateOfficialDataForLocation(location);
        }
    }

    private String getLocationFromGPS() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        criteria = new Criteria();

        // use gps for location
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE_ID);
        } else {
            return setLocation();
        }
        return "";
    }

    @SuppressLint("MissingPermission")
    private String setLocation() {

        String bestProvider = locationManager.getBestProvider(criteria, true);

        Location currentLocation = null;
        if (bestProvider != null) {
            currentLocation = locationManager.getLastKnownLocation(bestProvider);
        }
        if (currentLocation != null) {
            return String.format(Locale.getDefault(),
                            "%.4f, %.4f", currentLocation.getLatitude(), currentLocation.getLongitude());
        } else {
            return "";
        }


    }

    private void updateOfficialDataForLocation(String location) {
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
            if (!isConnectedToNetwork()) {
                showNoNetworkDialogue("Data cannot be retrieved");
            } else {
                showLocationDialogue();
            }
        }
        return true;
    }

    private void showLocationDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a City, State or a Zip Code:");

        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.location_dialogue, null);
        builder.setView(view);
        final EditText editText = view.findViewById(R.id.locationEnter);
        builder.setPositiveButton("OK", (dialog, id) -> {
            updateOfficialDataForLocation(editText.getText().toString());
        });
        builder.setNegativeButton("CANCEL", (dialog, id) -> {
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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

    public void updateOfficials(List<Official> newOfficials) {
        officials.clear();
        officials.addAll(newOfficials);
        adapter.notifyDataSetChanged();
        saveOfficials();
    }

    private void showNoNetworkDialogue(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isConnectedToNetwork() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void saveOfficials() {
        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));
            buildJson(writer);

            // LOGGING
            StringWriter sw = new StringWriter();
            writer = new JsonWriter(sw);
            buildJson(writer);
            Log.d(TAG, "Saving officials: \n" + sw.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildJson(JsonWriter writer) throws IOException {
        writer.setIndent("  ");
        writer.beginObject();
        writer.name("location").value(location);
        writer.name("officials");
        writer.beginArray();
        for (Official official : officials) {
            writer.beginObject();
            writer.name("name").value(official.getName());
            writer.name("title").value(official.getTitle());
            writer.name("address").value(official.getAddress());
            writer.name("city").value(official.getCity());
            writer.name("state").value(official.getState());
            writer.name("zip").value(official.getZip());
            writer.name("party").value(official.getParty());
            writer.name("phone").value(official.getPhone());
            writer.name("url").value(official.getUrl());
            writer.name("email").value(official.getEmail());
            writer.name("photoUrl").value(official.getPhotoUrl());
            writer.name("facebookId").value(official.getFacebookId());
            writer.name("twitterId").value(official.getTwitterId());
            writer.name("youTubeId").value(official.getYouTubeId());
            writer.endObject();
        }
        writer.endArray();
        writer.endObject();
        writer.close();
    }

    private void loadSavedOfficialsAndLocation() {
        try {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.file_name));
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }


            JSONObject data = new JSONObject(sb.toString());
            location = data.getString("location");
            JSONArray jsonArray = data.getJSONArray("officials");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject officialJson = jsonArray.getJSONObject(i);
                Official official = new Official();
                official.setName(officialJson.getString("name"));
                official.setTitle(officialJson.getString("title"));
                official.setAddress(officialJson.getString("address"));
                official.setCity(officialJson.getString("city"));
                official.setState(officialJson.getString("state"));
                official.setZip(officialJson.getString("zip"));
                official.setParty(officialJson.getString("party"));
                official.setPhone(officialJson.getString("phone"));
                official.setUrl(officialJson.getString("url"));
                official.setEmail(officialJson.getString("email"));
                official.setPhotoUrl(officialJson.getString("photoUrl"));
                official.setFacebookId(officialJson.getString("facebookId"));
                official.setTwitterId(officialJson.getString("twitterId"));
                official.setYouTubeId(officialJson.getString("youTubeId"));
                officials.add(official);
            }
        } catch (Exception e) {
            Log.e(TAG, "loadSavedOfficials: ", e);
        }
        adapter.notifyDataSetChanged();

        locationTextView = findViewById(R.id.userLocation);
        locationTextView.setText(location);
    }

}