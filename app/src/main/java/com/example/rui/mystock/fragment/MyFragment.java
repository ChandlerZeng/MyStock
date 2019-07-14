package com.example.rui.mystock.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rui.mystock.R;
import com.example.rui.mystock.activity.ExchangeActivity;
import com.example.rui.mystock.activity.LoginActivity;
import com.example.rui.mystock.widget.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author Zeng
 **/
public class MyFragment extends Fragment {

    @BindView(R.id.my_photo)
    CircleImageView myPhoto;
    @BindView(R.id.my_name)
    TextView myName;
    @BindView(R.id.my_number)
    TextView myNumber;
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
    @BindView(R.id.my_buy)
    TextView myBuy;
    @BindView(R.id.my_sell)
    TextView mySell;
    @BindView(R.id.my_cancel)
    TextView myCancel;
    @BindView(R.id.my_owner)
    TextView myOwner;
    @BindView(R.id.my_search)
    TextView mySearch;
    @BindView(R.id.my_operate_layout)
    LinearLayout myOperateLayout;
    @BindView(R.id.my_month_title)
    TextView myMonthTitle;
    @BindView(R.id.my_month_profit)
    TextView myMonthProfit;
    @BindView(R.id.my_month_vs)
    TextView myMonthVs;
    @BindView(R.id.month_profit)
    LinearLayout monthProfit;
    Unbinder unbinder;
    @BindView(R.id.my_head_layout)
    RelativeLayout myHeadLayout;

    public MyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.my_head_layout, R.id.my_money_layout, R.id.my_buy, R.id.my_sell, R.id.my_cancel, R.id.my_owner, R.id.my_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.my_head_layout:
                startActivity(new Intent(getActivity(),LoginActivity.class));
                break;
            case R.id.my_money_layout:
                break;
            case R.id.my_buy:
                startActivity(new Intent(getActivity(), ExchangeActivity.class));
                break;
            case R.id.my_sell:
                break;
            case R.id.my_cancel:
                break;
            case R.id.my_owner:
                break;
            case R.id.my_search:
                break;
        }
    }
}
