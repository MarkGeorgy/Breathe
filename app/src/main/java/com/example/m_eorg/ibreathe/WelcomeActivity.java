package com.example.m_eorg.ibreathe;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class WelcomeActivity extends AppCompatActivity {
    private Context context = WelcomeActivity.this;
    private static final String TAG = "Welcome Activity";
    private Button scanAirQualityButton = null;
    private Button scanAirQualityText;
    protected FloatingActionButton addReminder;
    private final String DEVICE_ADDRESS="98:D3:81:FD:4D:91";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    protected OutputStream outputStream;
    protected InputStream inputStream;
    boolean deviceConnected=false;
    static int scanButton = View.INVISIBLE;
    protected String temp = "empty";
    protected String hum = "empty";
    protected String carbon ="empty";
    protected String voc = "empty";

    byte buffer[];
    Thread thread;
    boolean stopThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Log.d(TAG, "The onCreate() event");
        scanAirQualityButton = findViewById(R.id.scanAirQualityButton);
        scanAirQualityButton.setOnClickListener(onClickViewButton);
        scanAirQualityText = findViewById(R.id.scanAirQualityTextView);
        addReminder = findViewById(R.id.addReminderButton);
        addReminder.setBackgroundColor(Color.YELLOW);
        addReminder.setOnClickListener(onClickViewButton2);
        scanAirQualityButton.setVisibility(scanButton);
        scanAirQualityText.setVisibility(scanButton);

    }


    private void openQuality() {
        Intent intent = new Intent(this, AirQualityActivity.class);
        startActivity(intent);
    }


    private void openReminder() {
        Intent intent = new Intent(this, ReminderActivity.class);
        startActivity(intent);
    }

    private void openHelp(){
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.helpMenu:
                openHelp();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private Button.OnClickListener onClickViewButton = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            openQuality();
        }
    };

    private Button.OnClickListener onClickViewButton2 = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            openReminder();
        }

    };

    public boolean BTinit()
    {
        boolean found=false;
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Device doesn't Support Bluetooth",Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if(bondedDevices.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please connect to the Device",Toast.LENGTH_SHORT).show();
        }
        else
        {
            for (BluetoothDevice iterator : bondedDevices)
            {
                if(iterator.getAddress().equals(DEVICE_ADDRESS))
                {
                    device=iterator;
                    found=true;
                    break;
                }
            }
        }
        return found;
    }

    public boolean BTconnect()
    {
        boolean connected=true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected=false;
        }
        if(connected)
        {
            try {
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream=socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return connected;
    }

    public void onClickStart(View v) {
        if(BTinit())
        {
            if(BTconnect())
            {
                beginListenForData();
                deviceConnected=true;
                scanButton = View.VISIBLE;
                scanAirQualityButton.setVisibility(scanButton);
                scanAirQualityText.setVisibility(scanButton);
                Toast.makeText(getApplicationContext(),"Connection Successful",Toast.LENGTH_SHORT).show();
            }

            else
            {
                Toast.makeText(getApplicationContext(),"Already Connected",Toast.LENGTH_SHORT).show();
            }

        }

    }

    public void beginListenForData()
    {
        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];
        thread  = new Thread(new Runnable()
        {

            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopThread)
                {
                    try
                    {
                        Thread.sleep(5000);
                        int byteCount = inputStream.available();
                        if(byteCount > 0)
                        {
                            byte[] rawBytes = new byte[byteCount];
                            inputStream.read(rawBytes);

                            final String string = new String(rawBytes,"UTF-8");

                            final String[] newstring = string.split("\n");

                            System.out.println("output everything");
                            System.out.println("end output eveything");
                            System.out.println("length: " + newstring.length);

                            for (int i =0; i < newstring.length; i++)
                            {
                                final String[] newstring2 = newstring[i].split(":");
                                System.out.println("output string");

                                if(newstring2[0].equals("T"))
                                    temp = newstring2[1];

                                else if (newstring2[0].equals("V"))
                                    voc = newstring2[1];

                                else if (newstring2[0].equals("H"))
                                    hum = newstring2[1];

                                else if (newstring2[0].equals("C"))
                                    carbon = newstring2[1];
                            }

                            System.out.println("values start");
                            System.out.println(temp);
                            System.out.println(hum);
                            System.out.println(carbon);
                            System.out.println(voc);
                            System.out.println("values end");

                            handler.post(new Runnable() {
                                public void run()
                                {

                                    Intent intent = new Intent("incomingMessage");
                                    intent.putExtra("Data",string);
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                }

                            });

                        }
                    }
                    catch (IOException ex)
                    {
                        stopThread = true;
                    }

                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        });

        thread.start();
    }

}