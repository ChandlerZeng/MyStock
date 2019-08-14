package com.chandler.red.mystock.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chandler.red.mystock.entity.AccStock;

public class StockAccountDaoImpl implements StockAccountDao {
    private final static String ACC_TABLE = "t_acc_stock";
    private SQLiteDatabase db;
    private SQLiteOpenHelper sqlHelper;
    private Context context;
    public StockAccountDaoImpl(Context context){
        this.context = context;
        sqlHelper = new MySqlHelper(context,MySqlHelper.DB_NAME,null,MySqlHelper.CUR_VERSION);
        db = sqlHelper.getWritableDatabase();
    }

    @Override
    public void insert(AccStock accStock) {
        ContentValues values = new ContentValues();
        values.put("name", accStock.getName());
        values.put("phone", accStock.getPhone());
        values.put("password", accStock.getPassword());
        values.put("init_value", accStock.getInitValue());
        values.put("cur_value", accStock.getCurValue());
        values.put("cur_stock_value", accStock.getCurStockValue());
        values.put("photo_url", accStock.getPhotoUrl());
        db.insert(ACC_TABLE,null,values);
    }

    @Override
    public void replace(AccStock accStock) {
        db.execSQL("replace into t_acc_stock(acc_id,name,phone,password,init_value,cur_value,cur_stock_value,photo_url) values(?,?,?,?,?,?,?,?)",
                new Object[]{accStock.getAccId(),accStock.getName(),accStock.getPhone(),accStock.getPassword(),accStock.getInitValue(),accStock.getCurValue(),accStock.getCurStockValue(),accStock.getPhotoUrl()});
    }

    @Override
    public boolean isAccountExist(String phone) {
        String sql = "select phone from t_acc_stock where phone = '"+phone+"';";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor==null || !cursor.moveToNext())return false;
        String phoneNum = cursor.getString(0);
        if(phoneNum!=null && phone.equals(phoneNum))return true;
        return false;
    }

    @Override
    public AccStock getStockAccountByPhone(String phone) {
        String sql = "select * from t_acc_stock where phone = '"+phone+"';";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor==null || !cursor.moveToNext())return null;
        AccStock accStock = new AccStock();
        accStock.setAccId(cursor.getInt(0));
        accStock.setName(cursor.getString(1));
        accStock.setPhone(cursor.getString(2));
        accStock.setPassword(cursor.getString(3));
        accStock.setInitValue(cursor.getDouble(4));
        accStock.setCurValue(cursor.getDouble(5));
        accStock.setCurStockValue(cursor.getDouble(6));
        accStock.setPhotoUrl(cursor.getString(7));
        Log.i(MySqlHelper.TAG,accStock.toString());
        return accStock;
    }

    @Override
    public int getAccId(String phone) {
        String sql = "select acc_id from t_acc_stock where phone = '"+phone+"';";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor.moveToNext()){
            return cursor.getInt(0);
        }
        return -1;
    }
}
