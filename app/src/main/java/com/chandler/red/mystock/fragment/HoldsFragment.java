package com.chandler.red.mystock.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chandler.red.mystock.R;
import com.chandler.red.mystock.StockImgActivity;
import com.chandler.red.mystock.StockImgApi;
import com.chandler.red.mystock.adapter.HoldsAdapter;
import com.chandler.red.mystock.db.MySqlHelper;
import com.chandler.red.mystock.db.StockBuisnessManager;
import com.chandler.red.mystock.entity.AccStock;
import com.chandler.red.mystock.entity.HoldStock;
import com.chandler.red.mystock.entity.HoldsBean;
import com.chandler.red.mystock.entity.Stock;
import com.chandler.red.mystock.presenter.StockPresenter;
import com.chandler.red.mystock.util.EncryptUtil;
import com.chandler.red.mystock.util.LogUtil;
import com.chandler.red.mystock.view.HttpResponseView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Zeng
 **/
public class HoldsFragment extends LazyLoadFragment {

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.my_all_money)
    TextView myAllMoney;
    @BindView(R.id.my_all_money_value)
    TextView myAllMoneyValue;
    @BindView(R.id.my_all_profit)
    TextView myAllProfit;
    @BindView(R.id.my_all_profit_value)
    TextView myAllProfitValue;
    @BindView(R.id.my_all_stock)
    TextView myAllStock;
    @BindView(R.id.my_all_stock_value)
    TextView myAllStockValue;
    @BindView(R.id.my_day_profit)
    TextView myDayProfit;
    @BindView(R.id.my_day_profit_value)
    TextView myDayProfitValue;
    @BindView(R.id.my_money_layout)
    RelativeLayout myMoneyLayout;
    @BindView(R.id.hold_value)
    TextView holdValue;
    @BindView(R.id.hold_profit)
    TextView holdProfit;
    @BindView(R.id.hold_count)
    TextView holdCount;
    @BindView(R.id.hold_cost)
    TextView holdCost;
    @BindView(R.id.my_operate_layout)
    LinearLayout myOperateLayout;
    @BindView(R.id.hold_list)
    ListView holdList;
    Unbinder unbinder;
    private String phone;
    private boolean isLogin;

    private HoldsAdapter holdsAdapter;
    private List<HoldsBean> holdsBeanList;

    private StockPresenter<ArrayList<Stock>> presenter;
    private List<HoldStock> holdStocks;

    private StockPresenter<ArrayList<Stock>> stockPresenter;
    private HttpResponseView<ArrayList<Stock>> httpResponseView2;
    private double totalStockValues;
    private double initValue;
    private double totalValues;
    private double freeMoney;
    private double totalProfit;
    private double dayProfit;
    private String list;
    private Timer timer;

    private void initView(){
        showProgressDialog();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MY_STOCK_PREF", Activity.MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", null);
        isLogin = sharedPreferences.getBoolean("is_login", false);
        holdsBeanList = new ArrayList<>();
        holdsAdapter = new HoldsAdapter(getActivity(),holdsBeanList);
        holdList.setAdapter(holdsAdapter);
        if (isLogin) {
            AccStock accStock = StockBuisnessManager.getInstance(getActivity()).getStockAccountByPhone(EncryptUtil.md5WithSalt(phone));
            if(accStock!=null){
                initValue = accStock.getInitValue();
                freeMoney = accStock.getCurValue();
            }
            list = StockBuisnessManager.getInstance(getActivity()).getNumberList();
            Log.i(MySqlHelper.TAG,"numberlist:"+list);
            holdStocks = StockBuisnessManager.getInstance(getActivity()).getHoldStocks();
            presenter = new StockPresenter<>(getActivity(),new ArrayList<Stock>());
            presenter.onCreate();
            presenter.attachView(httpResponseView);
            initHoldsStocks();
            timer = new Timer("RefreshStocks");
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    refreshStocks();
                }
            }, 500, 29500); // 30 seconds
            refreshHoldsStocks();
            swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.main_bg_light_light));
            swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main_blue_color_light));
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshStocks();
                    refreshNetHoldsStocks();
                }
            });
        }

        dimissProgressDialog();
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
        holdList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String number = holdsBeanList.get(position).getNumber();
                String url = StockImgApi.IMG_F + number + ".gif";
                Intent intent = new Intent(getActivity(), StockImgActivity.class);
                intent.putExtra("type",2);
                intent.putExtra("url", url);
                intent.putExtra("id", "s_"+number);
                startActivity(intent);
                LogUtil.w("number:"+number+" url:"+url);
            }
        });
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

    private HttpResponseView<ArrayList<Stock>> httpResponseView = new HttpResponseView<ArrayList<Stock>>() {
        @Override
        public void onSuccess(ArrayList<Stock> responseBody) {
            refreshView(responseBody);
        }

        @Override
        public void onError(String result) {
            LogUtil.e("errorresult:"+result);
        }
    };

    private void refreshView(ArrayList<Stock> responseBody){
        totalStockValues = 0;
        dayProfit = 0;
        for(HoldStock holdstock:holdStocks){
            for(Stock stock:responseBody){
                if(stock.getId_().equals(holdstock.getNumber())){
                    holdstock.setCurValue(Double.parseDouble(stock.getNow_()));
                    holdstock.setYesValue(Double.parseDouble(stock.getYest_value()));
                }
            }
            totalStockValues += holdstock.getCurValue()*holdstock.getCount();
            dayProfit += holdstock.getDayProfit();
            LogUtil.i("totalStockValues:"+totalStockValues+"  dayProfit:"+dayProfit);
            LogUtil.i("holdStock:"+holdstock.toString());
        }

        totalValues = freeMoney+totalStockValues;
        totalProfit = totalValues-initValue;
        myAllMoneyValue.setText(String.format(Locale.CHINA,"%.2f",totalValues));
        myAllStockValue.setText(String.format(Locale.CHINA,"%.2f",totalStockValues));
        myAllProfitValue.setText(String.format(Locale.CHINA,"%.2f",totalProfit));
        myDayProfitValue.setText(String.format(Locale.CHINA,"%.2f",dayProfit));
        setTextColor(myAllMoneyValue,totalProfit);
        setTextColor(myAllStockValue,totalProfit);
        setTextColor(myAllProfitValue,totalProfit);
        setTextColor(myDayProfitValue,dayProfit);
    }

    private void refreshStocks(){
        presenter.querySinaStocks(list);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(timer!=null){
            timer.cancel();
        }
        if(unbinder!=null)
        unbinder.unbind();
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_holds;
    }

    @Override
    protected void lazyLoad() {
        unbinder = ButterKnife.bind(this, getContentView());
        initView();
    }

}
