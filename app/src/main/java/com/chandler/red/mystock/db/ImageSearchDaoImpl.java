package com.chandler.red.mystock.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chandler.red.mystock.entity.baidupic.SearchImageHistory;

import java.util.ArrayList;
import java.util.List;

public class ImageSearchDaoImpl implements ImageSearchDao {
    private final static String HISTORY_TABLE = "t_search_image";
    private SQLiteDatabase db;
    private SQLiteOpenHelper sqlHelper;
    private Context context;

    public ImageSearchDaoImpl(Context context){
        this.context = context;
        sqlHelper = new MySqlHelper(context,MySqlHelper.DB_NAME,null,MySqlHelper.CUR_VERSION);
        db = sqlHelper.getWritableDatabase();
    }

    @Override
    public void insert(SearchImageHistory searchHistoryBean) {
        ContentValues values = new ContentValues();
        values.put("query_word", searchHistoryBean.getQueryWord());
        db.replace(HISTORY_TABLE,null,values);
    }

    @Override
    public void replace(SearchImageHistory searchHistoryBean) {
        db.execSQL("replace into t_search_image(id,query_word) values(?,?)",
                new Object[]{searchHistoryBean.getId(),searchHistoryBean.getQueryWord()});
    }

    @Override
    public boolean isQueryExist(String queryWord) {
        String sql = "select query_word from t_search_image where query_word = '"+queryWord+"';";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor==null || !cursor.moveToNext())return false;
        String query = cursor.getString(0);
        if(query!=null && query.equals(queryWord))return true;
        return false;
    }

    @Override
    public void clearHistory() {
        db.execSQL("delete from t_search_image");
    }

    @Override
    public void delete(SearchImageHistory searchHistoryBean) {
        db.execSQL("delete from t_search_image where query_word = '"+searchHistoryBean.getQueryWord()+"'");
    }

    @Override
    public List<SearchImageHistory> getSearchHistorys() {
        List<SearchImageHistory> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from t_search_image order by id desc limit 16",null);
        while (cursor.moveToNext()){
            String query = cursor.getString(1);
            SearchImageHistory searchHistoryBean = new SearchImageHistory();
            searchHistoryBean.setQueryWord(query);
            list.add(searchHistoryBean);
        }
        return list;
    }

    @Override
    public List<SearchImageHistory> getSearchHistorys(String queryWord) {
        List<SearchImageHistory> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from t_search_image where query_word like '"+queryWord+"%' order by id desc limit 16",null);
        while (cursor.moveToNext()){
            String query = cursor.getString(1);
            SearchImageHistory searchHistoryBean = new SearchImageHistory();
            searchHistoryBean.setQueryWord(query);
            list.add(searchHistoryBean);
        }
        return list;
    }
}
