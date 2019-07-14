package com.example.rui.mystock.entity;

public class StockBuy {
    private String name;
    private String value;
    private String count;
    private double increase;

    public StockBuy(String name,String value,String count,double increase){
        this.name = name;
        this.value = value;
        this.count = count;
        this.increase = increase;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public double getIncrease() {
        return increase;
    }

    public void setIncrease(double increase) {
        this.increase = increase;
    }
}
