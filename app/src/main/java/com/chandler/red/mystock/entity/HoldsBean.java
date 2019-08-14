package com.chandler.red.mystock.entity;

public class HoldsBean {
    private String number;
    private String name;
    private double profit;
    private int count;
    private double cost;
    private double totalValue;
    private double profitRate;
    private int available;
    private double curValue;

    public HoldsBean(){

    }

    public HoldsBean(String name, double profit, int count, double cost, double totalValue, double profitRate, int available, double curValue) {
        this.name = name;
        this.profit = profit;
        this.count = count;
        this.cost = cost;
        this.totalValue = totalValue;
        this.profitRate = profitRate;
        this.available = available;
        this.curValue = curValue;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
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

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public double getProfitRate() {
        return profitRate;
    }

    public void setProfitRate(double profitRate) {
        this.profitRate = profitRate;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public double getCurValue() {
        return curValue;
    }

    public void setCurValue(double curValue) {
        this.curValue = curValue;
    }

    @Override
    public String toString() {
        return "HoldsBean{" +
                "number='" + number + '\'' +
                "name='" + name + '\'' +
                ", profit=" + profit +
                ", count=" + count +
                ", cost=" + cost +
                ", totalValue=" + totalValue +
                ", profitRate=" + profitRate +
                ", available=" + available +
                ", curValue=" + curValue +
                '}';
    }
}
