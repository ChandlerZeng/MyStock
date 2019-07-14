package com.example.rui.mystock;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rui.mystock.adapter.MainStockListAdapter;
import com.example.rui.mystock.db.MyDbManager;
import com.example.rui.mystock.db.MySqlHelper;
import com.example.rui.mystock.entity.Stock;
import com.example.rui.mystock.entity.StockBean;
import com.example.rui.mystock.manager.ThreadPoolManager;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static HashSet<String> StockHeadIds_ = new HashSet();
    private static List<String> StockIds_ = new ArrayList<>();
    private final static String ShIndex = "s_sh000001";
    private final static String SzIndex = "s_sz399001";
    private final static String ChuangIndex = "s_sz399006";
    private final static String Sh50Index = "s_sh000016";
    private final static String Sh300Index = "s_sh000300";
    private final static String ZXIndex = "s_sz399005";
    private final static String DqsIndex = "gb_$dji";
    private final static String NsdkIndex = "gb_ixic";
    private final static String HkIndex = "rt_hkHSI";
    private final static int MAX_SH = 604000;
    private final static int MAX_SZ = 3000;
    private final static int MAX_CH = 302000;
    private TextView tvDate;
    private Timer timer;
    RecyclerView recyclerView;
    MainStockListAdapter adapter;
    List<Stock> stockList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initClickEvent();
    }

    private void initView() {
        tvDate = findViewById(R.id.tv_date);
        recyclerView = findViewById(R.id.recycle_view);
        stockList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainStockListAdapter(this, stockList);
        recyclerView.setAdapter(adapter);
        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        recyclerView.addItemDecoration(headersDecor);
        adapter.setOnItemClickLitener(new MainStockListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                String id = stockList.get(position).getId_();
                String url = StockImgApi.IMG_F + id.split("_")[1] + ".gif";
                Intent intent = new Intent(MainActivity.this, StockImgActivity.class);
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
    }

    private void initClickEvent(){
        findViewById(R.id.sh_layout).setOnClickListener(this);
        findViewById(R.id.sz_layout).setOnClickListener(this);
        findViewById(R.id.chuang_layout).setOnClickListener(this);
        findViewById(R.id.dqs_layout).setOnClickListener(this);
        findViewById(R.id.nsdq_layout).setOnClickListener(this);
        findViewById(R.id.hk_layout).setOnClickListener(this);
        findViewById(R.id.sh50_layout).setOnClickListener(this);
        findViewById(R.id.sh300_layout).setOnClickListener(this);
        findViewById(R.id.zx_layout).setOnClickListener(this);
    }

    private void refreshTime(){
        Calendar calendar = Calendar.getInstance();
        String now = calendar.get(Calendar.YEAR) + "年"
                + (calendar.get(Calendar.MONTH)+1) + "月"//从0计算
                + calendar.get(Calendar.DAY_OF_MONTH) + "日 "
                + "星期"+weekDayFormate(calendar.get(Calendar.DAY_OF_WEEK))
                + "  "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
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
        MyDbManager.getInstance(this).closeDb();

    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
        queue.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTime();

        timer = new Timer("RefreshStocks");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessageAtTime(2,500);
            }
        }, 0, 9500); // 10 seconds
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
//        saveStocksToPreferences();

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    String numberlist = "";
    int from = 1;
    int curNumber = 600001;
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
        querySinaStock(list);
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
                    }else {
                        MyDbManager.getInstance(MainActivity.this).getAllStocks();
                        if(curNumber>=MAX_SH && szNumber>=MAX_SZ && chuangNumber>=MAX_CH){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dimissProgressDialog();
                                    int count = MyDbManager.getInstance(MainActivity.this).getStockCount();
                                    Toast.makeText(MainActivity.this,"生成股票数据成功,共："+count+"支股票",Toast.LENGTH_LONG).show();
                                    Log.i(MySqlHelper.TAG,"curNumber:"+curNumber+" szNumber:"+szNumber+" chuangNumber:"+chuangNumber);
                                }
                            });
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dimissProgressDialog();
                                    int count = MyDbManager.getInstance(MainActivity.this).getStockCount();
                                    Toast.makeText(MainActivity.this,"生成股票数据失败,共："+count+"支股票",Toast.LENGTH_LONG).show();
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

    public void querySinaStock(String list){
        // Instantiate the RequestQueue.
        if(queue==null)
            queue = Volley.newRequestQueue(this);
        String url ="http://hq.sinajs.cn/list=" + list;
        //http://hq.sinajs.cn/list=sh600000,sh600536

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        ThreadPoolManager.getSinglePool().execute(new Runnable() {
                            @Override
                            public void run() {
                                MyDbManager.getInstance(MainActivity.this).replace(responseToStocks(response));
//                                handler.removeCallbacksAndMessages(null);
                                handler.sendEmptyMessage(1);
                                Log.i(MySqlHelper.TAG,"success curNumber:"+curNumber+" szNumber:"+szNumber+" chuangNumber:"+chuangNumber+" from:"+from);
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(from == 1){
                            from = 2;
                        }else if(from ==2){
                            from = 3;
                        }else if(from ==3){
                            from = 4;
                        }
                        Log.i(MySqlHelper.TAG,"error curNumber:"+curNumber+" szNumber:"+szNumber+" chuangNumber:"+chuangNumber+" from:"+from);
//                        handler.removeCallbacksAndMessages(null);
                        handler.sendEmptyMessage(1);
                    }
                });

        queue.add(stringRequest);
        queue.start();
    }

    public List<StockBean> responseToStocks(String response){
        response = response.replaceAll("\n", "");
        String[] stocks = response.split(";");

        List<StockBean> stockList = new ArrayList<>();
        for(String stock : stocks) {
            String[] leftRight = stock.split("=");
            if (leftRight.length < 2)
                continue;

            String right = leftRight[1].replaceAll("\"", "");
            if (right.isEmpty())
                continue;

            String left = leftRight[0];
            if (left.isEmpty())
                continue;
            String[] lefts = left.split("_");
            String number = lefts[2]+"_"+lefts[3];
            String name = "";
            String value = "";
            String[] values = right.split(",");
            try{
                if(number.equals(ShIndex) || number.equals(SzIndex) || number.equals(ChuangIndex)){
                    name = values[0];
                    value = values[1];
                }else if(number.equals(DqsIndex) || number.equals(NsdkIndex)){
                    name = values[0];
                    value = values[1];
                }else if(number.equals(HkIndex)){
                    name = values[0];
                    value = values[6];
                }else {
                    name = values[0];
                    value = values[1];
                }

            }catch (ArrayIndexOutOfBoundsException e){
                Log.e("MainActivity",e.toString());
            }
            StockBean stockNow = new StockBean(number,name,value);
            StockBean  stockBean= MyDbManager.getInstance(this).getStockByNumberOrName(number);
            if(stockBean!=null){
                stockNow.setSelected(stockBean.isSelected());
                stockNow.setTopTime(stockBean.getTopTime());
            }
            stockList.add(stockNow);
        }

        return stockList;
    }

    private void searchStockData(){
        startActivity(new Intent(MainActivity.this,SerachActivity.class));
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this,StockImgActivity.class);
        intent.putExtra("type",1);
        switch (view.getId()){
            case R.id.sh_layout:
                intent.putExtra("url",StockImgApi.SHIMG);
                intent.putExtra("id",ShIndex);
                break;
            case R.id.sz_layout:
                intent.putExtra("url",StockImgApi.SZIMG);
                intent.putExtra("id",SzIndex);
                break;
            case R.id.chuang_layout:
                intent.putExtra("url",StockImgApi.CHUANGIMG);
                intent.putExtra("id",ChuangIndex);
                break;
            case R.id.dqs_layout:
                intent.putExtra("url",StockImgApi.DQS);
                intent.putExtra("id",DqsIndex);
                break;
            case R.id.nsdq_layout:
                intent.putExtra("url",StockImgApi.NSDQ);
                intent.putExtra("id",NsdkIndex);
                break;
            case R.id.hk_layout:
                intent.putExtra("url",StockImgApi.HK);
                intent.putExtra("id",HkIndex);
                break;
            case R.id.sh50_layout:
                intent.putExtra("url",StockImgApi.SH50);
                intent.putExtra("id",Sh50Index);
                break;
            case R.id.sh300_layout:
                intent.putExtra("url",StockImgApi.SH300);
                intent.putExtra("id",Sh300Index);
                break;
            case R.id.zx_layout:
                intent.putExtra("url",StockImgApi.ZX);
                intent.putExtra("id",ZXIndex);
                break;
        }
        startActivity(intent);
    }

    // 浦发银行,15.06,15.16,15.25,15.27,14.96,15.22,15.24,205749026,3113080980,
    // 51800,15.22,55979,15.21,1404740,15.20,1016176,15.19,187800,15.18,300,15.24,457700,15.25,548900,15.26,712266,15.27,1057960,15.28,2015-09-10,15:04:07,00

    public List<Stock> sinaResponseToStocks(String response){
        response = response.replaceAll("\n", "");
        String[] stocks = response.split(";");

        List<Stock> stockBeanList = new ArrayList<>();
        for(String stock : stocks) {
            String[] leftRight = stock.split("=");
            if (leftRight.length < 2)
                continue;

            String right = leftRight[1].replaceAll("\"", "");
            if (right.isEmpty())
                continue;

            String left = leftRight[0];
            if (left.isEmpty())
                continue;

            Stock stockNow = new Stock();
            String[] lefts = left.split("_");
            stockNow.id_ = lefts[2]+"_"+lefts[3];

            String[] values = right.split(",");
            try{
                if(stockNow.id_.equals(ShIndex) || stockNow.id_.equals(SzIndex) || stockNow.id_.equals(ChuangIndex)){
                    stockNow.name_ = values[0];
                    stockNow.now_ = values[1];
                    stockNow.increase = values[2];
                    stockNow.percent = values[3];
                }else if(stockNow.id_.equals(DqsIndex) || stockNow.id_.equals(NsdkIndex)){
                    stockNow.name_ = values[0];
                    stockNow.now_ = values[1];
                    stockNow.increase = values[4];
                    stockNow.percent = values[2];
                }else if(stockNow.id_.equals(HkIndex)){
                    stockNow.name_ = values[1];
                    stockNow.now_ = values[6];
                    stockNow.increase = values[7];
                    stockNow.percent = values[8];
                }else {
                    stockNow.name_ = values[0];
                    stockNow.now_ = values[1];
                    stockNow.increase = values[2];
                    stockNow.percent = values[3];
                }

            }catch (ArrayIndexOutOfBoundsException e){
                Log.e("MainActivity",e.toString());
            }

            stockBeanList.add(stockNow);
        }

        return stockBeanList;
    }

    RequestQueue queue;
    public void querySinaHeadStocks(String list){
        // Instantiate the RequestQueue.
        if(queue==null)
            queue = Volley.newRequestQueue(this);
        String url ="http://hq.sinajs.cn/list=" + list;
        //http://hq.sinajs.cn/list=sh600000,sh600536

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        updateStockHeader(sinaResponseToStocks(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(MainActivity.this,"数据请求失败",Toast.LENGTH_LONG).show();
                        Log.e(MySqlHelper.TAG,"请求数据失败");
                    }
                });

        queue.add(stringRequest);
        queue.start();
    }

    public void querySinaStocks(String list){
        // Instantiate the RequestQueue.
        if(queue==null)
            queue = Volley.newRequestQueue(this);
        String url ="http://hq.sinajs.cn/list=" + list;
        //http://hq.sinajs.cn/list=sh600000,sh600536

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        updateStockListView(sinaResponseToStocks(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MySqlHelper.TAG,"请求数据失败");
                    }
                });

        queue.add(stringRequest);
        queue.start();
    }

    private void refreshStocks(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshTime();
            }
        });
        refreshHeaderStock();
        refreshSelectedStock();
    }

    private void refreshHeaderStock(){
        String idsStr = ShIndex + "," + SzIndex + "," + ChuangIndex+","+Sh50Index+","+Sh300Index+","+ZXIndex
                +","+DqsIndex+","+NsdkIndex+"," +HkIndex;

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
        List<String> selectids = MyDbManager.getInstance(this).getSelectedStockIds();
        for(String id:selectids){
            StockIds_.add(id);
        }
        String ids = "";
        for (String id : StockIds_){
            ids += id;
            ids += ",";
        }

        querySinaStocks(ids);
    }

    private void updateStockHeader(List<Stock> stocks) {
        for (Stock stock : stocks) {
            Double dIncrease = Double.parseDouble(stock.increase);
            Double dPercent = Double.parseDouble(stock.percent);
            String change = String.format("%.2f", dPercent) + "% " + String.format("%.2f", dIncrease);
            if (stock.id_.equals(ShIndex) || stock.id_.equals(SzIndex) || stock.id_.equals(ChuangIndex)
                    || stock.id_.equals(DqsIndex) || stock.id_.equals(NsdkIndex) || stock.id_.equals(HkIndex)
                    || stock.id_.equals(Sh50Index) || stock.id_.equals(Sh300Index) || stock.id_.equals(ZXIndex)) {
                int indexId;
                int changeId;
                int nameId;
                if (stock.id_.equals(ShIndex)) {
                    indexId = R.id.stock_sh_index;
                    changeId = R.id.stock_sh_change;
                    nameId = R.id.stock_sh_name;
                } else if (stock.id_.equals(SzIndex)) {
                    indexId = R.id.stock_sz_index;
                    changeId = R.id.stock_sz_change;
                    nameId = R.id.stock_sz_name;
                } else if (stock.id_.equals(ChuangIndex)) {
                    indexId = R.id.stock_chuang_index;
                    changeId = R.id.stock_chuang_change;
                    nameId = R.id.stock_chuang_name;
                } else if (stock.id_.equals(DqsIndex)) {
                    indexId = R.id.stock_dqs_index;
                    changeId = R.id.stock_dqs_change;
                    nameId = R.id.stock_dqs_name;
                } else if (stock.id_.equals(NsdkIndex)) {
                    indexId = R.id.stock_nsdk_index;
                    changeId = R.id.stock_nsdk_change;
                    nameId = R.id.stock_nsdk_name;
                } else if (stock.id_.equals(HkIndex)) {
                    indexId = R.id.stock_hk_index;
                    changeId = R.id.stock_hk_change;
                    nameId = R.id.stock_hk_name;
                } else if (stock.id_.equals(Sh50Index)) {
                    indexId = R.id.stock_sh50_index;
                    changeId = R.id.stock_sh50_change;
                    nameId = R.id.stock_sh50_name;
                } else if (stock.id_.equals(Sh300Index)) {
                    indexId = R.id.stock_sh300_index;
                    changeId = R.id.stock_sh300_change;
                    nameId = R.id.stock_sh300_name;
                } else {
                    indexId = R.id.stock_zx_index;
                    changeId = R.id.stock_zx_change;
                    nameId = R.id.stock_zx_name;
                }
                TextView indexText = findViewById(indexId);
                TextView changeText = findViewById(changeId);
                TextView nameText = findViewById(nameId);
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
        List<String> topIds = MyDbManager.getInstance(this).getTopStockIds();
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout,null);
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
                    MyDbManager.getInstance(MainActivity.this).updateStockTopTime(id,0);
                }else {
                    long time = System.currentTimeMillis();
                    MyDbManager.getInstance(MainActivity.this).updateStockTopTime(id,time);
                }
                handler.removeCallbacksAndMessages(null);
                handler.sendEmptyMessage(3);
            }
        });
        tvDel.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                MyDbManager.getInstance(MainActivity.this).clearSelected(id);
                stockList.remove(position);
                adapter.setData(stockList);
                Toast.makeText(MainActivity.this, "已删除", Toast.LENGTH_SHORT).show();
            }
        });
    }

    ProgressDialog mProgressDialog;
    protected void showProgressDialog(String msg){
        try{
            showProgressDialog(msg,true,true);
        }catch (Exception e){
        }
    }
    protected void showProgressDialog(String msg,boolean isCancelOutside){
        showProgressDialog(msg,true,isCancelOutside);
    }

    protected void showProgressDialog(String msg, boolean isCancelable,boolean isCancelOutside){
        if(mProgressDialog==null){
            mProgressDialog = new ProgressDialog(this);
        }
        if(mProgressDialog.isShowing()){
            return;
        }
        mProgressDialog.setMessage(msg);
        mProgressDialog.setCancelable(isCancelable);
        mProgressDialog.setCanceledOnTouchOutside(isCancelable);
        mProgressDialog.show();
    }

    protected void dimissProgressDialog(){
        if(mProgressDialog!=null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }
}
