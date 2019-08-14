package com.chandler.red.mystock.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.chandler.red.mystock.BaseActivity;
import com.chandler.red.mystock.R;
import com.chandler.red.mystock.adapter.TabPagerAdapter;
import com.chandler.red.mystock.fragment.BuyStockFragment;
import com.chandler.red.mystock.fragment.HistoryFragment;
import com.chandler.red.mystock.fragment.HoldsFragment;
import com.chandler.red.mystock.fragment.SellStockFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExchangeActivity extends BaseActivity {

    @BindView(R.id.toolbar_tab)
    TabLayout toolbarTab;
    @BindView(R.id.exchange_view_pager)
    ViewPager exchangeViewPager;

    private List<Fragment> mFrags;
    private TabPagerAdapter mAdapter;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        ButterKnife.bind(this);
        page = getIntent().getIntExtra("page",0);
        initFragment();
    }

    private void initFragment() {
        mFrags = new ArrayList<>();
        Fragment f1 = new BuyStockFragment();
        Fragment f2 = new SellStockFragment();
        Fragment f3 = new HoldsFragment();
        Fragment f4 = new HistoryFragment();
        mFrags.add(f1);
        mFrags.add(f2);
        mFrags.add(f3);
        mFrags.add(f4);
        initTab();
    }

    private void initTab(){
        List<String> indicators = new ArrayList<>();
        for(int i=0;i<toolbarTab.getTabCount();i++){
            indicators.add(toolbarTab.getTabAt(i).getText().toString());
        }
        mAdapter = new TabPagerAdapter(getSupportFragmentManager(),indicators, mFrags);
        exchangeViewPager.setAdapter(mAdapter);
        exchangeViewPager.setCurrentItem(page);
        exchangeViewPager.setOffscreenPageLimit(3);
        toolbarTab.setupWithViewPager(exchangeViewPager);
//        if(ScreenUtil.getScreenInch(this)>5){
//            toolbarTab.setTabMode(TabLayout.MODE_FIXED);
//        }else {
//            toolbarTab.setTabMode(TabLayout.MODE_SCROLLABLE);
//        }
//        Utils.reflex(toolbarTab,13);
    }
}
