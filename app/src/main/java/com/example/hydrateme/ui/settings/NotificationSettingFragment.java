package com.example.hydrateme.ui.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.example.hydrateme.MainActivity;
import com.example.hydrateme.R;
import com.example.hydrateme.ConfigManager;
import java.util.concurrent.TimeUnit;

public class NotificationSettingFragment extends Fragment {

    private View view;
    private static final String CONFIG_NAME = "GlobalConfig";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_notifications, container, false);

        MainActivity activity = (MainActivity) getActivity();
        ImageView menuIcon = activity.findViewById(R.id.menu_icon);
        menuIcon.setImageResource(R.drawable.arrow);

        AudioManager audioManager = (AudioManager) view.getContext().getSystemService(Context.AUDIO_SERVICE);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(CONFIG_NAME,Context.MODE_PRIVATE);
        ConfigManager configManager = new ConfigManager(sharedPreferences);

        TextView resolveNotificationProblems = view.findViewById(R.id.resolve_problems_notifications);
        TextView saveIntervalTime = view.findViewById(R.id.saveIntervalBtn);
        Button goToSecurityBtn = view.findViewById(R.id.goSecurityBtn);

        SeekBar notificationVolume = view.findViewById(R.id.notifications_volume);
        notificationVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
        notificationVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));

        Switch notificationEnabled = view.findViewById(R.id.notification_enabled);
        View popupListener = view.findViewById(R.id.popup_hide_listener);
        TextView intervalText = view.findViewById(R.id.intervalTime);

        long time = configManager.getNotificationInterval();
        long timeInHours = TimeUnit.MILLISECONDS.toHours(time);
        long timeInMinutes = TimeUnit.MILLISECONDS.toMinutes(time) - (timeInHours*60);

        intervalText.setText(String.valueOf(timeInHours)+"h "+String.valueOf(timeInMinutes)+"m");
        notificationEnabled.setChecked(configManager.getNotificationsEnabled());

        notificationEnabled.setOnClickListener(notificationSwitchListener);
        notificationVolume.setOnSeekBarChangeListener(notificationVolumeChange);
        resolveNotificationProblems.setOnClickListener(v -> ShowNotificationPopup());
        popupListener.setOnClickListener(v -> HideNotificationPopup());
        goToSecurityBtn.setOnClickListener(v -> openAutoStartSettings());
        intervalText.setOnClickListener(v -> ShowIntervalChanger());
        saveIntervalTime.setOnClickListener(IntervalSaveListener);

        //  Interval Picker:
        NumberPicker minutesPicker = view.findViewById(R.id.minutesPicker);
        NumberPicker hourPicker = view.findViewById(R.id.hoursPicker);

        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);

        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);

        return view;
    }

    private void ShowIntervalChanger() {
        LinearLayout intervalChanger = view.findViewById(R.id.notificationInterval);
        intervalChanger.bringToFront();
        intervalChanger.setVisibility(View.VISIBLE);
        intervalChanger.setOnClickListener(v -> HideIntervalChanger());
    }
    private void HideIntervalChanger(){
        LinearLayout intervalChanger = view.findViewById(R.id.notificationInterval);
        intervalChanger.setVisibility(View.GONE);
    }

    View.OnClickListener IntervalSaveListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            int viewId = v.getId();
            if(viewId == R.id.saveIntervalBtn){
                NumberPicker minutesPicker = view.findViewById(R.id.minutesPicker);
                NumberPicker hourPicker = view.findViewById(R.id.hoursPicker);
                TextView intervalText = view.findViewById(R.id.intervalTime);

                int minutes = minutesPicker.getValue();
                int hours = hourPicker.getValue();

                long combinedTime = TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes);
                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
                ConfigManager configManager = new ConfigManager(sharedPreferences);
                configManager.saveNotificationInterval(combinedTime);

                if(hours > 0) {
                    intervalText.setText(hours + "h " + minutes + "m");   //  Change to changing string/... to that
                }else{
                    intervalText.setText(minutes + "m");
                }

                System.out.println("Saved new notification interval time, "+configManager.getNotificationInterval());
                HideIntervalChanger();
            }
        }
    };

    View.OnClickListener notificationSwitchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Switch notificationEnabled = view.findViewById(R.id.notification_enabled);
            boolean value;

            if(notificationEnabled.isChecked()){
                value = true;
                notificationEnabled.setChecked(true);
            }else{
                value = false;
                notificationEnabled.setChecked(false);
            }
            saveNotificationEnabling(value);
        }
    };
    SeekBar.OnSeekBarChangeListener notificationVolumeChange = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            saveNotificationVolume(progress);
        }
    };

    private void saveNotificationEnabling(boolean status){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(CONFIG_NAME,Context.MODE_PRIVATE);
        ConfigManager configManager = new ConfigManager(sharedPreferences);
        configManager.saveNotificationsEnabled(status);
        System.out.println("Notification status saved! new status: " + status);
    }
    private void saveNotificationVolume(int volume){
        if(volume > 100){
            System.out.println("The volume amount is too big | bigger than 100");
            return;
        }

        AudioManager audioManager = (AudioManager) view.getContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume, 0);
        System.out.println("Notification Sound Volume saved! new volume: " + volume);
    }
    private void openAutoStartSettings() {
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        Intent intent;

        switch (manufacturer) {
            case "xiaomi":
                intent = new Intent();
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                break;
            case "oppo":
                intent = new Intent();
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                break;
            case "vivo":
                intent = new Intent();
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                break;
            case "huawei":
                intent = new Intent();
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"));
                break;
            case "samsung":
                intent = new Intent();
                intent.setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity"));
                break;
            default:
                // If the device manufacturer is not known, open the general settings
                intent = new Intent(Settings.ACTION_SETTINGS);
                break;
        }

        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void ShowNotificationPopup(){
        LinearLayout popup = view.findViewById(R.id.resolve_problem_popup);
        View popupListener = view.findViewById(R.id.popup_hide_listener);

        popup.setVisibility(View.VISIBLE);
        popupListener.setVisibility(View.VISIBLE);
        popupListener.bringToFront();
        popup.bringToFront();
    }

    public void HideNotificationPopup(){
        LinearLayout popup = view.findViewById(R.id.resolve_problem_popup);
        View popupListener = view.findViewById(R.id.popup_hide_listener);

        popup.setVisibility(View.GONE);
        popupListener.setVisibility(View.GONE);
    }
}
