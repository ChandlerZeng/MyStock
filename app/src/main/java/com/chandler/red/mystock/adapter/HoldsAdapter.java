package com.chandler.red.mystock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chandler.red.mystock.R;
import com.chandler.red.mystock.entity.HoldsBean;

import java.util.List;
import java.util.Locale;

public class HoldsAdapter extends BaseAdapter {
    private Context context;
    private List<HoldsBean> holdsBeans;
    private LayoutInflater inflater;
    public HoldsAdapter(Context context, List<HoldsBean> holdsList) {
        this.context = context;
        holdsBeans = holdsList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return holdsBeans.size();
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
            convertView = inflater.inflate(R.layout.item_hold_list,null);
            holder.tvName = convertView.findViewById(R.id.hold_name);
            holder.tvProfit = convertView.findViewById(R.id.hold_profit);
            holder.tvCount = convertView.findViewById(R.id.hold_count);
            holder.tvCost = convertView.findViewById(R.id.hold_cost);
            holder.tvTotalValue = convertView.findViewById(R.id.hold_value);
            holder.tvProfitRate = convertView.findViewById(R.id.hold_profit_rate);
            holder.tvAvail = convertView.findViewById(R.id.hold_available);
            holder.tvCurValue = convertView.findViewById(R.id.hold_cur_value);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        HoldsBean holdsBean = holdsBeans.get(position);
        holder.tvName.setText(holdsBean.getName());
        holder.tvProfit.setText(String.format(Locale.CHINA,"%.2f",holdsBean.getProfit()));
        holder.tvCount.setText(String.format(Locale.CHINA,"%d",holdsBean.getCount()));
        holder.tvCost.setText(String.format(Locale.CHINA,"%.2f",holdsBean.getCost()));
        holder.tvTotalValue.setText(String.format(Locale.CHINA,"%.2f",holdsBean.getTotalValue()));
        holder.tvProfitRate.setText(String.format(Locale.CHINA,"%.2f",holdsBean.getProfitRate())+"%");
        holder.tvAvail.setText(String.format(Locale.CHINA,"%d",holdsBean.getAvailable()));
        holder.tvCurValue.setText(String.format(Locale.CHINA,"%.2f",holdsBean.getCurValue()));
        int color;
        if(holdsBean.getProfit()>0){
            color = context.getResources().getColor(R.color.main_red_color);
        }else if(holdsBean.getProfit()<0){
            color = context.getResources().getColor(R.color.main_green_color);
        }else {
            color = context.getResources().getColor(R.color.main_text_color);
        }
        holder.tvName.setTextColor(color);
        holder.tvProfit.setTextColor(color);
        holder.tvCount.setTextColor(color);
        holder.tvCost.setTextColor(color);
        holder.tvTotalValue.setTextColor(color);
        holder.tvProfitRate.setTextColor(color);
        holder.tvAvail.setTextColor(color);
        holder.tvCurValue.setTextColor(color);
        return convertView;
    }

    public void setData(List<HoldsBean> list){
        this.holdsBeans = list;
        notifyDataSetChanged();
    }

    static class ViewHolder{
        TextView tvName;
        TextView tvProfit;
        TextView tvCount;
        TextView tvCost;
        TextView tvTotalValue;
        TextView tvProfitRate;
        TextView tvAvail;
        TextView tvCurValue;

    }
}
