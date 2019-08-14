package com.chandler.red.mystock.view;


/**
 * Created by win764-1 on 2016/12/12.
 */

public interface HttpResponseView<T> extends BaseView{
    void onSuccess(T responseBody);
    void onError(String result);
}
