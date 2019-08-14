package com.chandler.red.mystock.db;

import com.chandler.red.mystock.entity.SearchHistoryBean;

import java.util.List;

public interface StockSearchDao {
    void insert(SearchHistoryBean searchHistoryBean);
    void replace(SearchHistoryBean searchHistoryBean);
    void clearHistory();
    void delete(SearchHistoryBean searchHistoryBean);
    boolean isNumberExist(String number);
    List<SearchHistoryBean> getSearchHistorys();
}
