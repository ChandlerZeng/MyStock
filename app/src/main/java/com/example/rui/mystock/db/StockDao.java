package com.example.rui.mystock.db;

import com.example.rui.mystock.entity.StockBean;

import java.util.List;

public interface StockDao {
    void replace(StockBean stock);
    void replace(List<StockBean> stocks);
    void insert(StockBean stock);
    void insert(List<StockBean> stocks);
    void updateStockByNumber(String number,StockBean stock);
    void updateStockTopTime(String number,long time);
    void deleteByNumber(String number);
    StockBean getStockByNumberOrName(String numberOrName);
    List<StockBean> searchStockByNumberOrName(String numberOrName,int offset,int limit);
    List<StockBean> getAllStocks();
    int getStockCount();
    int getSearchStockCount();
    void closeDb();
    List<String> getSelectedStockIds();
    List<String> getTopStockIds();
    boolean clearSelected(String id);
    boolean addSelected(String id);
    boolean isSelected(String id);
}
