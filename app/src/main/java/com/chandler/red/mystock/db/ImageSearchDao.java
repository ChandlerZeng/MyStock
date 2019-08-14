package com.chandler.red.mystock.db;

import com.chandler.red.mystock.entity.baidupic.SearchImageHistory;

import java.util.List;

public interface ImageSearchDao {
    void insert(SearchImageHistory searchHistoryBean);
    void replace(SearchImageHistory searchHistoryBean);
    void clearHistory();
    void delete(SearchImageHistory searchHistoryBean);
    boolean isQueryExist(String queryWord);
    List<SearchImageHistory> getSearchHistorys();
    List<SearchImageHistory> getSearchHistorys(String query);
}
