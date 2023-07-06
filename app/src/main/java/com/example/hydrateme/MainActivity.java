package com.example.hydrateme;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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

    private final String sharedPreferencesName = "GlobalConfig";
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

        com.example.hydrateme.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView menuIcon = findViewById(R.id.menu_icon);
        View hidingListener = findViewById(R.id.hideListener);
        RelativeLayout dashboardBtn = findViewById(R.id.dashboardButton);
        RelativeLayout homeBtn = findViewById(R.id.homeButton);
        RelativeLayout notificationBtn = findViewById(R.id.notificationButton);
        RelativeLayout settingsBtn = findViewById(R.id.settingsButton);

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

    @Override
    protected void onStop(){
        super.onStop();
        System.out.println("Stopped!");
        SharedPreferences sharedPreferences = getSharedPreferences("TemporaryVariables",Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isSettingsFragment(currentFragment)) {
            if(isOtherSettingsFragment(currentFragment)) {
                OtherSettingsFragment otherSettingsFragment = new OtherSettingsFragment();
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, otherSettingsFragment);
            }else{
                SettingsFragment settingsFragment = new SettingsFragment();
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, settingsFragment);
            }
            fragmentTransaction.commit();
        } else {
            if(currentFragment instanceof HomeFragment){
                super.onBackPressed();
            }
            HomeFragment homeFragment = new HomeFragment();
            fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, homeFragment);
            fragmentTransaction.commit();
        }
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
            ImageView configIcon = findViewById(R.id.config_icon);
            configIcon.setVisibility(View.GONE);

            if(viewId == R.id.dashboardButton) {
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, dashboardFragment);
            }else if(viewId == R.id.homeButton){
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, homeFragment);
            }else if(viewId == R.id.notificationButton){
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, notificationSettingFragment);
            }else if(viewId == R.id.settingsButton){
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
        Animation slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in);

        if(currentFragment instanceof SettingsFragment || currentFragment instanceof HomeFragment || currentFragment instanceof DashboardFragment || currentFragment instanceof NavHostFragment){
            ConstraintLayout side_navigation = findViewById(R.id.sideNavBar);
            View hidingListener = findViewById(R.id.hideListener);
            ImageView menuIco = findViewById(R.id.menu_icon);
            ImageView configIcon = findViewById(R.id.config_icon);

            side_navigation.setVisibility(View.VISIBLE);
            side_navigation.startAnimation(slideInAnimation);
            side_navigation.setClickable(true);
            menuIco.setClickable(false);
            configIcon.setClickable(false);
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
            if(isOtherSettingsFragment(currentFragment)) {
                OtherSettingsFragment otherSettingsFragment = new OtherSettingsFragment();
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, otherSettingsFragment);
            }else {
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, settingsFragment);
            }
        }

        fragmentTransaction.commit();
    }

    private boolean isSettingsFragment(Fragment fragment) {
        return fragment instanceof PrivacyPolicy || fragment instanceof TermsOfService || fragment instanceof FrequentlyAskedQuestions || fragment instanceof GeneralSettingsFragment || fragment instanceof OtherSettingsFragment || fragment instanceof NotificationSettingFragment;
    }

    private boolean isOtherSettingsFragment(Fragment fragment){
        return fragment instanceof PrivacyPolicy || fragment instanceof FrequentlyAskedQuestions || fragment instanceof TermsOfService;
    }
    public void hideSideNavigation(){
        Animation slideOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out);
        ConstraintLayout side_navigation = findViewById(R.id.sideNavBar);
        View hidingListener = findViewById(R.id.hideListener);
        ImageView menuIcon = findViewById(R.id.menu_icon);
        ImageView configIcon = findViewById(R.id.config_icon);
        menuIcon.setClickable(true);
        configIcon.setClickable(true);

        side_navigation.startAnimation(slideOutAnimation);
        side_navigation.setVisibility(View.GONE);
        hidingListener.setVisibility(View.GONE);
    }

    public void setTextViewText(String text) {
        TextView textView = findViewById(R.id.toolbarText);
        textView.setText(text);
    }
}