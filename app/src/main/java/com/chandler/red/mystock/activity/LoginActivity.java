package com.chandler.red.mystock.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.tv_forget_pwd)
    TextView tvForgetPwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        SharedPreferences sharedPreferences = getSharedPreferences("MY_STOCK_PREF", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", null);
        if (phone != null) {
            etPhone.setText(phone);
            etPhone.setSelection(phone.length());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            String phone = data.getStringExtra("phone");
            etPhone.setText(phone);
        }
    }

    @OnClick({R.id.btn_login,R.id.tv_register, R.id.tv_forget_pwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                String number = etPhone.getText().toString();
                String pwd = etPwd.getText().toString();
                if (isInputComply(number,pwd)) {
                    String phone = EncryptUtil.md5WithSalt(number);
                    Log.i(App.TAG,"phone:"+phone);
                    AccStock accStock = StockBuisnessManager.getInstance(LoginActivity.this).getStockAccountByPhone(phone);
                    if(accStock == null){
                        Toast.makeText(LoginActivity.this,"账号不存在！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    pwd = EncryptUtil.md5WithSalt(pwd);
                    Log.i(App.TAG,"pwd:"+pwd);
                    if(!pwd.equals(accStock.getPassword())){
                        Toast.makeText(LoginActivity.this,"密码错误！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SharedPreferences sharedPreferences = getSharedPreferences("MY_STOCK_PREF", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("phone", number);
                    editor.putBoolean("is_login", true);
                    editor.commit();
                    Intent intent = new Intent();
                    intent.putExtra("phone", number);
                    intent.putExtra("name", accStock.getName());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            case R.id.tv_register:
                startActivityForResult(new Intent(LoginActivity.this,RegisterActivity.class),100);
                break;
            case R.id.tv_forget_pwd:
                startActivity(new Intent(LoginActivity.this,ForgetPwdActivity.class));
                break;
        }

    }

    private boolean isInputComply(String phone,String pwd){
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(LoginActivity.this,"手机号不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(pwd)){
            Toast.makeText(LoginActivity.this,"密码不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!NumberUtil.isPhone(phone)){
            Toast.makeText(LoginActivity.this,"手机号无效！",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
