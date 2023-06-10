package com.example.hydrateme.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.hydrateme.ConfigManager;
import com.example.hydrateme.R;
import java.util.Calendar;

public class GeneralSettingsFragment extends Fragment {
    private View view;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_general, container, false);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        ConfigManager configManager = new ConfigManager(sharedPreferences);

        TextView chosenClockFormatText = view.findViewById(R.id.clockSystemChosen);
        int userClockFormat = configManager.getClockFormat(); //    12 - am/pm | 24 - 24 hour sys.
        if(userClockFormat == 12){
            chosenClockFormatText.setText("AM/PM");
        }else if(userClockFormat == 24){
            chosenClockFormatText.setText("24h");
        }

        TextView unitText = view.findViewById(R.id.unitChosen);
        unitText.setText(configManager.getUnit());

        TextView dateFormatText = view.findViewById(R.id.dateChosen);
        dateFormatText.setText(getDateFormatted(configManager.getDateFormat(),'-'));

        ConstraintLayout unit = view.findViewById(R.id.generalSetting_unit);
        ConstraintLayout dateFormat = view.findViewById(R.id.generalSetting_dateFormat);
        ConstraintLayout clockFormat = view.findViewById(R.id.generalSetting_Clock);

        unit.setOnClickListener(settingListener);
        dateFormat.setOnClickListener(settingListener);
        clockFormat.setOnClickListener(settingListener);


        return view;
    }

    private View.OnClickListener settingListener = v -> {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        ConfigManager configManager = new ConfigManager(sharedPreferences);

        int viewID = v.getId();
        int unit = R.id.generalSetting_unit;
        int dateFormat = R.id.generalSetting_dateFormat;
        int clockFormat = R.id.generalSetting_Clock;

        if(viewID == unit){
            TextView chosenUnitText = view.findViewById(R.id.unitChosen);
            String oldUnit = (String)chosenUnitText.getText();
            oldUnit = oldUnit.toLowerCase();
            if(oldUnit.equals("ml")){
                chosenUnitText.setText("fl oz");
                configManager.saveUnit("fl oz");
            }else {
                configManager.saveUnit("ml");
                chosenUnitText.setText("ml");
            }
        }else if(viewID == dateFormat){
            ShowDateFormatSelection();
        }else if(viewID == clockFormat){
            //TODO: IMPLEMENT USING THIS CLOCK SYSTEM
            TextView chosenClockFormatText = view.findViewById(R.id.clockSystemChosen);
            String oldClockSystem = (String)chosenClockFormatText.getText();
            oldClockSystem = oldClockSystem.toLowerCase();
            if(oldClockSystem == "24h"){
                chosenClockFormatText.setText("AM/PM"); //  AM/PM is 12
                configManager.saveClockFormat(12);
            }else{
                chosenClockFormatText.setText("24h"); //    24H system is 24
                configManager.saveClockFormat(24);
            }
        }
    };

    private View.OnClickListener dateFormatSelectionListener = v ->{
        int viewID = v.getId();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        ConfigManager configManager = new ConfigManager(sharedPreferences);

        TextView dateFormatText = view.findViewById(R.id.dateChosen);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String currentDate = year + "-" + month + "-" + day;

        String monthVisible = String.valueOf(month);
        String dayVisible = String.valueOf(day);

        if(month < 10){
            monthVisible = "0" + monthVisible;
        }

        if(day < 10){
            dayVisible = "0" + dayVisible;
        }

        int dmy = R.id.dmyBtn1; //  Best one :D
        int ymd = R.id.ymdBtn2;
        int mdy = R.id.mdyBtn3;

        if(viewID == dmy){
            dateFormatText.setText(getDateFormatted("d/m/y",'-'));
            configManager.saveDateFormat("d/m/y");
        }else if(viewID == ymd){
            dateFormatText.setText(getDateFormatted("y/m/d",'-'));
            configManager.saveDateFormat("y/m/d");
        }else if(viewID == mdy){
            dateFormatText.setText(getDateFormatted("m/d/y",'-'));
            configManager.saveDateFormat("m/d/y");
        }else{
            System.out.println("Format not found :(");
        }

        HideDateFormat();
    };

    public String getDateFormatted(String dateFormat, char separator){
        String dateFormatted = "";
        char defaultSeparator = '/';
        char value = separator != '\0' ? separator : defaultSeparator;

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String yearVisible = String.valueOf(year);
        String monthVisible = String.valueOf(month);
        String dayVisible = String.valueOf(day);

        if(day < 10){
            dayVisible = "0" + dayVisible;
        }
        if(month < 10){
            monthVisible = "0" + monthVisible;
        }

        switch (dateFormat){
            case "d/m/y":
                dateFormatted = dayVisible+separator+monthVisible+separator+yearVisible;
                break;
            case "y/m/d":
                dateFormatted = yearVisible+separator+monthVisible+separator+dayVisible;
                break;
            case "m/d/y":
                dateFormatted = monthVisible+separator+dayVisible+separator+yearVisible;
                break;
            default:
                dateFormatted = dayVisible+separator+monthVisible+separator+yearVisible;
                break;

        }
        return dateFormatted;
    }
    public void ShowDateFormatSelection(){
        RadioGroup selection = view.findViewById(R.id.dateFormatSelection);
        selection.setVisibility(View.VISIBLE);
        selection.setClickable(true);
        selection.bringToFront();

        selection.setOnClickListener(v ->{
            HideDateFormat();
        });

        //  RadioButtons:
        RadioButton dmy = view.findViewById(R.id.dmyBtn1);
        RadioButton ymd = view.findViewById(R.id.ymdBtn2);
        RadioButton mdy = view.findViewById(R.id.mdyBtn3);

        dmy.setOnClickListener(dateFormatSelectionListener);
        ymd.setOnClickListener(dateFormatSelectionListener);
        mdy.setOnClickListener(dateFormatSelectionListener);
    }

    public void HideDateFormat(){
        RadioGroup selection = view.findViewById(R.id.dateFormatSelection);
        selection.setVisibility(View.GONE);
    }

}
