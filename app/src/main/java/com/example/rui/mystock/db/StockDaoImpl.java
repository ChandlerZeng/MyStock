package com.example.rui.mystock.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.rui.mystock.entity.StockBean;

import java.util.ArrayList;
import java.util.List;

import static com.example.rui.mystock.db.MySqlHelper.TAG;

public class StockDaoImpl implements StockDao{
    private SQLiteOpenHelper helper;
    private SQLiteDatabase db;
    private Context context;
    private int totalCount;

    public StockDaoImpl(Context context){
        this.context = context;
        helper = new MySqlHelper(context,"stock.db", null, MySqlHelper.CUR_VERSION);
    }

    @Override
    public void replace(StockBean stock) {
        db = helper.getWritableDatabase();
        db.execSQL("replace into t_stock(number,name, value,character,isSelected,topTime) values(?,?,?,?,?,?)" ,
                new Object[]{stock.getNumber(),stock.getName(),stock.getValue(),stock.getCharacter(),stock.isSelected(),stock.getTopTime()});
    }

    @Override
    public void replace(List<StockBean> stocks) {
        db = helper.getWritableDatabase();

        for (StockBean stock:stocks){
            ContentValues values = new ContentValues();
            db.execSQL("replace into t_stock(number,name, value,character,isSelected,topTime) values(?,?,?,?,?,?)" ,
                    new Object[]{stock.getNumber(),stock.getName(),stock.getValue(),stock.getCharacter(),stock.isSelected(),stock.getTopTime()});
        }
    }

    public void insert(StockBean stock){
        db = helper.getWritableDatabase();

        db.execSQL("insert into t_stock(number,name, value,character,isSelected) values(?,?,?,?,?)" ,
                new Object[]{stock.getNumber(),stock.getName(),stock.getValue(),stock.getCharacter(),stock.isSelected()});
    }

    @Override
    public void insert(List<StockBean> stocks) {
        db = helper.getWritableDatabase();

        for (StockBean stock:stocks){
            ContentValues values = new ContentValues();
            db.execSQL("insert into t_stock(number,name, value,character,isSelected) values(?,?,?,?,?)" ,
                    new Object[]{stock.getNumber(),stock.getName(),stock.getValue(),stock.getCharacter(),stock.isSelected()});
        }
    }

    public void updateStockByNumber(String number,StockBean stock){
        db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", stock.getName());
        values.put("value", stock.getValue());
        db.update("t_stock", values, "number=?", new String[]{number});
    }

    @Override
    public void updateStockTopTime(String number,long time) {
        db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("topTime", time);
        db.update("t_stock", values, "number=?", new String[]{number});
    }

    public void deleteByNumber(String number){
        db = helper.getWritableDatabase();

        db.delete("t_stock", "number=?", new String[]{number});
    }

    public List<StockBean> searchStockByNumberOrName(String numberOrName,int off,int lim){
        if(numberOrName==null || "".equals(numberOrName))return new ArrayList<>();
        db = helper.getWritableDatabase();

        List<StockBean> stockBeans = new ArrayList<>();
        String sql0 = "select number from t_stock where number like '" + numberOrName + "%'"+" or character " +
                "like '" + numberOrName + "%' ";
        String sql = "select number,name,value,character,isSelected from t_stock where number like '" + numberOrName + "%'"+" or character " +
                "like '" + numberOrName + "%' order by number asc limit "+ lim +" offset "+off;//注意：这里有单引号
        Log.i(TAG,sql);
        Cursor cursor = db.rawQuery(sql0,null);
        totalCount = cursor.getCount();
        cursor = db.rawQuery(sql,null);
        if(cursor == null){

            return null;

        }
        while(cursor.moveToNext()){

            StockBean stock = new StockBean(cursor.getString(0),cursor.getString(1),cursor.getString(2));
            stock.setSelected(cursor.getInt(4)==1);
            stockBeans.add(stock);
            Log.i(TAG,stock.toString());
        }
        cursor.close();
        return stockBeans;

    }



