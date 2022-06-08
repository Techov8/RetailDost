package com.techov8.retaildost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("LoginData",
                0);
        String string = prefs.getString("data",
                "no");

        SharedPreferences prefs2 = getSharedPreferences("RegisterData",
                0);
        String string2 = prefs2.getString("data",
                "no");

        if(!string.equals("Yes")){
            if(string2.equals("Yes")){
                Intent ii = new Intent(SplashActivity.this, UserIdActivity.class);
                startActivity(ii);
                finish();
            }else {
                Intent i = new Intent(SplashActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        }else{
            Intent ii=new Intent(SplashActivity.this,MainActivity.class);
            startActivity(ii);
            finish();

        }

    }
}