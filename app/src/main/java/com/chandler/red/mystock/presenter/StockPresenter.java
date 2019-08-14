package com.chandler.red.mystock.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chandler.red.mystock.db.MyDbManager;
import com.chandler.red.mystock.db.MySqlHelper;
import com.chandler.red.mystock.entity.Stock;
import com.chandler.red.mystock.entity.StockBean;
import com.chandler.red.mystock.manager.HttpManager;
import com.chandler.red.mystock.util.Constants;
import com.chandler.red.mystock.view.BaseView;
import com.chandler.red.mystock.view.HttpResponseView;

import java.util.ArrayList;
import java.util.List;

public class StockPresenter<T> implements Presenter{
    private Context context;
    HttpManager manager;
    HttpResponseView<T> httpResponseView;
    T t;

    public StockPresenter(Context context,T t){
        this.context = context;
        this.t = t;
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
        manager.stopQueue();
    }

    @Override
    public void pause() {

    }

    @Override
    public void attachView(BaseView view) {
        httpResponseView = (HttpResponseView<T>) view;
    }

    @Override
    public void attachIncomingIntent(Intent intent) {

    }

    public void querySimpleSinaStocks(final String list){
        String url ="http://hq.sinajs.cn/list=" + list;
        //http://hq.sinajs.cn/list=sh600000,sh600536

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(t instanceof String){
                            httpResponseView.onSuccess((T)response);
                        }else if(t instanceof Stock){
                            httpResponseView.onSuccess((T)sinaSimpleResponseToStock(response));
                        }else if(t instanceof List){
                            httpResponseView.onSuccess((T)sinaSimpleResponseToStocks(response));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        httpResponseView.onError(error.getMessage());
                        Log.e(MySqlHelper.TAG,"请求数据失败:"+list);
                    }
                });
        manager.startStringRequest(stringRequest);
    }

    public void querySinaStocks(final String list){
        String url ="http://hq.sinajs.cn/list=" + list;
        //http://hq.sinajs.cn/list=sh600000,sh600536

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(t instanceof String){
                            httpResponseView.onSuccess((T)response);
                        }else if(t instanceof Stock){
                            httpResponseView.onSuccess((T)sinaResponseToStock(response));
                        }else if(t instanceof List){
                            httpResponseView.onSuccess((T)sinaResponseToStocks(response));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        httpResponseView.onError(error.getMessage());
                        Log.e(MySqlHelper.TAG,"请求数据失败:"+list);
                    }
                });
        manager.startStringRequest(stringRequest);
    }

    public void queryHeaderSinaStocks(final String list){
        String url ="http://hq.sinajs.cn/list=" + list;
        //http://hq.sinajs.cn/list=sh600000,sh600536

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        httpResponseView.onSuccess((T)sinaHeaderSimpleResponseToStocks(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        httpResponseView.onError(error.getMessage());
                        Log.e(MySqlHelper.TAG,"请求数据失败:"+list);
                    }
                });
        manager.startStringRequest(stringRequest);
    }

    public void createSinaStocksData(final String list){
        String url ="http://hq.sinajs.cn/list=" + list;
        //http://hq.sinajs.cn/list=sh600000,sh600536

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        httpResponseView.onSuccess((T)simpleResponseToStockBeans(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        httpResponseView.onError(error.getMessage());
                        Log.e(MySqlHelper.TAG,"请求数据失败:"+list);
                    }
                });
        manager.startStringRequest(stringRequest);
    }

    private Stock sinaResponseToStock(String response){
        String[] leftRight = response.split("=");
        String right = leftRight[1].replaceAll("\"", "");
        String left = leftRight[0];
        Stock stockNow = new Stock();
        String[] lefts = left.split("_");
        stockNow.id_ = lefts[2];
        String[] values = right.split(",");
        try{
            stockNow.name_ = values[0];
            stockNow.yest_value = values[2];
            stockNow.now_ = values[3];

        }catch (ArrayIndexOutOfBoundsException e){
            Log.e("MainActivity",e.toString());
        }
        return stockNow;
    }

    public List<Stock> sinaResponseToStocks(String response){
        response = response.replaceAll("\n", "");
        String[] stocks = response.split(";");

        List<Stock> stockBeanList = new ArrayList<>();
        for(String stock : stocks) {
            String[] leftRight = stock.split("=");
            if (leftRight.length < 2)
                continue;

            String right = leftRight[1].replaceAll("\"", "");
            if (right.isEmpty())
                continue;

            String left = leftRight[0];
            if (left.isEmpty())
                continue;

            Stock stockNow = new Stock();
            String[] lefts = left.split("_");
            stockNow.id_ = lefts[2];

            String[] values = right.split(",");
            try{
                stockNow.name_ = values[0];
                stockNow.yest_value = values[2];
                stockNow.now_ = values[3];

            }catch (ArrayIndexOutOfBoundsException e){
                Log.e("MainActivity",e.toString());
            }

            stockBeanList.add(stockNow);
        }

        return stockBeanList;
    }

    public List<Stock> sinaHeaderSimpleResponseToStocks(String response){
        response = response.replaceAll("\n", "");
        String[] stocks = response.split(";");

        List<Stock> stockBeanList = new ArrayList<>();
        for(String stock : stocks) {
            String[] leftRight = stock.split("=");
            if (leftRight.length < 2)
                continue;

            String right = leftRight[1].replaceAll("\"", "");
            if (right.isEmpty())
                continue;

            String left = leftRight[0];
            if (left.isEmpty())
                continue;

            Stock stockNow = new Stock();
            String[] lefts = left.split("_");
            stockNow.id_ = lefts[2]+"_"+lefts[3];

            String[] values = right.split(",");
            try{
                if(stockNow.id_.equals(Constants.DqsIndex) || stockNow.id_.equals(Constants.NsdkIndex)){
                    stockNow.name_ = values[0];
                    stockNow.now_ = values[1];
                    stockNow.increase = values[4];
                    stockNow.percent = values[2];
                }else if(stockNow.id_.equals(Constants.HkIndex)){
                    stockNow.name_ = values[1];
                    stockNow.now_ = values[6];
                    stockNow.increase = values[7];
                    stockNow.percent = values[8];
                }else {
                    stockNow.name_ = values[0];
                    stockNow.now_ = values[1];
                    stockNow.increase = values[2];
                    stockNow.percent = values[3];
                }

            }catch (ArrayIndexOutOfBoundsException e){
                Log.e("MainActivity",e.toString());
            }

            stockBeanList.add(stockNow);
        }

        return stockBeanList;
    }

    public List<Stock> sinaSimpleResponseToStocks(String response){
        response = response.replaceAll("\n", "");
        String[] stocks = response.split(";");

        List<Stock> stockBeanList = new ArrayList<>();
        for(String stock : stocks) {
            String[] leftRight = stock.split("=");
            if (leftRight.length < 2)
                continue;

            String right = leftRight[1].replaceAll("\"", "");
            if (right.isEmpty())
                continue;

            String left = leftRight[0];
            if (left.isEmpty())
                continue;

            Stock stockNow = new Stock();
            String[] lefts = left.split("_");
            stockNow.id_ = lefts[2]+"_"+lefts[3];

            String[] values = right.split(",");
            try{
                stockNow.name_ = values[0];
                stockNow.now_ = values[1];
                stockNow.increase = values[2];
                stockNow.percent = values[3];
            }catch (ArrayIndexOutOfBoundsException e){
                Log.e("MainActivity",e.toString());
            }

            stockBeanList.add(stockNow);
        }

        return stockBeanList;
    }

    public List<StockBean> simpleResponseToStockBeans(String response){
        response = response.replaceAll("\n", "");
        String[] stocks = response.split(";");

        List<StockBean> stockList = new ArrayList<>();
        for(String stock : stocks) {
            String[] leftRight = stock.split("=");
            if (leftRight.length < 2)
                continue;

            String right = leftRight[1].replaceAll("\"", "");
            if (right.isEmpty())
                continue;

            String left = leftRight[0];
            if (left.isEmpty())
                continue;
            String[] lefts = left.split("_");
            String number = lefts[2]+"_"+lefts[3];
            String name = "";
            String value = "";
            String[] values = right.split(",");
            try{
                name = values[0];
                value = values[1];

            }catch (ArrayIndexOutOfBoundsException e){
                Log.e("MainActivity",e.toString());
            }
            StockBean stockNow = new StockBean(number,name,value);
            StockBean  stockBean= MyDbManager.getInstance(context).getStockByNumberOrName(number);
            if(stockBean!=null){
                stockNow.setSelected(stockBean.isSelected());
                stockNow.setTopTime(stockBean.getTopTime());
            }
            stockList.add(stockNow);
        }

        return stockList;
    }

    private Stock sinaSimpleResponseToStock(String response){
        String[] leftRight = response.split("=");
        String right = leftRight[1].replaceAll("\"", "");
        String left = leftRight[0];
        Stock stockNow = new Stock();
        String[] lefts = left.split("_");
        stockNow.id_ = lefts[2]+"_"+lefts[3];
        String[] values = right.split(",");
        try{
            stockNow.name_ = values[0];
            stockNow.now_ = values[1];
            stockNow.increase = values[2];
            stockNow.percent = values[3];

        }catch (ArrayIndexOutOfBoundsException e){
            Log.e("MainActivity",e.toString());
        }
        return stockNow;
    }

}
