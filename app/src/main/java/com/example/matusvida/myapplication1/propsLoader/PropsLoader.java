package com.example.matusvida.myapplication1.propsLoader;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by matus.vida on 4/24/2017.
 */

public class PropsLoader {
    private static PropsLoader ourInstance = null;
    private Properties properties = null;

    private PropsLoader() throws IOException{
        properties = new Properties();
        properties.load(getClass().getResourceAsStream("messages.properties"));
        System.out.println("something");
    }

    public static synchronized PropsLoader getInstance() {
        if(ourInstance == null){
            try{
                ourInstance = new PropsLoader();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return ourInstance;
    }

    public String getValue(String key){
        return properties.getProperty(key);
    }

}
