package com.chandler.red.mystock.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chandler.red.mystock.R;
import com.chandler.red.mystock.entity.Stock;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

public class MainStockListAdapter extends RecyclerView.Adapter<MainStockListAdapter.MyViewHolder>
        implements StickyRecyclerHeadersAdapter<MainStockListAdapter.MyViewHolder> {

    private Context context;
    private List<Stock> stockList;

    public MainStockListAdapter(Context context, List<Stock> stocks){
        this.context = context;
        this.stockList = stocks;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.main_list_item,viewGroup,false));
        return holder;
    }

    @TargetApi(16)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
            Stock stock = stockList.get(i);
            myViewHolder.tvNumber.setText(stock.getId_().substring(4));
            myViewHolder.tvName.setText(stock.getName_());
            myViewHolder.tvValue.setText(stock.getNow_());
            myViewHolder.tvPercent.setText(stock.getPercent());
            myViewHolder.tvIncrease.setText(stock.getIncrease());
            double increase = Double.parseDouble(stock.getIncrease());
            if(increase==0){
                myViewHolder.tvValue.setTextColor(context.getResources().getColor(R.color.main_text_color));
                myViewHolder.tvPercent.setTextColor(context.getResources().getColor(R.color.main_text_color));
                myViewHolder.tvIncrease.setTextColor(context.getResources().getColor(R.color.main_text_color));
            }else if(increase>0){
                myViewHolder.tvValue.setTextColor(context.getResources().getColor(R.color.main_red_color));
                myViewHolder.tvPercent.setTextColor(context.getResources().getColor(R.color.main_red_color));
                myViewHolder.tvIncrease.setTextColor(context.getResources().getColor(R.color.main_red_color));
            }else {
                myViewHolder.tvValue.setTextColor(context.getResources().getColor(R.color.main_green_color));
                myViewHolder.tvPercent.setTextColor(context.getResources().getColor(R.color.main_green_color));
                myViewHolder.tvIncrease.setTextColor(context.getResources().getColor(R.color.main_green_color));
            }
            if(stock.isTop()){
                myViewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.top_text_row_bg));
            }else {
                myViewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.text_row_bg));
            }
            if(mOnItemClickLitener!=null){
                myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickLitener.onItemClick(myViewHolder.itemView,i);
                    }
                });

                myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mOnItemClickLitener.onItemLongClick(myViewHolder.itemView, i);
                        return false;
                    }
                });
            }
    }

    @Override
    public long getHeaderId(int position) {
        return 0;
    }

    @Override
    public MyViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.main_list_item_header,parent,false));
        return holder;
    }

    @Override
    public void onBindHeaderViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        if(stockList.size()==0)return 0;
        return stockList.size();
    }

    public void setData(List<Stock> stocks){
        this.stockList = stocks;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvNumber;
        TextView tvName;
        TextView tvValue;
        TextView tvPercent;
        TextView tvIncrease;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tv_number);
            tvName = itemView.findViewById(R.id.tv_name);
            tvValue = itemView.findViewById(R.id.tv_value);
            tvPercent = itemView.findViewById(R.id.tv_percent);
            tvIncrease = itemView.findViewById(R.id.tv_increase);
        }
    }

    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
        void onItemLongClick(View view , int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

}
