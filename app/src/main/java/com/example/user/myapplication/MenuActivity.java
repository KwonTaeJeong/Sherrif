package com.example.user.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.support.v7.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void click_login(View v) {
        finish();
    }

    public void click_no(View v) {
        Intent intent = new Intent(getApplicationContext(), AcsnoActivity.class);
        startActivity(intent);
    }

    public void click_yes(View v) {
        Intent intent = new Intent(getApplicationContext(), AcsyesActivity.class);
        startActivity(intent);
    }

    public void click_realtime(View v) {
        Intent intent = new Intent(getApplicationContext(), RealtimeActivity.class);
        startActivity(intent);
    }

}
