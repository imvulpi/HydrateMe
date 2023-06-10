package com.example.hydrateme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;
import androidx.room.Room;

import com.example.hydrateme.database.AppDatabase;
import com.example.hydrateme.databinding.ActivityMainBinding;
import com.example.hydrateme.ui.dashboard.DashboardFragment;
import com.example.hydrateme.ui.home.HomeFragment;
import com.example.hydrateme.ui.settings.FrequentlyAskedQuestions;
import com.example.hydrateme.ui.settings.GeneralSettingsFragment;
import com.example.hydrateme.ui.settings.NotificationSettingFragment;
import com.example.hydrateme.ui.settings.OtherSettingsFragment;
import com.example.hydrateme.ui.settings.PrivacyPolicy;
import com.example.hydrateme.ui.settings.SettingsFragment;
import com.example.hydrateme.ui.settings.TermsOfService;


public class MainActivity extends AppCompatActivity {

    private DeviceUtils deviceUtils = new DeviceUtils();
    private ActivityMainBinding binding;
    private String sharedPreferencesName = "GlobalConfig";

    private AppDatabase appDatabase;
    public AppDatabase getAppDatabase() {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "AquaDB")
                    .build();
        }
        return appDatabase;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        ConfigManager configManager = new ConfigManager(sharedPreferences);
        configManager.initializeDefaultConfig();    // Initializes default config if is not changed by user

        if(configManager.getNotificationsEnabled()) { //    If Notifications are enabled
            NotificationHelper notificationHelper = new NotificationHelper();
            long time = configManager.getNotificationInterval();
            notificationHelper.scheduleNotification(this, time, "Notification", "This notification");
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView menuIcon = findViewById(R.id.menu_icon);
        View hidingListener = findViewById(R.id.hideListener);
        Button dashboardBtn = findViewById(R.id.dashBtn);
        Button homeBtn = findViewById(R.id.homeBtn);
        Button notificationBtn = findViewById(R.id.notificationBtn);
        Button settingsBtn = findViewById(R.id.settingsBtn);

        menuIcon.setOnClickListener(v ->{
            showSideNavigation();
        });

        hidingListener.setOnClickListener(v ->{
            hideSideNavigation();
        });

        dashboardBtn.setOnClickListener(navBarButtonClickListener);
        homeBtn.setOnClickListener(navBarButtonClickListener);
        notificationBtn.setOnClickListener(navBarButtonClickListener);
        settingsBtn.setOnClickListener(navBarButtonClickListener);

        //  database:
            appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "AquaDB")
                .build();
    }

    private View.OnClickListener navBarButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //  All fragments here
            Fragment dashboardFragment = new DashboardFragment();
            Fragment homeFragment = new HomeFragment();
            NotificationSettingFragment notificationSettingFragment = new NotificationSettingFragment();
            SettingsFragment settingsFragment = new SettingsFragment();

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }

            int viewId = v.getId();


            if(viewId == R.id.dashBtn) {
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, dashboardFragment);
            }else if(viewId == R.id.homeBtn){
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, homeFragment);
            }else if(viewId == R.id.notificationBtn){
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, notificationSettingFragment);
            }else if(viewId == R.id.settingsBtn){
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, settingsFragment);
            }else{
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, homeFragment); //   Default HOME
            }

            fragmentTransaction.commit();
            hideSideNavigation();
        }
    };
    public void showSideNavigation(){

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main);


        if(currentFragment instanceof SettingsFragment || currentFragment instanceof HomeFragment || currentFragment instanceof DashboardFragment || currentFragment instanceof NavHostFragment){
            LinearLayout side_navigation = findViewById(R.id.sideNavBar);
            View hidingListener = findViewById(R.id.hideListener);

            side_navigation.setVisibility(View.VISIBLE);
            hidingListener.setVisibility(View.VISIBLE);
        }else{
            comeBackToParentFragment(currentFragment);
        }
    }

    public void comeBackToParentFragment(Fragment currentFragment){
        SettingsFragment settingsFragment = new SettingsFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }

        if(isSettingsFragment(currentFragment)){
            fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, settingsFragment);
        }

        fragmentTransaction.commit();
    }

    private boolean isSettingsFragment(Fragment fragment) {
        return fragment instanceof PrivacyPolicy || fragment instanceof TermsOfService || fragment instanceof FrequentlyAskedQuestions || fragment instanceof GeneralSettingsFragment || fragment instanceof OtherSettingsFragment || fragment instanceof NotificationSettingFragment;
    }
    public void hideSideNavigation(){
        LinearLayout side_navigation = findViewById(R.id.sideNavBar);
        View hidingListener = findViewById(R.id.hideListener);

        side_navigation.setVisibility(View.GONE);
        hidingListener.setVisibility(View.GONE);
    }
}