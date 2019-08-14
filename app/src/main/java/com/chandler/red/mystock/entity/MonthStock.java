package com.chandler.red.mystock.entity;

public class MonthStock {
    private int id;
    private String number;
    private double curValue;
    private long time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public double getCurValue() {
        return curValue;
    }

    public void setCurValue(double curValue) {
        this.curValue = curValue;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "MonthStock{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", curValue='" + curValue + '\'' +
                ", time=" + time +
                '}';
    }
}
