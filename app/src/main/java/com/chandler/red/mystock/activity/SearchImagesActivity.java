package com.chandler.red.mystock.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.chandler.red.mystock.BaseActivity;
import com.chandler.red.mystock.R;
import com.chandler.red.mystock.adapter.ImageHistoryAdapter;
import com.chandler.red.mystock.adapter.PhotoAdapter;
import com.chandler.red.mystock.db.MySqlHelper;
import com.chandler.red.mystock.db.StockBuisnessManager;
import com.chandler.red.mystock.entity.PhotoBean;
import com.chandler.red.mystock.entity.baidupic.CrawerSimpleParams;
import com.chandler.red.mystock.entity.baidupic.SearchImageHistory;
import com.chandler.red.mystock.entity.baidupic.SimpleCrawerResult;
import com.chandler.red.mystock.entity.baidupic.SimpleDataBean;
import com.chandler.red.mystock.presenter.CrawlerPresenter;
import com.chandler.red.mystock.util.FileUtil;
import com.chandler.red.mystock.util.GsonUtil;
import com.chandler.red.mystock.util.ScreenUtil;
import com.chandler.red.mystock.util.TextUtils;
import com.chandler.red.mystock.view.HttpResponseView;
import com.github.jdsjlzx.ItemDecoration.SpacesItemDecoration;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchImagesActivity extends BaseActivity implements TextView.OnEditorActionListener {

    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.photo_grid)
    LRecyclerView mRecyclerView;
    @BindView(R.id.search_history_list)
    ListView searchHistoryList;

    private int curPage;
    private PhotoAdapter photoAdapter;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private List<PhotoBean> photoBeanLists;
    private RecyclerView.OnScrollListener onScrollListener;
    private String query;
    private List<SearchImageHistory> searchHistoryBeans;
    private ImageHistoryAdapter searchHistoryAdapter;
    private boolean isItemClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_images);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecyclerView.removeOnScrollListener(onScrollListener);
    }

    private void initView() {
        etSearch.setOnEditorActionListener(this);
        searchHistoryBeans = StockBuisnessManager.getInstance(this).getImageHistorys();
        searchHistoryAdapter = new ImageHistoryAdapter(this,searchHistoryBeans);
        searchHistoryList.setAdapter(searchHistoryAdapter);
        isHistoryShow = true;
        crawlerPresenter = new CrawlerPresenter(this);
        crawlerPresenter.onCreate();
        crawlerPresenter.attachView(httpResponseView);
        photoBeanLists = new ArrayList<>();
        readFileUrls();
        photoAdapter = new PhotoAdapter(this, this, photoBeanLists);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(photoAdapter);
        //setLayoutManager must before setAdapter
        final GridLayoutManager manager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(manager);
        int spacing = getResources().getDimensionPixelSize(R.dimen.default_grid_divider_padding);
        mRecyclerView.addItemDecoration(SpacesItemDecoration.newInstance(spacing, spacing, manager.getSpanCount(), Color.GRAY));
        mRecyclerView.setAdapter(lRecyclerViewAdapter);
        mRecyclerView.setLoadMoreEnabled(true);
        mRecyclerView.setPullRefreshEnabled(true);
        //设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.main_text_color, R.color.main_text_color, R.color.main_bg);
        //设置底部加载文字提示
        mRecyclerView.setFooterViewHint("拼命加载中", "---------- 我是有底线的 ----------", "网络不给力啊，点击再试一次吧");
        mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getBaiduPics(etSearch.getText().toString(), curPage);
                Log.w(MySqlHelper.TAG, "onLoadMore curPage:");
            }
        });
        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                curPage = 1;
                getBaiduPics(etSearch.getText().toString(), curPage);
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
                photoAdapter.setVisbleItem(firstVisible - 1, lastVisible - 1);
                Log.i("Search", "firstVisible:" + firstVisible + " lastVisible:" + lastVisible + " firstComVisible:" + firstComVisible + " lastComVisible:" + lastComVisible);
            }
        };
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideHistory();
                hideKeybord();
                return false;
            }
        });
        searchHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position<searchHistoryBeans.size()){
                    isItemClicked = true;
                    hideHistory();
                    curPage = 1;
                    etSearch.setText(searchHistoryBeans.get(position).getQueryWord());
                    etSearch.setSelection(etSearch.getText().length());
                    getBaiduPics(etSearch.getText().toString(),curPage);
                }
            }
        });
        mRecyclerView.addOnScrollListener(onScrollListener);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                query = s.toString();
                handler.removeCallbacksAndMessages(null);
                if(isItemClicked){
                    isItemClicked = false;
                }else {
                    handler.sendEmptyMessageDelayed(1, 500);
                }
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    refreshSearchHistroy();
                    break;
            }
        }
    };

    private void refreshSearchHistroy() {
        searchHistoryBeans = StockBuisnessManager.getInstance(this).getImageHistorys(query);
        searchHistoryAdapter.setData(searchHistoryBeans);
        if(!isHistoryShow){
            showHistory();
        }
    }

    private boolean isHistoryShow;
    /**
     * 隐藏历史记录
     */
    void hideHistory(){
        if(Build.VERSION.SDK_INT >=11) {
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(searchHistoryList, "alpha", 1.0f, 0.0f);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(searchHistoryList, "translationY", -ScreenUtil.dp2Px(this,20));
            AnimatorSet set = new AnimatorSet();
            set.setDuration(300).playTogether(animator1, animator2);
            set.start();
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    searchHistoryList.setVisibility(View.GONE);
                }
            });
        }else {
            searchHistoryList.setVisibility(View.GONE);
        }
        isHistoryShow = false;
    }

    /**
     * 显示历史记录
     */
    void showHistory(){
        if(Build.VERSION.SDK_INT>=11) {
            searchHistoryList.setVisibility(View.VISIBLE);//一定要先顯示，才能做動畫操作
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(searchHistoryList, "alpha", 0.0f, 1.0f);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(searchHistoryList, "translationY", ScreenUtil.dp2Px(this,20));
            AnimatorSet set = new AnimatorSet();
            set.setDuration(300).playTogether(animator1, animator2);
            set.start();
        }else {
            searchHistoryList.setVisibility(View.VISIBLE);//一定要先顯示，才能做動畫操作
        }
        isHistoryShow = true;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            curPage = 1;
            getBaiduPics(etSearch.getText().toString(), curPage);
            hideHistory();
        }
        return false;
    }

    private void readFileUrls() {
        String s = FileUtil.loadFromSDFile("mystock_image.txt");
        String sR = FileUtil.stringReplace(s);
        if (sR != null) {
            String[] sRs = sR.split(" ,");
            addPhotoList(sRs);
        }
    }

    private void addPhotoList(String[] urls) {
        for (String url : urls) {
            PhotoBean photoBean = new PhotoBean();
            photoBean.setUrl(url);
            photoBeanLists.add(photoBean);
        }
    }


    private CrawerSimpleParams params;
    private CrawlerPresenter crawlerPresenter;
    private HttpResponseView<SimpleCrawerResult> httpResponseView = new HttpResponseView<SimpleCrawerResult>() {
        @Override
        public void onSuccess(SimpleCrawerResult responseBody) {
            if (responseBody != null) {
                hideKeybord();
                if (curPage == 1) {
                    photoBeanLists.clear();
                }
                curPage++;
                List<SimpleDataBean> dataBeans = responseBody.getData();
                if (dataBeans != null && dataBeans.size() > 0) {
                    for (SimpleDataBean bean : dataBeans) {
                        if (!TextUtils.isEmpty(bean.getMiddleURL())) {
                            PhotoBean photoBean = new PhotoBean();
                            photoBean.setUrl(bean.getMiddleURL());
                            photoBeanLists.add(photoBean);
                        }
                    }
                }
                photoAdapter.setData(photoBeanLists);
                if (photoBeanLists.size() > 100) {
                    mRecyclerView.setNoMore(true);
                } else {
                    mRecyclerView.setNoMore(false);
                }
                mRecyclerView.refreshComplete(20);
            }
            FileUtil.save("my_stock_images", GsonUtil.objectToJson(responseBody));
        }

        @Override
        public void onError(String result) {
            Log.i("Crawler", "error:" + result);
            mRecyclerView.refreshComplete(20);
        }
    };

    private void hideKeybord(){
        InputMethodManager imm = (InputMethodManager) etSearch.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(),0);
        }
    }

    private void getBaiduPics(String queryWord, int offset) {
        params = new CrawerSimpleParams();
        params.setQueryWord(queryWord);
        params.setWord(queryWord);
        params.setRn("30");
        int pn = 30 * offset;
        params.setPn(pn + "");
        crawlerPresenter.getBaiduImages(params);
        SearchImageHistory searchImageHistory = new SearchImageHistory();
        searchImageHistory.setQueryWord(queryWord);
        StockBuisnessManager.getInstance(this).insertImageHistory(searchImageHistory);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_photo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_done:
                actionDone();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void actionDone() {
        Intent intent = new Intent();
        intent.putExtra("url", photoAdapter.getSelectUrl());
        setResult(RESULT_OK, intent);
        finish();
    }
}
