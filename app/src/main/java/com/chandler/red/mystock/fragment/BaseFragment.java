package com.chandler.red.mystock.fragment;

import android.support.v4.app.Fragment;

import com.chandler.red.mystock.widget.CustomProgressDialog;

public class BaseFragment extends Fragment {
    protected void showProgressDialog(){
        try{
            showProgressDialog("正在加载...",true,true);
        }catch (Exception e){
        }
    }
    protected void showProgressDialog(String msg){
        try{
            showProgressDialog(msg,true,true);
        }catch (Exception e){
        }
    }
    protected void showProgressDialog(String msg,boolean isCancelOutside){
        showProgressDialog(msg,true,isCancelOutside);
    }

    protected void showProgressDialog(String msg, boolean isCancelable,boolean isCancelOutside){
        CustomProgressDialog.showLoading(getActivity(),msg,isCancelOutside);
    }

    protected void dimissProgressDialog(){
        CustomProgressDialog.stopLoading();
    }
}
