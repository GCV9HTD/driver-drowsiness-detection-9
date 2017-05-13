package com.example.matusvida.myapplication1.detection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matus.vida on 4/30/2017.
 */

public class UserFatiqueDetection {

    private int fatigueRate;
    private List<Integer> listPulse;
    private List<Float> listTemperature;
    private int modusPulse;
    private int modusTemperature;
    private int minPulse;
    private int minTemperature;
    private int maxPulse;
    private int maxTemperature;
    private double avgPulse;
    private double avgTemperature;

    public UserFatiqueDetection(){
        fatigueRate = 0;
        listPulse = new ArrayList<Integer>();
        listTemperature = new ArrayList<Float>();
        modusPulse = 0;
        modusTemperature = 0;
        minPulse = 100;
        minTemperature = 39;
        maxPulse = 0;
        maxTemperature = 0;
        avgPulse = 0;
        avgTemperature = 0;
    }

    public void createProfile(List list){
        if(list.get(0) instanceof Integer){
            listPulse = list;
            avgPulse = getAveragePulse();
            System.out.println(modusPulse);
        } else if(list.get(0) instanceof Float){
            listTemperature = list;
        }
    }

    private double getAveragePulse(){
        int sum = 0;
        for(Integer number: listPulse){
            sum+=number;
        }
        return sum/listPulse.size();
    }

//    private void getPulseModeValue(){
//        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
//        for(Integer number: listPulse){
//            if(map.containsKey(number)){
//                map.put(number, map.get(number) +1);
//            } else{
//                map.put(number, 1);
//            }
//        }
//        Map.Entry<Integer,Integer> maxEntry = null;
//
//        for(Map.Entry<Integer,Integer> entry : map.entrySet()) {
//            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
//                maxEntry = entry;
//                modusPulse = maxEntry.getKey();
//            }
//            if(entry.getKey() > maxPulse){
//                maxPulse = entry.getKey();
//            }
//            if(entry.getKey() < minPulse){
//                minPulse = entry.getKey();
//            }
//        }
//    }
}
