package com.example.findme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import java.io.File;
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

public class UserSettingsConnection extends AppCompatActivity {

    static String urlSetPhoto, urlChangePhone, urlChangePassword;
    Context context;
    Activity activity;


    public void getUrl(String urlSetPhoto, String urlChangePhone, String urlChangePassword) {
        UserSettingsConnection.urlSetPhoto = urlSetPhoto;
        UserSettingsConnection.urlChangePhone = urlChangePhone;
        UserSettingsConnection.urlChangePassword = urlChangePassword;
    }

    public void getContextActivity(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }


    public void setPhoto(String encodedPhoto, String userId) {
        new UserSettingsConnection.AsyncSetPhoto().execute(encodedPhoto, userId);
    }

    private class AsyncSetPhoto extends AsyncTask<String, String, String> {

        final int CONNECTION_TIMEOUT=100000;
        final int READ_TIMEOUT=150000;

        ProgressDialog pdLoading = new ProgressDialog(activity);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("Please wait...\nUpload photo may take a few seconds.");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(urlSetPhoto);

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
                        .appendQueryParameter("encoded_string", params[0])
                        .appendQueryParameter("id_user", params[1]);
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
                Toast.makeText(activity, "Successful", Toast.LENGTH_LONG).show();
            } else if (result.equals("null")) {
                Toast.makeText(activity, "Shit happens... Something wrong.", Toast.LENGTH_LONG).show();
            } else if (result.equals("unsuccessful")) {
                Toast.makeText(activity, "Shit happens... Error connection. Check internet access.", Toast.LENGTH_LONG).show();
            }

        }

    }


    public void changePhone(String newPhone, String userId) {
        new UserSettingsConnection.AsyncChangePhone().execute(newPhone, userId);
    }

    private class AsyncChangePhone extends AsyncTask<String, String, String> {

        final int CONNECTION_TIMEOUT=10000;
        final int READ_TIMEOUT=15000;

        ProgressDialog pdLoading = new ProgressDialog(activity);
        HttpURLConnection conn;
        URL url = null;
        String param = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            param = params[0];
            try {
                url = new URL(urlChangePhone);

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
                        .appendQueryParameter("new_phone", params[0])
                        .appendQueryParameter("id_user", params[1]);
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

                SharedPreferences loginData = activity.getSharedPreferences("loginData", MODE_PRIVATE);
                SharedPreferences.Editor editor = loginData.edit();
                editor.putString("phone", param);
                editor.apply();

                Toast.makeText(activity, "Successful", Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, "Phone number has been changed.", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(activity, UserSettingsActivity.class);
                activity.startActivity(intent);
                activity.finish();

            } else if (result.equals("null")) {
                Toast.makeText(activity, "Shit happens... Something wrong.", Toast.LENGTH_LONG).show();
            } else if (result.equals("unsuccessful")) {
                Toast.makeText(activity, "Shit happens... Error connection. Check internet access.", Toast.LENGTH_LONG).show();
            }

        }

    }


    public void changePassword(String newPassword, String userId) {
        new UserSettingsConnection.AsyncChangePassword().execute(newPassword, userId);
    }

    private class AsyncChangePassword extends AsyncTask<String, String, String> {

        final int CONNECTION_TIMEOUT=10000;
        final int READ_TIMEOUT=15000;

        ProgressDialog pdLoading = new ProgressDialog(activity);
        HttpURLConnection conn;
        URL url = null;
        String param = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            param = params[0];
            try {
                url = new URL(urlChangePassword);

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
                        .appendQueryParameter("new_password", params[0])
                        .appendQueryParameter("id_user", params[1]);
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

                SharedPreferences loginData = activity.getSharedPreferences("loginData", MODE_PRIVATE);
                SharedPreferences.Editor editor = loginData.edit();
                editor.putString("password", param);
                editor.apply();

                Toast.makeText(activity, "Successful", Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, "Password has been changed.", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(activity, UserSettingsActivity.class);
                activity.startActivity(intent);
                activity.finish();

            } else if (result.equals("null")) {
                Toast.makeText(activity, "Shit happens... Something wrong.", Toast.LENGTH_LONG).show();
            } else if (result.equals("unsuccessful")) {
                Toast.makeText(activity, "Shit happens... Error connection. Check internet access.", Toast.LENGTH_LONG).show();
            }

        }

    }

}
