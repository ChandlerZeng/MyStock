package com.wc.dragphoto.v4;

import android.annotation.TargetApi;
import android.view.MotionEvent;

/**
 * Motion event compatibility class for API 12+.
 */

@TargetApi(12)
class MotionEventCompatHoneycombMr1 {
    static float getAxisValue(MotionEvent event, int axis) {
        return event.getAxisValue(axis);
    }

    static float getAxisValue(MotionEvent event, int axis, int pointerIndex) {
        return event.getAxisValue(axis, pointerIndex);
    }
}
