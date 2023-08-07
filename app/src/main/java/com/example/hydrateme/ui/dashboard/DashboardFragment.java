package com.example.hydrateme.ui.dashboard;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.hydrateme.MainActivity;
import com.example.hydrateme.R;
import com.example.hydrateme.database.AppDatabase;
import com.example.hydrateme.database.DataModel;
import com.example.hydrateme.LineGraphView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.UInt;
import kotlin.UnsignedKt;

public class DashboardFragment extends Fragment {

    private View view;
    private AppDatabase appDatabase;
    private int directionGraphs;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("TemporaryVariables", Context.MODE_PRIVATE);
        directionGraphs = sharedPreferences.getInt("directionGraph",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        MainActivity activity = (MainActivity) getActivity();
        ImageView menuIcon = activity.findViewById(R.id.menu_icon);
        menuIcon.setImageResource(R.drawable.menu);
        ImageView arrowLeft = view.findViewById(R.id.arrowLeft_graph);
        ImageView arrowRight = view.findViewById(R.id.arrowRight_graph);
        TextView days7 = view.findViewById(R.id.daysCount7);
        TextView days30 = view.findViewById(R.id.daysCount30);
        TextView days90 = view.findViewById(R.id.daysCount90);

        MainActivity mainActivity = (MainActivity) getActivity();
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

        arrowRight.setOnClickListener(v -> {
            directionGraphs++;
            editor.putInt("directionGraph",directionGraphs);
            editor.apply();
            setupGraph(directionGraphs);
        });

        arrowLeft.setOnClickListener(v -> {
            directionGraphs--;
            editor.putInt("directionGraph",directionGraphs);
            editor.apply();
            setupGraph(directionGraphs);
        });

        days7.setOnClickListener(v -> {saveDaysAccounted(7);});

        days30.setOnClickListener(v -> {saveDaysAccounted(30);});

        days90.setOnClickListener(v -> {saveDaysAccounted(90);});

        setupGraph(directionGraphs);

        return view;
    }

    private void saveDaysAccounted(@NonNull int number){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("TemporaryVariables", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("daysAccounted", number);
        editor.apply();
        setupGraph(directionGraphs);
    }
    private void setupGraph(int directionSize){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("TemporaryVariables", Context.MODE_PRIVATE);
        final int daysAccounted = sharedPreferences.getInt("daysAccounted",7);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        if(directionSize <= 0) {
            calendar.add(Calendar.DAY_OF_YEAR, directionSize * daysAccounted);
        }else{
            directionGraphs = 0;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("directionGraph", 0);
            editor.apply();
        }

        List<Float> values = retrieveValues(daysAccounted, calendar);
        LineGraphView graphView = view.findViewById(R.id.lineGraphView);
        float biggestValue = getBiggestValue(values);
        setGraphValues(biggestValue);
        graphView.setData(values);
    }
    public float getBiggestValue(@NonNull List<Float> floats){
        float biggestValue = 0;
        for (int i = 0; i < floats.size(); i++) {
            if(floats.get(i) > biggestValue){
                biggestValue = floats.get(i);
            }
        }
        return biggestValue;
    }
    public void setGraphValues(float biggestValue){
        int[] ids = {R.id.vGraphValue1,R.id.vGraphValue2,R.id.vGraphValue3,R.id.vGraphValue4,R.id.vGraphValue5,R.id.vGraphValue6,R.id.vGraphValue7};
        for(int i = 0; i < 7; i++){
            int textValue = (int)(i * (biggestValue / 6));
            //Log.d("Value Checker","Text value "+i+" is "+textValue);
            try {
                TextView tvEdit = view.findViewById(ids[i]);
                if(i == 0){
                    tvEdit.setText("0");
                }else {
                    tvEdit.setText(String.valueOf(textValue));
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

    }

    public List<Float> retrieveValues(int daysAccounted, Calendar date){
        List<Float> values = new ArrayList<Float>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd/HH:mm");
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            for (int i = 0; daysAccounted > i; i++) {
                date.add(Calendar.DAY_OF_YEAR, -i);
                //Log.d("Graph Init", "Getting data from date: " + dateFormat.format(date.getTime()));

                List<DataModel> data = appDatabase.myDataDao().getByDate(dateFormat.format(date.getTime()));
                float totalValue = 0;
                for (DataModel model : data) {
                    int value = model.getValue();
                    totalValue += value;
                    //Log.d("Value Checker", "Value: " + value);
                }
                values.add(totalValue);
            }
            latch.countDown();
        });

        try{
            latch.await();
        }catch (InterruptedException e){
            Log.d("Error","Interrupted "+e);
        }

        return values;
    }
}