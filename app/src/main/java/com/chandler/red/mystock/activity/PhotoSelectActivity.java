package com.chandler.red.mystock.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.chandler.red.mystock.BaseActivity;
import com.chandler.red.mystock.R;
import com.chandler.red.mystock.adapter.PhotoAdapter;
import com.chandler.red.mystock.db.MySqlHelper;
import com.chandler.red.mystock.entity.PhotoBean;
import com.chandler.red.mystock.util.FileUtil;
import com.github.jdsjlzx.ItemDecoration.SpacesItemDecoration;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoSelectActivity extends BaseActivity {

    @BindView(R.id.photo_grid)
    LRecyclerView mRecyclerView;

    private PhotoAdapter photoAdapter;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private List<PhotoBean> photoBeanLists;
    private RecyclerView.OnScrollListener onScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_select);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecyclerView.removeOnScrollListener(onScrollListener);
    }

    private void initView(){
        photoBeanLists = new ArrayList<>();
        readFileUrls();
        photoAdapter = new PhotoAdapter(this,this,photoBeanLists);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(photoAdapter);
        //setLayoutManager must before setAdapter
        final GridLayoutManager manager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(manager);
        int spacing = getResources().getDimensionPixelSize(R.dimen.default_grid_divider_padding);
        mRecyclerView.addItemDecoration(SpacesItemDecoration.newInstance(spacing, spacing, manager.getSpanCount(), Color.GRAY));
        mRecyclerView.setAdapter(lRecyclerViewAdapter);
        mRecyclerView.setLoadMoreEnabled(true);
        mRecyclerView.setPullRefreshEnabled(false);
        //设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.main_text_color, R.color.main_text_color ,R.color.main_bg);
        //设置底部加载文字提示
        mRecyclerView.setFooterViewHint("拼命加载中","---------- 我是有底线的 ----------","网络不给力啊，点击再试一次吧");
        mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.w(MySqlHelper.TAG,"onLoadMore curPage:");
                mRecyclerView.setLoadMoreEnabled(false);
            }
        });
        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = manager.findFirstVisibleItemPosition();
                int lastVisible = manager.findLastVisibleItemPosition();
                int firstComVisible = manager.findFirstCompletelyVisibleItemPosition();
                int lastComVisible = manager.findLastCompletelyVisibleItemPosition();
                photoAdapter.setVisbleItem(firstVisible-1,lastVisible-1);
                Log.i("Search","firstVisible:"+firstVisible+" lastVisible:"+lastVisible+" firstComVisible:"+firstComVisible+" lastComVisible:"+lastComVisible);
            }
        };
        mRecyclerView.addOnScrollListener(onScrollListener);
        getPersimmion();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_photo,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_done:
                actionDone();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void actionDone(){
        Intent intent = new Intent();
        intent.putExtra("url",photoAdapter.getSelectUrl());
        setResult(RESULT_OK,intent);
        finish();
    }

    private void readFileUrls(){
        String s = FileUtil.loadFromSDFile("mystock_image.txt");
        String sR = FileUtil.stringReplace(s);
        if(sR!=null){
            String[] sRs = sR.split(" ,");
            addPhotoList(sRs);
        }
    }

    private void addPhotoList(String[] urls){
        for(String url:urls){
            PhotoBean photoBean = new PhotoBean();
            photoBean.setUrl(url);
            photoBeanLists.add(photoBean);
        }
    }

    //授权信息
    private void getPersimmion() {
        if (Build.VERSION.SDK_INT >= 23) {
            //1. 检测是否添加权限   PERMISSION_GRANTED  表示已经授权并可以使用
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //手机为Android6.0的版本,未授权则动态请求授权
                //2. 申请请求授权权限
                //1. Activity
                // 2. 申请的权限名称
                // 3. 申请权限的 请求码
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(MySqlHelper.TAG,"requestCode:"+requestCode+" grandresults:"+grantResults[0]);
    }


}
