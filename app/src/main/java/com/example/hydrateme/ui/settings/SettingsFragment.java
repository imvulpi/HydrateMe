package com.example.hydrateme.ui.settings;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.example.hydrateme.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    private View view;
    public SettingsFragment() {
        // Required empty public constructor
    }
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
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        ConstraintLayout settingGeneral = view.findViewById(R.id.settings_general);
        ConstraintLayout settingNotification = view.findViewById(R.id.settings_notifications);
        ConstraintLayout settingOther = view.findViewById(R.id.settings_other);

        settingGeneral.setOnClickListener(settingListener);
        settingNotification.setOnClickListener(settingListener);
        settingOther.setOnClickListener(settingListener);

        return view;
    }

    private View.OnClickListener settingListener = v -> {

        GeneralSettingsFragment generalSettingsFragment = new GeneralSettingsFragment();
        NotificationSettingFragment notificationSettingFragment = new NotificationSettingFragment();
        OtherSettingsFragment otherSettingsFragment = new OtherSettingsFragment();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for (Fragment fragment : getActivity().getSupportFragmentManager().getFragments()) {
           getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }

        int viewId = v.getId();
        int settingGeneral = R.id.settings_general;
        int settingNotification = R.id.settings_notifications;
        int settingOther = R.id.settings_other;

            if(viewId == settingGeneral) {
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, generalSettingsFragment);
            }else if(viewId == settingNotification) {
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, notificationSettingFragment);
            }else if(viewId == settingOther) {
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, otherSettingsFragment);
            }

            fragmentTransaction.commit();
        };
}
