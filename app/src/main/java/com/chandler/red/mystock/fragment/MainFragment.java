package com.chandler.red.mystock.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.chandler.red.mystock.R;
import com.chandler.red.mystock.SerachActivity;
import com.chandler.red.mystock.StockImgActivity;
import com.chandler.red.mystock.StockImgApi;
import com.chandler.red.mystock.adapter.MainStockListAdapter;
import com.chandler.red.mystock.db.MyDbManager;
import com.chandler.red.mystock.db.MySqlHelper;
import com.chandler.red.mystock.entity.Stock;
import com.chandler.red.mystock.entity.StockBean;
import com.chandler.red.mystock.manager.ThreadPoolManager;
import com.chandler.red.mystock.presenter.StockPresenter;
import com.chandler.red.mystock.util.Constants;
import com.chandler.red.mystock.view.HttpResponseView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Zeng
 **/
public class MainFragment extends BaseFragment implements View.OnClickListener{

    private static HashSet<String> StockHeadIds_ = new HashSet();
    private static List<String> StockIds_ = new ArrayList<>();
    private final static int MAX_SH = 605000;
    private final static int MAX_SHKC = 689500;
    private final static int MAX_SZ = 4000;
    private final static int MAX_CH = 303000;
    private TextView tvDate;
    private Timer timer;
    RecyclerView recyclerView;
    MainStockListAdapter adapter;
    List<Stock> stockList;
    private View rootView;
    private StockPresenter<ArrayList<Stock>> stocksPresenter;
    private StockPresenter<ArrayList<Stock>> stocksHeaderPresenter;
    private StockPresenter<ArrayList<StockBean>> stockBeansPresenter;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        rootView = view;
        initView(view);
        initClickEvent(view);
        return view;
    }

    private HttpResponseView<ArrayList<Stock>> httpResponseView = new HttpResponseView<ArrayList<Stock>>() {
        @Override
        public void onSuccess(ArrayList<Stock> responseBody) {
            updateStockListView(responseBody);
        }

        @Override
        public void onError(String result) {
            Log.e(MySqlHelper.TAG,"请求数据失败");
        }
    };

    private HttpResponseView<ArrayList<Stock>> httpHeaderResponseView = new HttpResponseView<ArrayList<Stock>>() {
        @Override
        public void onSuccess(ArrayList<Stock> responseBody) {
            updateStockHeader(responseBody);
        }

        @Override
        public void onError(String result) {
            Log.e(MySqlHelper.TAG,"请求数据失败");
        }
    };

    private HttpResponseView<ArrayList<StockBean>> httpBeansResponseView = new HttpResponseView<ArrayList<StockBean>>() {
        @Override
        public void onSuccess(final ArrayList<StockBean> responseBody) {
            ThreadPoolManager.getSinglePool().execute(new Runnable() {
                @Override
                public void run() {
                    MyDbManager.getInstance(getActivity()).replace(responseBody);
//                                handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessage(1);
                    Log.i(MySqlHelper.TAG,"success curNumber:"+curNumber+" szNumber:"+szNumber+" chuangNumber:"+chuangNumber+" from:"+from);
                }
            });
        }

        @Override
        public void onError(String result) {
            Log.i(MySqlHelper.TAG,"errResult:"+result+" error curNumber:"+curNumber+" szNumber:"+szNumber+" chuangNumber:"+chuangNumber+" from:"+from);
//                        handler.removeCallbacksAndMessages(null);
            handler.sendEmptyMessage(1);
        }
    };

    private void initView(View view) {
        tvDate = view.findViewById(R.id.tv_date);
        recyclerView = view.findViewById(R.id.recycle_view);
        stockList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MainStockListAdapter(getActivity(), stockList);
        recyclerView.setAdapter(adapter);
        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        recyclerView.addItemDecoration(headersDecor);
        adapter.setOnItemClickLitener(new MainStockListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                String id = stockList.get(position).getId_();
                String url = StockImgApi.IMG_F + id.split("_")[1] + ".gif";
                Intent intent = new Intent(getActivity(), StockImgActivity.class);
                intent.putExtra("type",2);
                intent.putExtra("url", url);
                intent.putExtra("id", id);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                showMyDialog(position);
            }
        });
        stocksPresenter = new StockPresenter<>(getActivity(),new ArrayList<Stock>());
        stocksPresenter.onCreate();
        stocksPresenter.attachView(httpResponseView);
        stocksHeaderPresenter = new StockPresenter<>(getActivity(),new ArrayList<Stock>());
        stocksHeaderPresenter.onCreate();
        stocksHeaderPresenter.attachView(httpHeaderResponseView);
    }

    private void initClickEvent(View view){
        view.findViewById(R.id.sh_layout).setOnClickListener(this);
        view.findViewById(R.id.sz_layout).setOnClickListener(this);
        view.findViewById(R.id.chuang_layout).setOnClickListener(this);
        view.findViewById(R.id.dqs_layout).setOnClickListener(this);
        view.findViewById(R.id.nsdq_layout).setOnClickListener(this);
        view.findViewById(R.id.hk_layout).setOnClickListener(this);
        view.findViewById(R.id.sh50_layout).setOnClickListener(this);
        view.findViewById(R.id.sh300_layout).setOnClickListener(this);
        view.findViewById(R.id.zx_layout).setOnClickListener(this);
    }

    private void refreshTime(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        String mins = min<10?("0"+min):min+"";
        int sec = calendar.get(Calendar.SECOND);
        String secs = sec<10?("0"+sec):sec+"";
        String now = calendar.get(Calendar.YEAR) + "年"
                + (calendar.get(Calendar.MONTH)+1) + "月"//从0计算
                + calendar.get(Calendar.DAY_OF_MONTH) + "日 "
                + "星期"+weekDayFormate(calendar.get(Calendar.DAY_OF_WEEK))
                + "  "+hour+":"+mins+":"+secs;
        tvDate.setText(now);
    }

    private String weekDayFormate(int num){
        String week = "";
        if(num==1){
            week = "日";
        }else if(num==2){
            week = "一";
        }else if(num==3){
            week = "二";
        }else if(num==4){
            week = "三";
        }else if(num==5){
            week = "四";
        }else if(num==6){
            week = "五";
        }else if(num==7){
            week = "六";
        }
        return week;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass

//        saveStocksToPreferences();
        MyDbManager.getInstance(getActivity()).closeDb();

    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        stocksPresenter.onStop();
        stocksHeaderPresenter.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshTime();

        timer = new Timer("RefreshStocks");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessageAtTime(2,1000);
            }
        }, 0, 29000); // 30 seconds
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
//        saveStocksToPreferences();

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu);
        menu.setGroupVisible(R.menu.menu_main,true);
    }

    String numberlist = "";
    int from = 1;
    int curNumber = 600001;
    int shkcNumber = 688001;
    int szNumber = 1;
    int chuangNumber = 300001;
    private void createShNumber(){
        numberlist = "000001";
        for(int i=0;i<50;i++){
            if(curNumber>=MAX_SH)break;
            if(curNumber>=602000 && curNumber<603000)curNumber=603000;
            numberlist = numberlist+","+curNumber;
            curNumber++;
        }
        createStockData(numberlist);
    }

    private void createShkcNumber(){
        numberlist = "";
        for(int i=0;i<50;i++){
            if(shkcNumber>=MAX_SHKC)break;
            numberlist = numberlist+","+shkcNumber;
            shkcNumber++;
        }
        createStockData(numberlist);
    }

    private void createSzNumber(){
        numberlist = "399001";
        for(int i=0;i<50;i++){
            if(szNumber>=MAX_SZ)break;
            if(szNumber<10){
                numberlist = numberlist+",00000"+szNumber;
            }else if(szNumber<100){
                numberlist = numberlist+",0000"+szNumber;
            }else if(szNumber<1000){
                numberlist = numberlist+",000"+szNumber;
            }else {
                numberlist = numberlist+",00"+szNumber;
            }
            szNumber++;
        }
        createStockData(numberlist);
    }

    private void createChuangNumber(){
        numberlist = "399006";
        for(int i=0;i<50;i++){
            if(chuangNumber>=MAX_CH)break;
            numberlist = numberlist+","+chuangNumber;
            chuangNumber++;
        }
        createStockData(numberlist);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_create) {
            showProgressDialog("正在生成股票数据...");
            createShNumber();
            return true;
        }
        else if(id == R.id.action_search){
            searchStockData();
        }
        else if(id == R.id.action_refresh){
            refreshStocks();
        }

        return super.onOptionsItemSelected(item);
    }

    private void createStockData(String numbers){
        String[] nums = numbers.split(",");
        String list = "";
        for(String number:nums){
            if(number.length() != 6)
                continue;
            if (number.startsWith("6")) {
                number = "s_sh" + number;
            } else if (number.startsWith("0") || number.startsWith("3")) {
                number = "s_sz" + number;
            } else{
                continue;
            }
            list += number+",";
        }
        list = list.substring(0,list.length()-1);
        createSinaStockData(list);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if(curNumber<MAX_SH){
                        createShNumber();
                    }else if(szNumber<MAX_SZ){
                        createSzNumber();
                    }else if(chuangNumber<MAX_CH){
                        createChuangNumber();
                    }else if(shkcNumber<MAX_SHKC){
                        createShkcNumber();
                    }else {
                        MyDbManager.getInstance(getActivity()).getAllStocks();
                        if(curNumber>=MAX_SH && szNumber>=MAX_SZ && chuangNumber>=MAX_CH && shkcNumber>=MAX_SHKC){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dimissProgressDialog();
                                    int count = MyDbManager.getInstance(getActivity()).getStockCount();
                                    Toast.makeText(getActivity(),"生成股票数据成功,共："+count+"支股票",Toast.LENGTH_LONG).show();
                                    Log.i(MySqlHelper.TAG,"curNumber:"+curNumber+" szNumber:"+szNumber+" chuangNumber:"+chuangNumber+" SH");
                                }
                            });
                        }else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dimissProgressDialog();
                                    int count = MyDbManager.getInstance(getActivity()).getStockCount();
                                    Toast.makeText(getActivity(),"生成股票数据失败,共："+count+"支股票",Toast.LENGTH_LONG).show();
                                    Log.i(MySqlHelper.TAG,"curNumber:"+curNumber+" szNumber:"+szNumber+" chuangNumber:"+chuangNumber);
                                }
                            });
                        }
                    }
                    break;
                case 2:
                    refreshStocks();
                    break;
                case 3:
                    refreshSelectedStock();
                    break;
                default:

                    break;
            }
        }
    };

    public void createSinaStockData(String list){
        if(stockBeansPresenter==null){
            stockBeansPresenter = new StockPresenter<>(getActivity(),new ArrayList<StockBean>());
            stockBeansPresenter.onCreate();
            stockBeansPresenter.attachView(httpBeansResponseView);
        }
        stockBeansPresenter.createSinaStocksData(list);
    }

    private void searchStockData(){
        startActivity(new Intent(getActivity(),SerachActivity.class));
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(),StockImgActivity.class);
        intent.putExtra("type",1);
        switch (view.getId()){
            case R.id.sh_layout:
                intent.putExtra("url",StockImgApi.SHIMG);
                intent.putExtra("id",Constants.ShIndex);
                break;
            case R.id.sz_layout:
                intent.putExtra("url",StockImgApi.SZIMG);
                intent.putExtra("id",Constants.SzIndex);
                break;
            case R.id.chuang_layout:
                intent.putExtra("url",StockImgApi.CHUANGIMG);
                intent.putExtra("id",Constants.ChuangIndex);
                break;
            case R.id.dqs_layout:
                intent.putExtra("url",StockImgApi.DQS);
                intent.putExtra("id",Constants.DqsIndex);
                break;
            case R.id.nsdq_layout:
                intent.putExtra("url",StockImgApi.NSDQ);
                intent.putExtra("id",Constants.NsdkIndex);
                break;
            case R.id.hk_layout:
                intent.putExtra("url",StockImgApi.HK);
                intent.putExtra("id",Constants.HkIndex);
                break;
            case R.id.sh50_layout:
                intent.putExtra("url",StockImgApi.SH50);
                intent.putExtra("id",Constants.Sh50Index);
                break;
            case R.id.sh300_layout:
                intent.putExtra("url",StockImgApi.SH300);
                intent.putExtra("id",Constants.Sh300Index);
                break;
            case R.id.zx_layout:
                intent.putExtra("url",StockImgApi.ZX);
                intent.putExtra("id",Constants.Zz500Index);
                break;
        }
        startActivity(intent);
    }

    public void querySinaHeadStocks(String list){
        stocksHeaderPresenter.queryHeaderSinaStocks(list);
    }

    public void querySelectedStocks(String list){
        stocksPresenter.querySimpleSinaStocks(list);
    }

    private void refreshStocks(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshTime();
            }
        });
        refreshHeaderStock();
        refreshSelectedStock();
    }

    private void refreshHeaderStock(){
        String idsStr = Constants.ShIndex + "," + Constants.SzIndex + "," + Constants.ChuangIndex+","+Constants.Sh50Index+","+Constants.Sh300Index+","+Constants.Zz500Index
                +","+Constants.DqsIndex+","+Constants.NsdkIndex+"," +Constants.HkIndex;

        String[] ids = idsStr.split(",");
        StockHeadIds_.clear();
        for (String id : ids) {
            StockHeadIds_.add(id);
        }

        String headids = "";
        for (String id : StockHeadIds_){
            headids += id;
            headids += ",";
        }
        querySinaHeadStocks(headids);
    }

    private void refreshSelectedStock(){
        StockIds_.clear();
        List<String> selectids = MyDbManager.getInstance(getActivity()).getSelectedStockIds();
        for(String id:selectids){
            StockIds_.add(id);
        }
        String ids = "";
        for (String id : StockIds_){
            ids += id;
            ids += ",";
        }

        querySelectedStocks(ids);
    }

    private void updateStockHeader(List<Stock> stocks) {
        for (Stock stock : stocks) {
            Double dIncrease = Double.parseDouble(stock.increase);
            Double dPercent = Double.parseDouble(stock.percent);
            String change = String.format("%.2f", dPercent) + "% " + String.format("%.2f", dIncrease);
            if (stock.id_.equals(Constants.ShIndex) || stock.id_.equals(Constants.SzIndex) || stock.id_.equals(Constants.ChuangIndex)
                    || stock.id_.equals(Constants.DqsIndex) || stock.id_.equals(Constants.NsdkIndex) || stock.id_.equals(Constants.HkIndex)
                    || stock.id_.equals(Constants.Sh50Index) || stock.id_.equals(Constants.Sh300Index) || stock.id_.equals(Constants.Zz500Index)) {
                int indexId;
                int changeId;
                int nameId;
                if (stock.id_.equals(Constants.ShIndex)) {
                    indexId = R.id.stock_sh_index;
                    changeId = R.id.stock_sh_change;
                    nameId = R.id.stock_sh_name;
                } else if (stock.id_.equals(Constants.SzIndex)) {
                    indexId = R.id.stock_sz_index;
                    changeId = R.id.stock_sz_change;
                    nameId = R.id.stock_sz_name;
                } else if (stock.id_.equals(Constants.ChuangIndex)) {
                    indexId = R.id.stock_chuang_index;
                    changeId = R.id.stock_chuang_change;
                    nameId = R.id.stock_chuang_name;
                } else if (stock.id_.equals(Constants.DqsIndex)) {
                    indexId = R.id.stock_dqs_index;
                    changeId = R.id.stock_dqs_change;
                    nameId = R.id.stock_dqs_name;
                } else if (stock.id_.equals(Constants.NsdkIndex)) {
                    indexId = R.id.stock_nsdk_index;
                    changeId = R.id.stock_nsdk_change;
                    nameId = R.id.stock_nsdk_name;
                } else if (stock.id_.equals(Constants.HkIndex)) {
                    indexId = R.id.stock_hk_index;
                    changeId = R.id.stock_hk_change;
                    nameId = R.id.stock_hk_name;
                } else if (stock.id_.equals(Constants.Sh50Index)) {
                    indexId = R.id.stock_sh50_index;
                    changeId = R.id.stock_sh50_change;
                    nameId = R.id.stock_sh50_name;
                } else if (stock.id_.equals(Constants.Sh300Index)) {
                    indexId = R.id.stock_sh300_index;
                    changeId = R.id.stock_sh300_change;
                    nameId = R.id.stock_sh300_name;
                } else {
                    indexId = R.id.stock_zx_index;
                    changeId = R.id.stock_zx_change;
                    nameId = R.id.stock_zx_name;
                }
                TextView indexText = rootView.findViewById(indexId);
                TextView changeText = rootView.findViewById(changeId);
                TextView nameText = rootView.findViewById(nameId);
                nameText.setText(stock.name_);
                indexText.setText(stock.now_);
                int color = getResources().getColor(R.color.main_text_color);
                if (dIncrease > 0) {
                    color = getResources().getColor(R.color.main_red_color);
                } else if (dIncrease < 0) {
                    color = getResources().getColor(R.color.main_green_color);
                }
                indexText.setTextColor(color);
                changeText.setTextColor(color);
                changeText.setText(change);

                continue;
            }
        }
    }

    public void updateStockListView(List<Stock> stocks){
        List<String> topIds = MyDbManager.getInstance(getActivity()).getTopStockIds();
        for(Stock stock:stocks){
            if(topIds.contains(stock.getId_())){
                stock.setTop(true);
            }else {
                stock.setTop(false);
            }
        }
        stockList = stocks;
        adapter.setData(stockList);
    }

    private void showMyDialog(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_layout,null);
        TextView tvTop = view.findViewById(R.id.tv_top);
        TextView tvDel = view.findViewById(R.id.tv_delete);
        final String id = stockList.get(position).getId_();
        final boolean isTop = stockList.get(position).isTop();
        if(isTop){
            tvTop.setText("取消置顶");
        }else {
            tvTop.setText("置顶股票");
        }
        builder.setView(view);
        final AlertDialog alertDialog=builder.show();
        WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
        lp.width = getResources().getDimensionPixelSize(R.dimen.dialog_width);
        lp.height = getResources().getDimensionPixelSize(R.dimen.dialog_height);
        alertDialog.getWindow().setAttributes(lp);

        tvTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if(isTop){
                    MyDbManager.getInstance(getActivity()).updateStockTopTime(id,0);
                }else {
                    long time = System.currentTimeMillis();
                    MyDbManager.getInstance(getActivity()).updateStockTopTime(id,time);
                }
                handler.removeCallbacksAndMessages(null);
                handler.sendEmptyMessageAtTime(3,200);
            }
        });
        tvDel.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                MyDbManager.getInstance(getActivity()).clearSelected(id);
                stockList.remove(position);
                adapter.setData(stockList);
                Toast.makeText(getActivity(), "已删除", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
