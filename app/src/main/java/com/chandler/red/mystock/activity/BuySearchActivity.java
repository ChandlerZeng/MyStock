package com.chandler.red.mystock.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.chandler.red.mystock.BaseActivity;
import com.chandler.red.mystock.R;
import com.chandler.red.mystock.adapter.HistorySearchAdapter;
import com.chandler.red.mystock.adapter.SearchListAdapter;
import com.chandler.red.mystock.db.MyDbManager;
import com.chandler.red.mystock.db.MySqlHelper;
import com.chandler.red.mystock.db.StockBuisnessManager;
import com.chandler.red.mystock.entity.SearchHistoryBean;
import com.chandler.red.mystock.entity.StockBean;
import com.github.jdsjlzx.ItemDecoration.LuDividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class BuySearchActivity extends BaseActivity {

    private EditText etSearch;
    private LuRecyclerView lRecyclerView;
    private List<StockBean> stockBeanList;
    private SearchListAdapter searchListAdapter;
    private LuRecyclerViewAdapter recyclerViewAdapter;
    private String etText;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private int curPage = 0;
    private ListView searchHistoryList;
    private List<SearchHistoryBean> searchHistoryBeans;
    private HistorySearchAdapter searchHistoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_search);
        initView();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 1:
                    curPage = 0;
                    refreshSearchList(etText);
                    break;
                case 2:
                    curPage = 0;
                    refreshSearchList(null);
                    break;
                case 3:
                    loadMoreAction();
                    break;
                default:
                    break;
            }
        }
    };

    private void initView(){
        etSearch = findViewById(R.id.et_search);
        searchHistoryList = findViewById(R.id.search_history_list);
        searchHistoryBeans = StockBuisnessManager.getInstance(this).getSearchHistorys();
        searchHistoryAdapter = new HistorySearchAdapter(this,searchHistoryBeans);
        searchHistoryList.setAdapter(searchHistoryAdapter);
        lRecyclerView = findViewById(R.id.lrecycle_view);
        progressBar = findViewById(R.id.progress_bar);
        linearLayout = findViewById(R.id.search_layout);
        linearLayout.setVisibility(View.GONE);
        stockBeanList = new ArrayList<>();
        searchListAdapter = new SearchListAdapter(this,stockBeanList);
        recyclerViewAdapter = new LuRecyclerViewAdapter(searchListAdapter);
        lRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        lRecyclerView.setAdapter(recyclerViewAdapter);
        LuDividerDecoration divider = new LuDividerDecoration.Builder(this,recyclerViewAdapter)
                .setHeight(R.dimen.default_divider_height)
                .setPadding(R.dimen.default_divider_padding)
                .setColorResource(R.color.main_bg_grey)
                .build();

        lRecyclerView.setHasFixedSize(true);
        lRecyclerView.addItemDecoration(divider);
        recyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String idtext = stockBeanList.get(position).getNumber();
                String name = stockBeanList.get(position).getName();
                idtext = idtext.substring(2);
                Intent intent = new Intent();
                intent.putExtra("number",idtext);
                intent.putExtra("name",name);
                setResult(RESULT_OK,intent);
                finish();
            }

        });
        //设置底部加载颜色
        lRecyclerView.setFooterViewColor(R.color.main_text_color, R.color.main_text_color ,R.color.main_bg);
        //设置底部加载文字提示
        lRecyclerView.setFooterViewHint("拼命加载中","---------- 我是有底线的 ----------","网络不给力啊，点击再试一次吧");
        lRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.w(MySqlHelper.TAG,"onLoadMore curPage:"+curPage);
                handler.sendEmptyMessageDelayed(3,1000);
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                etText = s.toString();
                handler.removeCallbacksAndMessages(null);
                if(etText.length()>=2){
                    progressBar.setVisibility(View.VISIBLE);
                    handler.sendEmptyMessageDelayed(1,1000);
                }else {
                    handler.sendEmptyMessageDelayed(2,1000);
                }
            }
        });
        searchHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position<searchHistoryBeans.size()){
                    etSearch.setText(searchHistoryBeans.get(position).getNumber());
                    etSearch.setSelection(etSearch.getText().length());
                }
            }
        });
        searchListAdapter.setOnItemClickLitener(new SearchListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private int totalCount;
    private String numOrName;
    private void refreshSearchList(String number){
        boolean isInsert = false;
        String initNumber = number;
        if(number==null){
            stockBeanList.clear();
            totalCount = 0;
            searchHistoryList.setVisibility(View.VISIBLE);
        }else {
            searchHistoryList.setVisibility(View.GONE);
            if (number.startsWith("6")) {
                if(number.length()==6){
                    isInsert = true;
                }
                number = "s_sh" + number;
            } else if (number.startsWith("0") || number.startsWith("3")) {
                if(number.length()==6){
                    isInsert = true;
                }
                number = "s_sz" + number;
            }else {
                if(number.length()>2){
                    isInsert = true;
                }
            }
            if(isInsert){
                SearchHistoryBean searchHistoryBean = new SearchHistoryBean();
                searchHistoryBean.setNumber(initNumber);
                StockBuisnessManager.getInstance(this).insertSearchHistory(searchHistoryBean);
                searchHistoryBeans = StockBuisnessManager.getInstance(this).getSearchHistorys();
                searchHistoryAdapter.setData(searchHistoryBeans);
            }
            stockBeanList = MyDbManager.getInstance(this).searchStockByNumberOrName(number,curPage*20,20);
            totalCount = MyDbManager.getInstance(this).getSearchStockCount();
            Log.w(MySqlHelper.TAG,"totalCount:"+totalCount+" listsize:"+stockBeanList.size());
            if(stockBeanList.size()<totalCount){
//                lRecyclerView.setLoadMoreEnabled(true);
                lRecyclerView.setNoMore(false);
            }else {
//                lRecyclerView.setLoadMoreEnabled(false);
                lRecyclerView.setNoMore(true);
            }
        }
        numOrName = number;
        if(stockBeanList.size()>0){
            linearLayout.setVisibility(View.VISIBLE);
        }else {
            linearLayout.setVisibility(View.GONE);
        }
        searchListAdapter.setData(stockBeanList);
        recyclerViewAdapter.notifyDataSetChanged();
        lRecyclerView.refreshComplete(10);
        progressBar.setVisibility(View.GONE);
    }

    private void loadMoreAction(){
        if(stockBeanList.size()<totalCount){
            curPage++;
            List<StockBean> list = MyDbManager.getInstance(this).searchStockByNumberOrName(numOrName,curPage*20,20);
            stockBeanList.addAll(list);
            if(stockBeanList.size()<totalCount){
//                lRecyclerView.setLoadMoreEnabled(true);
                lRecyclerView.setNoMore(false);
            }else {
//                lRecyclerView.setLoadMoreEnabled(false);
                lRecyclerView.setNoMore(true);
            }
            searchListAdapter.setData(stockBeanList);
            recyclerViewAdapter.notifyDataSetChanged();
            lRecyclerView.refreshComplete(10);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyDbManager.getInstance(this).closeDb();
    }
}
