package com.chandler.red.mystock.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chandler.red.mystock.entity.SearchHistoryBean;

import java.util.ArrayList;
import java.util.List;

public class StockSearchDaoImpl implements StockSearchDao {
    private final static String HISTORY_TABLE = "t_search_stock";
    private SQLiteDatabase db;
    private SQLiteOpenHelper sqlHelper;
    private Context context;

    public StockSearchDaoImpl(Context context){
        this.context = context;
        sqlHelper = new MySqlHelper(context,MySqlHelper.DB_NAME,null,MySqlHelper.CUR_VERSION);
        db = sqlHelper.getWritableDatabase();
    }

    @Override
    public void insert(SearchHistoryBean searchHistoryBean) {
        ContentValues values = new ContentValues();
        values.put("number", searchHistoryBean.getNumber());
        db.replace(HISTORY_TABLE,null,values);
    }

    @Override
    public void replace(SearchHistoryBean searchHistoryBean) {
        db.execSQL("replace into t_search_stock(id,number) values(?,?)",
                new Object[]{searchHistoryBean.getId(),searchHistoryBean.getNumber()});
    }

    @Override
    public boolean isNumberExist(String number) {
        String sql = "select number from t_search_stock where number = '"+number+"';";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor==null || !cursor.moveToNext())return false;
        String numberS = cursor.getString(0);
        if(numberS!=null && numberS.equals(number))return true;
        return false;
    }

    @Override
    public void clearHistory() {
        db.execSQL("delete from t_search_stock");
    }

    @Override
    public void delete(SearchHistoryBean searchHistoryBean) {
        db.execSQL("delete from t_search_stock where number = '"+searchHistoryBean.getNumber()+"'");
    }

    @Override
    public List<SearchHistoryBean> getSearchHistorys() {
        List<SearchHistoryBean> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from t_search_stock order by id desc limit 16",null);
        while (cursor.moveToNext()){
            String number = cursor.getString(1);
            SearchHistoryBean searchHistoryBean = new SearchHistoryBean();
            searchHistoryBean.setNumber(number);
            list.add(searchHistoryBean);
        }
        return list;
    }
}
