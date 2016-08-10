package com.example.user.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    //~추가가
    public void clicked1(View v){
        Intent intent = new Intent(getApplicationContext(), nextactivity.class);
        startActivity(intent);
    }

    public void click_notice(View v){
        /*NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, NoticeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mCompatBuilder = new NotificationCompat.Builder(this);
        mCompatBuilder.setSmallIcon(R.mipmap.siren);
        mCompatBuilder.setTicker("경고!");
        mCompatBuilder.setWhen(System.currentTimeMillis());
        mCompatBuilder.setNumber(10);
        mCompatBuilder.setContentTitle("경고!!");
        mCompatBuilder.setContentText("승인되지 않은 사람이 침입하였습니다.");
        mCompatBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        mCompatBuilder.setContentIntent(pendingIntent);
        mCompatBuilder.setAutoCancel(true);

        nm.notify(222, mCompatBuilder.build());*/
    }
}
