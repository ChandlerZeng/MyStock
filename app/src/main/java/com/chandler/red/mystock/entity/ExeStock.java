package com.chandler.red.mystock.entity;

public class ExeStock {
    private String name;
    private String exeId;
    private int accId;
    private String number;
    private double exeValue;
    private int exeMount;
    private long exeTime;
    private int exeType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExeId() {
        return exeId;
    }

    public void setExeId(String exeId) {
        this.exeId = exeId;
    }

    public int getAccId() {
        return accId;
    }

    public void setAccId(int accId) {
        this.accId = accId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public double getExeValue() {
        return exeValue;
    }

    public void setExeValue(double exeValue) {
        this.exeValue = exeValue;
    }

    public int getExeMount() {
        return exeMount;
    }

    public void setExeMount(int exeMount) {
        this.exeMount = exeMount;
    }

    public long getExeTime() {
        return exeTime;
    }

    public void setExeTime(long exeTime) {
        this.exeTime = exeTime;
    }

    public int getExeType() {
        return exeType;
    }

    public void setExeType(int exeType) {
        this.exeType = exeType;
    }
}
