package com.example.hydrateme;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.example.hydrateme.ui.home.HomeFragment;

public class SwipeableScrollView extends ScrollView {
    private GestureDetector gestureDetector;
    private MainActivity mainActivity;

    public SwipeableScrollView(Context context) {
        super(context);
        init(context);
    }

    public SwipeableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SwipeableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setGestureDetector(GestureDetector gestureDetector) {
        this.gestureDetector = gestureDetector;
    }

    private void init(Context context) {
        if (context instanceof MainActivity) {
            mainActivity = (MainActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            Context baseContext = ((ContextThemeWrapper) context).getBaseContext();
            if (baseContext instanceof MainActivity) {
                mainActivity = (MainActivity) baseContext;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (gestureDetector != null) {
            gestureDetector.onTouchEvent(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (gestureDetector != null) {
            gestureDetector.onTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }
}

