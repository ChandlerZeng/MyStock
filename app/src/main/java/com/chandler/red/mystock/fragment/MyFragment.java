package com.chandler.red.mystock.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chandler.red.mystock.R;
import com.chandler.red.mystock.SerachActivity;
import com.chandler.red.mystock.activity.ExchangeActivity;
import com.chandler.red.mystock.activity.ImageShowActivity;
import com.chandler.red.mystock.activity.LoginActivity;
import com.chandler.red.mystock.activity.MyInfoActivity;
import com.chandler.red.mystock.db.MyDbManager;
import com.chandler.red.mystock.db.MySqlHelper;
import com.chandler.red.mystock.db.StockBuisnessManager;
import com.chandler.red.mystock.entity.AccStock;
import com.chandler.red.mystock.entity.ExeStock;
import com.chandler.red.mystock.entity.HoldStock;
import com.chandler.red.mystock.entity.MonthStock;
import com.chandler.red.mystock.entity.Stock;
import com.chandler.red.mystock.entity.StockBean;
import com.chandler.red.mystock.manager.ThreadPoolManager;
import com.chandler.red.mystock.presenter.StockPresenter;
import com.chandler.red.mystock.util.Constants;
import com.chandler.red.mystock.util.EncryptUtil;
import com.chandler.red.mystock.util.LogUtil;
import com.chandler.red.mystock.util.TextUtils;
import com.chandler.red.mystock.view.HttpResponseView;
import com.chandler.red.mystock.widget.CircleImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author Zeng
 **/
public class MyFragment extends BaseFragment {

    @BindView(R.id.my_photo)
    CircleImageView myPhoto;
    @BindView(R.id.my_name)
    TextView myName;
    @BindView(R.id.my_number)
    TextView myNumber;
    @BindView(R.id.my_all_money)
    TextView myAllMoney;
    @BindView(R.id.my_all_money_value)
    TextView myAllMoneyValue;
    @BindView(R.id.my_all_profit)
    TextView myAllProfit;
    @BindView(R.id.my_all_profit_value)
    TextView myAllProfitValue;
    @BindView(R.id.my_all_stock)
    TextView myAllStock;
    @BindView(R.id.my_all_stock_value)
    TextView myAllStockValue;
    @BindView(R.id.my_day_profit)
    TextView myDayProfit;
    @BindView(R.id.my_day_profit_value)
    TextView myDayProfitValue;
    @BindView(R.id.my_money_layout)
    RelativeLayout myMoneyLayout;
    @BindView(R.id.my_buy)
    TextView myBuy;
    @BindView(R.id.my_sell)
    TextView mySell;
    @BindView(R.id.my_owner)
    TextView myOwner;
    @BindView(R.id.my_history)
    TextView myHistory;
    @BindView(R.id.my_operate_layout)
    LinearLayout myOperateLayout;
    @BindView(R.id.my_head_layout)
    RelativeLayout myHeadLayout;
    @BindView(R.id.my_month_title)
    TextView myMonthTitle;
    @BindView(R.id.my_month_profit)
    TextView myMonthProfit;
    @BindView(R.id.my_month_profit_rate)
    TextView myMonthProfitRate;
    @BindView(R.id.my_month_vs_sh)
    TextView myMonthVsSh;
    @BindView(R.id.my_month_vs_sh_rate)
    TextView myMonthVsShRate;
    @BindView(R.id.my_month_vs_sz)
    TextView myMonthVsSz;
    @BindView(R.id.my_month_vs_sz_rate)
    TextView myMonthVsSzRate;
    @BindView(R.id.my_month_vs_sh50)
    TextView myMonthVsSh50;
    @BindView(R.id.my_month_vs_sh50_rate)
    TextView myMonthVsSh50Rate;
    @BindView(R.id.my_month_vs_zx500)
    TextView myMonthVsZx500;
    @BindView(R.id.my_month_vs_zx500_rate)
    TextView myMonthVsZx500Rate;
    @BindView(R.id.my_month_vs_dqs)
    TextView myMonthVsDqs;
    @BindView(R.id.my_month_vs_dqs_rate)
    TextView myMonthVsDqsRate;
    @BindView(R.id.my_month_vs_nsdq)
    TextView myMonthVsNsdq;
    @BindView(R.id.my_month_vs_nsdq_rate)
    TextView myMonthVsNsdqRate;
    @BindView(R.id.my_month_vs_hk)
    TextView myMonthVsHk;
    @BindView(R.id.my_month_vs_hk_rate)
    TextView myMonthVsHkRate;
    Unbinder unbinder;

