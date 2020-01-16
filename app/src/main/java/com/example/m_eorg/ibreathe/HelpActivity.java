package com.example.m_eorg.ibreathe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class HelpActivity extends AppCompatActivity {

    protected TextView details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
         details = findViewById(R.id.thresholds);

         details.setText("                   THRESHOLD EXPLANATIONS\n\n                                      [eCO2]\n\n0-700 ppm:\nTypical level found in occupied spaces with good air exchange.\n\n" +
                 "701-1500 ppm:\nLevel associated with complaints of drowsiness and poor air.\n\n1501-4000 ppm:\nLevel associated with headaches, sleepiness, and stagnant, stale, stuffy air; poor concentration, " +
                 "loss of attention, increased heart rate and slight nausea may also be present.\n\nOver 4000 ppm:\nThis indicates unusual air conditions where high levels of other gases also could be present. " +
                 "Toxicity or oxygen deprivation could occur. This is the permissible exposure limit for daily workplace exposures.\n\n\n     [Total Volatile Organic Compounds (TVOC)]\n\n0-200 ppb:\nNo irritation or discomfort is expected." +
                 "201-350 ppb:\nOccasional irritation or discomfort may be possible with sensitive individuals.\n\n351-500 ppb:\nComplaints about irritation and discomfort are possible in sensitive individuals. Irritation and disocomfort very likely." +
                 "\n\n\n                                  [Humidity]\n\nPeople with mild asthma may find that when summer temperatures soar, along with humidity levels, their asthma symptoms begin to act up. Breathing in such hot environments could lead to coughing and shortness of breath, " +
                 "suggests research reported in the American Journal of Respiratory and Critical Care Medicine.\n\nIf outside temperature is 20 to 40 degrees, humidity indoors should not be more than 40 percent.\n\n" +
                 "If outside temperature is 10 to 20 degrees, humidity indoors should not be more than 35 percent.\n\nIf outside temperature is 0 to 10 degrees, humidity indoors should not be more than 30 percent.\n\n\n                               [Temperature]\n\n" +
                 "Less than -15.5: Cold, dry air is a common asthma trigger and can cause bad flare-ups. That's especially true for people who play winter sports and have exercise-induced asthma.\n\n" +
                 "Greater than 26: Hot, humid air also can be a problem. In some places, heat and sunlight combine with pollutants to create ground-level ozone. This kind of ozone can be a strong asthma trigger.");

    }
}


