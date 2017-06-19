package com.example.findme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    public static String USER_ID, USER_NAME;
    EditText etEmail, etPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.login);
        etPassword = (EditText) findViewById(R.id.password);

        autoLogIn();
    }


    public void onLogInClick(View view) {
        if (isSetEmailPassword()) {
            LoginConnection loginConnection = new LoginConnection();
            loginConnection.getUrl(getString(R.string.login));
            loginConnection.getContextActivity(getApplicationContext(), this);
            loginConnection.loginConnection(etEmail.getText().toString(), RegisterActivity.MD5(etPassword.getText().toString()));
        }
    }

    public void onCreateAccountClick(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    private boolean isSetEmailPassword(){
        if (etEmail.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Enter EMAIL", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPassword.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Enter PASSWORD", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void autoLogIn() {
        SharedPreferences loginData = getSharedPreferences("loginData", MODE_PRIVATE);

        if (!loginData.getString("email", "").equals("") && !loginData.getString("password", "").equals("")) {
            LoginConnection loginConnection = new LoginConnection();
            loginConnection.getUrl(getString(R.string.login));
            loginConnection.getContextActivity(getApplicationContext(), this);
            loginConnection.loginConnection(loginData.getString("email", null), loginData.getString("password", null));
        }
    }

}
