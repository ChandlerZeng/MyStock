package com.example.rui.mystock.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rui.mystock.R;
import com.example.rui.mystock.db.MyDbManager;
import com.example.rui.mystock.entity.StockBean;

import java.util.List;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.MyViewHolder> {
    private Context context;
    private List<StockBean> stockBeanList;
    private LayoutInflater layoutInflater;
    public SearchListAdapter(Context context,List<StockBean> list){
        this.context = context;
        this.stockBeanList = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        MyViewHolder myViewHolder = new MyViewHolder(layoutInflater.inflate(R.layout.search_item,viewGroup,false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewTag, final int position) {
            final StockBean stockBean = stockBeanList.get(position);
            viewTag.numberTv.setText(stockBean.getNumber().substring(4));
            viewTag.nameTv.setText(stockBean.getName());
            viewTag.valueTv.setText(stockBean.getValue());
            viewTag.characterTv.setText(stockBean.getCharacter());
            viewTag.valueTv.setTextColor(context.getResources().getColor(R.color.main_red_color));
            viewTag.selectTv.setVisibility(View.VISIBLE);
            if(stockBean.isSelected()){
                viewTag.selectTv.setText("取消自选");
            }else {
                viewTag.selectTv.setText("+ 自选");
            }
            viewTag.selectTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stockBean.setSelected(!stockBean.isSelected());
                    MyDbManager.getInstance(context).replace(stockBean);
                    notifyDataSetChanged();
                }
            });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return stockBeanList.size();
    }

    public void setData(List<StockBean> list){
        stockBeanList = list;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView numberTv;
        TextView nameTv;
        TextView valueTv;
        TextView characterTv;
        TextView selectTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            numberTv = itemView.findViewById(R.id.tv_number);
            nameTv = itemView.findViewById(R.id.tv_name);
            valueTv = itemView.findViewById(R.id.tv_value);
            characterTv = itemView.findViewById(R.id.tv_character);
            selectTv = itemView.findViewById(R.id.tv_select);
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
