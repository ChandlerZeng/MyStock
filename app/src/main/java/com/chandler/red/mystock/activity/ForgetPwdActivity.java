package com.chandler.red.mystock.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chandler.red.mystock.App;
import com.chandler.red.mystock.BaseActivity;
import com.chandler.red.mystock.R;
import com.chandler.red.mystock.db.StockBuisnessManager;
import com.chandler.red.mystock.entity.AccStock;
import com.chandler.red.mystock.util.EncryptUtil;
import com.chandler.red.mystock.util.NumberUtil;
import com.chandler.red.mystock.util.TextUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgetPwdActivity extends BaseActivity {

    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.et_pwd_again)
    EditText etPwdAgain;
    @BindView(R.id.btn_commit)
    Button btnCommit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_commit)
    public void onViewClicked() {
        String phone = etPhone.getText().toString();
        String pwd = etPwd.getText().toString();
        String pwdAgain = etPwdAgain.getText().toString();
        if(!isInputComply(phone,pwd,pwdAgain)){
            return;
        }
        phone = EncryptUtil.md5WithSalt(phone);
        Log.i(App.TAG,"phone:"+phone);
        AccStock accStock = StockBuisnessManager.getInstance(ForgetPwdActivity.this).getStockAccountByPhone(phone);
        if(accStock == null){
            Toast.makeText(ForgetPwdActivity.this,"账号不存在！",Toast.LENGTH_SHORT).show();
            return;
        }

        pwd = EncryptUtil.md5WithSalt(pwd);
        Log.i(App.TAG,"pwd:"+pwd);
        accStock.setPassword(pwd);
        StockBuisnessManager.getInstance(ForgetPwdActivity.this).replaceAccount(accStock);
        Log.i(App.TAG,accStock.toString());
        Toast.makeText(ForgetPwdActivity.this,"密码修改成功",Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean isInputComply(String phone,String pwd,String pwdA){
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(ForgetPwdActivity.this,"手机号不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(pwd)){
            Toast.makeText(ForgetPwdActivity.this,"密码不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(pwdA)){
            Toast.makeText(ForgetPwdActivity.this,"再次输入密码不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!NumberUtil.isPhone(phone)){
            Toast.makeText(ForgetPwdActivity.this,"手机号无效！",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!pwd.equals(pwdA)){
            Toast.makeText(ForgetPwdActivity.this,"密码不一致！",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
