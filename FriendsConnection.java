package com.example.findme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
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

public class FriendsConnection extends AppCompatActivity {

    static String urlGetFriends, urlSearchFriends, urlAddFriends;
    Context context;
    Activity activity;


    public void getUrl(String urlGetFriends, String urlSearchFriends, String urlAddFriends) {
        FriendsConnection.urlGetFriends = urlGetFriends;
        FriendsConnection.urlSearchFriends = urlSearchFriends;
        FriendsConnection.urlAddFriends = urlAddFriends;
    }

    public void getContextActivity(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }


    public void getFriends(String userId) {
        new FriendsConnection.AsyncGetFriends().execute(userId);
    }

    private class AsyncGetFriends extends AsyncTask<String, String, String> {

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
                url = new URL(urlGetFriends);

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

                ListView listView = (ListView)activity.findViewById(R.id.list_item_friends);
                AdapterListViewFriends adapter = new AdapterListViewFriends(activity, R.layout.item_friend, getUserParserJSON(result));
                listView.setAdapter(adapter);

            } else if (result.equals("null")) {

            } else {
                Toast.makeText(activity, "Shit happens... Error connection. Check internet access.", Toast.LENGTH_LONG).show();
            }
        }

        private List<FriendsClass> getUserParserJSON(String buffer) {
            List<FriendsClass> friendsList = new ArrayList<>();

            try {
                JSONObject parentObject = new JSONObject(buffer);
                JSONArray parentArray = parentObject.getJSONArray("friends");

                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject userObject = parentArray.getJSONObject(i);
                    FriendsClass friend = new FriendsClass();

                    friend.setNameSurname(userObject.getString("userName"));
                    friend.setEmail(userObject.getString("userEmail"));
                    friend.setPhoto(userObject.getString("userPhoto"));

                    friendsList.add(friend);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return friendsList;
        }

    }


    public void searchFriends(String userEmail) {
        new FriendsConnection.AsyncSearchFriends().execute(userEmail);
    }

    private class AsyncSearchFriends extends AsyncTask<String, String, String> {

        final int CONNECTION_TIMEOUT=10000;
        final int READ_TIMEOUT=15000;

        ProgressDialog pdLoading = new ProgressDialog(activity);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("\tSearching...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(urlSearchFriends);

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

                ListView listView = (ListView)activity.findViewById(R.id.list_item_search_friends);
                AdapterListViewSearchFriends adapter = new AdapterListViewSearchFriends(activity, R.layout.item_search_friend, getUserParserJSON(result));
                listView.setAdapter(adapter);

            } else if (result.equals("null")) {
                Toast.makeText(activity, "Such a person does not exist", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(activity, "Shit happens... Error connection. Check internet access.", Toast.LENGTH_LONG).show();
            }
        }

        private List<FriendsClass> getUserParserJSON(String buffer) {
            List<FriendsClass> friendsList = new ArrayList<>();

            try {
                JSONObject parentObject = new JSONObject(buffer);
                JSONArray parentArray = parentObject.getJSONArray("friends");

                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject userObject = parentArray.getJSONObject(i);
                    FriendsClass friend = new FriendsClass();

                    friend.setId(userObject.getString("userId"));
                    friend.setNameSurname(userObject.getString("userName"));
                    friend.setEmail(userObject.getString("userEmail"));
                    friend.setPhoto(userObject.getString("userPhoto"));

                    friendsList.add(friend);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return friendsList;
        }

    }


    public void addFriends(String userId_0, String userId_1) {
        new FriendsConnection.AsyncAddFriends().execute(userId_0, userId_1);
    }

    private class AsyncAddFriends extends AsyncTask<String, String, String> {

        final int CONNECTION_TIMEOUT=10000;
        final int READ_TIMEOUT=15000;

        ProgressDialog pdLoading = new ProgressDialog(context);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("\tAdding Friend...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(urlAddFriends);

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
                        .appendQueryParameter("username_0", params[0])
                        .appendQueryParameter("username_1", params[1]);
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

            if (result.equals("successful")) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.drawable.ic_check_black_36dp);
                builder.setTitle("Success");
                builder.setMessage("The relationship has been created successfully.");
                builder.setCancelable(false);
                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            } else if (result.equals("null")) {
                Toast.makeText(context, "You are already friends", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Shit happens... Error connection. Check internet access.", Toast.LENGTH_LONG).show();
            }

        }

    }

}
