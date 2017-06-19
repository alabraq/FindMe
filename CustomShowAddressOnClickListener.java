package com.example.findme;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CustomShowAddressOnClickListener implements DialogInterface.OnClickListener, View.OnClickListener {

    private Double latitude, longitude, altitude;
    private String senderNameSurname;

    public CustomShowAddressOnClickListener(Double latitude, Double longitude, Double altitude, String senderNameSurname) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.senderNameSurname = senderNameSurname;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {}

    @Override
    public void onClick(View view) {
        Activity activity = LocationsFragment.activity;

        String address = ShareFragment.getAddressFromGPSLocation(latitude, longitude, view.getContext());

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_show_location, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setIcon(R.drawable.ic_gps_fixed_black_36dp);
        dialogBuilder.setTitle(senderNameSurname + " location");
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

        if (!address.equals("")) {
            tvCurrentAddress.setText(address);
        } else {
            tvCurrentAddress.setText("Cannot specify the address without access to the Internet");
        }

        tvCurrentLatitude.setText(String.valueOf(latitude));
        tvCurrentLongitude.setText(String.valueOf(longitude));
        tvCurrentAltitude.setText(String.valueOf(altitude) + " m");
    }

}
