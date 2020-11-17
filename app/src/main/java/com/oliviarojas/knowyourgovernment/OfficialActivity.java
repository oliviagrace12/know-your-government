package com.oliviarojas.knowyourgovernment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    private static final String TAG = "OfficialActivity";

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
            loadRemoteImage(official.getPhotoUrl());
            setPartyDesign(official);
        }
    }

    private void setPartyDesign(Official official) {
        ImageView partySymbol = findViewById(R.id.partyImageOfficial);
        ScrollView background = findViewById(R.id.scrollViewOfficial);

        if (official.getParty().contains("Republican")) {
            partySymbol.setImageResource(R.drawable.rep_logo);
            background.setBackgroundColor(this.getResources().getColor(R.color.colorBackgroundRep, null));
        } else if (official.getParty().contains("Democrat")) {
            partySymbol.setImageResource(R.drawable.dem_logo);
            background.setBackgroundColor(this.getResources().getColor(R.color.colorBackgroundDem, null));
        } else {
            background.setBackgroundColor(this.getResources().getColor(R.color.colorBlack, null));
        }
    }

    private void loadRemoteImage(final String imageURL) {
        // Needs gradle  implementation 'com.squareup.picasso:picasso:2.71828'


        final long start = System.currentTimeMillis(); // Used for timing

        ImageView imageView = findViewById(R.id.officialPhoto);

        if (imageURL.isEmpty()) {
            Picasso.get().load(R.drawable.missing).into(imageView);
            return;
        }

        Picasso.get().load(imageURL)
                .error(R.drawable.missing)
                .placeholder(R.drawable.placeholder)
                //.into(imageView); // Use this if you don't want a callback
                .into(imageView,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "onSuccess: Size: " +
                                        ((BitmapDrawable) imageView.getDrawable()).getBitmap().getByteCount());
                                long dur = System.currentTimeMillis() - start;
                                Log.d(TAG, "onSuccess: Time: " + dur);
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.d(TAG, "onError: " + e.getMessage());
                            }
                        });
    }

}
