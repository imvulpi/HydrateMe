package com.example.hydrateme.ui.settings;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.hydrateme.R;

public class OtherSettingsFragment extends Fragment {
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_other, container, false);

        TextView faq = view.findViewById(R.id.other_FAQ);
        TextView pPolicy = view.findViewById(R.id.other_PPOLICY);
        TextView tos = view.findViewById(R.id.other_TOS);

        faq.setOnClickListener(handleClick);
        pPolicy.setOnClickListener(handleClick);
        tos.setOnClickListener(handleClick);

        return view;
    }
    private View.OnClickListener handleClick = v -> {
        int viewId = v.getId();

        PrivacyPolicy privacyPolicy = new PrivacyPolicy();
        TermsOfService termsOfService = new TermsOfService();
        FrequentlyAskedQuestions frequentlyAskedQuestions = new FrequentlyAskedQuestions();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for (Fragment fragment : getActivity().getSupportFragmentManager().getFragments()) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }

        if (viewId == R.id.other_FAQ) {
            fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, frequentlyAskedQuestions);
        } else if (viewId == R.id.other_PPOLICY) {
            fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, privacyPolicy);
        } else if (viewId == R.id.other_TOS) {
            fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, termsOfService);
        }

        fragmentTransaction.commit();
    };
}
