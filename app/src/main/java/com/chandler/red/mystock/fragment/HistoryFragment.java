package com.chandler.red.mystock.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;

import com.chandler.red.mystock.R;
import com.chandler.red.mystock.adapter.HistoryAdapter;
import com.chandler.red.mystock.db.StockBuisnessManager;
import com.chandler.red.mystock.entity.ExeStock;
import com.chandler.red.mystock.entity.HistoryBean;
import com.chandler.red.mystock.entity.Stock;
import com.chandler.red.mystock.presenter.StockPresenter;
import com.chandler.red.mystock.util.DateUtil;
import com.chandler.red.mystock.util.LogUtil;
import com.chandler.red.mystock.view.HttpResponseView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Zeng
 **/
public class HistoryFragment extends LazyLoadFragment {

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.history_list)
    ListView historyList;
    Unbinder unbinder;
    private String phone;

    private List<HistoryBean> historyBeans;
    private List<ExeStock> exeStockList;
    private HistoryAdapter historyAdapter;
    private StockPresenter<ArrayList<Stock>> stockPresenter;
    private StringBuilder numberList;
    private HttpResponseView<ArrayList<Stock>> httpResponseView = new HttpResponseView<ArrayList<Stock>>() {
        @Override
        public void onSuccess(ArrayList<Stock> responseBody) {
            if(responseBody!=null && responseBody.size()>0){
                historyBeans.clear();
                for(int i = 0;i<exeStockList.size();i++){
                    ExeStock exeStock = exeStockList.get(i);
                    Stock stock = responseBody.get(i);
                    String name = exeStock.getName();
                    String date = DateUtil.parseToDate(exeStock.getExeTime());
                    int type = exeStock.getExeType();
                    double exeValue = exeStock.getExeValue();
                    int mount = Math.abs(exeStock.getExeMount());
                    double curValue = Double.parseDouble(stock.getNow_());
                    double profitRate;
                    if(type==0){
                        profitRate = (curValue-exeValue)/exeValue;
                    }else {
                        profitRate = (exeValue-curValue)/exeValue;
                    }
                    double profit = profitRate * mount * exeValue;
                    HistoryBean historyBean = new HistoryBean(name,date,type,profit ,profitRate * 100);
                    historyBean.setCost(exeValue);
                    historyBean.setCount(mount);
                    historyBean.setTime(DateUtil.parseToHHMMString(exeStock.getExeTime()));
                    historyBean.setValue(curValue);
                    historyBean.setNumber(stock.getId_().substring(2));
                    historyBeans.add(historyBean);
                }
                historyAdapter.setData(historyBeans);
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

    private void initView(){
        showProgressDialog();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MY_STOCK_PREF", Activity.MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", null);
        historyBeans = new ArrayList<>();
        historyAdapter = new HistoryAdapter(getActivity(),historyBeans);
        historyList.setAdapter(historyAdapter);
        stockPresenter = new StockPresenter<>(getActivity(),new ArrayList<Stock>(1));
        stockPresenter.onCreate();
        stockPresenter.attachView(httpResponseView);
        refreshHistoryList();
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.main_bg_light_light));
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main_blue_color_light));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNetHoldsStocks();
            }
        });
    }

    private void refreshHistoryList(){
        exeStockList = StockBuisnessManager.getInstance(getActivity()).getAllExeHoldStocks();
        if(exeStockList!=null && exeStockList.size()>0){
            numberList = new StringBuilder();
            for(ExeStock holdsBean:exeStockList){
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(unbinder!=null)
        unbinder.unbind();
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_history;
    }

    @Override
    protected void lazyLoad() {
        unbinder = ButterKnife.bind(this, getContentView());
        initView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

        }
    }
}
