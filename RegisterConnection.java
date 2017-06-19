package com.example.findme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

public class RegisterConnection extends AppCompatActivity {

    static String urlRegister;
    Context context;
    Activity activity;


    public void getUrl(String urlRegister) {
        RegisterConnection.urlRegister = urlRegister;
    }

    public void getContextActivity(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }


    public void registerConnection(Bundle dataRegister) {
        new RegisterConnection.AsyncRegister().execute(dataRegister);
    }

    private class AsyncRegister extends AsyncTask<Bundle, String, String> {

        public static final int CONNECTION_TIMEOUT=10000;
        public static final int READ_TIMEOUT=15000;

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
        protected String doInBackground(Bundle... params) {
            try {
                url = new URL(urlRegister);

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
                        .appendQueryParameter("nameSurname", params[0].getString("nameSurname"))
                        .appendQueryParameter("email", params[0].getString("email"))
                        .appendQueryParameter("phone", params[0].getString("phone"))
                        .appendQueryParameter("password", params[0].getString("password"));
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

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setIcon(R.drawable.ic_check_black_36dp);
                builder.setTitle("Success");
                builder.setMessage("The account has been created successfully. You can log in now.");
                builder.setCancelable(false);
                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences. */
            }
            else if (result.equals("full_email")) {
                Toast.makeText(activity, "EMAIL exist in the database", Toast.LENGTH_LONG).show();
            }
            else if (result.equals("unsuccessful")) {
                Toast.makeText(activity, "Shit happens... Error connection. Check internet access.", Toast.LENGTH_LONG).show();
            }

        }

    }

}