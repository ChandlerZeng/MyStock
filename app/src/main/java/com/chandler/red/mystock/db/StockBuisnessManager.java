package com.chandler.red.mystock.db;

import android.content.Context;

import com.chandler.red.mystock.entity.AccStock;
import com.chandler.red.mystock.entity.ExeStock;
import com.chandler.red.mystock.entity.HoldStock;
import com.chandler.red.mystock.entity.HoldsBean;
import com.chandler.red.mystock.entity.MonthStock;
import com.chandler.red.mystock.entity.SearchHistoryBean;
import com.chandler.red.mystock.entity.baidupic.SearchImageHistory;

import java.util.List;

public class StockBuisnessManager {
    private static StockBuisnessManager instance;
    private StockAccountDao stockAccountDao;
    private StockExchangeDao stockExchangeDao;
    private StockSearchDao stockSearchDao;
    private ImageSearchDao imageSearchDao;
    private StockMonthDao stockMonthDao;

    private StockBuisnessManager(Context context){
        stockAccountDao = new StockAccountDaoImpl(context);
        stockExchangeDao = new StockExchangeDaoImpl(context);
        stockSearchDao = new StockSearchDaoImpl(context);
        imageSearchDao = new ImageSearchDaoImpl(context);
        stockMonthDao = new StockMonthDaoImpl(context);
    }

    public static StockBuisnessManager getInstance(Context context){
        if(instance==null){
            synchronized (StockBuisnessManager.class){
                if(instance==null){
                    instance = new StockBuisnessManager(context);
                }
            }
        }
        return instance;
    }

    public void insertAccount(AccStock accStock){
        stockAccountDao.insert(accStock);
    }

    public boolean isAccountExist(String phone){
        return stockAccountDao.isAccountExist(phone);
    }

    public void replaceAccount(AccStock accStock){
        stockAccountDao.replace(accStock);
    }

    public AccStock getStockAccountByPhone(String phone){
        return stockAccountDao.getStockAccountByPhone(phone);
    }

    public String getNumberList(){
        return stockExchangeDao.getNumberList();
    }

    public List<HoldStock> getHoldStocks(){
        return stockExchangeDao.getHoldStocks();
    }

    public List<HoldsBean> getExeHoldStocks(){
        return stockExchangeDao.getExeHoldStocks();
    }

    public List<ExeStock> getAllExeHoldStocks(){
        return stockExchangeDao.getAllExeHoldStocks();
    }

    public List<ExeStock> getNullNameExeHoldStocks(){
        return stockExchangeDao.getNullNameExeHoldStocks();
    }

    public int getAccId(String phone){
        return stockAccountDao.getAccId(phone);
    }

    public void insertExchange(ExeStock exeStock){
        stockExchangeDao.insert(exeStock);
    }

    public void replaceExchange(ExeStock exeStock){
        stockExchangeDao.replace(exeStock);
    }

    public void replaceExchangeList(List<ExeStock> exeStockList){
        stockExchangeDao.replace(exeStockList);
    }

    public ExeStock getStockExchangeById(String id){
        return stockExchangeDao.getStockExchangeById(id);
    }

    public void insertSearchHistory(SearchHistoryBean searchHistoryBean){
        if(!stockSearchDao.isNumberExist(searchHistoryBean.getNumber())){
            stockSearchDao.insert(searchHistoryBean);
        }
    }

    public void replaceHistory(SearchHistoryBean searchHistoryBean){
        stockSearchDao.replace(searchHistoryBean);
    }

    public void clearHistory(){
        stockSearchDao.clearHistory();
    }

    public void deleteHistory(SearchHistoryBean searchHistoryBean){
        stockSearchDao.delete(searchHistoryBean);
    }

    public List<SearchHistoryBean> getSearchHistorys(){
        return stockSearchDao.getSearchHistorys();
    }

    public void insertImageHistory(SearchImageHistory searchHistoryBean){
        if(!imageSearchDao.isQueryExist(searchHistoryBean.getQueryWord())){
            imageSearchDao.insert(searchHistoryBean);
        }
    }

    public void replaceImageHistory(SearchImageHistory searchHistoryBean){
        imageSearchDao.replace(searchHistoryBean);
    }

    public void clearImageHistory(){
        imageSearchDao.clearHistory();
    }

    public void deleteImageHistory(SearchImageHistory searchHistoryBean){
        imageSearchDao.delete(searchHistoryBean);
    }

    public List<SearchImageHistory> getImageHistorys(){
        return imageSearchDao.getSearchHistorys();
    }

    public List<SearchImageHistory> getImageHistorys(String queryWord){
        return imageSearchDao.getSearchHistorys(queryWord);
    }

    public void insertMonthStock(MonthStock monthStock){
        stockMonthDao.insert(monthStock);
    }

    public boolean isMonthAdded(String number,long time){
        return stockMonthDao.isAdded(number,time);
    }

    public MonthStock getCurMonthValue(String myNumber){
        return stockMonthDao.getCurMonthValue(myNumber);
    }

}
