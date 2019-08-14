package com.chandler.red.mystock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chandler.red.mystock.R;
import com.chandler.red.mystock.entity.StockBuy;

import java.util.List;

public class BuyStockListAdapter extends BaseAdapter {
    private List<StockBuy> stockBuyList;
    private LayoutInflater layoutInflater;
    private Context context;
    public BuyStockListAdapter(Context context,List<StockBuy> list) {
        this.context = context;
        this.stockBuyList = list;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return stockBuyList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.buy_list_item,null);
        TextView tvName = convertView.findViewById(R.id.tv_buy_name);
        TextView tvValue = convertView.findViewById(R.id.tv_buy_value);
        TextView tvCount = convertView.findViewById(R.id.tv_buy_count);
        StockBuy stockBuy = stockBuyList.get(position);
        tvName.setText(stockBuy.getName());
        tvValue.setText(stockBuy.getValue());
        tvCount.setText(stockBuy.getCount());
        if(stockBuy.getIncrease()==0){
            tvCount.setTextColor(context.getResources().getColor(R.color.main_text_color));
        }else {
            tvCount.setTextColor(context.getResources().getColor(R.color.main_blue_color));
        }
        if(stockBuy.getIncrease()>0){
            tvValue.setTextColor(context.getResources().getColor(R.color.main_red_color));
        }else if(stockBuy.getIncrease()<0){
            tvValue.setTextColor(context.getResources().getColor(R.color.main_green_color));
        }else {
            tvValue.setTextColor(context.getResources().getColor(R.color.main_text_color));
        }
        return convertView;
    }

    public void setData(List<StockBuy> list){
        stockBuyList = list;
        notifyDataSetChanged();
    }
}
