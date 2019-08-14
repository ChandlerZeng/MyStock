package com.chandler.red.mystock.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chandler.red.mystock.R;
import com.chandler.red.mystock.activity.ImageShowActivity;
import com.chandler.red.mystock.entity.PhotoBean;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.MyViewHolder> {
    private Context context;
    private List<PhotoBean> photoBeanList;
    private LayoutInflater inflater;
    private RequestOptions requestOptions;
    private ImageView[] imageViews;
    private String[] urls;
    private Activity activity;
    private String selectUrl;
    private int firstVisible;
    private int lastVisible;

    public PhotoAdapter(Context context, Activity activity,List<PhotoBean> photoBeans) {
        this.context = context;
        this.photoBeanList =photoBeans;
        this.activity = activity;
        imageViews = new ImageView[photoBeans.size()];
        urls = new String[photoBeans.size()];
        for(int i = 0;i<photoBeans.size();i++){
            urls[i] = photoBeans.get(i).getUrl();
        }
        inflater = LayoutInflater.from(context);
        requestOptions = new RequestOptions().centerCrop().placeholder(R.mipmap.pictures_no)
                .error(R.mipmap.pictures_no).diskCacheStrategy(DiskCacheStrategy.NONE);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        MyViewHolder myViewHolder = new MyViewHolder(inflater.inflate(R.layout.item_photo_select,viewGroup,false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, final int position) {
        final PhotoBean photoBean = photoBeanList.get(position);
        if(imageViews.length>position){
            imageViews[position] = viewHolder.ivPhoto;
        }
        Glide.with(context).load(photoBean.getUrl()).apply(requestOptions).into(viewHolder.ivPhoto);
        if(photoBean.isSelect()){
            viewHolder.ivSelect.setImageResource(R.mipmap.ic_selected);
            viewHolder.ivPhoto.setColorFilter(Color.parseColor("#77000000"));
        }else {
            viewHolder.ivSelect.setImageResource(R.mipmap.ic_unselect);
            viewHolder.ivPhoto.setColorFilter(null);
        }

        viewHolder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageShowActivity.startImageActivity(activity,imageViews,urls,position,firstVisible,lastVisible);
            }
        });

        viewHolder.ivSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(PhotoBean bean:photoBeanList){
                    bean.setSelect(false);
                }
                photoBean.setSelect(true);
                selectUrl = photoBean.getUrl();
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
        return photoBeanList.size();
    }

    public void setData(List<PhotoBean> photoBeans){
        this.photoBeanList = photoBeans;
        imageViews = null;
        imageViews = new ImageView[photoBeans.size()];
        urls = null;
        urls = new String[photoBeans.size()];
        for(int i = 0;i<photoBeans.size();i++){
            urls[i] = photoBeans.get(i).getUrl();
        }
        notifyDataSetChanged();
    }

    public void setVisbleItem(int first,int last){
        firstVisible = first;
        lastVisible = last;
    }

    public String getSelectUrl(){
        return selectUrl;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        ImageView ivSelect;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            ivSelect = itemView.findViewById(R.id.iv_select);
        }
    }
}
