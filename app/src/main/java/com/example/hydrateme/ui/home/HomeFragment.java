package com.example.hydrateme.ui.home;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.hydrateme.DeviceUtils;
import com.example.hydrateme.MainActivity;
import com.example.hydrateme.R;
import com.example.hydrateme.database.AppDatabase;
import com.example.hydrateme.database.DataModel;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private View view;
    public AppDatabase appDatabase;

    private View.OnClickListener globalOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewID;
/*            ViewGroup parentView = (ViewGroup) v.getParent();
            if(true == true){ //    Replace with a condition
                System.out.println("Removing view :)");
                parentView.removeView(v);
                //  TODO: delete from aquaDB
            }*/
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        RetrieveFromSavedData();

        Button btnAddWater = view.findViewById(R.id.btnAddWater);
        Button btnWater1 = view.findViewById(R.id.btn100ml);
        Button btnWater2 = view.findViewById(R.id.btn300ml);
        Button btnWater3 = view.findViewById(R.id.btn600ml);

        btnAddWater.setOnClickListener(addWaterButtonClicked);
        btnWater1.setOnClickListener(v -> AddWater(100));
        btnWater2.setOnClickListener(v -> AddWater(300));
        btnWater3.setOnClickListener(v -> AddWater(600));

        EditText editText = view.findViewById(R.id.editTextNumber);
        Button buttonNext = view.findViewById(R.id.btnAddCustomWater);

        buttonNext.setOnClickListener(v -> CustomWaterValue());
        editText.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                CustomWaterValue();
                return true;
            }
            return false;
        });

        //  AquaDB:

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            appDatabase = mainActivity.getAppDatabase();
            if (appDatabase == null) {
                System.out.println("APP DATABASE is NULL :(");
                appDatabase = Room.databaseBuilder(view.getContext().getApplicationContext(), AppDatabase.class, "AquaDB")
                        .build();
            }
        }else{
            System.out.println("MAIN ACTIVITY is null");
        }



        return view;
    }

    public void CustomWaterValue() {
        EditText editText = view.findViewById(R.id.editTextNumber);
        String inputString = editText.getText().toString();

        int inputValue = 0;
        if (!inputString.isEmpty()) {
            inputValue = Integer.parseInt(inputString);
        }
        final int finalInputValue = inputValue;
        System.out.println("Final input value: " + finalInputValue);
        editText.setText("");
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        AddWater(finalInputValue);
    }
    private LinearLayout newLinearLayout;
    private int childCount = 0;
    private ImageView bottle;
    private int imageWidth = 40;
    private int imageCount = 4;
    public void CreateElement(int waterAmount){

        LinearLayout parentLinearLayout = view.findViewById(R.id.LinearLadder);
        System.out.println("Water: "+waterAmount);


        if(parentLinearLayout != null) {
            bottle = new ImageView(requireContext());
            SetupImageView(bottle, waterAmount);

            LinearLayout childLinearLayout = new LinearLayout(requireContext());

            if (childCount == imageCount || newLinearLayout == null) {
                childLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                parentLinearLayout.addView(childLinearLayout);
                newLinearLayout = childLinearLayout;
                childCount = newLinearLayout.getChildCount();
                newLinearLayout.addView(bottle);
            } else {
                newLinearLayout.addView(bottle);
            }
            childCount++;
        }
    }

    public void SetupImageView(ImageView imageView, int waterAmount){
        imageView.setTag(String.valueOf(waterAmount));
        switch (waterAmount){
            case 300:
                imageView.setImageResource(R.drawable.some_bottle);
                break;
            case 600:
                imageView.setImageResource(R.drawable.better_bottle);
                break;
            default:
                imageView.setImageResource(R.drawable.water_bottle);
                break;
        }
        bottle.setScaleType(ImageView.ScaleType.CENTER_CROP);
        bottle.setClickable(true);
        // Set the globalOnClickListener on the ImageView
        bottle.setOnClickListener(globalOnClickListener);
        bottle.setScaleType(ImageView.ScaleType.CENTER);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        bottle.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int width = bottle.getMeasuredWidth();
        int height = bottle.getMeasuredHeight();

        imageWidth = width;

        int deviceWidth = DeviceUtils.getDeviceWidth(requireContext());
        int totalSpacing = deviceWidth - (imageWidth * imageCount);
        int spacing = totalSpacing / (imageCount + 1);
        layoutParams.setMargins(spacing, 32, 0, 32);
        bottle.setLayoutParams(layoutParams);
    }
    private final View.OnClickListener addWaterButtonClicked = v -> {
        ShowOptions();
    };


    public void ShowOptions() {
        LinearLayout optionsList = view.findViewById(R.id.optionsLayout);
        int linearLayoutVisibility = optionsList.getVisibility();

        if (linearLayoutVisibility == 8) {
            optionsList.setVisibility(View.VISIBLE);
        } else {
            optionsList.setVisibility(View.GONE);
        }
    }

    public String getCurrentDate(){
        String dateTime;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // For API level 26 and higher, DateTimeFormatter is better
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            dateTime = LocalDateTime.now().format(formatter);
        }else{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateTime = dateFormat.format(new Date());
        }
        return dateTime;
    }

    public void RetrieveFromSavedData(){
        System.out.println("Trying to retrieve data");
        String dateTime = getCurrentDate();

        ProgressBar waterProgressBar = view.findViewById(R.id.progressBar);
        TextView waterAmountText = view.findViewById(R.id.waterAmount);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            appDatabase = mainActivity.getAppDatabase();
            if (appDatabase == null) {
                System.out.println("APP DATABASE is NULL ");
                appDatabase = Room.databaseBuilder(view.getContext().getApplicationContext(), AppDatabase.class, "AquaDB")
                        .build();
            }
        }else{
            System.out.println("MAIN ACTIVITY is null");
        }

        final int[] totalWaterValue = {0};
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            List<DataModel> data = appDatabase.myDataDao().getByDate(dateTime);
            for (DataModel model : data) {
                String dataFormat = model.getDateTime();
                int value = model.getValue();
                totalWaterValue[0] += value;
                CreateElement(value);
                System.out.println("DataFormat: " + dataFormat + ", Value: " + value + ", TotalWaterValue: "+String.valueOf(totalWaterValue[0]));
            }

            waterProgressBar.setProgress(totalWaterValue[0]);
            String waterAmountString = String.valueOf(totalWaterValue[0]) + "/" + waterProgressBar.getMax();
            waterAmountText.setText(waterAmountString);

        });
        executorService.shutdown();
    }

    public void AddWater(int waterAmount) {

        LinearLayout optionsList = view.findViewById(R.id.optionsLayout);
        TextView waterAmountText = view.findViewById(R.id.waterAmount);
        ProgressBar waterProgressBar = view.findViewById(R.id.progressBar);
        CountDownLatch latch = new CountDownLatch(1);


        CreateElement(waterAmount);
        String dateTime = getCurrentDate();

        final int[] totalWaterValue = {0};
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            List<DataModel> data = appDatabase.myDataDao().getByDate(dateTime);
            for (DataModel model : data) {
                String dataFormat = model.getDateTime();
                int value = model.getValue();
                totalWaterValue[0] += value;
            }

            totalWaterValue[0] += waterAmount;
            waterProgressBar.setProgress(totalWaterValue[0]);
            String waterAmountString = String.valueOf(totalWaterValue[0]) + "/" + waterProgressBar.getMax();
            waterAmountText.setText(waterAmountString);
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Log.d("Interrupted Exception", "The action was stopped because the handle was interrupted");
        }

        optionsList.setVisibility(View.GONE);
        SaveWaterData(waterAmount); //  Saves data in AquaDB
        executorService.shutdown();
    }

    public void SaveWaterData(int amountOfWater){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // For API level 26 and higher, use DateTimeFormatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm");
            String dateTime = LocalDateTime.now().format(formatter);
            System.out.println("Adding data\nDATE: "+dateTime+" Value: "+amountOfWater);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                DataModel data1 = new DataModel();
                data1.setDateTime(dateTime);
                data1.setValue(amountOfWater);

                appDatabase.myDataDao().insert(data1);
            });
            executor.shutdown();
        } else {
            // For API levels below 26, use SimpleDateFormat
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd/HH:mm", Locale.getDefault());
            String dateTime = dateFormat.format(new Date());
            System.out.println("Adding data\nDATE: "+dateTime+" Value: "+amountOfWater);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                DataModel data1 = new DataModel();
                data1.setDateTime(dateTime);
                data1.setValue(amountOfWater);

                appDatabase.myDataDao().insert(data1);
            });
            executor.shutdown();
        }
    }
}

