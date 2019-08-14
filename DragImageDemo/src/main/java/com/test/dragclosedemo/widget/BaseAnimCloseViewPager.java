package com.test.dragclosedemo.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;


/**
 * Created by Laughing on 2017/7/10.
 */
public class BaseAnimCloseViewPager extends ViewPager {

    private static final String TAG = "BaseAnimCloseViewPager";

    public interface IAnimClose {
        boolean canDrag();
        void onPictureClick();

        void onPictureRelease(View view);
    }

    protected float screenHeight ;
    protected View currentShowView;
    protected VelocityTracker mVelocityTracker;
    protected IAnimClose iAnimClose;
    protected int currentPageStatus;

    public void setiAnimClose(IAnimClose iAnimClose) {
        this.iAnimClose = iAnimClose;
    }

    public BaseAnimCloseViewPager(Context context) {
        super(context);
        init(context);
    }

    public BaseAnimCloseViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        setBackgroundColor(Color.BLACK);
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e(TAG,"onPageScrollStateChanged state:"+state);
                currentPageStatus = state;
            }
        });
    }

    protected void addIntoVelocity(MotionEvent event) {
        if (mVelocityTracker == null)
            mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(event);
    }


    protected float computeYVelocity() {
        float result = 0;
        if (mVelocityTracker != null) {
            mVelocityTracker.computeCurrentVelocity(1000);
            result = mVelocityTracker.getYVelocity();
            releaseVelocity();
        }
        return result;
    }

    protected void releaseVelocity() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    protected void setupBackground(float percent) {
        setBackgroundColor(convertPercentToBlackAlphaColor(percent));
    }


    protected int convertPercentToBlackAlphaColor(float percent) {
        percent = Math.min(1, Math.max(0, percent));
        int intAlpha = (int) (percent * 255);
        String stringAlpha = Integer.toHexString(intAlpha).toLowerCase();
        String color = "#" + (stringAlpha.length() < 2 ? "0" : "") + stringAlpha + "000000";
        return Color.parseColor(color);
    }

    public void setCurrentShowView(View currentShowView) {
        this.currentShowView = currentShowView;
    }
}
