package com.chandler.red.mystock.manager;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class HttpManager {
    private RequestQueue queue;
    private static HttpManager instance;
    private Context context;

    private HttpManager(){

    }

    public void init(Context context){
        this.context = context;
    }

    public static HttpManager getInstance(){
        if(instance==null){
            synchronized (HttpManager.class){
                if(instance==null){
                    instance = new HttpManager();
                }
            }
        }
        return instance;
    }

    public void startStringRequest(StringRequest stringRequest){
        if(queue==null)
        queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);
        queue.start();
    }

    public void stopQueue(){
        if(queue!=null)
        queue.stop();
    }
}
