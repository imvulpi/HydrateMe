package com.example.hydrateme.ui.settings;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.hydrateme.MainActivity;
import com.example.hydrateme.R;

public class FrequentlyAskedQuestions extends Fragment {
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frequently_asked_questions, container, false);

        MainActivity activity = (MainActivity) getActivity();
        ImageView menuIcon = activity.findViewById(R.id.menu_icon);
        menuIcon.setImageResource(R.drawable.arrow);

        return view;
    }

}
