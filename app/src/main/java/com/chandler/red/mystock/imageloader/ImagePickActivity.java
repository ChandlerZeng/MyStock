package com.chandler.red.mystock.imageloader;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chandler.red.mystock.BaseActivity;
import com.chandler.red.mystock.R;
import com.chandler.red.mystock.adapter.PhotoAdapter;
import com.chandler.red.mystock.db.MySqlHelper;
import com.chandler.red.mystock.entity.PhotoBean;
import com.github.jdsjlzx.ItemDecoration.SpacesItemDecoration;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ImagePickActivity extends BaseActivity implements View.OnClickListener{
    TextView tv_done;
    TextView tv_album_name;
    RelativeLayout rl_album;
    ListView lv_albumlist;
    ImageView iv_showalbum;
    private AlbumListAdapter albumListAdapter;
    private List<AlbumBean> albumList = new ArrayList<>();//相册列表
    boolean isShowAlbum;
    LRecyclerView mRecyclerView;

    private PhotoAdapter photoAdapter;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private List<PhotoBean> photoBeanLists;
    private RecyclerView.OnScrollListener onScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pick);
    }

    private void initView(){
        mRecyclerView = findViewById(R.id.gv_photo);
        tv_done = (TextView) findViewById(R.id.tv_done);
        tv_album_name = (TextView) findViewById(R.id.tv_album_name);
        findViewById(R.id.rl_center).setOnClickListener(this);
        iv_showalbum = (ImageView) findViewById(R.id.iv_showalbum);
        //选择相册的布局
        rl_album = (RelativeLayout) findViewById(R.id.rl_album);
        rl_album.setOnClickListener(this);
        lv_albumlist = (ListView) findViewById(R.id.lv_albumlist);
        tv_done.setOnClickListener(this);
        findViewById(R.id.rl_album).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        //检查权限,获取权限之后将手机所有注册图片搜索出来，并按照相册进行分类
        getPersimmion();

        photoBeanLists = new ArrayList<>();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecyclerView.removeOnScrollListener(onScrollListener);
    }

    //授权信息
    private void getPersimmion() {
        if (Build.VERSION.SDK_INT >= 23) {
            //1. 检测是否添加权限   PERMISSION_GRANTED  表示已经授权并可以使用
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //手机为Android6.0的版本,未授权则动态请求授权
                //2. 申请请求授权权限
                //1. Activity
                // 2. 申请的权限名称
                // 3. 申请权限的 请求码
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.READ_EXTERNAL_STORAGE}, 1002);
            }else {
                get500PhotoFromLocalStorage();
            }
        }else {
            get500PhotoFromLocalStorage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            get500PhotoFromLocalStorage();
        }else {
            Toast.makeText(ImagePickActivity.this,"请求读取SD卡权限失败",Toast.LENGTH_SHORT).show();
        }
        Log.i(MySqlHelper.TAG,"requestCode:"+requestCode+" grandresults:"+grantResults[0]);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(lv_albumlist == null)initView();
    }

    private void refreshPhotos(List<PhotoBean> photoArrayList){
        //添加一个默认的相册用来存放这最近的500张图片
        AlbumBean defaultAlbum = new AlbumBean();
        defaultAlbum.albumFolder = Environment.getExternalStorageDirectory();
//        LogUtil.i("Alex","folder是"+defaultAlbum.albumFolder.getAbsolutePath());
        defaultAlbum.topImagePath = photoArrayList.get(0).getUrl();
        defaultAlbum.imageCounts = photoArrayList.size();
        defaultAlbum.folderName = "最近";
        if(albumList.size()>0 && albumList.get(0)!=null && albumList.get(0).folderName.equals("最近")){
            albumList.set(0,defaultAlbum);
        }else {
            albumList.add(0,defaultAlbum);
        }
        if(albumListAdapter != null){//这个回调优先于查找相册回调
//            LogUtilUtil.i("Alex","500张图片落后回调");
            albumListAdapter.notifyDataSetChanged();
        }
        //查找并设置手机上的所有相册
        AlbumBean.getAllAlbumFromLocalStorage(ImagePickActivity.this, new AlbumBean.AlbumListCallback() {
            @Override
            public void onSuccess(ArrayList<AlbumBean> result) {
                albumList.addAll(result);
                albumListAdapter = new AlbumListAdapter(ImagePickActivity.this,albumList);
                lv_albumlist.setAdapter(albumListAdapter);
            }
        });
        lv_albumlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(albumList!=null && position < albumList.size() && albumList.get(position) != null){//满足条件才能set
                    tv_album_name.setText(albumList.get(position).folderName);
                }
                isShowAlbum=false;
                hideAlbum();
                AlbumBean.getAlbumPhotosFromLocalStorage(ImagePickActivity.this, albumList.get(position), new AlbumBean.AlbumPhotosCallback() {
                    @Override
                    public void onSuccess(List<PhotoBean> photos) {
//                        LogUtil.i("Alex","new photo list是"+photos);
                        photoBeanLists.clear();//因为是ArrayAdapter，所以引用不能重置
                        photoBeanLists.addAll(photos);
                        photoAdapter.setData(photoBeanLists);
                    }
                });
            }
        });
    }

    /**
     * 从系统相册里面取出图片的uri
     */
    private void get500PhotoFromLocalStorage() {
        new AlxMultiTask<Void,Void,List<PhotoBean>>(){

            @Override
            protected List<PhotoBean> doInBackground(Void... params) {
                List<PhotoBean> allPhotoArrayList = new ArrayList<>();

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = getContentResolver();//得到内容处理者实例

                String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";//设置拍摄日期为倒序
                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, new String[]{MediaStore.Images.Media.DATA}, MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?", new String[]{"image/jpeg", "image/png"}, sortOrder+" limit 500");
                if (mCursor == null) return allPhotoArrayList;
                int size = mCursor.getCount();
                if (size == 0) return allPhotoArrayList;
                for (int i = 0; i < size; i++) {//遍历全部图片
                    mCursor.moveToPosition(i);
                    String path = mCursor.getString(0);// 获取图片的路径
                    PhotoBean entity = new PhotoBean();
                    entity.setUrl(path);//将图片的uri放到对象里去
                    allPhotoArrayList.add(entity);
                }
                mCursor.close();
                return allPhotoArrayList;
            }

            @Override
            protected void onPostExecute(List<PhotoBean> photoArrayList) {
                super.onPostExecute(photoArrayList);
                if(photoArrayList == null || photoArrayList.size()==0){
                    Toast.makeText(ImagePickActivity.this,"没有本地图片",Toast.LENGTH_LONG).show();
                    return;
                }
                photoBeanLists = photoArrayList;
                photoAdapter.setData(photoBeanLists);
                refreshPhotos(photoBeanLists);
            }
        }.executeDependSDK();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_done:
                actionDone();
                break;
            case R.id.rl_center:
                if(isShowAlbum)hideAlbum();//现在是展示album的状态
                else showAlbum();//现在是关闭（正常）状态
                break;
            case R.id.rl_album:
                hideAlbum();
                break;
            default:
                break;
        }
    }

    private void actionDone(){
        Intent intent = new Intent();
        intent.putExtra("url",photoAdapter.getSelectUrl());
        setResult(RESULT_OK,intent);
        finish();
    }

    /**
     * 隐藏相册选择页
     */
    void hideAlbum(){
        if(Build.VERSION.SDK_INT >=11) {
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(rl_album, "alpha", 1.0f, 0.0f);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(iv_showalbum, "rotationX", 180f, 360f);
            ObjectAnimator animator3 = ObjectAnimator.ofFloat(rl_album, "translationY", -dp2Px(45));
            AnimatorSet set = new AnimatorSet();
            set.setDuration(300).playTogether(animator1, animator2, animator3);
            set.start();
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    rl_album.setVisibility(View.GONE);
                }
            });
        }else {
            rl_album.setVisibility(View.GONE);
        }
        isShowAlbum=false;
    }

    /**
     * 显示相册选择页
     */
    void showAlbum(){
        if(Build.VERSION.SDK_INT>=11) {
            rl_album.setVisibility(View.VISIBLE);//一定要先顯示，才能做動畫操作
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(rl_album, "alpha", 0.0f, 1.0f);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(iv_showalbum, "rotationX", 0f, 180f);
            ObjectAnimator animator3 = ObjectAnimator.ofFloat(rl_album, "translationY", dp2Px(45));
            AnimatorSet set = new AnimatorSet();
            set.setDuration(300).playTogether(animator1, animator2, animator3);
            set.start();
        }else {
            rl_album.setVisibility(View.VISIBLE);//一定要先顯示，才能做動畫操作
        }
        isShowAlbum=true;
    }

    public int dp2Px(int dp) {
        try {
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            return (int) (dp * metric.density + 0.5f);
        } catch (Exception e) {
            e.printStackTrace();
            return dp;
        }
    }

}
