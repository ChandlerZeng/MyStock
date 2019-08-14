package com.chandler.red.mystock.entity;

public class Stock {
    public String id_, name_,now_,increase,percent,yest_value;
    private boolean isTop;

    public String getYest_value() {
        return yest_value;
    }

    public void setYest_value(String yest_value) {
        this.yest_value = yest_value;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public String getId_() {
        return id_;
    }

    public void setId_(String id_) {
        this.id_ = id_;
    }

    public String getName_() {
        return name_;
    }

    public void setName_(String name_) {
        this.name_ = name_;
    }

    public String getNow_() {
        double d = Double.parseDouble(now_);
        return String.format("%.2f", d);
    }

    public void setNow_(String now_) {
        this.now_ = now_;
    }

    public String getIncrease() {
        double d = Double.parseDouble(increase);
        return String.format("%.2f", d);
    }

    public void setIncrease(String increase) {
        this.increase = increase;
    }

    public String getPercent() {
        double d = Double.parseDouble(percent);
        return String.format("%.2f", d) + "% ";
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id_='" + id_ + '\'' +
                ", name_='" + name_ + '\'' +
                ", now_='" + now_ + '\'' +
                ", increase='" + increase + '\'' +
                ", percent='" + percent + '\'' +
                ", yest_value='" + yest_value + '\'' +
                ", isTop=" + isTop +
                '}';
    }
}