    private static final String TAG = "MY_FRAGMENT";
    private String phone;
    private boolean isLogin;
    SharedPreferences sharedPreferences;

    private String[] stockArray;
    private String list;
    private Timer timer;
    private StockPresenter<ArrayList<Stock>> presenter;
    private StockPresenter<ArrayList<Stock>> exePresenter;
    private List<HoldStock> holdStocks;
    private double totalStockValues;
    private double initValue;
    private double totalValues;
    private double freeMoney;
    private double totalProfit;
    private double dayProfit;
    private String url = Constants.DEFALUT_URL;
    private HttpResponseView<ArrayList<Stock>> httpResponseView = new HttpResponseView<ArrayList<Stock>>() {
        @Override
        public void onSuccess(ArrayList<Stock> responseBody) {
            refreshView(responseBody);
        }

        @Override
        public void onError(String result) {
            Log.i(MySqlHelper.TAG, "errorresult:" + result);
        }
    };

    private void refreshView(ArrayList<Stock> responseBody) {
        totalStockValues = 0;
        dayProfit = 0;
        for (HoldStock holdstock : holdStocks) {
            for (Stock stock : responseBody) {
                if (stock.getId_().equals(holdstock.getNumber())) {
                    holdstock.setCurValue(Double.parseDouble(stock.getNow_()));
                    holdstock.setYesValue(Double.parseDouble(stock.getYest_value()));
                }
            }
            totalStockValues += holdstock.getCurValue() * holdstock.getCount();
            dayProfit += holdstock.getDayProfit();
            Log.i(TAG, "totalStockValues:" + totalStockValues + "  dayProfit:" + dayProfit);
            Log.i(TAG, "holdStock:" + holdstock.toString());
        }

        totalValues = freeMoney + totalStockValues;
        accStock.setCurTotalValue(totalValues);
        accStock.setCurStockValue(totalStockValues);
        totalProfit = totalValues - initValue;
        myAllMoneyValue.setText(String.format(Locale.CHINA, "%.2f", totalValues));
        myAllStockValue.setText(String.format(Locale.CHINA, "%.2f", totalStockValues));
        myAllProfitValue.setText(String.format(Locale.CHINA, "%.2f", totalProfit));
        myDayProfitValue.setText(String.format(Locale.CHINA, "%.2f", dayProfit));
        setTextColor(myAllMoneyValue, totalProfit);
        setTextColor(myAllStockValue, totalProfit);
        setTextColor(myAllProfitValue, totalProfit);
        setTextColor(myDayProfitValue, dayProfit);
        checkMonthValue();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        menu.setGroupVisible(R.menu.menu_main, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_create) {
            showProgressDialog("正在生成股票数据...");
            createShNumber();
            return true;
        } else if (id == R.id.action_search) {
            searchStockData();
        } else if (id == R.id.action_refresh) {
            if (isLogin) {
                refreshStocks();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    String numberlist = "";
    int from = 1;
    int curNumber = 600001;
    int shkcNumber = 688001;
    int szNumber = 1;
    int chuangNumber = 300001;

    private void createShNumber() {
        numberlist = "000001";
        for (int i = 0; i < 50; i++) {
            if (curNumber >= MAX_SH) break;
            if (curNumber >= 602000 && curNumber < 603000) curNumber = 603000;
            numberlist = numberlist + "," + curNumber;
            curNumber++;
        }
        createStockData(numberlist);
    }

    private void createShkcNumber() {
        numberlist = "";
        for (int i = 0; i < 50; i++) {
            if (shkcNumber >= MAX_SHKC) break;
            numberlist = numberlist + "," + shkcNumber;
            shkcNumber++;
        }
        createStockData(numberlist);
    }

    private void createSzNumber() {
        numberlist = "399001";
        for (int i = 0; i < 50; i++) {
            if (szNumber >= MAX_SZ) break;
            if (szNumber < 10) {
                numberlist = numberlist + ",00000" + szNumber;
            } else if (szNumber < 100) {
                numberlist = numberlist + ",0000" + szNumber;
            } else if (szNumber < 1000) {
                numberlist = numberlist + ",000" + szNumber;
            } else {
                numberlist = numberlist + ",00" + szNumber;
            }
            szNumber++;
        }
        createStockData(numberlist);
    }

    private void createChuangNumber() {
        numberlist = "399006";
        for (int i = 0; i < 50; i++) {
            if (chuangNumber >= MAX_CH) break;
            numberlist = numberlist + "," + chuangNumber;
            chuangNumber++;
        }
        createStockData(numberlist);
    }

    private void createStockData(String numbers) {
        String[] nums = numbers.split(",");
        String list = "";
        for (String number : nums) {
            if (number.length() != 6)
                continue;
            if (number.startsWith("6")) {
                number = "s_sh" + number;
            } else if (number.startsWith("0") || number.startsWith("3")) {
                number = "s_sz" + number;
            } else {
                continue;
            }
            list += number + ",";
        }
        list = list.substring(0, list.length() - 1);
        createSinaStockData(list);
    }

    private StockPresenter<ArrayList<StockBean>> stockBeansPresenter;

    public void createSinaStockData(String list) {
        if (stockBeansPresenter == null) {
            stockBeansPresenter = new StockPresenter<>(getActivity(), new ArrayList<StockBean>());
            stockBeansPresenter.onCreate();
            stockBeansPresenter.attachView(httpBeansResponseView);
        }
        stockBeansPresenter.createSinaStocksData(list);
    }

    private HttpResponseView<ArrayList<StockBean>> httpBeansResponseView = new HttpResponseView<ArrayList<StockBean>>() {
        @Override
        public void onSuccess(final ArrayList<StockBean> responseBody) {
            ThreadPoolManager.getSinglePool().execute(new Runnable() {
                @Override
                public void run() {
                    MyDbManager.getInstance(getActivity()).replace(responseBody);
//                                handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessage(1);
                    Log.i(MySqlHelper.TAG, "success curNumber:" + curNumber + " szNumber:" + szNumber + " chuangNumber:" + chuangNumber + " from:" + from);
                }
            });
        }

        @Override
        public void onError(String result) {
            Log.i(MySqlHelper.TAG, "errResult:" + result + " error curNumber:" + curNumber + " szNumber:" + szNumber + " chuangNumber:" + chuangNumber + " from:" + from);
//                        handler.removeCallbacksAndMessages(null);
            handler.sendEmptyMessage(1);
        }
    };
    private final static int MAX_SH = 604000;
    private final static int MAX_SHKC = 688500;
    private final static int MAX_SZ = 3000;
    private final static int MAX_CH = 302000;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (curNumber < MAX_SH) {
                        createShNumber();
                    } else if (szNumber < MAX_SZ) {
                        createSzNumber();
                    } else if (chuangNumber < MAX_CH) {
                        createChuangNumber();
                    } else if (shkcNumber < MAX_SHKC) {
                        createShkcNumber();
                    } else {
                        MyDbManager.getInstance(getActivity()).getAllStocks();
                        if (curNumber >= MAX_SH && szNumber >= MAX_SZ && chuangNumber >= MAX_CH && shkcNumber >= MAX_SHKC) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dimissProgressDialog();
                                    int count = MyDbManager.getInstance(getActivity()).getStockCount();
                                    Toast.makeText(getActivity(), "生成股票数据成功,共：" + count + "支股票", Toast.LENGTH_LONG).show();
                                    Log.i(MySqlHelper.TAG, "curNumber:" + curNumber + " szNumber:" + szNumber + " chuangNumber:" + chuangNumber + " SH");
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dimissProgressDialog();
                                    int count = MyDbManager.getInstance(getActivity()).getStockCount();
                                    Toast.makeText(getActivity(), "生成股票数据失败,共：" + count + "支股票", Toast.LENGTH_LONG).show();
                                    Log.i(MySqlHelper.TAG, "curNumber:" + curNumber + " szNumber:" + szNumber + " chuangNumber:" + chuangNumber);
                                }
                            });
                        }
                    }
                    break;
            }
        }
    };

    private void searchStockData() {
        startActivity(new Intent(getActivity(), SerachActivity.class));
    }

    private void refreshStocks() {
        presenter.querySinaStocks(list);
    }

    public MyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        unbinder = ButterKnife.bind(this, view);
        presenter = new StockPresenter<>(getActivity(), new ArrayList<Stock>());
        presenter.onCreate();
        presenter.attachView(httpResponseView);
        return view;
    }

    private boolean isExeNameAdded;
    private List<ExeStock> exeStockList;

    private void checkIsExeNameAdded() {
        if (!isExeNameAdded) {
            exeStockList = StockBuisnessManager.getInstance(getActivity()).getNullNameExeHoldStocks();
            exePresenter = new StockPresenter<>(getActivity(), new ArrayList<Stock>());
            exePresenter.onCreate();
            exePresenter.attachView(new HttpResponseView<ArrayList<Stock>>() {
                @Override
                public void onSuccess(ArrayList<Stock> responseBody) {
                    for (int i = 0; i < responseBody.size(); i++) {
                        String id = responseBody.get(i).getId_();
                        String number = exeStockList.get(i).getNumber();
                        LogUtil.i("id:" + id + " number:" + number);
                        if (id.contains(number)) {
                            exeStockList.get(i).setName(responseBody.get(i).getName_());
                        }
                    }
                    StockBuisnessManager.getInstance(getActivity()).replaceExchangeList(exeStockList);
                    int size = StockBuisnessManager.getInstance(getActivity()).getNullNameExeHoldStocks().size();
                    if (size == 0) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("is_name_added", true);
                        editor.commit();
                        LogUtil.w("addname success");
                    } else {
                        LogUtil.e("addname fail size:" + size);

                    }

                }

                @Override
                public void onError(String result) {
                    LogUtil.e("addname error:" + result);
                }
            });
            StringBuilder numList = new StringBuilder();
            for (ExeStock holdsBean : exeStockList) {
                numList.append("s_");
                numList.append(holdsBean.getNumber());
                numList.append(",");
            }
            if (numList.length() > 0) {
                numList.deleteCharAt(numList.length() - 1);
            }
            exePresenter.querySimpleSinaStocks(numList.toString());
        }
    }

    private StockPresenter<ArrayList<Stock>> stockPresenter;

    private void checkMonthValue() {
        StockBuisnessManager stockBuisnessManager = StockBuisnessManager.getInstance(getActivity());
        boolean isMyAdded = stockBuisnessManager.isMonthAdded(Constants.MyIndex, System.currentTimeMillis());
        curMyMonthValue = accStock.getCurTotalValue();
        if (!isMyAdded) {
            MonthStock myMonthStock = new MonthStock();
            myMonthStock.setNumber(Constants.MyIndex);
            myMonthStock.setCurValue(curMyMonthValue);
            myMonthStock.setTime(System.currentTimeMillis());
            stockBuisnessManager.insertMonthStock(myMonthStock);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constants.ShIndex);
        stringBuilder.append(",");
        stringBuilder.append(Constants.SzIndex);
        stringBuilder.append(",");
        stringBuilder.append(Constants.Sh50Index);
        stringBuilder.append(",");
        stringBuilder.append(Constants.Zz500Index);
        stringBuilder.append(",");
        stringBuilder.append(Constants.HkIndex);
        stringBuilder.append(",");
        stringBuilder.append(Constants.DqsIndex);
        stringBuilder.append(",");
        stringBuilder.append(Constants.NsdkIndex);
        stockPresenter = new StockPresenter<>(getActivity(), new ArrayList<Stock>());
        stockPresenter.onCreate();
        stockPresenter.attachView(new HttpResponseView<ArrayList<Stock>>() {
            @Override
            public void onSuccess(ArrayList<Stock> responseBody) {
                if (responseBody != null && responseBody.size() > 0)
                    handleMonthResult(responseBody);
            }

            @Override
            public void onError(String result) {
                LogUtil.e(result);
            }
        });
        stockPresenter.queryHeaderSinaStocks(stringBuilder.toString());

    }

    private void handleMonthResult(List<Stock> list) {
        StockBuisnessManager stockBuisnessManager = StockBuisnessManager.getInstance(getActivity());
        boolean isShAdded = stockBuisnessManager.isMonthAdded(Constants.ShIndex, System.currentTimeMillis());
        boolean isSzAdded = stockBuisnessManager.isMonthAdded(Constants.SzIndex, System.currentTimeMillis());
        boolean isSh50Added = stockBuisnessManager.isMonthAdded(Constants.Sh50Index, System.currentTimeMillis());
        boolean isZz500Added = stockBuisnessManager.isMonthAdded(Constants.Zz500Index, System.currentTimeMillis());
        boolean isHkAdded = stockBuisnessManager.isMonthAdded(Constants.HkIndex, System.currentTimeMillis());
        boolean isDqsAdded = stockBuisnessManager.isMonthAdded(Constants.DqsIndex, System.currentTimeMillis());
        boolean isNsdqAdded = stockBuisnessManager.isMonthAdded(Constants.NsdkIndex, System.currentTimeMillis());
        for (Stock stock : list) {
            MonthStock monthStock = new MonthStock();
            double now = Double.parseDouble(stock.getNow_());
            if (Constants.ShIndex.contains(stock.getId_())) {
                curShMonthValue = now;
                if (!isShAdded)
                    monthStock.setNumber(Constants.ShIndex);
            } else if (Constants.SzIndex.contains(stock.getId_())) {
                curSzMonthValue = now;
                if (!isSzAdded)
                    monthStock.setNumber(Constants.SzIndex);
            } else if (Constants.Sh50Index.contains(stock.getId_())) {
                curSh50MonthValue = now;
                if (!isSh50Added)
                    monthStock.setNumber(Constants.Sh50Index);
            } else if (Constants.Zz500Index.contains(stock.getId_())) {
                curZz500MonthValue = now;
                if (!isZz500Added)
                    monthStock.setNumber(Constants.Zz500Index);
            } else if (Constants.HkIndex.contains(stock.getId_())) {
                curHkMonthValue = now;
                if (!isHkAdded)
                    monthStock.setNumber(Constants.HkIndex);
            } else if (Constants.DqsIndex.contains(stock.getId_())) {
                curDqsMonthValue = now;
                if (!isDqsAdded)
                    monthStock.setNumber(Constants.DqsIndex);
            } else if (Constants.NsdkIndex.contains(stock.getId_())) {
                curNsdqMonthValue = now;
                if (!isNsdqAdded)
                    monthStock.setNumber(Constants.NsdkIndex);
            }
            if (!TextUtils.isEmpty(monthStock.getNumber())) {
                monthStock.setTime(System.currentTimeMillis());
                monthStock.setCurValue(Double.parseDouble(stock.getNow_()));
                StockBuisnessManager.getInstance(getActivity()).insertMonthStock(monthStock);
                LogUtil.i("insert:" + monthStock.toString());
            }
        }
        refreshMonthProfitValue();
    }

    private double monthProfitValue;
    private double monthProfitRate;
    private double curMyMonthValue;
    private double shProfitRate;
    private double curShMonthValue;
    private double curSzMonthValue;
    private double curSh50MonthValue;
    private double curZz500MonthValue;
    private double curHkMonthValue;
    private double curDqsMonthValue;
    private double curNsdqMonthValue;
    private double szProfitRate;
    private double sh50ProfitRate;
    private double zz500ProfitRate;
    private double hkProfitRate;
    private double dqsProfitRate;
    private double nsdqProfitRate;

    private void refreshMonthProfitValue() {
        MonthStock myMonth = StockBuisnessManager.getInstance(getActivity()).getCurMonthValue(Constants.MyIndex);
        monthProfitValue = curMyMonthValue - myMonth.getCurValue();
        monthProfitRate = monthProfitValue / myMonth.getCurValue();
        myMonth = StockBuisnessManager.getInstance(getActivity()).getCurMonthValue(Constants.ShIndex);
        shProfitRate = (curShMonthValue - myMonth.getCurValue()) / myMonth.getCurValue();
        myMonth = StockBuisnessManager.getInstance(getActivity()).getCurMonthValue(Constants.SzIndex);
        szProfitRate = (curSzMonthValue - myMonth.getCurValue()) / myMonth.getCurValue();
        myMonth = StockBuisnessManager.getInstance(getActivity()).getCurMonthValue(Constants.Sh50Index);
        sh50ProfitRate = (curSh50MonthValue - myMonth.getCurValue()) / myMonth.getCurValue();
        myMonth = StockBuisnessManager.getInstance(getActivity()).getCurMonthValue(Constants.Zz500Index);
        zz500ProfitRate = (curZz500MonthValue - myMonth.getCurValue()) / myMonth.getCurValue();
        myMonth = StockBuisnessManager.getInstance(getActivity()).getCurMonthValue(Constants.HkIndex);
        hkProfitRate = (curHkMonthValue - myMonth.getCurValue()) / myMonth.getCurValue();
        myMonth = StockBuisnessManager.getInstance(getActivity()).getCurMonthValue(Constants.DqsIndex);
        dqsProfitRate = (curDqsMonthValue - myMonth.getCurValue()) / myMonth.getCurValue();
        myMonth = StockBuisnessManager.getInstance(getActivity()).getCurMonthValue(Constants.NsdkIndex);
        nsdqProfitRate = (curNsdqMonthValue - myMonth.getCurValue()) / myMonth.getCurValue();
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        myMonthTitle.setText(String.format("%d", month));
        myMonthProfit.setText(String.format("%.2f", monthProfitValue));
        myMonthProfitRate.setText(String.format("%.2f", monthProfitRate * 100) + "%");
        myMonthVsSh.setText(String.format("%.2f", shProfitRate * 100) + "%");
        myMonthVsShRate.setText(String.format("%.2f", (monthProfitRate - shProfitRate) * 100) + "%");
        myMonthVsSz.setText(String.format("%.2f", szProfitRate * 100) + "%");
        myMonthVsSzRate.setText(String.format("%.2f", (monthProfitRate - szProfitRate) * 100) + "%");
        myMonthVsSh50.setText(String.format("%.2f", sh50ProfitRate * 100) + "%");
        myMonthVsSh50Rate.setText(String.format("%.2f", (monthProfitRate - sh50ProfitRate) * 100) + "%");
        myMonthVsZx500.setText(String.format("%.2f", zz500ProfitRate * 100) + "%");
        myMonthVsZx500Rate.setText(String.format("%.2f", (monthProfitRate - zz500ProfitRate) * 100) + "%");
        myMonthVsHk.setText(String.format("%.2f", hkProfitRate * 100) + "%");
        myMonthVsHkRate.setText(String.format("%.2f", (monthProfitRate - hkProfitRate) * 100) + "%");
        myMonthVsDqs.setText(String.format("%.2f", dqsProfitRate * 100) + "%");
        myMonthVsDqsRate.setText(String.format("%.2f", (monthProfitRate - dqsProfitRate) * 100) + "%");
        myMonthVsNsdq.setText(String.format("%.2f", nsdqProfitRate * 100) + "%");
        myMonthVsNsdqRate.setText(String.format("%.2f", (monthProfitRate - nsdqProfitRate) * 100) + "%");
        setTextColor(myMonthProfit, monthProfitRate);
        setTextColor(myMonthProfitRate, monthProfitRate);
        setTextColor(myMonthVsSh, shProfitRate);
        setTextColor(myMonthVsShRate, monthProfitRate - shProfitRate);
        setTextColor(myMonthVsSz, szProfitRate);
        setTextColor(myMonthVsSzRate, monthProfitRate - szProfitRate);
        setTextColor(myMonthVsSh50, sh50ProfitRate);
        setTextColor(myMonthVsSh50Rate, monthProfitRate - sh50ProfitRate);
        setTextColor(myMonthVsZx500, zz500ProfitRate);
        setTextColor(myMonthVsZx500Rate, monthProfitRate - zz500ProfitRate);
        setTextColor(myMonthVsHk, hkProfitRate);
        setTextColor(myMonthVsHkRate, monthProfitRate - hkProfitRate);
        setTextColor(myMonthVsDqs, dqsProfitRate);
        setTextColor(myMonthVsDqsRate, monthProfitRate - dqsProfitRate);
        setTextColor(myMonthVsNsdq, nsdqProfitRate);
        setTextColor(myMonthVsNsdqRate, monthProfitRate - nsdqProfitRate);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timer != null)
            timer.cancel();
    }

    AccStock accStock;

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences = getActivity().getSharedPreferences("MY_STOCK_PREF", Activity.MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", null);
        isLogin = sharedPreferences.getBoolean("is_login", false);
        isExeNameAdded = sharedPreferences.getBoolean("is_name_added", false);
        Log.i(TAG, "onResume phone:" + phone + " isLogin:" + isLogin);
        if (isLogin) {
            checkIsExeNameAdded();
            myNumber.setText(phone);
            accStock = StockBuisnessManager.getInstance(getActivity()).getStockAccountByPhone(EncryptUtil.md5WithSalt(phone));
            if (accStock != null) {
                initValue = accStock.getInitValue();
                freeMoney = accStock.getCurValue();
                myName.setText(accStock.getName());
                url = accStock.getPhotoUrl();
            }
            list = StockBuisnessManager.getInstance(getActivity()).getNumberList();
            Log.i(MySqlHelper.TAG, "numberlist:" + list);
            holdStocks = StockBuisnessManager.getInstance(getActivity()).getHoldStocks();
            timer = new Timer("RefreshStocks");
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    refreshStocks();
                }
            }, 500, 29500); // 30 seconds
        } else {
            myName.setText("游客");
            url = Constants.DEFALUT_URL;
            myNumber.setText(Constants.DEFAULT_NUMBER);
            myAllMoneyValue.setText(Constants.DEFAULT_ASSETS);
            myAllStockValue.setText(Constants.DEFAULT_ASSETS);
            myAllProfitValue.setText(Constants.DEFAULT_ASSETS);
            myDayProfitValue.setText(Constants.DEFAULT_ASSETS);
        }
        showImage(url);
    }

    private void setTextColor(TextView tv, double profit) {
        int color;
        if (profit > 0) {
            color = getResources().getColor(R.color.main_red_color);
        } else if (profit == 0) {
            color = getResources().getColor(R.color.main_text_color);
        } else {
            color = getResources().getColor(R.color.main_green_color);
        }
        tv.setTextColor(color);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void showImage(String urlS) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .dontAnimate()
                .placeholder(R.mipmap.pictures_no)
                .error(R.mipmap.pictures_no)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(this).load(urlS)
                .apply(options)
                .into(myPhoto);
    }

    @OnClick({R.id.my_photo, R.id.my_head_layout, R.id.my_money_layout, R.id.my_buy, R.id.my_sell, R.id.my_owner, R.id.my_history})
    public void onViewClicked(View view) {
        if (!isLogin && view.getId() != R.id.my_photo) {
            startActivityForResult(new Intent(getActivity(), LoginActivity.class), 1000);
            return;
        }
        switch (view.getId()) {
            case R.id.my_photo:
                ImageShowActivity.startImageActivity(getActivity(), myPhoto, url);
                break;
            case R.id.my_head_layout:
                Intent intent0 = new Intent(getActivity(), MyInfoActivity.class);
                intent0.putExtra("phone", phone);
                startActivityForResult(intent0, 2000);
                break;
            case R.id.my_money_layout:
                break;
            case R.id.my_buy:
                Intent intent = new Intent(getActivity(), ExchangeActivity.class);
                intent.putExtra("page", 0);
                startActivity(intent);
                break;
            case R.id.my_sell:
                Intent intent1 = new Intent(getActivity(), ExchangeActivity.class);
                intent1.putExtra("page", 1);
                startActivity(intent1);
                break;
            case R.id.my_owner:
                Intent intent2 = new Intent(getActivity(), ExchangeActivity.class);
                intent2.putExtra("page", 2);
                startActivity(intent2);
                break;
            case R.id.my_history:
                Intent intent3 = new Intent(getActivity(), ExchangeActivity.class);
                intent3.putExtra("page", 3);
                startActivity(intent3);
                break;
        }
    }
}
