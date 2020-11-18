package com.oliviarojas.knowyourgovernment;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoRetriever implements Runnable {

    private static final String TAG = "InfoRetriever";
    private final String address;
    private final MainActivity mainActivity;
    private String apiKey = "";

    public InfoRetriever(String address, MainActivity mainActivity) {
        this.address = address;
        this.mainActivity = mainActivity;
    }


    @Override
    public void run() {
        String urlString = "https://www.googleapis.com/civicinfo/v2/representatives?key=" + apiKey + "&address=" + address;

        HttpURLConnection conn = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                return;
            }
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            Log.d(TAG, "Response: " + stringBuilder.toString());
        } catch (IOException ex) {
            Log.e(TAG, "Error in getting info: " + ex.getLocalizedMessage(), ex);
            return;
        }

        try {
            parse(stringBuilder.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error in parsing info: " + e.getLocalizedMessage(), e);
        }
    }

    private void parse(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        JSONObject normalizedInput = jsonObject.getJSONObject("normalizedInput");
        String location = normalizedInput.getString("city") +
                ", " + normalizedInput.getString("state") +
                " " + normalizedInput.getString("zip");

        List<Official> officials = parseOfficials(jsonObject.getJSONArray("officials"));
        addTitlesToOfficials(officials, jsonObject.getJSONArray("offices"));

        mainActivity.runOnUiThread(() -> {
            mainActivity.setLocation(location);
            mainActivity.updateOfficials(officials);
        });
    }

    private List<Official> parseOfficials(JSONArray officialsArray) throws JSONException {
        List<Official> officials = new ArrayList<>();
        for (int i = 0; i < officialsArray.length(); i++) {
            JSONObject officialObject = officialsArray.getJSONObject(i);
            Official official = new Official();
            official.setName(officialObject.getString("name"));
            if (officialObject.has("address")) {
                JSONObject addressObject = officialObject.getJSONArray("address").getJSONObject(0);
                official.setAddress(getAddress(addressObject));
            }
            official.setParty("(" + (officialObject.optString("party", "Unknown") + ")"));
            official.setPhone(officialObject.has("phones") ? officialObject.getJSONArray("phones").optString(0) : "");
            official.setUrl(officialObject.has("urls") ? officialObject.getJSONArray("urls").optString(0) : "");
            official.setEmail(officialObject.has("emails") ? officialObject.getJSONArray("emails").optString(0) : "");
            official.setPhotoUrl(officialObject.has("photoUrl") ? officialObject.optString("photoUrl") : "");
            if (officialObject.has("channels")) {
                JSONArray socialMedia = officialObject.getJSONArray("channels");
                Map<String, String> map = new HashMap<>();
                for (int j = 0; j < socialMedia.length(); j++) {
                    JSONObject account = socialMedia.getJSONObject(j);
                    map.put(account.getString("type"), account.getString("id"));
                }
                official.setFacebookId(map.get("Facebook"));
                official.setTwitterId(map.get("Twitter"));
                official.setYouTubeId(map.get("YouTube"));
            }

            officials.add(official);
        }

        return officials;
    }

    private String getAddress(JSONObject address) throws JSONException {
        StringBuilder stringBuilder = new StringBuilder(address.getString("line1"));
        if (address.has("line2")) {
            stringBuilder.append("\n").append(address.getString("line2"));
        }
        if (address.has("line3")) {
            stringBuilder.append("\n").append(address.getString("line3"));
        }
        stringBuilder.append("\n").append(address.getString("city"));
        stringBuilder.append(", ").append(address.getString("state"));
        stringBuilder.append(" ").append(address.getString("zip"));
        return stringBuilder.toString();
    }

    private void addTitlesToOfficials(List<Official> officials, JSONArray officesArray) throws JSONException {
        for (int i = 0; i < officesArray.length(); i++) {
            JSONObject officeObject = officesArray.getJSONObject(i);
            String officeTitle = officeObject.getString("name");
            JSONArray officialIndices = officeObject.getJSONArray("officialIndices");
            for (int j = 0; j < officialIndices.length(); j++) {
                officials.get(officialIndices.getInt(j)).setTitle(officeTitle);
            }
        }
    }

}