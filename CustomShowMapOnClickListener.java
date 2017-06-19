package com.example.findme;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

public class CustomShowMapOnClickListener implements DialogInterface.OnClickListener, View.OnClickListener {

    private Double latitude, longitude;
    private String senderNameSurname;

    public CustomShowMapOnClickListener(Double latitude, Double longitude, String senderNameSurname) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.senderNameSurname = senderNameSurname;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {}

    @Override
    public void onClick(View view) {
        Activity activity = LocationsFragment.activity;

        Intent intent = new Intent(view.getContext(), MapsActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("nameMarker", senderNameSurname + " is here");
        activity.startActivity(intent);
    }

}
