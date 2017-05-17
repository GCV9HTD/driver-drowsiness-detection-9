package com.example.matusvida.myapplication1.constants;

/**
 * Created by matus.vida on 4/30/2017.
 */

public final class Props {

    public static final int USER_PROFILE_PULSE_DATA = 10;
    public static final int USER_PROFILE_BLINK_DATA = 10;
    public static final int USER_PROFILE_TEMP_DATA = 6;
    public static final int PULSE_CHANGING_INTERVAL = 3000;
    public static final int TEMP_CHANGING_INTERVAL = 5000;
    public static final int BLINK_CHANGING_INTERVAL = 3000;
    public static final int USER_PROFILE_CREATED_RESULT_VALUE = 100;
    public static final String PULSE_DATA_ERROR = "Can't get pulse data !";
    public static final String BLINK_DATA_ERROR = "Can't get blinking data !";
    public static final String TEMP_DATA_ERROR = "Can't get temperature data !";

    public static double RESULT_CHANGING_INTERVAL_CALCULATION = 100.0/(USER_PROFILE_PULSE_DATA*(PULSE_CHANGING_INTERVAL/1000.0));
}
