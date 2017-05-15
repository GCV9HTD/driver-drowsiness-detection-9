package com.example.matusvida.myapplication1;

import android.widget.Toast;

import com.example.matusvida.myapplication1.constants.Props;
import com.example.matusvida.myapplication1.detection.UserFatiqueDetection;

import java.util.List;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

/**
 * Created by matus.vida on 5/13/2017.
 */

public class CircleViewRefresher {

    private CircleProgressView mCirclePulse;
    private CircleProgressView mCircleTemp;
    private CircleProgressView mCircleBlink;
    private CircleProgressView mCircleResult;
    private List<Integer> listHeartRate;
    private List<Integer> listBlinkRate;
    private List<Float> listTemperature;
    private List<Integer> userProfilePulseList;
    private List<Integer> userProfileBlinkList;
    private List<Float> userProfileTempList;
    private UserFatiqueDetection detection;


    public CircleViewRefresher(CircleProgressView mCirclePulse, CircleProgressView mCircleTemp, CircleProgressView mCircleBlink,
                               CircleProgressView mCircleResult, List<Integer> listHeartRate, List<Integer> userProfilePulseList){
        this.mCirclePulse = mCirclePulse;
        this.mCircleBlink = mCircleBlink;
        this.mCircleTemp = mCircleTemp;
        this.mCircleResult = mCircleResult;
        this.listHeartRate = listHeartRate;
        this.userProfilePulseList = userProfilePulseList;
        detection = new UserFatiqueDetection();
    }

    public void run(){
        refreshHeartRate.run();
    }
    //public void refreshValue(){
      //  refreshHeartRate =
        Runnable refreshHeartRate =new Runnable() {
            int i = 0;
            @Override
            public void run() {
                mCirclePulse.setValueAnimated(listHeartRate.get(i)*2);
                if(i < Props.USER_PROFILE_PULSE_DATA){
                    userProfilePulseList.add(listHeartRate.get(i));
                    setCircleViewResultValue(i);
                } else if(i == Props.USER_PROFILE_PULSE_DATA){
                    setCircleViewResultValue(i);
                    //detection.createProfile(userProfilePulseList, userProfileBlinkList, userProfileTempList);
                   // Toast.makeText(MainActivity.this, "User profile created !", Toast.LENGTH_LONG).show();
                } else{
                    mCircleResult.setTextMode(TextMode.TEXT);
                    mCircleResult.setUnitVisible(false);
                    mCircleResult.setText("Profile created");
                }
                i++;
                mCirclePulse.postDelayed(this, 3000);
            }
        };

    //}

    private void setCircleViewResultValue(int i){
        mCircleResult.setValueAnimated((listHeartRate.size() / (Props.USER_PROFILE_PULSE_DATA)) * (i+1));
    }
}
