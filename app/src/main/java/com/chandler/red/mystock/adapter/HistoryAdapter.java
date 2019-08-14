package com.chandler.red.mystock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chandler.red.mystock.R;
import com.chandler.red.mystock.entity.HistoryBean;

import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends BaseAdapter {
    private Context context;
    private List<HistoryBean> historyBeans;
    private LayoutInflater inflater;
    public HistoryAdapter(Context context, List<HistoryBean> historyList) {
        this.context = context;
        historyBeans = historyList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return historyBeans.size();
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
        ViewHolder holder = null;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_history_list,null);
            holder.tvName = convertView.findViewById(R.id.history_name);
            holder.tvNumber = convertView.findViewById(R.id.history_number);
            holder.tvDate = convertView.findViewById(R.id.exchange_date);
            holder.tvTime = convertView.findViewById(R.id.exchange_time);
            holder.tvType = convertView.findViewById(R.id.history_type);
            holder.tvCount = convertView.findViewById(R.id.history_count);
            holder.tvCost = convertView.findViewById(R.id.history_cost);
            holder.tvValue = convertView.findViewById(R.id.history_value);
            holder.tvProfit = convertView.findViewById(R.id.history_profit);
            holder.tvProfitRate = convertView.findViewById(R.id.history_profit_rate);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        HistoryBean historyBean = historyBeans.get(position);
        holder.tvName.setText(historyBean.getName());
        holder.tvNumber.setText(historyBean.getNumber());
        holder.tvTime.setText(historyBean.getTime());
        holder.tvDate.setText(historyBean.getDate());
        holder.tvCost.setText(String.format(Locale.CHINA,"%.2f",historyBean.getCost()));
        holder.tvValue.setText(String.format(Locale.CHINA,"%.2f",historyBean.getValue()));
        holder.tvProfit.setText(String.format(Locale.CHINA,"%.2f",historyBean.getProfit()));
        holder.tvProfitRate.setText(String.format(Locale.CHINA,"%.2f",historyBean.getProfitRate())+"%");
        holder.tvType.setText(historyBean.getTypeString());
        holder.tvCount.setText(String.format(Locale.CHINA,"%d",historyBean.getCount()));
        int color;
        if(historyBean.getProfit()>0){
            color = context.getResources().getColor(R.color.main_red_color);
        }else if(historyBean.getProfit()<0){
            color = context.getResources().getColor(R.color.main_green_color);
        }else {
            color = context.getResources().getColor(R.color.main_text_color);
        }
        holder.tvName.setTextColor(color);
        holder.tvNumber.setTextColor(color);
        holder.tvDate.setTextColor(color);
        holder.tvTime.setTextColor(color);
        holder.tvCost.setTextColor(color);
        holder.tvValue.setTextColor(color);
        holder.tvProfit.setTextColor(color);
        holder.tvProfitRate.setTextColor(color);
        holder.tvType.setTextColor(color);
        holder.tvCount.setTextColor(color);
        return convertView;
    }

    public void setData(List<HistoryBean> list){
        this.historyBeans = list;
        notifyDataSetChanged();
    }

    static class ViewHolder{
        TextView tvName;
        TextView tvNumber;
        TextView tvDate;
        TextView tvTime;
        TextView tvType;
        TextView tvCount;
        TextView tvCost;
        TextView tvValue;
        TextView tvProfit;
        TextView tvProfitRate;

    }
}
