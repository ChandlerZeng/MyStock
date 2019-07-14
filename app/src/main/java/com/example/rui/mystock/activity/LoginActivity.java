package com.example.rui.mystock.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.rui.mystock.R;
import com.example.rui.mystock.util.NumberUtil;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.spinner_name)
    Spinner spinnerName;

    private String[] names = {"国债收益率与债券价格成反比","滞胀持有现金，萧条持有债券","股神巴菲特入门大弟子法号币升", "爱股一万年", "炒股炒的就是淡定", "只认大盘指数", "高抛低吸是我成功秘诀"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //自定义选择填充后的字体样式
        //只能是textview样式，否则报错：ArrayAdapter requires the resource ID to be a TextView
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, names);
        //自定义下拉的字体样式
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        //这个在不同的Theme下，显示的效果是不同的
        //spinnerAdapter.setDropDownViewTheme(Theme.LIGHT);
        spinnerName.setAdapter(spinnerAdapter);
        SharedPreferences sharedPreferences = getSharedPreferences("MY_STOCK_PREF", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", null);
        String name = sharedPreferences.getString("name", null);
        if (phone != null) {
            etPhone.setText(phone);
            etPhone.setSelection(phone.length());
        }
        if(name!=null){
            for(int i=0;i<names.length;i++){
                if(name.equals(names[i])){
                    spinnerName.setSelection(i);
                    break;
                }
            }
        }
    }

    @OnClick({R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                String number = etPhone.getText().toString();
                if (NumberUtil.isPhone(number)) {
                    String name = spinnerName.getSelectedItem().toString();
                    SharedPreferences sharedPreferences = getSharedPreferences("MY_STOCK_PREF", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("phone", number);
                    editor.putString("name", name);
                    editor.commit();
                    Intent intent = new Intent();
                    intent.putExtra("phone", number);
                    intent.putExtra("name",name);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "手机号码错误", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }
}
