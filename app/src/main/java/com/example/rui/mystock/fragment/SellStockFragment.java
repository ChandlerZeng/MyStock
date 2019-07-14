package com.example.rui.mystock.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rui.mystock.R;
import com.example.rui.mystock.activity.BuySearchActivity;
import com.example.rui.mystock.adapter.BuyStockListAdapter;
import com.example.rui.mystock.db.MySqlHelper;
import com.example.rui.mystock.entity.StockBuy;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellStockFragment#} factory method to
 * create an instance of this fragment.
 */
public class SellStockFragment extends Fragment {
    @BindView(R.id.et_stock)
    EditText etStock;
    @BindView(R.id.value_minus)
    TextView valueMinus;
    @BindView(R.id.et_value)
    EditText etValue;
    @BindView(R.id.value_plus)
    TextView valuePlus;
    @BindView(R.id.value_layout)
    LinearLayout valueLayout;
    @BindView(R.id.min_value)
    TextView minValue;
    @BindView(R.id.max_value)
    TextView maxValue;
    @BindView(R.id.count_minus)
    TextView countMinus;
    @BindView(R.id.et_count)
    EditText etCount;
    @BindView(R.id.count_plus)
    TextView countPlus;
    @BindView(R.id.count_layout)
    LinearLayout countLayout;
    @BindView(R.id.can_buy_count)
    TextView canBuyCount;
    @BindView(R.id.all_buy)
    TextView allBuy;
    @BindView(R.id.half_buy)
    TextView halfBuy;
    @BindView(R.id.one_third_buy)
    TextView oneThirdBuy;
    @BindView(R.id.one_fourth_buy)
    TextView oneFourthBuy;
    @BindView(R.id.buy_layout)
    LinearLayout buyLayout;
    @BindView(R.id.btn_buy)
    TextView btnBuy;
    @BindView(R.id.buy_stock_list_view)
    ListView buyStockListView;
    Unbinder unbinder;

    private String[] buyNameArr = {"卖五","卖四","卖三","卖二","卖一","买一","买二","买三","买四","买五"};

    private BuyStockListAdapter buyStockListAdapter;
    private List<StockBuy> stockBuyList;
    private RequestQueue queue;
    private String number;
    private String[] stockArray;
    private double miniValue;
    private double maxiValue;
    private int curCount;
    private int maxCount = 1000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sell_stock, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        if(queue!=null)
            queue.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Timer("RefreshStocks");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessageAtTime(1,500);
            }
        }, 0, 9500); // 10 seconds
        querySinaStocks();
    }

    public void querySinaStocks(){
        // Instantiate the RequestQueue
        if(number==null || number.equals(""))return;
        if(queue==null)
            queue = Volley.newRequestQueue(getActivity());
        String url ="http://hq.sinajs.cn/list=" + number;
        //http://hq.sinajs.cn/list=sh600000,sh600536

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        responseToStocks(response);
                        if(stockArray!=null && stockArray.length>=30)
                            refreshView();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MySqlHelper.TAG,"请求数据失败");
                    }
                });

        queue.add(stringRequest);
        queue.start();
    }

    Timer timer;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    querySinaStocks();
                    break;
                case 2:
                    etValue.setText(String.format("%.2f",curValue));
                    break;
                case 3:
                    etCount.setText(curCount+"");
                    break;
            }
        }
    };
    private void initView(){
        canBuyCount.setText("可卖"+maxCount+"股");
        stockBuyList = new ArrayList<>();
        for(int i=0;i<10;i++){
            stockBuyList.add(new StockBuy(buyNameArr[i],"--","--",0));
        }
        buyStockListAdapter = new BuyStockListAdapter(getActivity(),stockBuyList);
        buyStockListView.setAdapter(buyStockListAdapter);
    }

    private double curValue;
    private double yesValue;
    private void refreshView(){
        stockBuyList.clear();
        yesValue = Double.parseDouble(stockArray[2]);
        curValue = Double.parseDouble(stockArray[3]);
        miniValue = yesValue-yesValue/10;
        maxiValue = yesValue+yesValue/10;
        etValue.setText(String.format("%.2f",curValue));
        minValue.setText("跌停"+String.format("%.2f",miniValue));
        maxValue.setText("涨停"+String.format("%.2f",maxiValue));
        double increase = curValue-yesValue;
        String cstr = "";
        for(int i=0;i<5;i++){
            int count = Integer.parseInt(stockArray[28-i*2])/100;
            if(count>=10000){
                cstr = String.format("%.2f", count/10000.0)+"万";
            }else {
                cstr = count+"";
            }
            stockBuyList.add(new StockBuy(buyNameArr[i],String.format("%.2f",Double.parseDouble(stockArray[29-i*2])),cstr,increase));
        }
        for(int i=0;i<5;i++){
            int count = Integer.parseInt(stockArray[10+i*2])/100;
            if(count>=10000){
                cstr = String.format("%.2f", count/10000.0)+"万";
            }else {
                cstr = count+"";
            }
            stockBuyList.add(new StockBuy(buyNameArr[5+i],String.format("%.2f",Double.parseDouble(stockArray[11+i*2])),cstr,increase));
        }
        buyStockListAdapter.setData(stockBuyList);
        Log.i("BUY","refreshview number:"+number);
    }

    public void responseToStocks(String response){
        String[] leftRight = response.split("=");
        String right = leftRight[1].replaceAll("\"", "");
        stockArray = right.split(",");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.et_stock, R.id.value_minus, R.id.et_value, R.id.value_plus, R.id.min_value, R.id.max_value, R.id.count_minus, R.id.et_count, R.id.count_plus, R.id.all_buy, R.id.half_buy, R.id.one_third_buy, R.id.one_fourth_buy, R.id.btn_buy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.et_stock:
                startActivityForResult(new Intent(getActivity(),BuySearchActivity.class),100);
                break;
            case R.id.value_minus:
                if(curValue>miniValue){
                    curValue = curValue-curValue/100;
                    if(curValue<miniValue)curValue = miniValue;
                    handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessageDelayed(2,100);
                }
                break;
            case R.id.et_value:
                break;
            case R.id.value_plus:
                if(curValue<maxiValue){
                    curValue = curValue+curValue/100;
                    if(curValue>maxiValue) curValue=maxiValue;
                    handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessageDelayed(2,100);
                }
                break;
            case R.id.min_value:
                break;
            case R.id.max_value:
                break;
            case R.id.count_minus:
                if(curCount>=100){
                    curCount -= 100;
                    handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessageDelayed(3,100);
                }
                break;
            case R.id.et_count:
                break;
            case R.id.count_plus:
                if(curCount<=maxCount-100){
                    curCount += 100;
                    handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessageDelayed(3,100);
                }
                break;
            case R.id.all_buy:
                break;
            case R.id.half_buy:
                break;
            case R.id.one_third_buy:
                break;
            case R.id.one_fourth_buy:
                break;
            case R.id.btn_buy:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("BUY","requestcode:"+requestCode+" resultcode:"+resultCode);
        switch (requestCode){
            case 100:
                if(resultCode == RESULT_OK){
                    number = data.getStringExtra("number");
                    etStock.setText(data.getStringExtra("name"));
                    Log.i("BUY","number:"+number);
                    if(number!=null && !number.equals("")){
                        querySinaStocks();
                    }
                }
                break;
        }
    }

}
