package com.example.matusvida.myapplication1;

import android.app.ActivityManager;
import android.content.pm.ActivityInfo;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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

import static java.lang.Thread.sleep;


public class MainActivity extends AppCompatActivity {

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
    int createProfileIterationValue;
    UserFatiqueDetection detection;
    private float currentPulse;
    private float currentBlink;
    private float currentTemperature;
    private float fatiqueRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        setSpinText();
        loadData();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStopped){
                    start.setText("STOP");
                    isStopped = false;
                    setStartMeasure(0.7f);
                    refreshHeartRate.run();
                    refreshBlinkRate.run();
                    refreshTemperature.run();
                    if(getCreateProfileIterationValue() < Props.USER_PROFILE_PULSE_DATA -2){
                        mCircleResult.setBarColor(getResources().getColor(R.color.accent));
                        refreshResult.run();
                    }
                } else if(!isStopped){
                    setStop();

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
                                mCircleBlink.setTextMode(TextMode.VALUE);
                                mCircleBlink.setUnitVisible(mShowUnit);
                                mCircleTemp.setUnitVisible(mShowUnit);
                                break;
                            case SPINNING:
                                mCirclePulse.setTextMode(TextMode.TEXT); // show text while spinning
                                mCirclePulse.setUnitVisible(false);
                                mCircleBlink.setTextMode(TextMode.TEXT);
                                mCircleBlink.setUnitVisible(false);
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
            if(!isValueInList(listHeartRate, i)){
                setDataErrorResult(Props.PULSE_DATA_ERROR);
                setStop();
                isStopped = false;
                return;
            }
            mCirclePulse.setValueAnimated(listHeartRate.get(i)*2);
            if(i < Props.USER_PROFILE_PULSE_DATA){
                setCreateProfileIterationValue(i);
                userProfilePulseList.add(listHeartRate.get(i));
            } else if(i == Props.USER_PROFILE_PULSE_DATA){
                changeResultProgressViewState();
                detection.createProfile(userProfilePulseList, userProfileBlinkList, userProfileTempList);
                Toast.makeText(MainActivity.this, "User profile created !", Toast.LENGTH_LONG).show();
            } else if(i == Props.USER_PROFILE_PULSE_DATA +1){
                mCircleResult.setTextMode(TextMode.PERCENT);

                detectDrowsiness.run();
            }
            i++;
            mCirclePulse.postDelayed(this, Props.PULSE_CHANGING_INTERVAL);
        }
    };

    private Runnable refreshBlinkRate = new Runnable() {
        int i = 0;
        @Override
        public void run() {
            if(!isValueInList(listHeartRate, i)){
                setDataErrorResult(Props.BLINK_DATA_ERROR);
                setStop();
                isStopped = false;
                return;
            }
            mCircleBlink.setValueAnimated(listBlinkRate.get(i));
            if(i < Props.USER_PROFILE_BLINK_DATA){
                userProfileBlinkList.add(listBlinkRate.get(i));
            }
            i++;
            mCircleBlink.postDelayed(this, Props.BLINK_CHANGING_INTERVAL);
        }
    };

    private Runnable refreshTemperature = new Runnable() {
        int i = 0;
        @Override
        public void run() {
            if(!isValueInList(listHeartRate, i)){
                setDataErrorResult(Props.TEMP_DATA_ERROR);
                stopThreads();
                isStopped = false;
                return;
            }
            mCircleTemp.setTextMode(TextMode.TEXT);
            mCircleTemp.setValue(listTemperature.get(i));
            mCircleTemp.setText(String.valueOf(listTemperature.get(i)));
            if(i< Props.USER_PROFILE_TEMP_DATA){
                userProfileTempList.add(listTemperature.get(i));
            } else if(i > Props.USER_PROFILE_TEMP_DATA){
                currentTemperature += mCircleTemp.getCurrentValue();
            }
            i++;
            mCircleTemp.postDelayed(this, Props.TEMP_CHANGING_INTERVAL);
        }
    };

    private Runnable refreshResult = new Runnable() {
        float i = 1;
        @Override
        public void run() {
            i+=(Props.RESULT_CHANGING_INTERVAL_CALCULATION);
            if(getCreateProfileIterationValue() < Props.USER_PROFILE_PULSE_DATA) {
                mCircleResult.setValueAnimated(i);
            } else{

            }
            mCircleResult.postDelayed(this, 1000);
        }
    };

    private Runnable detectDrowsiness = new Runnable() {
        int i = 0;
        @Override
        public void run() {
            mCircleResult.removeCallbacks(showDrowsinessResult);
            currentPulse += mCirclePulse.getCurrentValue();
            currentBlink += mCircleBlink.getCurrentValue();
            if(i==3){
                fatiqueRate = detection.calculateDrowsiness(currentPulse,currentBlink,currentTemperature);
                showDrowsinessResult.run();
                setToNull();
                i=0;
            } else {
                i++;
            }

            mCircleResult.postDelayed(this, 3000);
        }
    };

    private Runnable showDrowsinessResult = new Runnable() {
        @Override
        public void run() {
            mCircleResult.setBarColor(getResources().getColor(R.color.lowFatique), getResources().getColor(R.color.lowMiddleFatique),
                    getResources().getColor(R.color.middleFatique), getResources().getColor(R.color.middleHighFatique),
                    getResources().getColor(R.color.highFatique));

            if(fatiqueRate < 50){
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            }
            mCircleResult.setValueAnimated(fatiqueRate);
            mCircleResult.postDelayed(this, 12000);
        }
    };

    private void setStop(){
        start.setText("START");
        setSpinText();
        setSpin(1);
        isStopped = true;
        stopThreads();
    }

    private void setSpin(float size){
        mCirclePulse.setTextScale(size);
        mCircleBlink.setTextScale(size);
        mCirclePulse.spin();
        mCircleBlink.spin();
        mCircleTemp.spin();
        mCircleResult.stopSpinning();
    }

    private void setStartMeasure(float size){
        mCirclePulse.setTextScale(size);
        mCircleBlink.setTextScale(size);
        mCirclePulse.stopSpinning();
        mCircleBlink.stopSpinning();
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
        mCircleBlink.setShowTextWhileSpinning(true);
        mCircleBlink.setText("Paused ...");
        mCircleResult.setTextMode(TextMode.PERCENT);
    }

    private void setCircleViewResultValue(int i){
        mCircleResult.setValueAnimated((listHeartRate.size() / (Props.USER_PROFILE_PULSE_DATA)) * (i+1));
    }

    public int getCreateProfileIterationValue() {
        return createProfileIterationValue;
    }

    public void setCreateProfileIterationValue(int createProfileIterationValue) {
        this.createProfileIterationValue = createProfileIterationValue;
    }

    public void changeResultProgressViewState(){
        setCircleViewResultValue(Props.USER_PROFILE_CREATED_RESULT_VALUE);
        mCircleResult.removeCallbacks(refreshResult);
        mCircleResult.setTextMode(TextMode.TEXT);
        mCircleResult.setUnitVisible(false);
        mCircleResult.setText("Profile created");
    }

    private void stopThreads(){
        mCirclePulse.removeCallbacks(refreshHeartRate);
        mCircleBlink.removeCallbacks(refreshBlinkRate);
        mCircleTemp.removeCallbacks(refreshTemperature);
        mCircleResult.removeCallbacks(refreshResult);
    }

    private void setToNull(){
        currentPulse = 0;
        currentBlink = 0;
        currentTemperature = 0;
    }

    private void setDataErrorResult(String message){
        mCircleResult.setTextMode(TextMode.TEXT);
        mCircleResult.setText(message);
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        //start.callOnClick();
    }

    private boolean isValueInList(List<Integer> list, int i){
        if(list.size() == i){
            return false;
        }

        return true;
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
                sleep(2000);
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
