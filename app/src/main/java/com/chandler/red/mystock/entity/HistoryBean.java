package com.chandler.red.mystock.entity;

public class HistoryBean {
    private String name;
    private String number;
    private String date;
    private String time;
    private int type;
    private int count;
    private double profit;
    private double profitRate;
    private double cost;
    private double value;

    public HistoryBean(String name,String date, int type, double profit, double profitRate) {
        this.name = name;
        this.date = date;
        this.type = type;
        this.profit = profit;
        this.profitRate = profitRate;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeString(){
        if(type==0){
            return "买入";
        }else {
            return "卖出";
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public double getProfitRate() {
        return profitRate;
    }

    public void setProfitRate(double profitRate) {
        this.profitRate = profitRate;
    }

    @Override
    public String toString() {
        return "HistoryBean{" +
                "name='" + name + '\'' +
                "date='" + date + '\'' +
                ", type=" + type +
                ", profit=" + profit +
                ", profitRate=" + profitRate +
                '}';
    }
}
