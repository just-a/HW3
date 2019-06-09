package com.example.myapplication2;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    static public SensorManager mSensorManager;
    private Sensor mSensor;
    private long lastUpdate = -1;

    float positionYStart = 200f;
    float pivotYStart = 400f;

    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        setContentView(R.layout.activity_main);

        TextView answerTextView = findViewById(R.id.answer);
        answerTextView.setVisibility(View.INVISIBLE);

        if(MainActivity.mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null){
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

    }

    public void onSensorChanged(SensorEvent event) {
        TextView answerTextView = findViewById(R.id.answer);
        final ImageView ballImg = findViewById(R.id.ball);

        ballImg.setPivotY(pivotYStart);

        long timeMicro;
        if(lastUpdate == -1){
            lastUpdate = event.timestamp;
            timeMicro = 0;
        }
        else{
            timeMicro = (event.timestamp - lastUpdate)/1000L;
            lastUpdate = event.timestamp;
        }

        if(event.values[0] != 0) {
            check = true;
            ballImg.clearAnimation();
            animationUp();
            ballImg.setY(positionYStart);
            ballImg.setImageResource(R.drawable.hw3ball_empty);
            answerTextView.setText(setResult(timeMicro));
            answerTextView.setVisibility(View.VISIBLE);
        }
        else{
            check = false;
            answerTextView.setVisibility(View.INVISIBLE);
            ballImg.setImageResource(R.drawable.hw3ball_front);
            animationDown();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onResume(){
        super.onResume();
        if(mSensor != null) mSensorManager.registerListener(this, mSensor, 100000);
    }

    protected void onPause(){
        super.onPause();
        if(mSensor != null) mSensorManager.unregisterListener(this, mSensor);
    }

    private String setResult(long time){
        Resources res = getResources();
        String[] answers = res.getStringArray(R.array.results);
        String result = "";
        double num = time % 12500.0;

        int x = 625;
        for(int i=0; i<20; i++){
            if(num < x){
                result = answers[i];
                break;
            }
            x += 625;
        }

        return result;
    }

    private void animationDown(){
        final ImageView ballImg = findViewById(R.id.ball);
        final TextView answer = findViewById(R.id.answer);
        answer.setAnimation(null);
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 200);
        animation.setDuration(200);
        animation.setRepeatCount(0);
        animation.setFillAfter(true);
        ballImg .startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                if(check) return;
                float positionY = ballImg.getY();
                ballImg.setY(positionY + 200f);
                RotateAnimation anim = new RotateAnimation(0f, 350f, ballImg.getPivotX(),  ballImg.getPivotY());
                anim.setInterpolator(new LinearInterpolator());
                anim.setRepeatCount(Animation.INFINITE);
                anim.setDuration(700);
                ballImg.startAnimation(anim);
            }
        });

    }

    private void animationUp(){
        final ImageView ballImg = findViewById(R.id.ball);
        final TextView answer = findViewById(R.id.answer);
        ballImg.setAnimation(null);
        answer.setAnimation(null);
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, 200f, 0.0f);
        animation.setDuration(200);
        animation.setRepeatCount(0);
        animation.setFillAfter(true);
        ballImg.startAnimation(animation);
        answer.startAnimation(animation);
    }

}