    public List<StockBean> getSelectedStocks(){
        db = helper.getWritableDatabase();

        List<StockBean> stockBeans = new ArrayList<>();
        String sql = "select number,name,value,character,isSelected from t_stock where isSelected = 1 order by topTime desc,number asc";//注意：这里有单引号
        Log.i(TAG,sql);
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor == null){

            return null;

        }
        while(cursor.moveToNext()){

            StockBean stock = new StockBean(cursor.getString(0),cursor.getString(1),cursor.getString(2));
            stock.setSelected(cursor.getInt(4)==1);
            stockBeans.add(stock);
            Log.i(TAG,stock.toString());
        }
        cursor.close();
        return stockBeans;

    }

    public StockBean getStockByNumberOrName(String numberOrName){
        db = helper.getWritableDatabase();

        StockBean stock = null;
        Cursor cursor = db.query("t_stock", new String[]{"number","name","value","character","isSelected","topTime"}, "number=? or name=?", new String[]{numberOrName}, null, null, null);

        if(cursor == null){

            return null;

        }
        if(cursor.moveToFirst()){

            stock = new StockBean(cursor.getString(0),cursor.getString(1),cursor.getString(2));
            stock.setSelected(cursor.getInt(4)==1);
            stock.setTopTime(cursor.getLong(5));
            Log.i("MYSQLITEHELPER",stock.toString());
        }
        cursor.close();
        return stock;

    }

    public List<StockBean> getAllStocks(){
        db = helper.getWritableDatabase();


        List<StockBean> list = new ArrayList<StockBean>();


        Cursor cursor = db.rawQuery("select number,name,value,character,isSelected from t_stock", null);

        if(cursor == null){
            return null;
        }
        while(cursor.moveToNext()){

            StockBean sto = new StockBean(cursor.getString(0),cursor.getString(1),cursor.getString(2));
            sto.setSelected(cursor.getInt(4)==1);

            Log.i("MYSQLITEHELPER",sto.toString());

            list.add(sto);

        }
        cursor.close();
        return list;
    }

    public int getStockCount(){
        db = helper.getWritableDatabase();

        Cursor cursor = db.rawQuery("select number from t_stock", null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    @Override
    public int getSearchStockCount() {
        return totalCount;
    }

    @Override
    public void closeDb() {
        if(db!=null)
            db.close();
    }

    @Override
    public List<String> getSelectedStockIds() {
        db = helper.getWritableDatabase();

        List<String> stockIds = new ArrayList<>();
        String sql = "select number from t_stock where isSelected = 1 order by topTime desc,number asc";//注意：这里有单引号
        Log.i(TAG,sql);
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor == null){
            return null;
        }
        String ids = "";
        while(cursor.moveToNext()){
            stockIds.add(cursor.getString(0));
            ids += cursor.getString(0)+",";
        }
        cursor.close();
        Log.i(TAG,ids);
        return stockIds;
    }

    @Override
    public List<String> getTopStockIds() {
        db = helper.getWritableDatabase();

        List<String> stockIds = new ArrayList<>();
        String sql = "select number from t_stock where isSelected = 1 and topTime > 0 order by topTime desc,number asc";//注意：这里有单引号
        Log.i(TAG,sql);
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor == null){
            return null;
        }
        String ids = "";
        while(cursor.moveToNext()){
            stockIds.add(cursor.getString(0));
            ids += cursor.getString(0)+",";
        }
        cursor.close();
        Log.i(TAG,ids);
        return stockIds;
    }

    @Override
    public boolean clearSelected(String id) {
        db = helper.getWritableDatabase();

        StockBean stock = null;
        Cursor cursor = db.query("t_stock", new String[]{"number","name","value"}, "number=?", new String[]{id}, null, null, null);
        if(cursor == null){
            return false;
        }
        if(cursor.moveToFirst()){
            stock = new StockBean(cursor.getString(0),cursor.getString(1),cursor.getString(2));
            stock.setSelected(false);
        }
        cursor.close();
        replace(stock);
        return true;
    }

    @Override
    public boolean addSelected(String id) {
        db = helper.getWritableDatabase();

        StockBean stock = null;
        Cursor cursor = db.query("t_stock", new String[]{"number","name","value"}, "number=?", new String[]{id}, null, null, null);
        if(cursor == null){
            return false;
        }
        if(cursor.moveToFirst()){
            stock = new StockBean(cursor.getString(0),cursor.getString(1),cursor.getString(2));
            stock.setSelected(true);
        }
        cursor.close();
        replace(stock);
        return true;
    }

    @Override
    public boolean isSelected(String id) {
        db = helper.getWritableDatabase();

        StockBean stock = null;
        Cursor cursor = db.query("t_stock", new String[]{"isSelected"}, "number=?", new String[]{id}, null, null, null);
        if(cursor == null){
            return false;
        }
        if(cursor.moveToFirst()){
           return cursor.getInt(0)==1;
        }
        cursor.close();
        return false;
    }
}
