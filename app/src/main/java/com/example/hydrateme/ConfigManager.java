package com.example.hydrateme;

import android.content.SharedPreferences;
import android.util.Log;

public class ConfigManager {

    private static final String CONFIG_PREFS_NAME = "GlobalConfig";
    private SharedPreferences sharedPreferences;

    public ConfigManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void initializeDefaultConfig() {
        if(!sharedPreferences.contains("unitType")){
            String defaultUnit = "ml";
            saveUnit(defaultUnit);
        }

        if (!sharedPreferences.contains("notificationsEnabled")) {
            saveNotificationsEnabled(true); // Enable notifications by default
            Log.d("SharedPreferences", "Default settings initialized! Notifications enabled");
        }

        if(!sharedPreferences.contains("notificationInterval")){
            long oneHour = 3600000; //  One hour in milliseconds
            saveNotificationInterval(oneHour);
        }

        if(!sharedPreferences.contains("dateFormat")){
            String defaultDateFormat = "d/m/y";
            saveDateFormat(defaultDateFormat);
        }

        if(!sharedPreferences.contains("clockFormat")){
            int hourSystem = 24;
            saveClockFormat(hourSystem);
        }
    }


    //  hourSystem The preferred clock format: 24 for 24-hour format, 12 for am/pm format.
    public void saveClockFormat(int hourSystem){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (hourSystem == 12 || hourSystem == 24) {
            editor.putInt("clockFormat", hourSystem);
            editor.apply();
        }
    }
    public int getClockFormat(){return sharedPreferences.getInt("clockFormat",-1);} //  -1 means error
    public void saveDateFormat(String dateFormat){
        dateFormat = dateFormat.toLowerCase();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("dateFormat",dateFormat);
        editor.apply();
    }
    public String getDateFormat(){return sharedPreferences.getString("dateFormat","d/m/y");} // d - day | m - month | y = year | / - separator
    public void saveUnit(String unit){
        unit = unit.toLowerCase();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("unitType",unit);
        editor.apply();
    }
    public String getUnit(){return sharedPreferences.getString("unitType","ml");}
    public void saveNotificationInterval(long timeInMilliseconds){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("notificationInterval", timeInMilliseconds);
        editor.apply();
    }
    public long getNotificationInterval(){return sharedPreferences.getLong("notificationInterval",0);}

    public void saveNotificationsEnabled(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notificationsEnabled", enabled);
        editor.apply();
    }

    public boolean getNotificationsEnabled() {
        return sharedPreferences.getBoolean("notificationsEnabled", true);
    }
}

