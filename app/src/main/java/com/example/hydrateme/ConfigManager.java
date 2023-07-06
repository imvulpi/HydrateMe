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

        if(!sharedPreferences.contains("weight")){
            float weight = 65;
            saveWeight(weight);
        }

        if(!sharedPreferences.contains("activityLevel")){
            int activity = 2;
            saveActivity(activity);
        }

        if(!sharedPreferences.contains("temperature")){
            int temperature = 15;
            saveTemperature(temperature);
        }

        if(!sharedPreferences.contains("adjustment")){
            float adjustment = 0;
            saveAdjustment(adjustment);
        }

        if(!sharedPreferences.contains("weightUnit")){
            String weightUnit = "kg";
            saveWeightUnit(weightUnit);
        }

        if(!sharedPreferences.contains("waterIntakeGoal")){
            int waterIntakeGoal = 35*63;
            saveWaterIntakeGoal(waterIntakeGoal);
        }
    }
    public void saveWaterIntakeGoal(int waterIntakeGoal){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("waterIntakeGoal",waterIntakeGoal);
        editor.apply();
    }
    public int getWaterIntakeGoal(){return sharedPreferences.getInt("waterIntakeGoal",35*63);}
    public void saveWeightUnit(String weightUnit){
        weightUnit = weightUnit.toLowerCase();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("weightUnit", weightUnit);
        editor.apply();
    }

    public String getWeightUnit(){return sharedPreferences.getString("weightUnit","kg");}

    public void saveAdjustment(float adjustment){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("adjustment", adjustment);
        editor.apply();
    }
    public float getAdjustment(){return sharedPreferences.getFloat("adjustment",0);}
    public void saveTemperature(int temperature){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("temperature", temperature);
        editor.apply();
    }
    public int getTemperature(){return sharedPreferences.getInt("temperature",15);}

    /**
     * Saves activity level in the shared preferences file
     * @param activity the level of activity. 1 - Quiet, 2 - Normal, 3 - Active, 4 - Very Active
     */
    public void saveActivity(int activity){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(activity >= 1 && activity < 5) {
            editor.putInt("activityLevel", activity);
            editor.apply();
        }
    }
    public int getActivityLevel(){return sharedPreferences.getInt("activityLevel",2);}

    /**
    * Saves weight in the shared preferences file
    * @param weight Weight in kilogram
    */
    public void saveWeight(float weight){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("weight", weight);
        editor.apply();
    }
    public float getWeight(){return sharedPreferences.getFloat("weight",65);}

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

