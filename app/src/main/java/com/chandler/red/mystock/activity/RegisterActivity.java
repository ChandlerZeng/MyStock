package com.chandler.red.mystock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.chandler.red.mystock.App;
import com.chandler.red.mystock.BaseActivity;
import com.chandler.red.mystock.R;
import com.chandler.red.mystock.db.StockBuisnessManager;
import com.chandler.red.mystock.entity.AccStock;
import com.chandler.red.mystock.util.Constants;
import com.chandler.red.mystock.util.EncryptUtil;
import com.chandler.red.mystock.util.NumberUtil;
import com.chandler.red.mystock.util.TextUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.spinner_name)
    Spinner spinnerName;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.btn_register)
    Button btnRegister;


    private double initValue = 100000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        //自定义选择填充后的字体样式
        //只能是textview样式，否则报错：ArrayAdapter requires the resource ID to be a TextView
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, Constants.names);
        //自定义下拉的字体样式
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        //这个在不同的Theme下，显示的效果是不同的
        //spinnerAdapter.setDropDownViewTheme(Theme.LIGHT);
        spinnerName.setAdapter(spinnerAdapter);
    }

    @OnClick(R.id.btn_register)
    public void onViewClicked() {
        String ph = etPhone.getText().toString();
        String pwd = etPwd.getText().toString();
        if(!isInputComply(ph,pwd)){
            return;
        }
        String phone = EncryptUtil.md5WithSalt(ph);
        Log.i(App.TAG,"phone:"+phone);
        if(StockBuisnessManager.getInstance(RegisterActivity.this).isAccountExist(phone)){
            Toast.makeText(RegisterActivity.this,"账号已存在！",Toast.LENGTH_SHORT).show();
            return;
        }
        String name = spinnerName.getSelectedItem().toString();
        pwd = EncryptUtil.md5WithSalt(pwd);
        Log.i(App.TAG,"pwd:"+pwd);
        AccStock accStock = new AccStock();
        accStock.setName(name);
        accStock.setPhone(phone);
        accStock.setPassword(pwd);
        accStock.setInitValue(initValue);
        accStock.setCurValue(initValue);
        accStock.setPhotoUrl(Constants.DEFALUT_URL);
        StockBuisnessManager.getInstance(RegisterActivity.this).insertAccount(accStock);
        Log.i(App.TAG,accStock.toString());
        Toast.makeText(RegisterActivity.this,"注册成功！",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("phone",ph);
        setResult(RESULT_OK,intent);
        finish();
    }

    private boolean isInputComply(String phone,String pwd){
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(RegisterActivity.this,"手机号不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(pwd)){
            Toast.makeText(RegisterActivity.this,"密码不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!NumberUtil.isPhone(phone)){
            Toast.makeText(RegisterActivity.this,"手机号无效！",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
