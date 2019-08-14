package com.chandler.red.mystock.util;

import android.app.Activity;
import android.util.DisplayMetrics;

public class ScreenUtil {
    public static int[] getMiddleLocation(Activity activity){
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        int px = screenWidth / 2;
        int py = screenHeight/2;
        int[] locs = new int[]{px,py};
        return locs;
    }

    public static int dp2Px(Activity activity,int dp) {
        try {
            DisplayMetrics metric = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
            return (int) (dp * metric.density + 0.5f);
        } catch (Exception e) {
            e.printStackTrace();
            return dp;
        }
    }
}
