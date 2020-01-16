package com.example.m_eorg.ibreathe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class GraphActivity extends AppCompatActivity {
    private LineGraphSeries<DataPoint> series;
    private LineGraphSeries<DataPoint> series1;
    private Context context = GraphActivity.this;
    private static final Random RANDOM = new Random();
    protected String temp = "empty";
    protected String hum = "empty";
    protected String carbon ="empty";
    protected String voc = "empty";
    private int lastX=0;
    private int lastX1=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        //we get graph instance
        GraphView graph = (GraphView) findViewById(R.id.graph);
        GraphView graph1 = (GraphView) findViewById(R.id.graph1);
        //data
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        series1=new LineGraphSeries<DataPoint>();
        graph1.addSeries(series1);
        //customize view ports
        Viewport viewport = graph.getViewport();
        viewport.setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(10);
       graph.getViewport().setMinY(350);
       graph.getViewport().setMaxY(4000);
        graph.setTitle("eCO2");
        graph.setTitleTextSize(60);
        series.setThickness(8);
        series.setColor(Color.BLUE);
       //graph 1
        graph1.getViewport().setYAxisBoundsManual(true);
        graph1.getViewport().setScalable(true);
        graph1.getViewport().setScrollable(true);
        graph1.getViewport().setScalableY(true);
        graph1.getViewport().setScrollableY(true);
        graph1.getViewport().setMinX(0);
        graph1.getViewport().setMaxX(10);
        graph1.getViewport().setMinY(0);
        graph1.getViewport().setMaxY(600);
        LocalBroadcastManager.getInstance(context).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));
        graph1.setTitle("TVOC");
        graph1.setTitleTextSize(60);
        series1.setThickness(8);
        series1.setColor(Color.BLUE);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("Data");
            String[] msg1 = text.split("\n");

            for (int i =0; i < msg1.length; i++) {
                String[] msg2 = msg1[i].split(":");
                System.out.println("output string");

                if (msg2[0].equals("T"))
                    temp = msg2[1];

                else if (msg2[0].equals("V"))
                    voc = msg2[1];

                else if (msg2[0].equals("H"))
                    hum = msg2[1];

                else if (msg2[0].equals("C"))
                    carbon = msg2[1];
            }
            onListen();
        }
    };
    // add random data to graph
    private void addEntry(){
        //here we choose to display max 10 points on the viewport amd we scroll to end
      //  series.appendData(new DataPoint(lastX++, Double.parseDouble(carbon) ), true, 50);
    }

    protected void onListen() {

        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {
            @Override
            public void run() {
                // we add 100 new entries
                while (true){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            series.appendData(new DataPoint(lastX++, Double.parseDouble(carbon) ), true, 5000);
                            series1.appendData(new DataPoint(lastX1++, Double.parseDouble(voc) ), true, 500);
                        }
                    });
                    // sleep to slow don the add of entires
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        //manage error
                        // Now, we can test the result emulator
                        e.printStackTrace();
                    }
                }
            }
        }) .start();
    }
}
