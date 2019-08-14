package com.chandler.red.mystock.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chandler.red.mystock.entity.baidupic.CrawerSimpleParams;
import com.chandler.red.mystock.entity.baidupic.SimpleCrawerResult;
import com.chandler.red.mystock.manager.HttpManager;
import com.chandler.red.mystock.view.BaseView;
import com.chandler.red.mystock.view.HttpResponseView;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class CrawlerPresenter implements Presenter{
    private static final String URL = "https://image.baidu.com/search/acjson";
    private static final String URL0 = "https://image.baidu.com/search/acjson?tn=resultjson_com&ipn=rj&ct=201326592&fp=result&queryWord=%E6%B8%85%E7%BA%AF%E7%BE%8E%E5%A5%B3&cl=2&lm=-1&ie=utf-8&oe=utf-8&word=%E6%B8%85%E7%BA%AF%E7%BE%8E%E5%A5%B3&pn=4&rn=2&gsm=5a";
    private Context context;
    HttpManager manager;
    HttpResponseView<SimpleCrawerResult> httpResponseView;

    public CrawlerPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        manager = HttpManager.getInstance();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void attachView(BaseView view) {
        httpResponseView = (HttpResponseView<SimpleCrawerResult>) view;
    }

    @Override
    public void attachIncomingIntent(Intent intent) {

    }

    public void getBaiduImages(CrawerSimpleParams queryParams){
        String url = addGetParams(queryParams);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                SimpleCrawerResult crawerResult = gson.fromJson(response,SimpleCrawerResult.class);
                Log.e("Gson","first url:"+crawerResult.getData().get(0).getMiddleURL());
                httpResponseView.onSuccess(crawerResult);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                httpResponseView.onError(error.getMessage());
            }
        });
        Log.i("Crawler","originUrl:"+stringRequest.getOriginUrl());
        Log.i("Crawler","Url:"+stringRequest.getUrl());
        manager.startStringRequest(stringRequest);
    }

    private String addGetParams(CrawerSimpleParams queryParams){
        Map<String, String> params = new HashMap<>();
        params.put("tn", "resultjson_com");
        params.put("ipn", "rj");
        params.put("ct", "201326592");
        params.put("fp", "result");
        params.put("queryWord", queryParams.getQueryWord());
        params.put("cl", "2");
        params.put("lm", "-1");
        params.put("ie", "utf-8");
        params.put("oe", "utf-8");
        params.put("word", queryParams.getWord());
        params.put("pn", queryParams.getPn());
        params.put("rn", queryParams.getRn());
        params.put("gsm", "5a");
        StringBuilder sb = new StringBuilder();
        sb.append(URL).append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            // 如果请求参数中有中文，需要进行URLEncoder编码 gbk/utf8
            try {
                sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
