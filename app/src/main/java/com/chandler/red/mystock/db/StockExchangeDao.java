package com.chandler.red.mystock.db;

import com.chandler.red.mystock.entity.ExeStock;
import com.chandler.red.mystock.entity.HoldStock;
import com.chandler.red.mystock.entity.HoldsBean;

import java.util.List;

public interface StockExchangeDao {
    void insert(ExeStock exeStock);
    void replace(ExeStock exeStock);
    void replace(List<ExeStock> exeStockList);
    String getNumberList();
    ExeStock getStockExchangeById(String id);
    List<HoldStock> getHoldStocks();
    List<HoldsBean> getExeHoldStocks();
    List<ExeStock> getAllExeHoldStocks();
    List<ExeStock> getNullNameExeHoldStocks();
}
