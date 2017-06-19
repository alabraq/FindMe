package com.example.findme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
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

public class LoginConnection extends AppCompatActivity {

    static String urlLogin;
    Context context;
    Activity activity;


    public void getUrl(String urlLogin) {
        LoginConnection.urlLogin = urlLogin;
    }

    public void getContextActivity(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }


    public void loginConnection(String email, String password) {
        new AsyncLogin().execute(email, password);
    }

    private class AsyncLogin extends AsyncTask<String, String, String> {

        public static final int CONNECTION_TIMEOUT=10000;
        public static final int READ_TIMEOUT=15000;

        String userId, userName, userEmail, userPassword, userPhone, userPhoto;
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
                url = new URL(urlLogin);

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
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);
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

            if (!result.equals("null_email") && !result.equals("null_password") && !result.equals("unsuccessful")) {

                getUserParserJSON(result);

                LoginActivity.USER_ID = userId;
                LoginActivity.USER_NAME = userName;

                SharedPreferences loginData = activity.getSharedPreferences("loginData", MODE_PRIVATE);
                SharedPreferences.Editor editor = loginData.edit();
                editor.putString("name", userName);
                editor.putString("email", userEmail);
                editor.putString("password", userPassword);
                editor.putString("phone", userPhone);
                editor.putString("photo", userPhoto);
                editor.apply();

                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences. */

                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.finish();

            }
            else if (result.equals("null_email")) {
                Toast.makeText(activity, "EMAIL does not exist in the database", Toast.LENGTH_LONG).show();
            }
            else if (result.equals("null_password")) {
                Toast.makeText(activity, "Incorrect PASSWORD", Toast.LENGTH_LONG).show();
            }
            else if (result.equals("unsuccessful")) {
                Toast.makeText(activity, "Shit happens... Error connection. Check internet access.", Toast.LENGTH_LONG).show();
            }
        }

        private void getUserParserJSON(String buffer) {
            try {
                JSONObject parentObject = new JSONObject(buffer);
                JSONArray parentArray = parentObject.getJSONArray("user");

                for (int i = 0; i < parentArray.length(); i++) {

                    JSONObject userObject = parentArray.getJSONObject(i);

                    userId = userObject.getString("userId");
                    userName = userObject.getString("userName");
                    userEmail = userObject.getString("userEmail");
                    userPassword = userObject.getString("userPassword");
                    userPhone = userObject.getString("userPhone");
                    userPhoto = userObject.getString("userPhoto");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}
