package com.example.findme;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class AdapterListViewSelectReceiver extends ArrayAdapter {

    private List<FriendsClass> friendsClassList;
    private int resource;
    private LayoutInflater layoutInflater;

    public AdapterListViewSelectReceiver(Context context, int resource, List<FriendsClass> objects) {
        super(context, resource, objects);
        friendsClassList = objects;
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
        TextView tvNameSurname;
        TextView tvEmail;

        ivPhoto = (ImageView)convertView.findViewById(R.id.imageViewSelectUserPhoto);
        tvNameSurname = (TextView)convertView.findViewById(R.id.textViewSelectUserNameSurname);
        tvEmail = (TextView)convertView.findViewById(R.id.textViewSelectUserEmail);

        if (friendsClassList.get(position).getPhoto().isEmpty()) {
            ivPhoto.setImageResource(R.drawable.default_user_photo);
        } else {
            ImageLoader.getInstance().displayImage(friendsClassList.get(position).getPhoto(), ivPhoto);
        }

        tvNameSurname.setText(friendsClassList.get(position).getNameSurname());
        tvEmail.setText(friendsClassList.get(position).getEmail());

        return convertView;
    }

}
