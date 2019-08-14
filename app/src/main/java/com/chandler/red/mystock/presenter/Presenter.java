package com.chandler.red.mystock.presenter;


import android.content.Intent;

import com.chandler.red.mystock.view.BaseView;


/**
 * @author zcq
 */

public interface Presenter {
    void onCreate();

    void onStart();

    void onStop();

    void pause();

    void attachView(BaseView view);

    void attachIncomingIntent(Intent intent);
}
