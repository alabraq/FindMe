package com.example.findme;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
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
import java.util.Locale;
import java.util.TimerTask;

public class ShareFragment extends Fragment {

    private boolean onCreate = false;

    private Handler handler;
    private Runnable runnable;

    private long timeUpdateLocation = 15000;
    private long timeCheckUpdateLocation = 30000;

    private Double latitude, longitude, altitude;
    private String address;

    private TextView tvInfoGPS;
    private SearchView svSelectReceiver;
    private ListView lvSelectReceiver;
    private Button btSendLocation, btSendLocationSMS, btShowLocation;

    private List<FriendsClass> friendsList = new ArrayList<>();
    private String idReceiver = null;

    static String urlGetFriends, urlSendLocation;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreate = true;

        urlGetFriends = getString(R.string.get_friends);
        urlSendLocation = getString(R.string.send_location);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);

        tvInfoGPS = (TextView) view.findViewById(R.id.infoGPS);
        svSelectReceiver = (SearchView) view.findViewById(R.id.selectReceiver);
        lvSelectReceiver = (ListView) view.findViewById(R.id.lvSelectReceiver);
        btSendLocation = (Button) view.findViewById(R.id.sendLocationByFindMe);
        btSendLocationSMS = (Button) view.findViewById(R.id.sendLocationBySMS);
        btShowLocation = (Button) view.findViewById(R.id.showLocation);


        setReceiver();
        sendLocationClick();
        sendLocationSMSClick();
        showLocationClick();


        if (onCreate) {
            onCreate = false;
            getGPSLocation();
            checkGPS();
        }


        LocationManager manager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
        if (latitude == null || longitude == null) {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                infoGPSSearch();
            } else {
                infoGPSOff();
            }
        } else {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                infoGPSOn();
            } else {
                infoGPSOff();
            }
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        friendsList.clear();
        idReceiver = null;
    }


    private void setReceiver() {
        svSelectReceiver.clearFocus();
        svSelectReceiver.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    new AsyncGetFriends().execute(LoginActivity.USER_ID);
                }
            }
        });

        svSelectReceiver.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    lvSelectReceiver.setVisibility(View.VISIBLE);

                    AdapterListViewSelectReceiver adapter = new AdapterListViewSelectReceiver(getActivity(), R.layout.item_select_receiver, searchReceiver(newText));
                    lvSelectReceiver.setAdapter(adapter);

                    selectReceiver(searchReceiverId(searchReceiver(newText)));

                } else {
                    lvSelectReceiver.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }

    private List searchReceiver(String newText) {
        List<FriendsClass> receiverList = new ArrayList<>();
        for (FriendsClass friend : friendsList) {
            if (friend.getNameSurname().contains(newText) || friend.getEmail().contains(newText)) {
                receiverList.add(friend);
            }
        }
        return receiverList;
    }

    private List searchReceiverId(List<FriendsClass> receiverList) {
        List<String> receiverListId = new ArrayList<>();
        for (FriendsClass friend : receiverList) {
            receiverListId.add(friend.getId());
        }
        return receiverListId;
    }

    private void selectReceiver(final List<String> receiverListId) {
        lvSelectReceiver.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (FriendsClass friend : friendsList) {
                    if (friend.getId().equals(receiverListId.get(i))) {
                        idReceiver = receiverListId.get(i);

                        svSelectReceiver.setQuery(friend.getNameSurname(), false);
                        svSelectReceiver.clearFocus();
                        lvSelectReceiver.setVisibility(View.GONE);

                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                        svSelectReceiver.clearFocus();
                    }
                }
            }
        });
    }


    private void sendLocationClick() {
        btSendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationManager manager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);

                if (latitude != null && longitude != null && idReceiver != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    sendJSON();
                }

                if (idReceiver == null) {
                    Toast.makeText(getActivity(), "Select the receiver", Toast.LENGTH_SHORT).show();
                }

                if (latitude == null && longitude == null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showLocationClickWhenGPSSearch();
                }

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showLocationClickWhenGPSOff();
                }
            }
        });
    }

    private void sendJSON() {
        JSONObject location = new JSONObject();
        try {
            location.put("idSender", LoginActivity.USER_ID);
            location.put("idReceiver", idReceiver);
            location.put("latitude", latitude);
            location.put("longitude", longitude);
            location.put("altitude", altitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new AsyncSendLocation().execute(location);
    }


    private void sendLocationSMSClick() {
        btSendLocationSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationManager manager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);

                if (latitude != null && longitude != null && idReceiver != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    sendSMS();
                }

                if (idReceiver == null) {
                    Toast.makeText(getActivity(), "Select the receiver", Toast.LENGTH_SHORT).show();
                }

                if (latitude == null && longitude == null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showLocationClickWhenGPSSearch();
                }

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showLocationClickWhenGPSOff();
                }
            }
        });
    }

    private void sendSMS() {
        String phoneNumber = null, message;

        for (FriendsClass friend : friendsList) {
            if (friend.getId().equals(idReceiver)) {
                phoneNumber = friend.getPhone();
                break;
            }
        }

        if (phoneNumber.equals("")) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle("No number");
            dialogBuilder.setMessage("Selected receiver has not added a phone number.");
            dialogBuilder.setCancelable(true);
            dialogBuilder.setNeutralButton("BACK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {}
            });
            dialogBuilder.create().show();
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("Information sent from the FindMe application.").append("\n\n");
            builder.append(address).append("\n\n");
            builder.append("Latitude: ").append(latitude).append("\n");
            builder.append("Longitude: ").append(longitude).append("\n");
            builder.append("Altitude: ").append(altitude).append("\n");

            message = builder.toString();

            sendSMSAccept(phoneNumber, message);

        }
    }

    private void sendSMSAccept(final String phoneNumber, final String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_send_sms, null);

        TextView tvSMSContent = (TextView) dialogView.findViewById(R.id.tvSMSContent);
        final EditText etSMSAddContent = (EditText) dialogView.findViewById(R.id.etSMSAddContent);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setIcon(R.drawable.ic_sms_black_36dp);
        dialogBuilder.setTitle("Send locations by SMS");
        dialogBuilder.setCancelable(false);
        dialogBuilder.setNeutralButton("BACK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });
        dialogBuilder.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String customMessage = message;

                if (!etSMSAddContent.getText().toString().equals("")) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(message).append("\n");
                    stringBuilder.append("Message from the sender:\n");
                    stringBuilder.append(etSMSAddContent.getText().toString()).append("\n");

                    customMessage = stringBuilder.toString();
                }

                SmsManager sms = SmsManager.getDefault();
                ArrayList<String> smsParts = sms.divideMessage(customMessage);
                sms.sendMultipartTextMessage(phoneNumber, null, smsParts, null, null);

                Toast.makeText(getActivity(), "Message was sent successfully", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        tvSMSContent.setText(message);
    }


    private void showLocationClick() {
        btShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationManager manager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);

                if (latitude != null && longitude != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showLocationClickWhenGPSOn();
                }
                if (latitude == null && longitude == null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showLocationClickWhenGPSSearch();
                }
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showLocationClickWhenGPSOff();
                }
            }
        });
    }

    private void showLocationClickWhenGPSOn() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_show_current_location, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setIcon(R.drawable.ic_gps_fixed_black_36dp);
        dialogBuilder.setTitle("Current location");
        dialogBuilder.setCancelable(false);
        dialogBuilder.setNeutralButton("BACK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        TextView tvCurrentAddress = (TextView) dialogView.findViewById(R.id.textViewCurrentAddress);
        TextView tvCurrentLatitude = (TextView) dialogView.findViewById(R.id.textViewCurrentLatitude);
        TextView tvCurrentLongitude = (TextView) dialogView.findViewById(R.id.textViewCurrentLongitude);
        TextView tvCurrentAltitude = (TextView) dialogView.findViewById(R.id.textViewCurrentAltitude);
        Button btShowMap = (Button) dialogView.findViewById(R.id.buttonShowMap);

        if (!address.equals("")) {
            tvCurrentAddress.setText(address);
        } else {
            tvCurrentAddress.setText("Cannot specify the address without access to the Internet");
        }

        tvCurrentLatitude.setText(String.valueOf(latitude));
        tvCurrentLongitude.setText(String.valueOf(longitude));
        tvCurrentAltitude.setText(String.valueOf(altitude) + " m");

        btShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("nameMarker", "You are here");
                startActivity(intent);
            }
        });
    }

    private void showLocationClickWhenGPSSearch() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_info_search_location, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setIcon(R.drawable.ic_gps_not_fixed_black_36dp);
        dialogBuilder.setTitle("Please wait");
        dialogBuilder.setCancelable(true);
        dialogBuilder.setNeutralButton("BACK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void showLocationClickWhenGPSOff() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_info_off_location, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setIcon(R.drawable.ic_gps_off_black_36dp);
        dialogBuilder.setTitle("Turn on GPS to get your location");
        dialogBuilder.setCancelable(true);
        dialogBuilder.setNeutralButton("BACK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });
        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        Button btSettings = (Button) dialogView.findViewById(R.id.buttonGPSSettings);
        btSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dialog.cancel();
            }
        });
    }


    private Bundle getGPSLocation() {
        final Bundle gpsCoordinates = new Bundle();

        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                altitude = location.getAltitude();
                address = getAddressFromGPSLocation(latitude, longitude, getContext());

                gpsCoordinates.putDouble("latitude", latitude);
                gpsCoordinates.putDouble("longitude", longitude);
                gpsCoordinates.putDouble("altitude", altitude);

                //Toast.makeText(getActivity(), String.valueOf(latitude), Toast.LENGTH_LONG).show();
                //Toast.makeText(getActivity(), String.valueOf(longitude), Toast.LENGTH_LONG).show();
                //Toast.makeText(getActivity(), String.valueOf(altitude), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                infoGPSOn();
                timeCheckUpdateLocation = 30000;
            }

            @Override
            public void onProviderEnabled(String s) {
                infoGPSSearch();
            }

            @Override
            public void onProviderDisabled(String s) {
                infoGPSOff();
            }
        };

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {}

        locationManager.requestLocationUpdates("gps", timeUpdateLocation, 0, locationListener);

        return gpsCoordinates;
    }

    public static String getAddressFromGPSLocation(Double latitude, Double longitude, Context context) {
        StringBuilder address = new StringBuilder();

        try {
            Geocoder geo = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);

            for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
                address.append(addresses.get(0).getAddressLine(i)).append("\n");
            }
            address.append(addresses.get(0).getCountryName()).append(" ");
            address.append(addresses.get(0).getCountryCode()).append(" ");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return address.toString();
    }


    private void infoGPSOn() {
        tvInfoGPS.setText("GPS is ON (location is updated every " + timeUpdateLocation / 1000 + " seconds)");
        tvInfoGPS.setBackgroundColor(Color.GREEN);
        tvInfoGPS.setTextColor(Color.DKGRAY);
        tvInfoGPS.setVisibility(View.VISIBLE);

        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvInfoGPS.setVisibility(View.INVISIBLE);
            }
        }, 3000);*/
    }

    private void infoGPSSearch() {
        tvInfoGPS.setText("GPS Search location ...");
        tvInfoGPS.setBackgroundColor(Color.YELLOW);
        tvInfoGPS.setTextColor(Color.DKGRAY);
    }

    private void infoGPSOff() {
        tvInfoGPS.setText("GPS is OFF");
        tvInfoGPS.setBackgroundColor(Color.RED);
        tvInfoGPS.setTextColor(Color.WHITE);
    }

    private void checkGPS() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);

                if (timeCheckUpdateLocation < 0) {
                    LocationManager manager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
                    if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) infoGPSSearch();
                }
                timeCheckUpdateLocation = timeCheckUpdateLocation - 1000;
            }
        };
        handler.postDelayed(runnable, 0);
    }


    private class AsyncGetFriends extends AsyncTask<String, String, String> {

        final int CONNECTION_TIMEOUT=10000;
        final int READ_TIMEOUT=15000;

        ProgressDialog pdLoading = new ProgressDialog(getActivity());
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
                getUserParserJSON(result);
            } else if (result.equals("null")) {
                Toast.makeText(getActivity(), "You have no friends", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Shit happens... Error connection. Check internet access.", Toast.LENGTH_LONG).show();
            }
        }

        private List<FriendsClass> getUserParserJSON(String buffer) {
            try {
                JSONObject parentObject = new JSONObject(buffer);
                JSONArray parentArray = parentObject.getJSONArray("friends");

                friendsList.clear();

                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject userObject = parentArray.getJSONObject(i);
                    FriendsClass friend = new FriendsClass();

                    friend.setId(userObject.getString("userId"));
                    friend.setNameSurname(userObject.getString("userName"));
                    friend.setEmail(userObject.getString("userEmail"));
                    friend.setPhoto(userObject.getString("userPhoto"));
                    friend.setPhone(userObject.getString("userPhone"));

                    friendsList.add(friend);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return friendsList;
        }

    }

    private class AsyncSendLocation extends AsyncTask<JSONObject, String, String> {

        final int CONNECTION_TIMEOUT=10000;
        final int READ_TIMEOUT=15000;

        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("\tSending...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(JSONObject... params) {
            try {
                url = new URL(urlSendLocation);

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

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(params[0].toString());
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
                Toast.makeText(getActivity(), "Location was sent successfully", Toast.LENGTH_LONG).show();
            } else if (result.equals("null")) {
                Toast.makeText(getActivity(), "Shit happens... Something wrong.", Toast.LENGTH_LONG).show();
            } else if (result.equals("unsuccessful")) {
                Toast.makeText(getActivity(), "Shit happens... Error connection. Check internet access.", Toast.LENGTH_LONG).show();
            }

        }

    }

}
