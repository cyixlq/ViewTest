package com.example.viewtest.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.view.ViewGroup;

import com.example.viewtest.Config;

public class TestViewGroup extends RelativeLayout {

    public TestViewGroup(Context context) {
        super(context);
    }

    public TestViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(Config.TAG, "TestViewGroup dispatchTouchEvent: " + super.dispatchTouchEvent(ev));
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final boolean interceptTouchEvent = false;
        Log.d(Config.TAG, "TestViewGroup onInterceptTouchEvent: " + interceptTouchEvent);
        return interceptTouchEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final boolean isDell = true;
        Log.d(Config.TAG, "TestViewGroup onTouchEvent: " + isDell);
        return isDell;
    }
}
