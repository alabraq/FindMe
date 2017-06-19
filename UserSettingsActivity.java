package com.example.findme;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;

public class UserSettingsActivity extends AppCompatActivity {

    ImageView ivPhoto;
    TextView tvName, tvEmail, tvPhone, tvPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        setTitle("User Settings");

        ivPhoto = (ImageView)findViewById(R.id.imageViewUserSettingsPhoto);
        tvName = (TextView)findViewById(R.id.textViewUserSettingsName);
        tvEmail = (TextView)findViewById(R.id.textViewUserSettingsEmail);
        tvPhone = (TextView)findViewById(R.id.textViewUserSettingsPhone);
        tvPassword = (TextView)findViewById(R.id.textViewUserSettingsPassword);

        setImageView();
        setTextViewValue();

        new UserSettingsConnection().getUrl(
                getString(R.string.send_photo),
                getString(R.string.change_phone),
                getString(R.string.change_password));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String[] projection = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(projection[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            ivPhoto.setImageBitmap(bitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);

            byte[] array = stream.toByteArray();
            String encodedPhoto = Base64.encodeToString(array, 0);

            UserSettingsConnection userSettingsConnection = new UserSettingsConnection();
            userSettingsConnection.getContextActivity(getApplicationContext(), this);
            userSettingsConnection.setPhoto(encodedPhoto, LoginActivity.USER_ID);
        }
    }


    public void onInsertPhotoClick(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(photoPickerIntent, 1);
    }

    public void onPhoneChangeClick(View view) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_change_phone, null);
        dialogBuilder.setView(dialogView);

        final EditText etNewPhone = (EditText) dialogView.findViewById(R.id.alertDialogNewPhone);
        etNewPhone.append(RegisterActivity.getPhoneCountryCode(view.getContext()));

        dialogBuilder.setTitle("Change phone number");
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                UserSettingsConnection userSettingsConnection = new UserSettingsConnection();
                userSettingsConnection.getContextActivity(getApplicationContext(), UserSettingsActivity.this);
                userSettingsConnection.changePhone(etNewPhone.getText().toString(), LoginActivity.USER_ID);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

    }

    public void onPasswordChangeClick(View view) {
        final SharedPreferences loginData = getSharedPreferences("loginData", MODE_PRIVATE);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_change_password, null);
        dialogBuilder.setView(dialogView);

        final EditText etPasswordCurrent = (EditText) dialogView.findViewById(R.id.alertDialogCurrentPassword);
        final EditText etPassword = (EditText) dialogView.findViewById(R.id.alertDialogNewPassword);
        final EditText etPasswordRepeat = (EditText) dialogView.findViewById(R.id.alertDialogRepeatNewPassword);

        dialogBuilder.setTitle("Change password");
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (etPassword.getText().toString().equals(etPasswordRepeat.getText().toString())) {
                    if (!etPasswordCurrent.getText().toString().equals("") &&
                            !etPassword.getText().toString().equals("") &&
                                !etPasswordRepeat.getText().toString().equals("")) {
                        if (RegisterActivity.MD5(etPasswordCurrent.getText().toString()).equals(loginData.getString("password", null))) {

                            UserSettingsConnection userSettingsConnection = new UserSettingsConnection();
                            userSettingsConnection.getContextActivity(getApplicationContext(), UserSettingsActivity.this);
                            userSettingsConnection.changePassword(RegisterActivity.MD5(etPassword.getText().toString()), LoginActivity.USER_ID);

                        } else {
                            Toast.makeText(getApplicationContext(), "Unsuccessful", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Unsuccessful", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Unsuccessful", Toast.LENGTH_LONG).show();
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        etPasswordCurrent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {

                if (RegisterActivity.MD5(editable.toString()).equals(loginData.getString("password", null))) {
                    etPasswordCurrent.setTextColor(getResources().getColor(R.color.positive));
                } else {
                    etPasswordCurrent.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().equals(etPasswordRepeat.getText().toString())) {
                    etPassword.setTextColor(getResources().getColor(R.color.positive));
                    etPasswordRepeat.setTextColor(getResources().getColor(R.color.positive));
                } else {
                    etPassword.setTextColor(getResources().getColor(R.color.colorAccent));
                    etPasswordRepeat.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }
        });
        etPasswordRepeat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().equals(etPassword.getText().toString())) {
                    etPassword.setTextColor(getResources().getColor(R.color.positive));
                    etPasswordRepeat.setTextColor(getResources().getColor(R.color.positive));
                } else {
                    etPassword.setTextColor(getResources().getColor(R.color.colorAccent));
                    etPasswordRepeat.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }
        });

    }


    private void setImageView() {
        SharedPreferences loginData = getSharedPreferences("loginData", MODE_PRIVATE);

        if (loginData.getString("photo", "").equals("")) {
            ivPhoto.setImageResource(R.drawable.default_user_photo);
        } else {
            ImageLoader.getInstance().displayImage(loginData.getString("photo", null), ivPhoto);
        }
    }

    private void setTextViewValue() {
        SharedPreferences loginData = getSharedPreferences("loginData", MODE_PRIVATE);
        tvName.setText(loginData.getString("name", null));
        tvEmail.setText(loginData.getString("email", null));
        tvPhone.setText(loginData.getString("phone", null));
        tvPassword.setText("●●●●");
    }

}
