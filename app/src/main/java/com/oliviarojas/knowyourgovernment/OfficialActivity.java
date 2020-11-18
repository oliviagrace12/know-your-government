package com.oliviarojas.knowyourgovernment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    private static final String TAG = "OfficialActivity";
    private Official official;
    private String locationString;

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
        TextView email = findViewById(R.id.email);
        TextView website = findViewById(R.id.website);

        Intent intent = getIntent();
        if (intent.hasExtra("Location")) {
            locationString = intent.getStringExtra("Location");
            location.setText(locationString);
        }
        if (intent.hasExtra("Official")) {
            official = (Official) intent.getSerializableExtra("Official");
            title.setText(official.getTitle());
            name.setText(official.getName());
            party.setText(official.getParty());
            address.setText(official.getAddress());
            phone.setText(official.getPhone());
            email.setText(official.getEmail());
            website.setText(official.getUrl());
            loadRemoteImage(official.getPhotoUrl());
            setPartyDesign(official);

            Linkify.addLinks(address, Linkify.ALL);
            Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
            Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
            Linkify.addLinks(website, Linkify.WEB_URLS);

            if (official.getFacebookId() != null) {
                ImageView facebookIcon = findViewById(R.id.facebookImage);
                facebookIcon.setImageResource(R.drawable.facebook);
            }
            if (official.getTwitterId() != null) {
                ImageView twitterIcon = findViewById(R.id.twitterImage);
                twitterIcon.setImageResource(R.drawable.twitter);
            }
            if (official.getYouTubeId() != null) {
                ImageView youTubeIcon = findViewById(R.id.youtubeImage);
                youTubeIcon.setImageResource(R.drawable.youtube);
            }
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
        final long start = System.currentTimeMillis(); // Used for timing

        ImageView imageView = findViewById(R.id.officialPhoto);

        if (imageURL.isEmpty()) {
            imageView.setImageResource(R.drawable.missing);
            return;
        }

        Picasso.get().load(imageURL)
                .error(R.drawable.brokenimage)
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

    public void openPhotoActivity(View view) {
        if (official.getPhotoUrl() == null || official.getPhotoUrl().isEmpty()) {
            return;
        }
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("Location", locationString);
        intent.putExtra("Official", official);
        startActivity(intent);
    }

    public void facebookClicked(View v) {
        String facebookId = official.getFacebookId();
        if (facebookId == null) {
            return;
        }
        String FACEBOOK_URL = "https://www.facebook.com/" + facebookId;
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + facebookId;
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void youTubeClicked(View v) {
        String name = official.getYouTubeId();
        if (name == null) {
            return;
        }
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));

        }
    }

    public void twitterClicked(View v) {
        Intent intent = null;
        String name = official.getTwitterId();
        if (name == null) {
            return;
        }
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }
}
