package com.chandler.red.mystock;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chandler.red.mystock.activity.ExchangeActivity;
import com.chandler.red.mystock.activity.ImageShowActivity;
import com.chandler.red.mystock.activity.LoginActivity;
import com.chandler.red.mystock.db.MyDbManager;
import com.chandler.red.mystock.entity.Stock;
import com.chandler.red.mystock.entity.StockBean;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class StockImgActivity extends BaseActivity implements View.OnClickListener{

    private ImageView image;
    private TextView textTitle;
    private TextView textExchange;
    private TextView textSelect;
    private TextView type;
    private String idurl;
    private String url;
    private int fromType;
    private RequestQueue queue;
    private String number;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_img);
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        queue.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    Timer timer;
    private String id;
    boolean isSelected = false;
    private void initView(){
        textTitle = findViewById(R.id.text_title);
        textExchange = findViewById(R.id.tv_exchange);
        textSelect = findViewById(R.id.tv_select);
        type = findViewById(R.id.stock_type);
        type.setText("分时图");
        image = findViewById(R.id.image_view);
        id = getIntent().getStringExtra("id");
        idurl = StockImgApi.INDEX_F+id;
        url = getIntent().getStringExtra("url");
        fromType = getIntent().getIntExtra("type",0);
        if(fromType==1){
            textSelect.setVisibility(View.GONE);
            textExchange.setVisibility(View.GONE);
        }else {
            textSelect.setVisibility(View.VISIBLE);
            textExchange.setVisibility(View.VISIBLE);
        }
        timer = new Timer("RefreshStocks");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshView();
            }
        }, 0, 60000); // 60 seconds
        isSelected = MyDbManager.getInstance(this).isSelected(id);
        if(isSelected){
            textSelect.setText("取消自选");
        }else {
            textSelect.setText("+ 自选");
        }
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageShowActivity.startImageActivity(StockImgActivity.this,image,url);
            }
        });
        textSelect.setOnClickListener(this);
        textExchange.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stock_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_search){
            startActivity(new Intent(StockImgActivity.this,SerachActivity.class));
            return true;
        }
        String[] urls = url.split("\\/");
        String fronturl ="" ;
        for(int i=2;i<urls.length-2;i++){
            fronturl = fronturl+"/"+urls[i];
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_mink) {
            fronturl = fronturl+"/min/";
            type.setText("分时K图");
            type.setTextColor(getResources().getColor(R.color.main_red_color));
        }
        else if(id == R.id.action_dailyk){
            fronturl = fronturl+"/daily/";
            type.setText("日K图");
            type.setTextColor(getResources().getColor(R.color.main_text_color));
        }else if(id == R.id.action_weeklyk){
            fronturl = fronturl+"/weekly/";
            type.setText("周K图");
            type.setTextColor(getResources().getColor(R.color.main_green_color));


        }else if(id == R.id.action_monthlyk){
            fronturl = fronturl+"/monthly/";
            type.setText("月K图");
            type.setTextColor(getResources().getColor(R.color.main_blue_color));

        }
        url = "http:/"+fronturl+urls[urls.length-1];
        refreshView();
        return super.onOptionsItemSelected(item);
    }

    private void refreshView(){
        Calendar calendar = Calendar.getInstance();
        final String now = calendar.get(Calendar.YEAR) + "年"
                + (calendar.get(Calendar.MONTH)+1) + "月"//从0计算
                + calendar.get(Calendar.DAY_OF_MONTH) + "日 "
                + "星期"+weekDayFormate(calendar.get(Calendar.DAY_OF_WEEK))
                + "  "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textTitle.setText(now);
                image.setScaleType(ImageView.ScaleType.FIT_XY);
                RequestOptions options = new RequestOptions()
                        .dontAnimate()
                        .placeholder(R.mipmap.pictures_no)
                        .error(R.mipmap.pictures_no)
                        .fitCenter()
                        .priority(Priority.HIGH)
                        .diskCacheStrategy(DiskCacheStrategy.NONE);
                Glide.with(StockImgActivity.this).load(url)
                        .apply(options)
                        .into(image);
            }
        });
        if(queue==null)
        queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, idurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateStockView(sinaResponseToStocks(response));
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        queue.add(stringRequest);
        queue.start();
    }
    private TextView tvId,tvName,tvNow,tvIncrese,tvPercent;
    private void updateStockView(Stock stock){
        StockBean stockNow = new StockBean(stock.id_,stock.name_,stock.now_);
        StockBean  stockBean= MyDbManager.getInstance(this).getStockByNumberOrName(stock.id_);
        if(stockBean!=null){
            stockNow.setSelected(stockBean.isSelected());
            stockNow.setTopTime(stockBean.getTopTime());
        }
        MyDbManager.getInstance(this).replace(stockNow);
        number = stock.id_.substring(2);
        name = stock.name_;
        tvId =  findViewById(R.id.stock_id);
        tvName = findViewById(R.id.stock_name);
        tvNow = findViewById(R.id.stock_now);
        tvIncrese = findViewById(R.id.stock_increase);
        tvPercent = findViewById(R.id.stock_percent);
        tvId.setText(stock.id_);
        tvName.setText(stock.name_);
        tvNow.setText(stock.now_);
        double i = Double.parseDouble(stock.increase);
        double p = Double.parseDouble(stock.percent);
        tvIncrese.setText(String.format("%.2f",i));
        tvPercent.setText(String.format("%.2f", p) + "% " );
        int color = getResources().getColor(R.color.main_text_color);
        if (Double.parseDouble(stock.increase) > 0) {
            color = getResources().getColor(R.color.main_red_color);
        } else if (Double.parseDouble(stock.increase) < 0) {
            color = getResources().getColor(R.color.main_green_color);
        }
        tvNow.setTextColor(color);
        tvIncrese.setTextColor(color);
        tvPercent.setTextColor(color);
    }

    private final static String ShIndex = "s_sh000001";
    private final static String SzIndex = "s_sz399001";
    private final static String ChuangIndex = "s_sz399006";
    private final static String DqsIndex = "gb_$dji";
    private final static String NsdkIndex = "gb_ixic";
    private final static String HkIndex = "rt_hkHSI";
    private final static String DqsoyIndex = "b_SX5E";
    private final static String Fs100Index = "b_UKX";
    private final static String Rj225Index = "b_NKY";

    private Stock sinaResponseToStocks(String response){
        String[] leftRight = response.split("=");
        String right = leftRight[1].replaceAll("\"", "");
        String left = leftRight[0];
        Stock stockNow = new Stock();
        String[] lefts = left.split("_");
        stockNow.id_ = lefts[2]+"_"+lefts[3];
        String[] values = right.split(",");
        try{
            if(stockNow.id_.equals(ShIndex) || stockNow.id_.equals(SzIndex) || stockNow.id_.equals(ChuangIndex)){
                stockNow.name_ = values[0];
                stockNow.now_ = values[1];
                stockNow.increase = values[2];
                stockNow.percent = values[3];
            }else if(stockNow.id_.equals(DqsIndex) || stockNow.id_.equals(NsdkIndex)){
                stockNow.name_ = values[0];
                stockNow.now_ = values[1];
                stockNow.increase = values[4];
                stockNow.percent = values[2];
            }else if(stockNow.id_.equals(HkIndex)){
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
        return stockNow;
    }

    private String weekDayFormate(int num){
        String week = "";
        if(num==1){
            week = "日";
        }else if(num==2){
            week = "一";
        }else if(num==3){
            week = "二";
        }else if(num==4){
            week = "三";
        }else if(num==5){
            week = "四";
        }else if(num==6){
            week = "五";
        }else if(num==7){
            week = "六";
        }
        return week;
    }

    private boolean isLogin;
    @Override
    public void onClick(View v) {
        SharedPreferences sharedPreferences = getSharedPreferences("MY_STOCK_PREF", Activity.MODE_PRIVATE);
        isLogin = sharedPreferences.getBoolean("is_login", false);
        if(!isLogin){
            startActivityForResult(new Intent(StockImgActivity.this,LoginActivity.class),1000);
            return;
        }
        switch (v.getId()){
            case R.id.tv_select:
                if(isSelected){
                    MyDbManager.getInstance(StockImgActivity.this).clearSelected(id);
                    isSelected = false;
                    textSelect.setText("+ 自选");
                }else {
                    MyDbManager.getInstance(StockImgActivity.this).addSelected(id);
                    isSelected = true;
                    textSelect.setText("取消自选");
                }
                break;
            case R.id.tv_exchange:
                Intent intent = new Intent(StockImgActivity.this, ExchangeActivity.class);
                intent.putExtra("page",0);
                intent.putExtra("number",number);
                intent.putExtra("name",name);
                startActivity(intent);
                break;
        }
    }
}
