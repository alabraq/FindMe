package com.example.findme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LocationsConnection extends AppCompatActivity {

    static String urlGetLocations;
    Context context;
    Activity activity;


    public void getUrl(String urlGetLocations) {
        LocationsConnection.urlGetLocations = urlGetLocations;
    }

    public void getContextActivity(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }


    public void getLocations(String userId) {
        new LocationsConnection.AsyncGetLocations().execute(userId);
    }

    private class AsyncGetLocations extends AsyncTask<String, String, String> {

        final int CONNECTION_TIMEOUT=10000;
        final int READ_TIMEOUT=15000;

        ProgressDialog pdLoading = new ProgressDialog(activity);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(urlGetLocations);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "unsuccessful";
            }

            try {
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0]);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                e1.printStackTrace();
                return "unsuccessful";
            }

            try {
                int response_code = conn.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {

                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    return(result.toString());

                } else {
                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "unsuccessful";
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            pdLoading.dismiss();

            if (!result.equals("unsuccessful") && !result.equals("null")) {

                ListView listView = (ListView)activity.findViewById(R.id.list_item_location);
                AdapterListViewLocations adapter = new AdapterListViewLocations(activity, R.layout.item_location, getUserParserJSON(result));
                listView.setAdapter(adapter);

            } else if (result.equals("null")) {

            } else {
                Toast.makeText(activity, "Shit happens... Error connection. Check internet access.", Toast.LENGTH_LONG).show();
            }
        }

        private List<LocationsClass> getUserParserJSON(String buffer) {
            List<LocationsClass> locationsList = new ArrayList<>();

            try {
                JSONObject parentObject = new JSONObject(buffer);
                JSONArray parentArray = parentObject.getJSONArray("locations");

                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject userObject = parentArray.getJSONObject(i);
                    LocationsClass location = new LocationsClass();

                    location.setSenderNameSurname(userObject.getString("userName"));
                    location.setSenderPhoto(userObject.getString("userPhoto"));
                    location.setLatitude(userObject.getDouble("latitude"));
                    location.setLongitude(userObject.getDouble("longitude"));
                    location.setAltitude(userObject.getDouble("altitude"));
                    location.setDate(userObject.getString("date"));

                    locationsList.add(location);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return locationsList;
        }

    }

}
