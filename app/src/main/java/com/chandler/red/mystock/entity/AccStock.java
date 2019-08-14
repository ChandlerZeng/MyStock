package com.chandler.red.mystock.entity;

public class AccStock {
    private int accId;
    private String name;
    private String phone;
    private String password;
    private double initValue;
    private double curValue;
    private double curTotalValue;
    private double curStockValue;
    private String photoUrl;

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public double getCurTotalValue() {
        return curValue+curStockValue;
    }

    public void setCurTotalValue(double curTotalValue) {
        this.curTotalValue = curTotalValue;
    }

    public int getAccId() {
        return accId;
    }

    public void setAccId(int accId) {
        this.accId = accId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getInitValue() {
        return initValue;
    }

    public void setInitValue(double initValue) {
        this.initValue = initValue;
    }

    public double getCurValue() {
        return curValue;
    }

    public void setCurValue(double curValue) {
        this.curValue = curValue;
    }

    public double getCurStockValue() {
        return curStockValue;
    }

    public void setCurStockValue(double curStockValue) {
        this.curStockValue = curStockValue;
    }

    @Override
    public String toString() {
        return "AccStock{" +
                "accId=" + accId +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", initValue=" + initValue +
                ", curValue=" + curValue +
                ", curTotalValue=" + curTotalValue +
                ", curStockValue=" + curStockValue +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}
