package com.example.hydrateme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.example.hydrateme.database.AppDatabase;
import com.example.hydrateme.ui.home.HomeFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private View view;
    private SharedPreferences sharedPreferences;
    public AppDatabase appDatabase;
    private HomeFragment homeFragment;
    private int direction;
    public SwipeGestureListener(Context context, View view, MainActivity mainActivity, HomeFragment homeFragment) {
        sharedPreferences = context.getSharedPreferences("TemporaryVariables", Context.MODE_PRIVATE);
        this.view = view;
        this.homeFragment = homeFragment;

        if (mainActivity != null) {
            appDatabase = mainActivity.getAppDatabase();
            if (appDatabase == null) {
                System.out.println("APP DATABASE is null");
                appDatabase = Room.databaseBuilder(view.getContext().getApplicationContext(), AppDatabase.class, "AquaDB")
                        .build();
            }
        }else{
            System.out.println("MAIN ACTIVITY is null");
        }
        direction = sharedPreferences.getInt("direction", 0);
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();

        if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > SWIPE_THRESHOLD
                && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if (diffX > 0) {
                onSwipeRight();
            } else {
                onSwipeLeft();
            }
            return true;
        }

        return false;
    }
    private void onSwipeLeft() {
        System.out.println("Swiped left");

        if(direction < 0) {
            System.out.println("Direction before: "+direction);
            direction = direction + 1;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("direction", direction);
            editor.apply();
            System.out.println("Direction after: " + direction);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, direction);
            Date resultDate = calendar.getTime();
            Log.d("Calculations","Result date: "+resultDate);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd/HH:mm");
            String formattedDate = dateFormat.format(resultDate);
            System.out.println("Formatted date: " + formattedDate);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    homeFragment.SetTextInMainActivity(formattedDate);
                    homeFragment.ClearIcons(view);
                    homeFragment.SaveCorrectDate(formattedDate);

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    homeFragment.RetrieveFromSavedData(formattedDate);
                }
            });
        }
    }

    private void onSwipeRight() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        System.out.println("Direction before: "+direction);
        direction = direction - 1;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("direction", direction);
        editor.apply();
        System.out.println("Direction after: "+direction);

        calendar.add(Calendar.DAY_OF_YEAR, direction);
        Date resultDate = calendar.getTime();
        Log.d("Calculations","Result date: "+resultDate);

        System.out.println("Swiped right");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd/HH:mm");
        String formattedDate = dateFormat.format(resultDate);

        System.out.println("Formatted date: "+ formattedDate);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                homeFragment.SetTextInMainActivity(formattedDate);
                homeFragment.ClearIcons(view);
                homeFragment.SaveCorrectDate(formattedDate);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                homeFragment.RetrieveFromSavedData(formattedDate);
            }
        });
    }
}
