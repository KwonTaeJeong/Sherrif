package com.example.user.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AcsyesActivity extends AppCompatActivity {

    DBManager my;
    SQLiteDatabase sql;

    TextView date, id, access;

    String date1,id1,access1;
    Cursor cursor;
    ArrayList<ArduinoEntity> arduList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acsyes);

        date = (TextView) findViewById(R.id.data);
        id = (TextView) findViewById(R.id.id);
        access = (TextView) findViewById(R.id.access);

        my = new DBManager(this);

        sql = my.getReadableDatabase();

        cursor = sql.rawQuery("SELECT * FROM member where access = '정상';", null);

        date1 = "시간" + "\r\n";
        id1 = "ID" + "\r\n";
        access1 = "상태" + "\r\n";

        getAllNomalArduino();


//        while(cursor.moveToNext()) {
//            date1 += cursor.getString(0) + "\r\n";
//            id1 += cursor.getString(1) + "\r\n";
//            access1 += cursor.getString(2) + "\r\n";
//        }
//
//        System.out.println(date1);
//        date.setText(date1);
//        id.setText(id1);
//        access.setText(access1);
//        cursor.close();
//        sql.close();
    }
    public void click_yes(View v){
        finish();
    }

    public void getAllNomalArduino() {

        HttpServerRequest request = new HttpServerRequest(this, "HttpResponse", "SEARCHALLNORMAL",
                getAllNormalArduinoHandler, true);

        request.submit();

    }

    HttpServerRequest.ResponseHandler getAllNormalArduinoHandler = new HttpServerRequest.ResponseHandler() {

        @Override
        public JSONArray getJsonArray() {
            return super.getJsonArray();
        }

        public void responseArrived(JSONArray jsonArray, boolean isError, int what) throws JSONException {

            if ( null != jsonArray)  {   //jsonArray.isNull(0)
                try {
                    ArrayList<ArduinoEntity> arduList = new ArrayList<ArduinoEntity>();


                    for (int i = 0; i < jsonArray.length(); i++) {
                        ArduinoEntity ardu = new ArduinoEntity();

                        JSONObject obj = jsonArray.getJSONObject(i);
                        ardu.setId(obj.getString("id"));
                        ardu.setInsertime(obj.getString("insertime"));
                        ardu.setAccessStatus(obj.getString("accessStatus"));

                        Toast.makeText(AcsyesActivity.this, ardu.getId()  + "hi", Toast.LENGTH_LONG);

                        arduList.add(ardu);


                    }

                    for(int i = 0; i < arduList.size(); i++)
                    {
                        ArduinoEntity ardu = arduList.get(i);
                        date1 += ardu.getInsertime() + "\r\n";
                        id1 += ardu.getId() + "\r\n";
                        access1 += ardu.getAccessStatus() + "\r\n";
                    }


                    System.out.println(date1);
                    date.setText(date1);
                    id.setText(id1);
                    access.setText(access1);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {

            }

        }

    };
}