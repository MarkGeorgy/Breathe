package com.example.m_eorg.ibreathe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;

    private TextView[] mDots;

    private SliderAdapter sliderAdapter;
    private Button mNextBtn;
    private Button mFinishBtn;
    private Button mSkipBtn;

    private ImageView img;

    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);

        mNextBtn = (Button) findViewById(R.id.nextBtn);
        mFinishBtn = (Button) findViewById(R.id.finishBtn);
        mSkipBtn = (Button) findViewById(R.id.skipBtn);

        sliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);


        addDotsIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage+1);
            }
        });


        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(i);
            }
        });

        mSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(i);
            }
        });


    }


    public void addDotsIndicator(int position){
        mDots = new TextView[4];
        mDotLayout.removeAllViews();

        for(int i=0; i < mDots.length; i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));

            mDotLayout.addView(mDots[i]);
        }

        if(mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
            mCurrentPage = i;

            if(i == 0){
                mSkipBtn.setEnabled(false);
                mNextBtn.setEnabled(true);

                mFinishBtn.setEnabled(false);

                mNextBtn.setVisibility(View.VISIBLE);
                mFinishBtn.setVisibility(View.INVISIBLE);
                mSkipBtn.setVisibility(View.INVISIBLE);

                mSkipBtn.setText("");
                mNextBtn.setText("NEXT");

                mFinishBtn.setText("");
            } else if ( i == mDots.length - 1){
                mNextBtn.setEnabled(false);
                mSkipBtn.setEnabled(false);

                mFinishBtn.setEnabled(true);

                mNextBtn.setVisibility(View.INVISIBLE);
                mFinishBtn.setVisibility(View.VISIBLE);
                mSkipBtn.setVisibility(View.INVISIBLE);


                mSkipBtn.setText("");
                mNextBtn.setText("");

                mFinishBtn.setText("FINISH");
            }else if (i == mDots.length - 2 ) {
                mSkipBtn.setEnabled(true);
                mNextBtn.setEnabled(true);

                mFinishBtn.setEnabled(false);

                mNextBtn.setVisibility(View.VISIBLE);
                mFinishBtn.setVisibility(View.INVISIBLE);
                mSkipBtn.setVisibility(View.VISIBLE);

                mSkipBtn.setText("SKIP");
                mNextBtn.setText("NEXT");
                mFinishBtn.setText("");
            } else {
                mSkipBtn.setEnabled(true);
                mNextBtn.setEnabled(true);

                mFinishBtn.setEnabled(false);

                mNextBtn.setVisibility(View.VISIBLE);
                mFinishBtn.setVisibility(View.INVISIBLE);
                mSkipBtn.setVisibility(View.VISIBLE);

                mSkipBtn.setText("SKIP");
                mNextBtn.setText("NEXT");
                mFinishBtn.setText("");
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

}
