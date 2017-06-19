package com.example.findme;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class AdapterListViewLocations extends ArrayAdapter {

    private List<LocationsClass> locationsClassList;
    private int resource;
    private LayoutInflater layoutInflater;

    public AdapterListViewLocations(Context context, int resource, List<LocationsClass> objects) {
        super(context, resource, objects);
        locationsClassList = objects;
        this.resource = resource;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(resource, null);
        }

        ImageView ivPhoto;
        TextView tvDate;
        TextView tvNameSurname;
        Button btAddress;
        Button btMap;

        ivPhoto = (ImageView) convertView.findViewById(R.id.ivUserPhotoLocation);
        tvDate = (TextView) convertView.findViewById(R.id.tvDateLocation);
        tvNameSurname = (TextView) convertView.findViewById(R.id.tvUserNameSurnameLocation);
        btAddress = (Button) convertView.findViewById(R.id.btShowAddressLocation);
        btMap = (Button) convertView.findViewById(R.id.btShowMapLocation);

        if (locationsClassList.get(position).getSenderPhoto().isEmpty()) {
            ivPhoto.setImageResource(R.drawable.default_user_photo);
        } else {
            ImageLoader.getInstance().displayImage(locationsClassList.get(position).getSenderPhoto(), ivPhoto);
        }

        tvDate.setText(locationsClassList.get(position).getDate());
        tvNameSurname.setText(locationsClassList.get(position).getSenderNameSurname());

        btAddress.setOnClickListener(new CustomShowAddressOnClickListener(
                locationsClassList.get(position).getLatitude(),
                locationsClassList.get(position).getLongitude(),
                locationsClassList.get(position).getAltitude(),
                locationsClassList.get(position).getSenderNameSurname()));

        btMap.setOnClickListener(new CustomShowMapOnClickListener(
                locationsClassList.get(position).getLatitude(),
                locationsClassList.get(position).getLongitude(),
                locationsClassList.get(position).getSenderNameSurname()));

        return convertView;
    }

}
