package com.example.findme;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

public class CustomAddFriendOnClickListener implements DialogInterface.OnClickListener, View.OnClickListener {

    String userEmail, userName;

    public CustomAddFriendOnClickListener(String userEmail, String userName) {
        this.userEmail = userEmail;
        this.userName = userName;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {}

    @Override
    public void onClick(final View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setIcon(R.drawable.ic_person_add_black_36dp);
        builder.setTitle("New relationship");
        builder.setMessage("Do you want to create a new relationship with " + userName + "?");
        builder.setCancelable(false);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                FriendsConnection friendsConnection = new FriendsConnection();
                friendsConnection.getContextActivity(view.getContext(), null);
                friendsConnection.addFriends(userEmail, LoginActivity.USER_ID);

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
