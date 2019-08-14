package com.chandler.red.mystock.fragment;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chandler.red.mystock.R;
import com.chandler.red.mystock.activity.BuySearchActivity;
import com.chandler.red.mystock.adapter.BuyStockListAdapter;
import com.chandler.red.mystock.adapter.HoldsAdapter;
import com.chandler.red.mystock.db.StockBuisnessManager;
import com.chandler.red.mystock.entity.AccStock;
import com.chandler.red.mystock.entity.ExeStock;
import com.chandler.red.mystock.entity.HoldsBean;
import com.chandler.red.mystock.entity.Stock;
import com.chandler.red.mystock.entity.StockBuy;
import com.chandler.red.mystock.presenter.StockPresenter;
import com.chandler.red.mystock.util.Constants;
import com.chandler.red.mystock.util.DateUtil;
import com.chandler.red.mystock.util.EncryptUtil;
import com.chandler.red.mystock.util.LogUtil;
import com.chandler.red.mystock.util.NumberUtil;
import com.chandler.red.mystock.util.TextUtils;
import com.chandler.red.mystock.view.HttpResponseView;

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
 * Use the {@link BuyStockFragment#} factory method to
 * create an instance of this fragment.
 */
public class BuyStockFragment extends LazyLoadFragment {

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
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
    @BindView(R.id.today_value)
    TextView todayValue;
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
    @BindView(R.id.buy_list)
    ListView buyList;

    private boolean isInit = false;

    private String[] buyNameArr = {"卖五", "卖四", "卖三", "卖二", "卖一", "买一", "买二", "买三", "买四", "买五"};

    private BuyStockListAdapter buyStockListAdapter;
    private List<StockBuy> stockBuyList;
    private RequestQueue queue;
    private String number;
    private String name;
    private String[] stockArray;
    private double miniValue;
    private double maxiValue;
    private int curCount;
    private int maxCount;
    private double freeMoney;

    private String phone;
    private int accId;
    private AccStock accStock;
    private List<HoldsBean> holdsBeanList;
    private HoldsAdapter holdsAdapter;

    private StockPresenter<ArrayList<Stock>> stockPresenter;
    private HttpResponseView<ArrayList<Stock>> httpResponseView;

    private void initView() {
        showProgressDialog();
        number = getActivity().getIntent().getStringExtra("number");
        name = getActivity().getIntent().getStringExtra("name");
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MY_STOCK_PREF", Activity.MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", null);
        accStock = StockBuisnessManager.getInstance(getActivity()).getStockAccountByPhone(EncryptUtil.md5WithSalt(phone));
        accId = accStock.getAccId();
        freeMoney = accStock.getCurValue();
        stockBuyList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            stockBuyList.add(new StockBuy(buyNameArr[i], "--", "--", 0));
        }
        buyStockListAdapter = new BuyStockListAdapter(getActivity(), stockBuyList);
        buyStockListView.setAdapter(buyStockListAdapter);
        holdsBeanList = new ArrayList<>();
        holdsAdapter = new HoldsAdapter(getActivity(),holdsBeanList);
        buyList.setAdapter(holdsAdapter);
        initHoldsStocks();
        timer = new Timer("RefreshStocks");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessageAtTime(1, 500);
            }
        }, 0, 9500); // 10 seconds
        refreshHoldsStocks();
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.main_bg_light_light));
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main_blue_color_light));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                querySinaStocks();
                refreshNetHoldsStocks();
            }
        });
        queryByNumber();
    }

    private void initHoldsStocks(){
        stockPresenter = new StockPresenter<>(getActivity(),new ArrayList<Stock>());
        httpResponseView = new HttpResponseView<ArrayList<Stock>>() {
            @Override
            public void onSuccess(ArrayList<Stock> responseBody) {
                for(int i=0;i<responseBody.size();i++){
                    String id = responseBody.get(i).getId_();
                    String number = holdsBeanList.get(i).getNumber();
                    LogUtil.i("id:"+id+" number:"+number);
                    if(id.contains(number)){
                        holdsBeanList.get(i).setName(responseBody.get(i).getName_());
                        double curValue = Double.parseDouble(responseBody.get(i).getNow_());
                        double cost = holdsBeanList.get(i).getCost();
                        holdsBeanList.get(i).setCurValue(curValue);
                        holdsBeanList.get(i).setTotalValue(curValue * holdsBeanList.get(i).getCount());
                        double profitRate = (curValue-cost)/cost;
                        double profit = (curValue-cost) * holdsBeanList.get(i).getCount();
                        holdsBeanList.get(i).setProfitRate(profitRate*100);
                        holdsBeanList.get(i).setProfit(profit);
                        LogUtil.i(holdsBeanList.get(i).toString());
                    }
                    holdsAdapter.setData(holdsBeanList);
                    dimissProgressDialog();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onError(String result) {
                dimissProgressDialog();
                swipeRefreshLayout.setRefreshing(false);
                LogUtil.e(result);
            }
        };
        stockPresenter.onCreate();
        stockPresenter.attachView(httpResponseView);
        buyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                number = holdsBeanList.get(position).getNumber();
                name = holdsBeanList.get(position).getName();
                refreshViewAfteriItemClicked();
                queryByNumber();
            }
        });
    }

    private void refreshViewAfteriItemClicked(){
        stockBuyList.clear();
        for (int i = 0; i < 10; i++) {
            stockBuyList.add(new StockBuy(buyNameArr[i], "--", "--", 0));
        }
        buyStockListAdapter.setData(stockBuyList);
        todayValue.setText("当日--");
        minValue.setText("跌停--");
        maxValue.setText("涨停--");
        etValue.setText("");
        etCount.setText("");
        setTextColor(todayValue,0);
        maxCount = 0;
        canBuyCount.setText("可买--股");
    }

    public void querySinaStocks() {
        // Instantiate the RequestQueue
        if (number == null || number.equals("")) return;
        if (queue == null)
            queue = Volley.newRequestQueue(getActivity());
        String url = "http://hq.sinajs.cn/list=" + number;
        //http://hq.sinajs.cn/list=sh600000,sh600536

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        responseToStocks(response);
                        if (stockArray != null && stockArray.length >= 30)
                            refreshView();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogUtil.e("请求数据失败");
                    }
                });

        queue.add(stringRequest);
        queue.start();
    }

    private StringBuilder numberList;
    private void refreshHoldsStocks(){
        holdsBeanList = StockBuisnessManager.getInstance(getActivity()).getExeHoldStocks();
        if(holdsBeanList!=null && holdsBeanList.size()>0){
            holdsAdapter.setData(holdsBeanList);
            numberList = new StringBuilder();
            for(HoldsBean holdsBean:holdsBeanList){
                numberList.append("s_");
                numberList.append(holdsBean.getNumber());
                numberList.append(",");
            }
            if(numberList.length()>0){
                numberList.deleteCharAt(numberList.length()-1);
            }
            refreshNetHoldsStocks();
        }else {
            dimissProgressDialog();
        }
    }

    private void refreshNetHoldsStocks(){
        if(numberList!=null){
            stockPresenter.querySimpleSinaStocks(numberList.toString());
        }else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    Timer timer;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    querySinaStocks();
                    refreshNetHoldsStocks();
                    break;
                case 2:
                    etValue.setText(String.format("%.2f", curValue));
                    break;
                case 3:
                    etCount.setText(curCount + "");
                    break;
            }
        }
    };

    private double curValue;
    private double yesValue;

    private void refreshView() {
        stockBuyList.clear();
        curValue = Double.parseDouble(stockArray[3]);
        if (isInit) {
            isInit = false;
            yesValue = Double.parseDouble(stockArray[2]);
            miniValue = yesValue - yesValue / 10;
            maxiValue = yesValue + yesValue / 10;
            etValue.setText(String.format("%.2f", curValue));
            minValue.setText("跌停" + String.format("%.2f", miniValue));
            maxValue.setText("涨停" + String.format("%.2f", maxiValue));
            double dayProfit = (curValue-yesValue)/yesValue * 100;
            todayValue.setText("当日"+String.format("%.2f",dayProfit)+"%");
            setTextColor(todayValue,dayProfit);
        }
        int dcount = (int) (freeMoney / curValue / 100);
        maxCount = dcount * 100;
        canBuyCount.setText("可买" + maxCount + "股");

        double increase = curValue - yesValue;
        String cstr = "";
        for (int i = 0; i < 5; i++) {
            int count = Integer.parseInt(stockArray[28 - i * 2]) / 100;
            if (count >= 10000) {
                cstr = String.format("%.2f", count / 10000.0) + "万";
            } else {
                cstr = count + "";
            }
            stockBuyList.add(new StockBuy(buyNameArr[i], String.format("%.2f", Double.parseDouble(stockArray[29 - i * 2])), cstr, increase));
        }
        for (int i = 0; i < 5; i++) {
            int count = Integer.parseInt(stockArray[10 + i * 2]) / 100;
            if (count >= 10000) {
                cstr = String.format("%.2f", count / 10000.0) + "万";
            } else {
                cstr = count + "";
            }
            stockBuyList.add(new StockBuy(buyNameArr[5 + i], String.format("%.2f", Double.parseDouble(stockArray[11 + i * 2])), cstr, increase));
        }
        buyStockListAdapter.setData(stockBuyList);
        LogUtil.i("refreshview number:" + number);
    }

    private void setTextColor(TextView tv,double profit){
        int color;
        if(profit>0){
            color = getResources().getColor(R.color.main_red_color);
        }else if(profit==0){
            color = getResources().getColor(R.color.main_text_color);
        }else {
            color = getResources().getColor(R.color.main_green_color);
        }
        tv.setTextColor(color);
    }

    public void responseToStocks(String response) {
        String[] leftRight = response.split("=");
        String right = leftRight[1].replaceAll("\"", "");
        stockArray = right.split(",");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(timer!=null)
            timer.cancel();
        if(queue!=null)
            queue.stop();
        if(unbinder!=null)
            unbinder.unbind();
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_buy_stock;
    }

    @Override
    protected void lazyLoad() {
        unbinder = ButterKnife.bind(this, getContentView());
        initView();
    }

    @OnClick({R.id.et_stock, R.id.value_minus, R.id.et_value, R.id.value_plus, R.id.min_value, R.id.max_value, R.id.count_minus, R.id.et_count, R.id.count_plus, R.id.all_buy, R.id.half_buy, R.id.one_third_buy, R.id.one_fourth_buy, R.id.btn_buy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.et_stock:
                startActivityForResult(new Intent(getActivity(), BuySearchActivity.class), 100);
                break;
            case R.id.value_minus:
                if (curValue > miniValue) {
                    curValue = curValue - curValue / 100;
                    if (curValue < miniValue) curValue = miniValue;
                    handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessageDelayed(2, 100);
                }
                break;
            case R.id.et_value:
                break;
            case R.id.value_plus:
                if (curValue < maxiValue) {
                    curValue = curValue + curValue / 100;
                    if (curValue > maxiValue) curValue = maxiValue;
                    handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessageDelayed(2, 100);
                }
                break;
            case R.id.min_value:
                break;
            case R.id.max_value:
                break;
            case R.id.count_minus:
                if (curCount >= 100) {
                    curCount -= 100;
                    handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessageDelayed(3, 100);
                }
                break;
            case R.id.et_count:
                break;
            case R.id.count_plus:
                if (curCount <= maxCount - 100) {
                    curCount += 100;
                    handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessageDelayed(3, 100);
                }
                break;
            case R.id.all_buy:
                curCount = maxCount;
                etCount.setText(curCount + "");
                break;
            case R.id.half_buy:
                curCount = maxCount / 2 / 100 * 100;
                etCount.setText(curCount + "");
                break;
            case R.id.one_third_buy:
                curCount = maxCount / 3 / 100 * 100;
                etCount.setText(curCount + "");
                break;
            case R.id.one_fourth_buy:
                curCount = maxCount / 4 / 100 * 100;
                etCount.setText(curCount + "");
                break;
            case R.id.btn_buy:
                buyStocks();
                break;
        }
    }

    private void buyStocks() {
        if (!DateUtil.isExchangeTime(System.currentTimeMillis())) {
            Toast.makeText(getActivity(), "当前非交易时间", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isGoodInput()) {
            ExeStock exeStock = new ExeStock();
            exeStock.setName(etStock.getText().toString());
            exeStock.setAccId(accId);
            exeStock.setExeId(NumberUtil.getUUID());
            exeStock.setExeValue(Double.parseDouble(etValue.getText().toString()));
            exeStock.setExeMount(Integer.parseInt(etCount.getText().toString()));
            exeStock.setNumber(number);
            exeStock.setExeType(Constants.TYPE_BUY);
            exeStock.setExeTime(System.currentTimeMillis());
            StockBuisnessManager.getInstance(getActivity()).insertExchange(exeStock);
            double cStockValue = accStock.getCurStockValue();
            double cValue = accStock.getCurValue();
            cValue = cValue - exeStock.getExeMount() * exeStock.getExeValue();
            cStockValue = cStockValue + exeStock.getExeMount() * exeStock.getExeValue();
            accStock.setCurStockValue(cStockValue);
            accStock.setCurValue(cValue);
            StockBuisnessManager.getInstance(getActivity()).replaceAccount(accStock);
            refreshHoldsStocks();
            Toast.makeText(getActivity(), "购买成功", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isGoodInput() {
        if (TextUtils.isEmpty(etStock.getText().toString())) {
            Toast.makeText(getActivity(), "请选择需要购买的股票", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(etValue.getText().toString())) {
            Toast.makeText(getActivity(), "请输入购买的价格", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(etCount.getText().toString()) || "0".equals(etCount.getText().toString())) {
            Toast.makeText(getActivity(), "请输入购买的数量", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i("requestcode:" + requestCode + " resultcode:" + resultCode);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    number = data.getStringExtra("number");
                    name = data.getStringExtra("name");
                    queryByNumber();
                }
                break;
        }
    }

    private void queryByNumber(){
        etStock.setText(name);
        LogUtil.i("number:" + number);
        if (number != null && !number.equals("")) {
            isInit = true;
            querySinaStocks();
        }
    }
}
