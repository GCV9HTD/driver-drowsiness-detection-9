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

    private final int pulseRating = 6;
    private final int blinkRating = 4;
    private final int temperatureRating = 2;

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
        if (currentTemperature > 100){// * blinkRating;
            currentTempAvg = (currentTemperature / 3);// * temperatureRating;
        } else{
            currentTempAvg = currentTemperature / 2;
        }
        float result = 0;

        float pulse = currentPulseAvg - minPulse;
        float blink = currentBlinkAvg - minBlink;
        float temp = currentTempAvg - minTemperature;

        float maxSum = (maxPulse-minPulse) + (maxBlink-minBlink) + (maxTemperature-minTemperature) * 100;
        if(pulse < 0){
            pulse *= 3;
        }
        if(blink < 0){
            blink *= 2;
        }
        if(temp < 0){
            temp *= 3;
        }
        fatigueRate = pulse + blink + temp;

        if(fatigueRate < 0){
            result = fatigueRate*(-1)*2.5f + 50;
        } else{
            result = 50 - fatigueRate *2.5f;
        }
        return result;
    }

    public int getMinPulse() {
        return minPulse;
    }

    public void setMinPulse(int minPulse) {
        this.minPulse = minPulse;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(float minTemperature) {
        this.minTemperature = minTemperature;
    }

    public int getMinBlink() {
        return minBlink;
    }

    public void setMinBlink(int minBlink) {
        this.minBlink = minBlink;
    }

    public int getMaxPulse() {
        return maxPulse;
    }

    public void setMaxPulse(int maxPulse) {
        this.maxPulse = maxPulse;
    }

    public int getMaxBlink() {
        return maxBlink;
    }

    public void setMaxBlink(int maxBlink) {
        this.maxBlink = maxBlink;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(float maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public double getAvgPulse() {
        return avgPulse;
    }

    public void setAvgPulse(double avgPulse) {
        this.avgPulse = avgPulse;
    }

    public double getAvgBlink() {
        return avgBlink;
    }

    public void setAvgBlink(double avgBlink) {
        this.avgBlink = avgBlink;
    }

    public double getAvgTemperature() {
        return avgTemperature;
    }

    public void setAvgTemperature(double avgTemperature) {
        this.avgTemperature = avgTemperature;
    }
}
