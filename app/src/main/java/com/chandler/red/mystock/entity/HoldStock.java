package com.chandler.red.mystock.entity;

import com.chandler.red.mystock.util.DateUtil;

public class HoldStock {
    private String number;
    private int count;
    private double curValue;
    private double yesValue;
    private double exeValue;
    private long exeTime;

    public long getExeTime() {
        return exeTime;
    }

    public void setExeTime(long exeTime) {
        this.exeTime = exeTime;
    }

    public double getExeValue() {
        return exeValue;
    }

    public void setExeValue(double exeValue) {
        this.exeValue = exeValue;
    }

    public double getDayProfit(){
        if(DateUtil.isSameDay(exeTime,System.currentTimeMillis())){
            return (getCurValue()-getExeValue())*getCount();
        }else {
            return (getCurValue()-getYesValue())*getCount();
        }
    }

    public double getCurValue() {
        return curValue;
    }

    public void setCurValue(double curValue) {
        this.curValue = curValue;
    }

    public double getYesValue() {
        return yesValue;
    }

    public void setYesValue(double yesValue) {
        this.yesValue = yesValue;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "HoldStock{" +
                "number='" + number + '\'' +
                ", count=" + count +
                ", curValue=" + curValue +
                ", yesValue=" + yesValue +
                ", exeValue=" + exeValue +
                ", exeTime=" + DateUtil.parseToString(exeTime) +
                '}';
    }
}
