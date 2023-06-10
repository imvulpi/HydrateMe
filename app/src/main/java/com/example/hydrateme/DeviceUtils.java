package com.example.hydrateme;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;

public class DeviceUtils {

    public static int getDeviceWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        display.getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getDeviceHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        display.getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}

