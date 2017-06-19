package com.example.findme;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    EditText etNameSurname, etEmail, etPhone, etPassword, etPasswordRepeat;
    TextView tvPasswordDifferent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Register");

        etNameSurname = (EditText)findViewById(R.id.register_name_surname);
        etEmail = (EditText)findViewById(R.id.register_email);
        etPhone = (EditText)findViewById(R.id.register_phone);
        etPassword = (EditText)findViewById(R.id.register_password);
        etPasswordRepeat = (EditText)findViewById(R.id.register_password_repeat);
        tvPasswordDifferent = (TextView)findViewById(R.id.register_password_different);

        comparingPasswords();
        setPhoneCountryCode();
    }


    public void onRegisterNowClick(View view) {
        if (isSetAllFields() && isPasswordSame()) {

            Bundle dataRegister = new Bundle();
            dataRegister.putString("nameSurname", etNameSurname.getText().toString());
            dataRegister.putString("email", etEmail.getText().toString());
            dataRegister.putString("phone", etPhone.getText().toString());
            dataRegister.putString("password", MD5(etPassword.getText().toString()));

            RegisterConnection registerConnection = new RegisterConnection();
            registerConnection.getUrl(getString(R.string.register));
            registerConnection.getContextActivity(getApplicationContext(), this);
            registerConnection.registerConnection(dataRegister);
        }
    }


    private boolean isSetAllFields() {
        if (etNameSurname.getText().toString().trim().equals("")) {
            Toast.makeText(this, "You forgot to enter NAME SURNAME", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etEmail.getText().toString().trim().equals("")) {
            Toast.makeText(this, "You forgot to enter EMAIL", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPhone.getText().toString().trim().equals("")) {
            Toast.makeText(this, "You forgot to enter PHONE", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPassword.getText().toString().trim().equals("")) {
            Toast.makeText(this, "You forgot to enter PASSWORD", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isPasswordSame() {
        return etPassword.getText().toString().equals(etPasswordRepeat.getText().toString());
    }

    private void comparingPasswords() {

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
                    tvPasswordDifferent.setVisibility(View.INVISIBLE);
                } else {
                    etPassword.setTextColor(getResources().getColor(R.color.colorAccent));
                    etPasswordRepeat.setTextColor(getResources().getColor(R.color.colorAccent));
                    tvPasswordDifferent.setVisibility(View.VISIBLE);
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
                    tvPasswordDifferent.setVisibility(View.INVISIBLE);
                } else {
                    etPassword.setTextColor(getResources().getColor(R.color.colorAccent));
                    etPasswordRepeat.setTextColor(getResources().getColor(R.color.colorAccent));
                    tvPasswordDifferent.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setPhoneCountryCode() {
        etPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b && etPhone.getText().toString().equals("")) {
                    etPhone.setText(getPhoneCountryCode(view.getContext()));
                }
            }
        });
    }

    public static String getPhoneCountryCode(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(TELEPHONY_SERVICE);
        String countryCode = telephonyManager.getSimCountryIso().toUpperCase();
        String[] phoneArray = context.getResources().getStringArray(R.array.PhoneCountryCodes);
        String[] tempArray;

        for (int i = 0; i < phoneArray.length; i++) {
            tempArray = phoneArray[i].split(",");
            if (tempArray[1].equals(countryCode)) {
                tempArray[0] = "+" + tempArray[0];
                return tempArray[0];
            }
        }
        return null;
    }

    public static String MD5(String toHash) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(toHash.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

}
