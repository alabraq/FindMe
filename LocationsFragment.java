package com.example.findme;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LocationsFragment extends Fragment {

    public static Activity activity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locations, container, false);

        LocationsConnection locationsConnection = new LocationsConnection();
        locationsConnection.getUrl(getString(R.string.get_locations));
        locationsConnection.getContextActivity(getContext(), getActivity());
        locationsConnection.getLocations(LoginActivity.USER_ID);

        return view;
    }


}
