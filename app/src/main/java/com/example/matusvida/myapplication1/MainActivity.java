package com.example.matusvida.myapplication1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.matusvida.myapplication1.constants.Props;
import com.example.matusvida.myapplication1.detection.UserFatiqueDetection;

import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    CircleProgressView mCirclePulse;
    CircleProgressView mCircleTemp;
    CircleProgressView mCircleBlink;
    CircleProgressView mCircleResult;
    Boolean mShowUnit = true;
    Button start;
    List<Integer> listHeartRate;
    List<Integer> listBlinkRate;
    List<Float> listTemperature;
    List<Integer> userProfilePulseList;
    List<Integer> userProfileBlinkList;
    List<Float> userProfileTempList;
    boolean isStopped = true;
    UserFatiqueDetection detection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setSpinText();
        loadData();


        start.setOnClickListener(new View.OnClickListener() {
            long startTime = System.currentTimeMillis();
            @Override
            public void onClick(View v) {
                if(isStopped){
                    start.setText("STOP");
                    isStopped = false;
                    setMeasure(0.7f);
                    refreshHeartRate.run();
                    refreshBlinkRate.run();
                    refreshTemperature.run();
                    //new ProgressTask().doInBackground();
                    if(userProfilePulseList.size() == 2) {
                        Toast.makeText(MainActivity.this, "User profile created !", Toast.LENGTH_LONG).show();
                    }

                } else if(!isStopped){
                    start.setText("START");
                    setSpinText();
                    setSpin(1); //1 is size of text while spinning
                    isStopped = true;
                    mCirclePulse.removeCallbacks(refreshHeartRate);
                    mCircleTemp.removeCallbacks(refreshTemperature);
                }
            }
        });

        mCirclePulse.setOnAnimationStateChangedListener(
                new AnimationStateChangedListener() {
                    @Override
                    public void onAnimationStateChanged(AnimationState _animationState) {
                        switch (_animationState) {
                            case IDLE:
                            case ANIMATING:
                            case START_ANIMATING_AFTER_SPINNING:
                                mCirclePulse.setTextMode(TextMode.PERCENT); // show percent if not spinning
                                mCirclePulse.setUnitVisible(mShowUnit);
                                mCircleTemp.setUnitVisible(mShowUnit);
                                break;
                            case SPINNING:
                                mCirclePulse.setTextMode(TextMode.TEXT); // show text while spinning
                                mCirclePulse.setUnitVisible(false);
                                mCircleTemp.setUnitVisible(false);
                            case END_SPINNING:
                                break;
                            case END_SPINNING_START_ANIMATING:
                                break;

                        }
                    }
                }
        );
    }

    private Runnable refreshHeartRate =new Runnable() {
        int i = 0;
        @Override
        public void run() {
            mCirclePulse.setValueAnimated(listHeartRate.get(i)*2);
            if(i < Props.USER_PROFILE_PULSE_DATA){
                userProfilePulseList.add(listHeartRate.get(i));
                setCircleViewResultValue(i);
            } else if(i == Props.USER_PROFILE_PULSE_DATA){
                setCircleViewResultValue(i);
                detection.createProfile(userProfilePulseList);
                Toast.makeText(MainActivity.this, "User profile created !", Toast.LENGTH_LONG).show();
            } else{
                mCircleResult.setTextMode(TextMode.TEXT);
                mCircleResult.setUnitVisible(false);
                mCircleResult.setText("Profile created");
            }
            i++;
            mCirclePulse.postDelayed(this, 3000);
        }
    };

    private Runnable refreshBlinkRate = new Runnable() {
        int i = 0;
        @Override
        public void run() {
            mCircleBlink.setValueAnimated(listBlinkRate.get(i));
            if(i < Props.USER_PROFILE_BLINK_DATA){
                userProfileBlinkList.add(listBlinkRate.get(i));
            }
            i++;
            mCircleBlink.postDelayed(this, 3000);
        }
    };

    private Runnable refreshTemperature = new Runnable() {
        int i = 0;
        @Override
        public void run() {
            mCircleTemp.setTextMode(TextMode.TEXT);
            mCircleTemp.setText(String.valueOf(listTemperature.get(i)));

            if(i< Props.USER_PROFILE_TEMP_DATA){
                userProfileTempList.add(listTemperature.get(i));
            } else{
                detection.createProfile(userProfileTempList);
            }
            i++;
            mCircleTemp.postDelayed(this, 5000);
        }
    };

//    private Runnable refreshResult = new Runnable() {
//        @Override
//        public void run() {
//            setCircleViewResultValue(i);
//        }
//    }

    private void setSpin(float size){
        mCirclePulse.setTextScale(size);
        mCirclePulse.spin();
        mCircleTemp.spin();
        mCircleResult.stopSpinning();
    }

    private void setMeasure(float size){
        mCirclePulse.setTextScale(size);
        mCirclePulse.stopSpinning();
        mCircleTemp.stopSpinning();
        //mCircleResult.spin();
        //mCircleResult.setValueAnimated(listHeartRate.size());

    }

    private void loadData(){
        Loader loader = new Loader();
        listHeartRate = new ArrayList<Integer>();
        listBlinkRate = new ArrayList<Integer>();
        listTemperature = new ArrayList<Float>();
        listHeartRate = loader.load("heartRate.txt", this, "int");
        listBlinkRate = loader.load("blinkRate.txt", this, "int");
        listTemperature = loader.load("temperature.txt", this, "double");
    }

    private void setSpinText(){
        mCirclePulse.setShowTextWhileSpinning(true); // Show/hide text in spinning mode
        mCirclePulse.setText("Paused ...");
        mCircleTemp.setShowTextWhileSpinning(true); // Show/hide text in spinning mode
        mCircleTemp.setText("Paused ...");
        mCircleResult.setTextMode(TextMode.PERCENT);
    }

    private void setCircleViewResultValue(int i){
        mCircleResult.setValueAnimated((listHeartRate.size() / (Props.USER_PROFILE_PULSE_DATA)) * (i+1));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void init(){
        mCirclePulse = (CircleProgressView) findViewById(R.id.circleViewHeart);
        mCircleTemp= (CircleProgressView) findViewById(R.id.circleViewTemp);
        mCircleBlink = (CircleProgressView) findViewById(R.id.circleViewBlink);
        mCircleResult = (CircleProgressView) findViewById(R.id.circleViewResult);
        start = (Button) findViewById(R.id.button);

        userProfilePulseList = new ArrayList<Integer>();
        userProfileBlinkList = new ArrayList<Integer>();
        userProfileTempList = new ArrayList<Float>();

        detection = new UserFatiqueDetection();

        mCirclePulse.setClickable(false);
        mCircleTemp.setClickable(false);
        mCircleBlink.setClickable(false);
        mCircleResult.setClickable(false);
    }

    private class ProgressTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //mCircleResult.setValue(0);
                    mCircleResult.spin();
                }
            });

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }
}
