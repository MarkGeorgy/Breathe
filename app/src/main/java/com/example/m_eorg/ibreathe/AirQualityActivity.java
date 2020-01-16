package com.example.m_eorg.ibreathe;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class AirQualityActivity extends AppCompatActivity {
    private Context context = AirQualityActivity.this;
    public static final String CHANNEL_10_ID = "channel10";
    public static final String CHANNEL_11_ID = "channel11";
    public static final String CHANNEL_ID = "exampleServiceChannel";
    protected NotificationManagerCompat notificationManager;
    protected TextView numberTextCO2;
    protected TextView numberTextVOC;
    protected TextView variableValuesView;
    protected ProgressBar circularViewCO2;
    protected ProgressBar circularViewVOC;
    protected TextView unitCO2;
    protected TextView unitVOC;
    protected TextView tempValue;
    protected TextView humValue;
    protected RelativeLayout relativeLayout;
    protected final String DEVICE_ADDRESS="98:D3:81:FD:4D:91";
    protected final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    protected BluetoothDevice device;
    protected BluetoothSocket socket;
    protected OutputStream outputStream;
    protected InputStream inputStream;
    protected String temp = "empty";
    protected String hum = "empty";
    protected String carbon ="empty";
    protected String voc = "empty";
    protected BluetoothAdapter bluetoothAdapter;
    TextView textView;
    boolean deviceConnected=false;
    byte buffer[];
    Thread thread;
    boolean stopThread;
    static boolean notifyisON = false;
    private static final String TAG = "AirQuality";

    StringBuilder messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");


        setContentView(R.layout.activity_air_quality);

        setTitle("Air Quality Data"); // Setting the title of the second` activity (Appears on top of the screen as Air Quality Data)

        variableValuesView = findViewById(R.id.variableValuesListView);
        relativeLayout = findViewById(R.id.relativeLayout);
        numberTextCO2=findViewById(R.id.numberTextViewCO2);
        numberTextVOC=findViewById(R.id.numberTextViewVOC);
        circularViewCO2 = findViewById(R.id.circularBarViewCO2);
        circularViewVOC = findViewById(R.id.circularBarViewVOC);
        unitCO2 = findViewById(R.id.unitCO2);
        unitVOC = findViewById(R.id.unitVOC);
        tempValue = findViewById(R.id.tempValueTextView);
        humValue = findViewById(R.id.humValueTextView);
        notificationManager = NotificationManagerCompat.from(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        createNotificationChannels();

        messages = new StringBuilder();

        LocalBroadcastManager.getInstance(context).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.notificationON:
                if(!notifyisON){
                    //startService();
                    notifyisON=true;
                }
                else{
                    //stopService();
                    notifyisON=false;
                }
                break;

            case R.id.seeGraphs:
                openGraph();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendNotificationCO2(String title, String message) {
        Intent intent = new Intent(this, AirQualityActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_10_ID)
                .setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setOnlyAlertOnce(true);

        notificationManager.notify(1, builder.build());

    }

    public void sendNotificationVOC(String title, String message) {
        Intent intent = new Intent(this, AirQualityActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_11_ID)
                .setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setOnlyAlertOnce(true);

        notificationManager.notify(2, builder.build());

    }


    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel (CHANNEL_10_ID, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("This is CO2 Channel");
            NotificationChannel channel2 = new NotificationChannel(CHANNEL_11_ID, "Channel 2", NotificationManager.IMPORTANCE_HIGH);
            channel2.setDescription("This is VOC Channel");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Notification Service", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);

        }
    }

    public void startService(){
        String input = "CO2: " + carbon + " ppm" + "\t\t\t\t            TVOC: "+ voc + " ppb" + "               \t\t\t\tTemperature: " + temp + " °C" +  "\t\t\t\t   Humidity: " + hum + " %";

        Intent serviceIntent = new Intent(this, NotificationService.class);
        serviceIntent.putExtra("inputExtra", input);

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, NotificationService.class);
        stopService(serviceIntent);
    }

    void checkThresholdCO2() {
        if (Integer.parseInt(carbon) <= 700) {
            Drawable progressDrawable = circularViewCO2.getProgressDrawable().mutate();
            progressDrawable.setColorFilter(Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
            numberTextCO2.setTextColor(Color.GREEN);
            unitCO2.setTextColor(Color.GREEN);
            circularViewCO2.setProgressDrawable(progressDrawable);
        } else if (Integer.parseInt(carbon) > 700 && Integer.parseInt(carbon) <= 1500) {
            Drawable progressDrawable = circularViewCO2.getProgressDrawable().mutate();
            progressDrawable.setColorFilter(Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
            numberTextCO2.setTextColor(Color.YELLOW);
            unitCO2.setTextColor(Color.YELLOW);
            circularViewCO2.setProgressDrawable(progressDrawable);

        } else if (Integer.parseInt(carbon) > 1500 && Integer.parseInt(carbon) <= 4000) {
            Drawable progressDrawable = circularViewCO2.getProgressDrawable().mutate();
            progressDrawable.setColorFilter(Color.parseColor("#ff6d00"), android.graphics.PorterDuff.Mode.SRC_IN);
            numberTextCO2.setTextColor(Color.parseColor("#ff6d00"));
            unitCO2.setTextColor(Color.parseColor("#ff6d00"));
            circularViewCO2.setProgressDrawable(progressDrawable);
            sendNotificationCO2("WARNING", "Amount of CO₂ is BAD");
        } else {
            Drawable progressDrawable = circularViewCO2.getProgressDrawable().mutate();
            progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
            numberTextCO2.setTextColor(Color.RED);
            unitCO2.setTextColor(Color.RED);
            circularViewCO2.setProgressDrawable(progressDrawable);
            sendNotificationCO2("ALERT", "Amount of CO₂ is HAZARDOUS");
        }
    }
    void checkThresholdVOC() {

        if (Integer.parseInt(voc)<=200 ){
            Drawable progressDrawable = circularViewVOC.getProgressDrawable().mutate();
            progressDrawable.setColorFilter(Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
            numberTextVOC.setTextColor(Color.GREEN);
            unitVOC.setTextColor(Color.GREEN);
            circularViewVOC.setProgressDrawable(progressDrawable);
        }
        else if (Integer.parseInt(voc)> 200 && Integer.parseInt(voc)<=350 )
        {
            Drawable progressDrawable = circularViewVOC.getProgressDrawable().mutate();
            progressDrawable.setColorFilter(Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
            numberTextVOC.setTextColor(Color.YELLOW);
            unitVOC.setTextColor(Color.YELLOW);
            circularViewVOC.setProgressDrawable(progressDrawable);

        }
        else if (Integer.parseInt(voc)> 350 && Integer.parseInt(voc)<=500 )
        {
            Drawable progressDrawable = circularViewVOC.getProgressDrawable().mutate();
            progressDrawable.setColorFilter(Color.parseColor("#ff6d00"), android.graphics.PorterDuff.Mode.SRC_IN);
            numberTextVOC.setTextColor(Color.parseColor("#ff6d00"));
            unitVOC.setTextColor(Color.parseColor("#ff6d00"));
            circularViewVOC.setProgressDrawable(progressDrawable);
            sendNotificationVOC("WARNING", "Amount of TVOC is BAD");
        }
        else {
            Drawable progressDrawable = circularViewVOC.getProgressDrawable().mutate();
            progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
            numberTextVOC.setTextColor(Color.RED);
            unitVOC.setTextColor(Color.RED);
            circularViewVOC.setProgressDrawable(progressDrawable);
            sendNotificationVOC("ALERT", "Amount of TVOC is HAZARDOUS");
        }
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("Data");
            String[] msg1 = text.split("\n");

            for (int i =0; i < msg1.length; i++)
            {
                String[] msg2 = msg1[i].split(":");
                System.out.println("output string");

                if(msg2[0].equals("T"))
                    temp = msg2[1];

                else if (msg2[0].equals("V"))
                    voc = msg2[1];

                else if (msg2[0].equals("H"))
                    hum = msg2[1];

                else if (msg2[0].equals("C"))
                    carbon = msg2[1];
            }
            // Do something with the message
            // Here you will receive values from your arduino sensor
            // Get these values and set text by using below method

            String temp_value = temp + " °C";
            String hum_value = hum + " %";
            tempValue.setText(temp_value);
            humValue.setText(hum_value);
            numberTextCO2.setText(carbon);
            numberTextVOC.setText(voc);
            checkThresholdCO2();
            checkThresholdVOC();
            if (notifyisON){
               startService();
            }
            else{
                stopService();
            }
        }
    };

    private void openGraph() {
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }
}
