package com.example.matusvida.myapplication1;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by matus.vida on 4/24/2017.
 */

public class Loader {

    private ClassLoader classLoader;
    private List<Integer> listInt;
    private List<Float> listFloat;
    private String output;
    private BufferedReader reader;


    public Loader(){
        reader = null;
        listInt = new ArrayList<Integer>();
        listFloat = new ArrayList<Float>();
    }

    public List load(String fileName, Context c, String type){
        classLoader = getClass().getClassLoader();
        try {
            reader = new BufferedReader(new InputStreamReader(c.getAssets().open(fileName)));
        } catch (IOException e) {

        }
        if(type.equals("int")){
            getIntItems(c);
            return listInt;
        } else {
            getDoubleItems(c);
            return listFloat;
        }
    }

    public void getIntItems (Context c){
        listInt = new ArrayList<Integer>();
        try {
            while((output = reader.readLine()) != null){
                listInt.add(Integer.valueOf(output));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getDoubleItems (Context c){
        try {
            while((output = reader.readLine()) != null){
                listFloat.add(Float.valueOf(output));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
