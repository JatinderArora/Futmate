package com.footmate.utils;

/**
 * Created by patas tech on 2/24/2016.
 */
import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.footmate.Map_Activity;

public class TouchableWrapper extends FrameLayout {

    public TouchableWrapper(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Map_Activity.mMapIsTouched = true;
                break;
            case MotionEvent.ACTION_UP:
                Map_Activity.mMapIsTouched = false;
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}