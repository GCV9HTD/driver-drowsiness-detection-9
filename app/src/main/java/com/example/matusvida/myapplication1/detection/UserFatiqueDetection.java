package com.example.matusvida.myapplication1.detection;

import com.example.matusvida.myapplication1.constants.Props;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matus.vida on 4/30/2017.
 */

public class UserFatiqueDetection {

    private final int pulseRating = 3;
    private final int blinkRating = 2;
    private final int temperatureRating = 3;

    private float fatigueRate;
    private List<Integer> listPulse;
    private List<Integer> listBlink;
    private List<Float> listTemperature;
    private int minPulse;
    private float minTemperature;
    private int minBlink;
    private int maxPulse;
    private int maxBlink;
    private float maxTemperature;
    private double avgPulse;
    private double avgBlink;
    private double avgTemperature;

    public UserFatiqueDetection(){
        fatigueRate = 0;
        listPulse = new ArrayList<Integer>();
        listBlink = new ArrayList<Integer>();
        listTemperature = new ArrayList<Float>();
    }

    public void createProfile(List<Integer> pulse, List<Integer> blink, List<Float> temp){
        listPulse = pulse;
        listBlink = blink;
        listTemperature = temp;
        setAverageValues();
        setMaxPulse(Collections.max(listPulse));
        setMinPulse(Collections.min(listPulse));
        setMaxBlink(Collections.max(listBlink));
        setMinBlink(Collections.min(listBlink));
        setMaxTemperature(Collections.max(listTemperature));
        setMinTemperature(Collections.min(listTemperature));
    }

    private void setAverageValues(){
        double pulseSum=0;
        double tempSum=0;
        double blinkSum=0;
        for(Float number: listTemperature){
            tempSum += number;
        }
        for(int i=0; i<listPulse.size(); i++){
            pulseSum += listPulse.get(i);
            blinkSum += listBlink.get(i);
        }
        setAvgPulse(pulseSum/listPulse.size());
        setAvgBlink(blinkSum/listBlink.size());
        setAvgTemperature(tempSum/listTemperature.size());
    }

    public float calculateDrowsiness(float currentPulse, float currentBlink, float currentTemperature) {
        float currentPulseAvg = (currentPulse / 8);// * pulseRating;
        float currentBlinkAvg = (currentBlink / 4);
        float currentTempAvg;
        if (currentTemperature > 100){
            currentTempAvg = (currentTemperature / 3);
        } else{
            currentTempAvg = currentTemperature / 2;
        }
        float result = 0;

        float pulse = currentPulseAvg - minPulse;
        float blink = currentBlinkAvg - minBlink;
        float temp = currentTempAvg - minTemperature;

        float maxSum = (maxPulse-minPulse) + (maxBlink-minBlink) + (maxTemperature-minTemperature) * 100;
        if(pulse < 0){
            pulse *= pulseRating;
        }
        if(blink < 0){
            blink *= blinkRating;
        }
        if(temp < 0){
            temp *= temperatureRating;
        }
        fatigueRate = pulse + blink + temp;

        if(fatigueRate < 0){
            result = fatigueRate*(-1)*2.5f + Props.MIN_VALUES_PERCENTAGE;
        } else{
            result = Props.MIN_VALUES_PERCENTAGE - fatigueRate *2.5f;
        }
        return result;
    }


    public void setMinPulse(int minPulse) {
        this.minPulse = minPulse;
    }

    public void setMinTemperature(float minTemperature) {
        this.minTemperature = minTemperature;
    }

    public void setMinBlink(int minBlink) {
        this.minBlink = minBlink;
    }

    public void setMaxPulse(int maxPulse) {
        this.maxPulse = maxPulse;
    }

    public void setMaxBlink(int maxBlink) {
        this.maxBlink = maxBlink;
    }

    public void setMaxTemperature(float maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public void setAvgPulse(double avgPulse) {
        this.avgPulse = avgPulse;
    }

    public void setAvgBlink(double avgBlink) {
        this.avgBlink = avgBlink;
    }

    public void setAvgTemperature(double avgTemperature) {
        this.avgTemperature = avgTemperature;
    }
}
