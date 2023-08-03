package com.example.hydrateme.ui.dashboard;
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

public class DashboardFragment extends Fragment {

    private View view;
    private AppDatabase appDatabase;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        MainActivity activity = (MainActivity) getActivity();
        ImageView menuIcon = activity.findViewById(R.id.menu_icon);
        menuIcon.setImageResource(R.drawable.menu);
        ImageView arrowLeft = view.findViewById(R.id.arrowLeft_graph);
        ImageView arrowRight = view.findViewById(R.id.arrowRight_graph);


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


        setupGraph();

        return view;
    }

    private void setupGraph(){
        final int daysAccounted = 7;
        List<Float> values = retrieveValues(daysAccounted);
        LineGraphView graphView = view.findViewById(R.id.lineGraphView);

        float biggestValue = 0;
        for (int i = 0; i < values.size(); i++) {
            if(values.get(i) > biggestValue){
                biggestValue = values.get(i);
            }
        }

        int[] ids = {R.id.vGraphValue1,R.id.vGraphValue2,R.id.vGraphValue3,R.id.vGraphValue4,R.id.vGraphValue5,R.id.vGraphValue6,R.id.vGraphValue7};
        for(int i = 0; i < 7; i++){
            int textValue = (int)(i * (biggestValue / 6));
            Log.d("Value Checker","Text value "+i+" is "+textValue);
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

        graphView.setData(values);
    }
    public String getCurrentDate(){
        String dateTime;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // For API level 26 and higher, DateTimeFormatter is better
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm");
            dateTime = LocalDateTime.now().format(formatter);
        }else{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd/HH:mm");
            dateTime = dateFormat.format(new Date());
        }
        return dateTime;
    }

    public List<Float> retrieveValues(int daysAccounted){
        List<Float> values = new ArrayList<Float>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd/HH:mm");
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            for (int i = 0; daysAccounted > i; i++) {
                Calendar calendarDate = Calendar.getInstance();
                calendarDate.setTime(new Date());
                calendarDate.add(Calendar.DAY_OF_YEAR, -i);
                Log.d("Graph Init", "Getting data from date: " + dateFormat.format(calendarDate.getTime()));

                int finalI = i;
                List<DataModel> data = appDatabase.myDataDao().getByDate(dateFormat.format(calendarDate.getTime()));
                float totalValue = 0;
                for (DataModel model : data) {
                    int value = model.getValue();
                    totalValue += value;
                    Log.d("Value Checker", "Value: " + value);
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