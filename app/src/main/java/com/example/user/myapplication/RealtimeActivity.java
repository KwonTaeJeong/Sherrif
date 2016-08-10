package com.example.user.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by user on 2016-08-05.
 */
public class RealtimeActivity extends AppCompatActivity {

    DBManager my;
    SQLiteDatabase sql;

    int sensortoint1;
    TextView txtString, txtStringLength, sensorView0, sensorView1, sensorView2, sensorView3, sensorView4;
    Handler bluetoothIn;

    final int handlerState = 0;        				 //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    private ArrayList<String> RFIDidList = new ArrayList();

    private ConnectedThread mConnectedThread;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static String address;
    String checkresult = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime);


        my = new DBManager(this);

        txtString = (TextView) findViewById(R.id.txtString);
        txtStringLength = (TextView) findViewById(R.id.testView1);
        sensorView0 = (TextView) findViewById(R.id.sensorView0);
        sensorView1 = (TextView) findViewById(R.id.sensorView1);
        sensorView2 = (TextView) findViewById(R.id.sensorView2);
        sensorView3 = (TextView) findViewById(R.id.sensorView3);
        sensorView4 = (TextView) findViewById(R.id.sensorView4);



        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage);
                    int endOfLineIndex = recDataString.indexOf("~");
                    if (endOfLineIndex > 0) {
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);
                        txtString.setText("Data Received = " + dataInPrint);
                        int dataLength = dataInPrint.length();
                        txtStringLength.setText("String Length = " + String.valueOf(dataLength));

                            if (recDataString.charAt(0) == '#') {

                                String tempReceiveData = recDataString.toString();
                                String[] sensorValue = tempReceiveData.split("\\+");

                                char sensor0 = sensorValue[1].charAt(0);
                                String sensor1 = sensorValue[2];
                                char sensor2 = sensorValue[3].charAt(0);
                                String sensor3 = sensorValue[4];

                                sensortoint1 = Integer.parseInt(sensor1);

                                sensorView0.setText(" Sensor 0 감지센서 = " + sensor0);
                                sensorView1.setText(" Sensor 1 거리센서 = " + sensor1 + "cm");
                                sensorView2.setText(" Sensor 2 LED센서 = " + sensor2 + "V");
                                sensorView3.setText(" Sensor 3 RFID = " + sensor3 );


                                if( sensor3.length() > 0 ) {
                                    if( sensor3.startsWith("-")) {
                                        // skip
                                    } else {
                                        // store RFID id
                                        if( !RFIDidList.contains(sensor3)) {
                                            RFIDidList.add(sensor3);
                                        }
                                    }
                                }

                                checkresult = "";
                                if(sensor0=='1')  // 열감지
                                {
                                    if(sensortoint1 <50)
                                    {
                                        if(RFIDidList.size() > 0) {
                                            // 정상 진입
                                            checkresult = "정상";
                                            sql = my.getWritableDatabase();
                                            sql.execSQL("INSERT INTO member VALUES(DATETIME('NOW', 'LOCALTIME'), '"+sensor3+"', '정상');");//정상 INSERT
                                            sql.close();

                                            insertDataToOracle(sensor3 + "", "정상");
                                        } else {
                                            // 비정상 진입
                                            checkresult = "비정상";
                                            sql = my.getWritableDatabase();
                                            sql.execSQL("INSERT INTO member VALUES(DATETIME('NOW', 'LOCALTIME'),'"+sensor3+"', '비정상');");//비정상 INSERT
                                            sql.close();
                                            notification();
                                            insertDataToOracle(sensor3 + "", "비정상");
                                        }
                                    }

                                } else { // 진출경우
                                    RFIDidList.clear();
                                    sql = my.getWritableDatabase();
                                    sql.execSQL("INSERT INTO member VALUES(DATETIME('NOW', 'LOCALTIME'), '"+sensor3+"', '출');");//진출 INSERT
                                    sql.close();
                                    insertDataToOracle(sensor3 + "", "출");

                                }
                                sensorView4.setText(checkresult );
                            }

                        recDataString.delete(0, recDataString.length());
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }


        };

        checkBTState();
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);//블루투스 uuid
    }

    @Override
    public void onResume() {
        super.onResume();

        address = "20:16:06:16:59:82";

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }

        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        mConnectedThread.write("x");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            btSocket.close();
        } catch (IOException e2) {
        }
    }

    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }

    public void click_no(View v){
        finish();
    }

    public void notification(){
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


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

        nm.notify(222, mCompatBuilder.build());
    }
    public void insertDataToOracle(String id, String accessStatus) {

        HttpServerRequest request = new HttpServerRequest(RealtimeActivity.this, "HttpResponse", "INSERTARDUINO",
                insertToArduinoHandler, true);
        request.setParameter("id", id );
        request.setParameter("accessStatus", accessStatus);

        request.submit();

    }

    HttpServerRequest.ResponseHandler insertToArduinoHandler = new HttpServerRequest.ResponseHandler() {
        @Override
        public JSONArray getJsonArray() {
            return super.getJsonArray();
        }
        public void responseArrived(JSONArray jsonArray, boolean isError, int what) {
            // list = jsonArray;
//            if (!jsonArray.isNull(0)) {
//
//                // ***************need to check if insert
//                // success*******************
//
//
//            } else {
//
//            }
        }

    };

}
