package com.chandler.red.mystock;

public class RegText {
    public static void main(String[] args){
        String DQS = "http://image.sinajs.cn/newchart/usstock/daily/dji.gif";
        String[] s = DQS.split("/");
        for(String s1:s){
            System.out.println(s1);
        }
    }
}
