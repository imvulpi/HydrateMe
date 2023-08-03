package com.example.hydrateme.ui.home;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import com.example.hydrateme.ConfigManager;
import com.example.hydrateme.DeviceUtils;
import com.example.hydrateme.MainActivity;
import com.example.hydrateme.R;
import com.example.hydrateme.SwipeGestureListener;
import com.example.hydrateme.SwipeableScrollView;
import com.example.hydrateme.database.AppDatabase;
import com.example.hydrateme.database.DataModel;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment implements View.OnTouchListener {

    private View view;
    public AppDatabase appDatabase;
    private GestureDetector gestureDetector;
    private String accessingDate;
    private final View.OnClickListener globalOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            LinearLayout deleteMenu = view.findViewById(R.id.deleteMenuLayout);
            LinearLayout duplicateButton = view.findViewById(R.id.duplicateButton);
            MainActivity activity = (MainActivity) getActivity();
            View shadowViewMain = activity.findViewById(R.id.dimmedView_Main);
            View shadowViewLocal = view.findViewById(R.id.dimmedView);
            TextView elementDate = view.findViewById(R.id.dateInDelMenu);
            ImageView closeButton = view.findViewById(R.id.closeImage);
            deleteMenu.setClickable(true);
            View.OnClickListener closeDeleteMenu = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteMenu.setVisibility(View.GONE);
                    shadowViewMain.setVisibility(View.GONE);
                    shadowViewLocal.setVisibility(View.GONE);
                    EnableMainActivityToolbar();
                }
            };

            deleteMenu.bringToFront();
            deleteMenu.setVisibility(View.VISIBLE);
            shadowViewMain.setVisibility(View.VISIBLE);
            shadowViewLocal.setVisibility(View.VISIBLE);
            DisableMainActivityToolbar();
            elementDate.setText(accessingDate);

            shadowViewLocal.setOnClickListener(closeDeleteMenu);
            shadowViewMain.setOnClickListener(closeDeleteMenu);
            closeButton.setOnClickListener(closeDeleteMenu);

            LinearLayout deleteBtn  = view.findViewById(R.id.deleteButton);
            deleteBtn.setOnClickListener(v1 -> {
                deleteWaterData(v);
                deleteMenu.setVisibility(View.GONE);
                shadowViewMain.setVisibility(View.GONE);
                shadowViewLocal.setVisibility(View.GONE);
                EnableMainActivityToolbar();
            });

            duplicateButton.setOnClickListener(v1 ->{
                int value = -1;
                Object tag = v.getTag();
                try{
                    value = Integer.parseInt(tag.toString());
                }catch (NumberFormatException e){
                    System.out.println("Error in parsing: "+e.getMessage());
                }

                if(value != -1) {
                    AddWater(value);
                }
                deleteMenu.setVisibility(View.GONE);
                shadowViewMain.setVisibility(View.GONE);
                shadowViewLocal.setVisibility(View.GONE);
                EnableMainActivityToolbar();
            });
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ViewGroup rootView = view.findViewById(R.id.homeRoot);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        ConfigManager configManager = new ConfigManager(sharedPreferences);

        SwipeableScrollView scrollView = view.findViewById(R.id.iconView);
        changeProgress(configManager.getWaterIntakeGoal(),0);

        view.setOnTouchListener(this);
        gestureDetector = new GestureDetector(requireContext(), new SwipeGestureListener(requireContext(), view, (MainActivity) getActivity(), this));
        scrollView.setGestureDetector(gestureDetector);

        MainActivity activity = (MainActivity) getActivity();
        ImageView menuIcon;
        if (activity != null) {
            menuIcon = activity.findViewById(R.id.menu_icon);
            ImageView configIcon = activity.findViewById(R.id.config_icon);

            menuIcon.setImageResource(R.drawable.menu);
            configIcon.setVisibility(View.VISIBLE);

            configIcon.setOnClickListener(v -> {
                configIcon.setClickable(false);
                ConfigIconClickEvent();
            });
        }

        scrollView.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });
        rootView.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });

        accessingDate = getCurrentDate();
        System.out.println("Accessing date is "+accessingDate);
        SaveCorrectDate("");
        System.out.println("Accessing date is "+accessingDate);

        RetrieveFromSavedData(accessingDate);

        Button btnAddWater = view.findViewById(R.id.btnAddWater);
        ImageView cup150 = view.findViewById(R.id.cup150ml);
        ImageView cup250 = view.findViewById(R.id.cup250ml);
        ImageView cup400 = view.findViewById(R.id.cup400ml);
        ImageView cup500 = view.findViewById(R.id.cup500ml);
        ImageView cup600 = view.findViewById(R.id.cup600ml);

        btnAddWater.setOnClickListener(addWaterButtonClicked);
        cup150.setOnClickListener(v -> AddWater(150));
        cup250.setOnClickListener(v -> AddWater(250));
        cup400.setOnClickListener(v -> AddWater(400));
        cup500.setOnClickListener(v -> AddWater(500));
        cup600.setOnClickListener(v -> AddWater(600));

        EditText editText = view.findViewById(R.id.editTextNumber);
        Button buttonNext = view.findViewById(R.id.btnAddCustomWater);
        LinearLayout deleteMenu = view.findViewById(R.id.deleteMenuLayout);
        deleteMenu.bringToFront();

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
                System.out.println("APP DATABASE is null");
                appDatabase = Room.databaseBuilder(view.getContext().getApplicationContext(), AppDatabase.class, "AquaDB")
                        .build();
            }
        }else{
            System.out.println("MAIN ACTIVITY is null");
        }

        return view;
    }

    private void DisableMainActivityToolbar(){
        MainActivity activity = (MainActivity) getActivity();
        if(activity != null){
            ImageView menuIcon = activity.findViewById(R.id.menu_icon);
            ImageView configIcon = activity.findViewById(R.id.config_icon);

            menuIcon.setClickable(false);
            configIcon.setClickable(false);
        }
    }

    private void EnableMainActivityToolbar(){
        MainActivity activity = (MainActivity) getActivity();
        if(activity != null){
            ImageView menuIcon = activity.findViewById(R.id.menu_icon);
            ImageView configIcon = activity.findViewById(R.id.config_icon);

            menuIcon.setClickable(true);
            configIcon.setClickable(true);
        }
    }

    public void ConfigIconClickEvent(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("GlobalConfig",Context.MODE_PRIVATE);
        ConfigManager configManager = new ConfigManager(sharedPreferences);
        ConstraintLayout configMenu = view.findViewById(R.id.config_setup_card);
        Spinner activitySpinner = view.findViewById(R.id.activitySpinner);
        Spinner temperatureSpinner = view.findViewById(R.id.temperatureSpinner);
        EditText weightEditText = view.findViewById(R.id.editWeight);
        EditText adjustmentEditText = view.findViewById(R.id.Adjustment_config_info);
        TextView weightTextUnit = view.findViewById(R.id.weightUnit);
        TextView cupTextUnit = view.findViewById(R.id.unitOfCup);
        TextView saveText = view.findViewById(R.id.save_text);
        TextView cancelText = view.findViewById(R.id.cancel_text);
        View shadowView = view.findViewById(R.id.shadowedView_configSetup);

        float weight = configManager.getWeight();
        float adjustment = configManager.getAdjustment();
        final float[] changedWeight = {weight};
        final float[] changedAdjustment = {adjustment};
        final int[] activity = new int[1];
        activity[0] = configManager.getActivityLevel();
        final int[] temperature = new int[1];
        temperature[0] = configManager.getTemperature();
        final int[] qualitativeTemperature = {2};
        if(temperature[0]<10){
            qualitativeTemperature[0] = 1;
        }else if(temperature[0] >= 20 && temperature[0] <= 30){
            qualitativeTemperature[0] = 3;
        }else if(temperature[0] > 30){
            qualitativeTemperature[0] = 4;
        }

        String unit = configManager.getUnit();
        Log.d("Value","unit is: "+unit);
        String weightUnit = configManager.getWeightUnit();

        weightTextUnit.setText(weightUnit.toUpperCase());
        cupTextUnit.setText(unit.toUpperCase());

        Log.d("Value","unit is: "+unit);
        double adjustmentValue = 5;
        if(unit.equals("fl oz")){
            Log.d("GoesHere?","Yes");
            adjustmentValue = 0.17;
        }

        final String[][] temperatures = {{"Cold " + weight * (-adjustmentValue) + unit, "Mild", "Warm +" + weight * adjustmentValue + unit, "Hot +" + weight * (adjustmentValue * 2) + unit}};
        String[] activities = {"Quiet " + weight * (-adjustmentValue) + unit, "Normal", "Active +" + weight * adjustmentValue + unit, "Very Active +" + weight * (adjustmentValue * 2) + unit};

        ArrayAdapter<String> activitiesAdapter = new ArrayAdapter<>(requireContext(),android.R.layout.simple_spinner_dropdown_item, activities);
        activitySpinner.setAdapter(activitiesAdapter);
        activitySpinner.setSelection(activity[0]-1);

        ArrayAdapter<String> temperaturesAdapter = new ArrayAdapter<>(requireContext(),android.R.layout.simple_spinner_dropdown_item, temperatures[0]);
        temperatureSpinner.setAdapter(temperaturesAdapter);
        temperatureSpinner.setSelection(qualitativeTemperature[0]-1);


        configMenu.setVisibility(View.VISIBLE);
        configMenu.bringToFront();
        weightEditText.setText(String.valueOf(configManager.getWeight()));
        if(configManager.getAdjustment() > 0) {
            adjustmentEditText.setText(String.valueOf(configManager.getAdjustment()));
        }

        double finalAdjustmentValue = adjustmentValue;
        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String toString = s.toString();
                float floatValue = weight;
                try {
                    floatValue = Float.parseFloat(toString);
                } catch (NumberFormatException e) {
                    Log.d("Error","Couldn't parse s into float");
                }

                if(floatValue < 999) {
                    Log.d("Text Watcher", "Changed to " + s + "Parsed: " + floatValue);
                    String[] temperatures = {"Cold " + floatValue * (-finalAdjustmentValue) + unit, "Mild", "Warm +" + floatValue * finalAdjustmentValue + unit, "Hot +" + floatValue * (finalAdjustmentValue * 2) + unit};
                    String[] activities = {"Quiet " + floatValue * (-finalAdjustmentValue) + unit, "Normal", "Active +" + floatValue * finalAdjustmentValue + unit, "Very Active +" + floatValue * (finalAdjustmentValue * 2) + unit};

                    ArrayAdapter<String> activitiesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, activities);
                    activitySpinner.setAdapter(activitiesAdapter);
                    activitySpinner.setSelection(activity[0] - 1);

                    ArrayAdapter<String> temperaturesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, temperatures);
                    temperatureSpinner.setAdapter(temperaturesAdapter);
                    temperatureSpinner.setSelection(qualitativeTemperature[0] - 1);
                    changedWeight[0] = floatValue;

                    TypedArray typedArray = requireActivity().obtainStyledAttributes(new int[]{R.attr.primaryTextColor});
                    int mainAccentColor = ContextCompat.getColor(requireContext(), R.color.main_accent_light);
                    int customColor = typedArray.getColor(0, 0);
                    typedArray.recycle();

                    weightEditText.setTextColor(customColor);
                    weightEditText.setBackgroundTintList(ColorStateList.valueOf(customColor));
                    saveText.setClickable(true);
                    saveText.setTextColor(mainAccentColor);
                }else{
                    int color = ContextCompat.getColor(requireContext(), R.color.incorrect_value);
                    weightEditText.setTextColor(color);
                    weightEditText.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.incorrect_value)));
                    saveText.setClickable(false);
                    saveText.setTextColor(color);
                }
            }
        });

        adjustmentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String toString = s.toString();
                float floatValue = adjustment;
                try {
                    floatValue = Float.parseFloat(toString);
                } catch (NumberFormatException e) {
                    Log.d("Error","Couldn't parse s into float");
                }
                changedAdjustment[0] = floatValue;
            }
        });

        activitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                System.out.println("Selected: " + selectedItem);
                selectedItem = selectedItem.toLowerCase();
                if(selectedItem.contains("quiet")){
                    activity[0] = 1;
                }else if(selectedItem.contains("normal")){
                    activity[0] = 2;
                }else if(selectedItem.contains("active") && !selectedItem.contains("very")){
                    activity[0] = 3;
                }else if(selectedItem.contains("very active")){
                    activity[0] = 4;
                }
                System.out.println("Which is equal to activity number: " + activity[0]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        temperatureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                System.out.println("Selected: " + selectedItem);
                selectedItem = selectedItem.toLowerCase();
                if (selectedItem.contains("cold")) {
                    temperature[0] = 5;
                    qualitativeTemperature[0] = 1;
                }else if(selectedItem.contains("mild")){
                    temperature[0] = 15;
                    qualitativeTemperature[0] = 2;
                }else if(selectedItem.contains("warm")){
                    temperature[0] = 25;
                    qualitativeTemperature[0] = 3;
                }else if(selectedItem.contains("hot")){
                    temperature[0] = 35;
                    qualitativeTemperature[0] = 4;
                }
                System.out.println("Which is equal to average temperature of: " + temperature[0]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        saveText.setOnClickListener(v -> {
            CloseConfigPopup();
            SaveConfiguration(changedWeight[0], changedAdjustment[0],activity[0],temperature[0]);
        });
        cancelText.setOnClickListener(v -> CloseConfigPopup());
        shadowView.setOnClickListener(v ->{
            CloseConfigPopup();
        });
    }


    private void SaveConfiguration(float changedWeight, float changedAdjustment, int activity, int temperature){
        ConfigManager configManager = new ConfigManager(requireContext().getSharedPreferences("GlobalConfig",Context.MODE_PRIVATE));
        if(changedWeight > 0.0) {
            configManager.saveWeight(changedWeight);
        }

        configManager.saveAdjustment(changedAdjustment);

        if(activity > 0.0) {
            configManager.saveActivity(activity);
        }

        Log.d("SharedPreferences", "Saved: -Weight: "+changedWeight+" -Adjustment: "+changedAdjustment+" -Activity: "+activity+" -Temperature: "+temperature);

        configManager.saveTemperature(temperature);
        float waterIntake = CalculateWaterIntakeNeed(changedWeight,temperature,activity) + changedAdjustment;
        Log.d("Calculation","New water intake goal: "+ waterIntake);
        int waterIntakeRounded = Math.round(waterIntake);
        configManager.saveWaterIntakeGoal(waterIntakeRounded);
        changeProgress(waterIntakeRounded, -1);
    }

    public void  CloseConfigPopup(){
        MainActivity activity = (MainActivity) getActivity();
        ConstraintLayout configMenu = view.findViewById(R.id.config_setup_card);
        ImageView configIcon;
        if (activity != null) {
            configIcon = activity.findViewById(R.id.config_icon);
            configIcon.setClickable(true);
            configMenu.setVisibility(View.GONE);
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public void SetTextInMainActivity(String text) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.setTextViewText(text);
        }
    }
    public void deleteWaterData(View v){
        ViewGroup parentView = (ViewGroup) v.getParent();
        Object tagObject = v.getTag();
        String tag = String.valueOf(tagObject);
        int intValue = Integer.parseInt(tag); // 300

        System.out.println("Removing view with tag: " + tag);
        parentView.removeView(v);
        CountDownLatch countDownLatch = new CountDownLatch(1);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() ->{
            appDatabase.myDataDao().deleteByValueAndDate(intValue, accessingDate);
            countDownLatch.countDown();
        });

        try{
            countDownLatch.await();
        }catch (InterruptedException e){
            System.out.println("INTERRUPTED EXCEPTION: "+ e);
        }

        ClearIcons(view);
        RetrieveFromSavedData(accessingDate);
    }

    public void SaveCorrectDate(String correctDate){
        if(correctDate.equals("")){
            accessingDate = getCurrentDate();
        }else{
            accessingDate = correctDate;
        }
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
    private LinearLayout iconsLinearLayout;
    private LinearLayout textLinearLayout;
    private int childCount = 0;
    private ImageView bottle;
    private final int imageCount = 4;
    public void CreateElement(int waterAmount){

        LinearLayout parentLinearLayout = view.findViewById(R.id.LinearLadder);
        System.out.println("Water: "+waterAmount);

        if(parentLinearLayout != null) {
            bottle = new ImageView(requireContext());
            TextView amountOfWaterText = new TextView(requireContext());
            SetupImageView(bottle, waterAmount);
            SetupTextView(amountOfWaterText, waterAmount);

            LinearLayout childLinearLayout = new LinearLayout(requireContext());
            LinearLayout newTextLinearLayout = new LinearLayout(requireContext());

            if (childCount == imageCount || iconsLinearLayout == null) {
                textLinearLayout = newTextLinearLayout;
                childLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                parentLinearLayout.addView(childLinearLayout);
                parentLinearLayout.addView(newTextLinearLayout);
                iconsLinearLayout = childLinearLayout;
                childCount = iconsLinearLayout.getChildCount();
                iconsLinearLayout.addView(bottle);
                textLinearLayout.addView(amountOfWaterText);
                //amountOfWaterText.setText("Hel");
            } else {
                iconsLinearLayout.addView(bottle);
                textLinearLayout.addView(amountOfWaterText);
                //amountOfWaterText.setText("Hel");
            }
            childCount++;
            Log.d("Creation of Elements", "Created element | additional info: "+childCount);
        }
    }

    private void SetupTextView(TextView textView, int waterAmount){
        int deviceWidth = DeviceUtils.getDeviceWidth(requireContext());
        String unit = getFluidUnit();

        LinearLayout.LayoutParams textViewLayoutParameters = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        TypedArray typedArray = requireActivity().obtainStyledAttributes(new int[]{R.attr.primaryTextColor});
        int customColor = typedArray.getColor(0, 0);
        typedArray.recycle();

        if(unit.equals("ml")) {
            textView.setText(waterAmount + getFluidUnit());
        }else{
            textView.setText(getFluidOunces(waterAmount) + "oz");
        }

        int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        textView.measure(widthSpec, heightSpec);
        int neededSpacing = (deviceWidth - (textView.getMeasuredWidth()*imageCount))/(imageCount+1);

        textViewLayoutParameters.setMargins(neededSpacing,0,0,0);
        textViewLayoutParameters.gravity = Gravity.CENTER;
        textView.setLayoutParams(textViewLayoutParameters);
        textView.setTextColor(customColor);
    }
    private void SetupImageView(ImageView imageView, int waterAmount){
        int deviceWidth = DeviceUtils.getDeviceWidth(requireContext());
        int adjustmentToMargin = 0;
        int desiredWidth = 128;
        int desiredHeight = 256;
        Bitmap originalBitmap;

        imageView.setTag(String.valueOf(waterAmount));
        switch (waterAmount){
            case 150:
                //imageView.setImageResource(R.drawable.teacup_nohandle);
                originalBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.teacup_nohandle);
                desiredWidth = 110;
                desiredHeight = 128;
                break;
            case 250:
                //imageView.setImageResource(R.drawable.hot_beverage);
                originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hot_beverage);
                desiredWidth = 110;
                desiredHeight = 128;
                break;
            case 400:
                //imageView.setImageResource(R.drawable.bubble_tea_cup);
                originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bubble_tea_cup);
                break;
            case 500:
                //imageView.setImageResource(R.drawable.water_bottle500ml);
                originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.water_bottle500ml);
                break;
            case 600:
                //imageView.setImageResource(R.drawable.coffe_cup);
                originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.coffe_cup);
                break;
            default:
                //imageView.setImageResource(R.drawable.bottle_water_universal);
                originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bottle_water_universal);
                break;
        }

        if(originalBitmap != null) {
            Bitmap scaledBitmap = scaleBitmap(originalBitmap, desiredWidth, desiredHeight);
            bottle.setImageBitmap(scaledBitmap);
        }else{
            bottle.setImageResource(R.drawable.bottle_water_universal);
        }

        //bottle.setScaleType(ImageView.ScaleType.FIT_XY);
        bottle.setClickable(true);
        // Set the globalOnClickListener on the ImageView
        bottle.setOnClickListener(globalOnClickListener);
        bottle.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.BOTTOM;

        bottle.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int imageWidth = bottle.getMeasuredWidth();
        int totalSpacing = deviceWidth - (imageWidth * imageCount);
        int spacing = totalSpacing / (imageCount + 1) + adjustmentToMargin;

        layoutParams.setMargins(spacing, 32, 0, 32);
        bottle.setLayoutParams(layoutParams);
    }

    public Bitmap scaleBitmap(Bitmap originalBitmap, int desiredWidth, int desiredHeight) {
        // Calculate the scale factors to fit the image within the desired dimensions
        float widthScale = (float) desiredWidth / originalBitmap.getWidth();
        float heightScale = (float) desiredHeight / originalBitmap.getHeight();

        // Create a matrix to apply the scaling
        Matrix matrix = new Matrix();
        matrix.postScale(widthScale, heightScale);

        // Create the scaled bitmap
        Bitmap scaledBitmap = Bitmap.createBitmap(
                originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true
        );

        return scaledBitmap;

    }


        private final View.OnClickListener addWaterButtonClicked = v -> ShowOptions();

    /**
     * Calculates the area of a rectangle.
     *
     * @param weight the weight of person in kilograms
     * @param temperature the temperature in the area in celsius.
     * @param activity the level of activity. 1 - Quiet, 2 - Normal, 3 - Active, 4 - Very Active
     * @return returns the water intake needed in a day
     */
    public float CalculateWaterIntakeNeed(float weight, int temperature, int activity){
        int milliliterPerKilogram = 33; //  Base value
        if(temperature < 10){ // Cold
            milliliterPerKilogram -= 5;
        }else if(temperature >= 20 && temperature <= 30){
            milliliterPerKilogram += 5;
        }else if(temperature > 30) {
            milliliterPerKilogram += 10;
        }

        if(activity == 1){
            milliliterPerKilogram -= 5;
        }else if(activity == 3){
            milliliterPerKilogram += 5;
        }else if(activity == 4){
            milliliterPerKilogram += 10;
        }

        return weight * milliliterPerKilogram;
    }

    public void ShowOptions() {
        LinearLayout optionsList = view.findViewById(R.id.optionsLayout);
        int linearLayoutVisibility = optionsList.getVisibility();

        if (linearLayoutVisibility == 8) {
            Animation fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in);
            optionsList.startAnimation(fadeInAnimation);
            optionsList.setVisibility(View.VISIBLE);
        } else {
            Animation fadeOutAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out);
            optionsList.startAnimation(fadeOutAnimation);
            optionsList.setVisibility(View.GONE);
        }
    }

    public void ClearIcons(View viewX){
        Log.d("Cleanup", "Beginning to clear icons from Linear Ladder");
        LinearLayout linearLayout = viewX.findViewById(R.id.LinearLadder);
        ProgressBar waterProgressBar = view.findViewById(R.id.progressBar);
        linearLayout.removeAllViews();
        changeProgress(-1,0);
        childCount = 0;
        iconsLinearLayout = null;
        Log.d("Cleanup", "Icons from Linear Ladder cleaned");
    }

    /**
     *
     * @param newMax New progress maximum value in milliliters value of -1 if there is no new max
     * @param newProgress New progress in milliliters, value of -1 if there is no new progress (since there isn't negative progress)
     */
    private void changeProgress(int newMax, int newProgress){
        TextView waterAmountText = view.findViewById(R.id.waterAmount);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        Boolean isNewProgress = true;
        Boolean isNewMax = true;

        if(newProgress == -1){ // value if there is no new progress (it extracts the progress from the text)
            String[] stringParts = waterAmountText.getText().toString().split("/");
            newProgress = Integer.parseInt(stringParts[0]);
            isNewProgress = false;
        }

        if(newMax == -1){
            newMax = progressBar.getMax();
            isNewMax = false;
        }

        if(getFluidUnit().equals("ml")) {
            progressBar.setMax(newMax);
            progressBar.setProgress(newProgress);
            waterAmountText.setText(newProgress + "/" + newMax);
            System.out.println("(ml) New max: "+newMax+"New progress"+newProgress);
        }else{
            if(isNewProgress){
                newProgress = (int) getFluidOunces(newProgress);
            }
            if(isNewMax){
                newMax = (int) getFluidOunces(newMax);
            }

            progressBar.setMax(newMax);
            progressBar.setProgress(newProgress);
            waterAmountText.setText(newProgress + "/" + newMax);
            System.out.println("(oz) New max: "+newMax+"New progress"+newProgress);
        }
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
    private String getFluidUnit(){
        ConfigManager configManager = new ConfigManager(requireContext().getSharedPreferences("GlobalConfig",Context.MODE_PRIVATE));
        return configManager.getUnit();
    }

    private float getFluidOunces(int milliliters){
        float conversionRate = 0.0338f;
        float roundedNumber = Math.round((milliliters * conversionRate) * 10.0f) / 10.0f;
        return roundedNumber;
    }

    public void RetrieveFromSavedData(String dateTime){
        ProgressBar waterProgressBar = view.findViewById(R.id.progressBar);
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
        Handler mainHandler = new Handler(Looper.getMainLooper());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        AppDatabase finalAppDatabase = appDatabase;
        executorService.execute(() -> {
            List<DataModel> data = finalAppDatabase.myDataDao().getByDate(dateTime);
            for (DataModel model : data) {
                String dataFormat = model.getDateTime();
                int value = model.getValue();
                totalWaterValue[0] += value;
                mainHandler.post(() -> {
                    CreateElement(value);
                    System.out.println("total water value "+totalWaterValue[0]+"Max = "+waterProgressBar.getMax());
                    changeProgress(-1,totalWaterValue[0]);
                });
                System.out.println("DataFormat: " + dataFormat + ", Value: " + value + ", TotalWaterValue: "+ totalWaterValue[0]);
            }
        });
        executorService.shutdown();
    }

    public void AddWater(int waterAmount) {

        LinearLayout optionsList = view.findViewById(R.id.optionsLayout);
        TextView waterAmountText = view.findViewById(R.id.waterAmount);
        ProgressBar waterProgressBar = view.findViewById(R.id.progressBar);
        CountDownLatch latch = new CountDownLatch(1);

        CreateElement(waterAmount);
        String dateTime = accessingDate;

        final int[] totalWaterValue = {0};
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            List<DataModel> data = appDatabase.myDataDao().getByDate(dateTime);
            for (DataModel model : data) {
                int value = model.getValue();
                totalWaterValue[0] += value;
            }

            totalWaterValue[0] += waterAmount;
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Log.d("Interrupted Exception", "The action was stopped because the handle was interrupted");
        }
        changeProgress(-1,totalWaterValue[0]);
        optionsList.setVisibility(View.GONE);
        SaveWaterData(waterAmount); //  Saves data in AquaDB
        executorService.shutdown();
    }

    public void SaveWaterData(int amountOfWater){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // For API level 26 and higher, use DateTimeFormatter
            DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm");

            System.out.println("Adding data | DATE: "+accessingDate+" Value: "+amountOfWater);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                DataModel data1 = new DataModel();
                data1.setDateTime(accessingDate);
                data1.setValue(amountOfWater);

                appDatabase.myDataDao().insert(data1);
            });
            executor.shutdown();
        } else {
            // For API levels below 26, use SimpleDateFormat
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd/HH:mm");
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

