package com.chandler.red.mystock.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chandler.red.mystock.entity.MonthStock;
import com.chandler.red.mystock.util.DateUtil;

public class StockMonthDaoImpl implements StockMonthDao{

    private final static String MONTH_TABLE = "t_month_stock";
    private SQLiteDatabase db;
    private SQLiteOpenHelper sqlHelper;
    public StockMonthDaoImpl(Context context){
        sqlHelper = new MySqlHelper(context,MySqlHelper.DB_NAME,null,MySqlHelper.CUR_VERSION);
        db = sqlHelper.getWritableDatabase();
    }
    @Override
    public void insert(MonthStock monthStock) {
        ContentValues values = new ContentValues();
        values.put("number", monthStock.getNumber());
        values.put("cur_value", monthStock.getCurValue());
        values.put("time", monthStock.getTime());
        db.insert(MONTH_TABLE,null,values);

    }

    @Override
    public boolean isAdded(String number, long time) {
        Cursor cursor = db.rawQuery("select time from t_month_stock where number = '"+number+"' order by time desc limit 1",null);
        while (cursor.moveToNext()){
            long timeS = cursor.getLong(0);
            if(DateUtil.isSameMonth(timeS,System.currentTimeMillis())){
                return true;
            }
        }
        return false;
    }

    @Override
    public MonthStock getCurMonthValue(String myNumber) {
        Cursor cursor = db.rawQuery("select * from t_month_stock where number = '"+myNumber+"' order by time desc limit 1",null);
        while (cursor.moveToNext()){
            MonthStock monthStock = new MonthStock();
            monthStock.setId(cursor.getInt(0));
            monthStock.setNumber(cursor.getString(1));
            monthStock.setCurValue(cursor.getDouble(2));
            monthStock.setTime(cursor.getLong(3));
            return monthStock;
        }
        return null;
    }
}
