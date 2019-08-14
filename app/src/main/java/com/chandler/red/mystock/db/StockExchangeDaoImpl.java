package com.chandler.red.mystock.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chandler.red.mystock.entity.ExeStock;
import com.chandler.red.mystock.entity.HoldStock;
import com.chandler.red.mystock.entity.HoldsBean;
import com.chandler.red.mystock.util.DateUtil;
import com.chandler.red.mystock.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class StockExchangeDaoImpl implements StockExchangeDao {
    private final static String EXE_TABLE = "t_exe_stock";
    private SQLiteDatabase db;
    private SQLiteOpenHelper sqlHelper;
    private Context context;
    public StockExchangeDaoImpl(Context context){
        this.context = context;
        sqlHelper = new MySqlHelper(context,MySqlHelper.DB_NAME,null,MySqlHelper.CUR_VERSION);
        db = sqlHelper.getWritableDatabase();
    }

    @Override
    public void insert(ExeStock exeStock) {
        db.execSQL("insert into t_exe_stock(exe_id,acc_id,number,exe_value, exe_mount,exe_time,exe_type,name) values(?,?,?,?,?,?,?,?)",
                new Object[]{exeStock.getExeId(),exeStock.getAccId(),exeStock.getNumber(),exeStock.getExeValue(),exeStock.getExeMount(),exeStock.getExeTime(),exeStock.getExeType(),exeStock.getName()});
    }

    @Override
    public void replace(ExeStock exeStock) {
        db.execSQL("replace into t_exe_stock(exe_id,acc_id,number,exe_value, exe_mount,exe_time,exe_type,name) values(?,?,?,?,?,?,?,?)",
                new Object[]{exeStock.getExeId(),exeStock.getAccId(),exeStock.getNumber(),exeStock.getExeValue(),exeStock.getExeMount(),exeStock.getExeTime(),exeStock.getExeType(),exeStock.getName()});
    }

    @Override
    public void replace(List<ExeStock> exeStockList) {
        for(ExeStock exeStock:exeStockList){
            db.execSQL("replace into t_exe_stock(exe_id,acc_id,number,exe_value, exe_mount,exe_time,exe_type,name) values(?,?,?,?,?,?,?,?)",
                    new Object[]{exeStock.getExeId(),exeStock.getAccId(),exeStock.getNumber(),exeStock.getExeValue(),exeStock.getExeMount(),exeStock.getExeTime(),exeStock.getExeType(),exeStock.getName()});
        }
    }

    @Override
    public String getNumberList() {
        String sql = "select number from t_exe_stock;";
        Cursor cursor = db.rawQuery(sql,null);
        StringBuilder stringBuilder = new StringBuilder();
        while (cursor.moveToNext()){
            String number = cursor.getString(0);
            if(!stringBuilder.toString().contains(number)){
                stringBuilder.append(number);
                stringBuilder.append(",");
            }
        }
        if(stringBuilder.length()>0)
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
        return stringBuilder.toString();
    }

    @Override
    public ExeStock getStockExchangeById(String id) {
        String sql = "select * from t_exe_stock where exe_id = '"+id+"';";
        return null;
    }

    @Override
    public List<HoldStock> getHoldStocks() {
        String sql = "select number,exe_mount,exe_type,exe_time,exe_value from t_exe_stock;";
        Cursor cursor = db.rawQuery(sql,null);
        List<HoldStock> list = new ArrayList<>();
        while (cursor.moveToNext()){
            String number = cursor.getString(0);
            int count = cursor.getInt(1);
            int type = cursor.getInt(2);
            long time = cursor.getLong(3);
            double exeValue =  cursor.getDouble(4);
            if(type==1){
                count = count - 2*count;
            }
            HoldStock stock = new HoldStock();
            stock.setNumber(number);
            stock.setCount(count);
            stock.setExeTime(time);
            stock.setExeValue(exeValue);
            list.add(stock);
        }
        return list;
    }

    @Override
    public List<HoldsBean> getExeHoldStocks() {
        String sql = "select number,sum(exe_mount) as hold_count,sum(exe_mount*exe_value) as cost_money,name from t_exe_stock group by number";
        Cursor cursor = db.rawQuery(sql,null);
        List<HoldsBean> list = new ArrayList<>();
        while (cursor.moveToNext()){
            HoldsBean holdsBean = new HoldsBean();
            holdsBean.setNumber(cursor.getString(0));
            holdsBean.setCount(cursor.getInt(1));
            holdsBean.setCost(cursor.getDouble(2)/holdsBean.getCount());
            holdsBean.setAvailable(getAvailableCount(holdsBean.getNumber()));
            holdsBean.setName(cursor.getString(3));
            list.add(holdsBean);
            LogUtil.i(holdsBean.toString());
        }
        return list;
    }

    @Override
    public List<ExeStock> getAllExeHoldStocks() {
        String sql = "select * from t_exe_stock";
        Cursor cursor = db.rawQuery(sql,null);
        List<ExeStock> list = new ArrayList<>();
        while (cursor.moveToNext()){
            ExeStock exeStock = new ExeStock();
            exeStock.setExeId(cursor.getString(0));
            exeStock.setAccId(cursor.getInt(1));
            exeStock.setNumber(cursor.getString(2));
            exeStock.setExeValue(cursor.getDouble(3));
            exeStock.setExeMount(cursor.getInt(4));
            exeStock.setExeTime(cursor.getLong(5));
            exeStock.setExeType(cursor.getInt(6));
            exeStock.setName(cursor.getString(7));
            LogUtil.i(exeStock.toString());
            list.add(exeStock);
        }
        return list;
    }

    @Override
    public List<ExeStock> getNullNameExeHoldStocks() {
        String sql = "select * from t_exe_stock where name is null";
        Cursor cursor = db.rawQuery(sql,null);
        List<ExeStock> list = new ArrayList<>();
        while (cursor.moveToNext()){
            ExeStock exeStock = new ExeStock();
            exeStock.setExeId(cursor.getString(0));
            exeStock.setAccId(cursor.getInt(1));
            exeStock.setNumber(cursor.getString(2));
            exeStock.setExeValue(cursor.getDouble(3));
            exeStock.setExeMount(cursor.getInt(4));
            exeStock.setExeTime(cursor.getLong(5));
            exeStock.setExeType(cursor.getInt(6));
            exeStock.setName(cursor.getString(7));
            LogUtil.i(exeStock.toString());
            list.add(exeStock);
        }
        return list;
    }

    private int getAvailableCount(String number){
        String sql = "select exe_time,exe_mount from t_exe_stock where number = '"+number+"'";
        Cursor cursor = db.rawQuery(sql,null);
        int avaCount= 0;
        int notAvaCount = 0;
        while (cursor.moveToNext()){
            long exeTime = cursor.getLong(0);
            int exeMount = cursor.getInt(1);
            if(exeMount>0 && DateUtil.isSameDay(exeTime,System.currentTimeMillis())){
                notAvaCount += exeMount;
            }else {
                avaCount += exeMount;
            }
        }
        LogUtil.i("avaCount:"+avaCount+" notAvaCount:"+notAvaCount);
        return avaCount;
    }
}
