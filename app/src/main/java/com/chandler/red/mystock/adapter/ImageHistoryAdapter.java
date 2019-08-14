package com.chandler.red.mystock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chandler.red.mystock.R;
import com.chandler.red.mystock.db.StockBuisnessManager;
import com.chandler.red.mystock.entity.baidupic.SearchImageHistory;

import java.util.List;

public class ImageHistoryAdapter extends BaseAdapter {
    private Context context;
    private List<SearchImageHistory> historyBeans;
    private LayoutInflater inflater;
    public ImageHistoryAdapter(Context context, List<SearchImageHistory> historyList) {
        this.context = context;
        historyBeans = historyList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(historyBeans.size()>0){
            return historyBeans.size()+1;
        }else {
            return 0;
        }
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_search_history_list,null);
            holder.tvNumber = convertView.findViewById(R.id.tv_history_number);
            holder.ivClear = convertView.findViewById(R.id.iv_clear);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(position<historyBeans.size()){
            SearchImageHistory historyBean = historyBeans.get(position);
            holder.tvNumber.setText(historyBean.getQueryWord());
        }else {
            holder.tvNumber.setText("清空历史记录");
        }

        holder.ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position==historyBeans.size()){
                    StockBuisnessManager.getInstance(context).clearImageHistory();
                }else {
                    StockBuisnessManager.getInstance(context).deleteImageHistory(historyBeans.get(position));
                }
                setData(StockBuisnessManager.getInstance(context).getImageHistorys());
            }
        });
        return convertView;
    }

    public void setData(List<SearchImageHistory> searchHistoryBeans){
        this.historyBeans = searchHistoryBeans;
        notifyDataSetChanged();
    }

    static class ViewHolder{
        TextView tvNumber;
        ImageView ivClear;
    }
}
