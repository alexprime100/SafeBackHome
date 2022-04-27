package com.example.safebackhome;

import android.util.Log;

import com.example.safebackhome.models.Contact;
import com.example.safebackhome.models.User;

import java.util.ArrayList;

public class Data {
    public static User user;
    public static final int LOCATION_SERVICE_ID = 175;
    public static final String ACTION_START_LOCATION_SERVICE = "startLocationService";
    public static final String ACTION_STOP_LOCATION_SERVICE = "stopLocationService";

    public static void logger(Exception e){
        Log.e("ERROR", e.getMessage().toString(), e);
    }
}
