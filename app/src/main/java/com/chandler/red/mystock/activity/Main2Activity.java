package com.chandler.red.mystock.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.chandler.red.mystock.R;
import com.chandler.red.mystock.adapter.MainPagerAdapter;
import com.chandler.red.mystock.fragment.MainFragment;
import com.chandler.red.mystock.fragment.MyFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Main2Activity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.rb_main_page)
    RadioButton rbMainPage;
    @BindView(R.id.rb_my_main)
    RadioButton rbMyMain;
    @BindView(R.id.radio_group_main)
    RadioGroup radioGroupMain;
    @BindView(R.id.main_view_pager)
    ViewPager mainViewPager;

    protected List<Fragment> fragLists;
    private MainFragment mainFragment;
    private MyFragment myFragment;
    private PagerAdapter pagerAdapter;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        radioGroupMain.setOnCheckedChangeListener(this);
        initFragment();
    }

    private boolean isFromViewPager;
    private void initFragment(){
        fragLists = new ArrayList<>();
        mainFragment = new MainFragment();
        myFragment = new MyFragment();
        fragLists.add(mainFragment);
        fragLists.add(myFragment);
        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(),fragLists);
        mainViewPager.setAdapter(pagerAdapter);
        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                position = i;
                isFromViewPager = true;
                Log.w("MAIN","onPageSelected:"+i+" isFromViewPager:"+isFromViewPager);
                if(position == 0){
                    radioGroupMain.check(R.id.rb_main_page);
                }else {
                    radioGroupMain.check(R.id.rb_my_main);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                Log.w("MAIN","onPageScrollStateChanged:"+i+" isFromViewPager:"+isFromViewPager);
                if(i==0){
                    isFromViewPager = false;
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Log.w("MAIN","onCheckedChanged:"+checkedId+" isFromViewPager:"+isFromViewPager);
        if(isFromViewPager){
            return;
        }
        switch (checkedId){
            case R.id.rb_main_page:
                mainViewPager.setCurrentItem(0);
                break;
            case R.id.rb_my_main:
                mainViewPager.setCurrentItem(1);
                break;
        }
    }
}
