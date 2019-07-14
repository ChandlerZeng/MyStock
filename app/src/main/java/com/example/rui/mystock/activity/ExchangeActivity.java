package com.example.rui.mystock.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.rui.mystock.R;
import com.example.rui.mystock.adapter.TabPagerAdapter;
import com.example.rui.mystock.fragment.BuyStockFragment;
import com.example.rui.mystock.fragment.MyFragment;
import com.example.rui.mystock.fragment.SellStockFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExchangeActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_tab)
    TabLayout toolbarTab;
    @BindView(R.id.exchange_view_pager)
    ViewPager exchangeViewPager;

    private List<Fragment> mFrags;
    private TabPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        ButterKnife.bind(this);
        initFragment();
    }

    private void initFragment() {
        mFrags = new ArrayList<>();
        Fragment f1 = new BuyStockFragment();
        Fragment f2 = new SellStockFragment();
        Fragment f3 = new MyFragment();
        Fragment f4 = new MyFragment();
        Fragment f5 = new MyFragment();
        mFrags.add(f1);
        mFrags.add(f2);
        mFrags.add(f3);
        mFrags.add(f4);
        mFrags.add(f5);
        initTab();
    }

    private void initTab(){
        List<String> indicators = new ArrayList<>();
        for(int i=0;i<toolbarTab.getTabCount();i++){
            indicators.add(toolbarTab.getTabAt(i).getText().toString());
        }
        mAdapter = new TabPagerAdapter(getSupportFragmentManager(),indicators, mFrags);
        exchangeViewPager.setAdapter(mAdapter);
        exchangeViewPager.setCurrentItem(0);
        exchangeViewPager.setOffscreenPageLimit(4);
        toolbarTab.setupWithViewPager(exchangeViewPager);
//        if(ScreenUtil.getScreenInch(this)>5){
//            toolbarTab.setTabMode(TabLayout.MODE_FIXED);
//        }else {
//            toolbarTab.setTabMode(TabLayout.MODE_SCROLLABLE);
//        }
//        Utils.reflex(toolbarTab,13);
    }
}
