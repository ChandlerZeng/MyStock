package com.example.rui.mystock.db;

import android.content.Context;

import com.example.rui.mystock.entity.StockBean;

import java.util.List;

public class MyDbManager {
    public static MyDbManager instance;
    private StockDao stockDao;

    private MyDbManager(Context context){
        stockDao = new StockDaoImpl(context);
    }

    public static MyDbManager getInstance(Context context){
        if(instance==null){
            synchronized (MyDbManager.class){
                if(instance==null){
                    instance = new MyDbManager(context);
                }
            }
        }
        return instance;
    }
    public void replace(StockBean stock){
        stockDao.replace(stock);
    }
    public void replace(List<StockBean> stocks){
        stockDao.replace(stocks);
    }

    public void insert(StockBean stock){
        stockDao.insert(stock);
    }
    public void insert(List<StockBean> stocks){
        stockDao.insert(stocks);
    }
    public void updateStockByNumber(String number,StockBean stock){
        stockDao.updateStockByNumber(number,stock);
    }
    public void deleteByNumber(String number){
        stockDao.deleteByNumber(number);
    }
    public StockBean getStockByNumberOrName(String numberOrName){
        return stockDao.getStockByNumberOrName(numberOrName);
    }
    public List<StockBean> searchStockByNumberOrName(String numberOrName,int offset,int limit){
        return stockDao.searchStockByNumberOrName(numberOrName,offset,limit);
    }
    public List<StockBean> getAllStocks(){
        return stockDao.getAllStocks();
    }
    public int getStockCount(){
        return stockDao.getStockCount();
    }
    public void closeDb(){
        stockDao.closeDb();
    }

    public List<String> getSelectedStockIds(){
        return stockDao.getSelectedStockIds();
    }
    public boolean clearSelected(String id){
        return stockDao.clearSelected(id);
    }

    public boolean addSelected(String id){
        return stockDao.addSelected(id);
    }
    public boolean isSelected(String id){
        return stockDao.isSelected(id);
    }

    public List<String> getTopStockIds(){
        return stockDao.getTopStockIds();
    }

    public void updateStockTopTime(String number,long time) {
        stockDao.updateStockTopTime(number,time);
    }

    public int getSearchStockCount(){
        return stockDao.getSearchStockCount();
    }
}
