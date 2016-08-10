package com.example.user.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.app.AppCompatActivity;
/**
 * Created by user on 2016-08-05.
 */
public class SignActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);


        final EditText et_name=(EditText)findViewById(R.id.et_name);
        final EditText et_pass=(EditText)findViewById(R.id.et_pass);

        Button btnInsert = (Button)findViewById(R.id.btn_sign);

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString();
                String pass = et_pass.getText().toString();

                Intent intent = new Intent(getApplicationContext(), nextactivity.class);
                startActivity(intent);
            }
        });

    }
    public void click_sign(View v){finish();}
}