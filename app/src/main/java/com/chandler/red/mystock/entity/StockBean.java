package com.chandler.red.mystock.entity;

import com.chandler.red.mystock.util.HanyuUtil;

public class StockBean {
    private String number;
    private String character;
    private String name;
    private String value;
    private boolean isSelected;
    private long topTime;

    public StockBean(){

    }

    public StockBean(String number, String name, String value){
        this.number = number;
        this.name = name;
        this.value = value;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCharacter() {
        this.character = HanyuUtil.getUpperCase(getName(),false);
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public long getTopTime() {
        return topTime;
    }

    public void setTopTime(long topTime) {
        this.topTime = topTime;
    }

    @Override

    public String toString() {

        return "StockBean [number=" + number+ ", name=" + name + ", value=" + value+ ", character=" + getCharacter() + ", isSelected=" + isSelected+ ", topTime=" + topTime+ "]";

    }
}
