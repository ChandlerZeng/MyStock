package com.chandler.red.mystock.db;

import com.chandler.red.mystock.entity.AccStock;

public interface StockAccountDao {
    void insert(AccStock accStock);
    void replace(AccStock accStock);
    boolean isAccountExist(String phone);
    AccStock getStockAccountByPhone(String phone);
    int getAccId(String phone);
}
