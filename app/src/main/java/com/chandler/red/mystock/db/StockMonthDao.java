package com.chandler.red.mystock.db;

import com.chandler.red.mystock.entity.MonthStock;

public interface StockMonthDao {
    void insert(MonthStock monthStock);
    boolean isAdded(String number,long time);
    MonthStock getCurMonthValue(String myNumber);

}
