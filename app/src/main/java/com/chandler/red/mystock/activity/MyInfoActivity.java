package com.chandler.red.mystock.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chandler.red.mystock.BaseActivity;
import com.chandler.red.mystock.R;
import com.chandler.red.mystock.db.StockBuisnessManager;
import com.chandler.red.mystock.entity.AccStock;
import com.chandler.red.mystock.imageloader.ImagePickActivity;
import com.chandler.red.mystock.util.EncryptUtil;
import com.chandler.red.mystock.util.NumberUtil;
import com.chandler.red.mystock.util.TextUtils;
import com.chandler.red.mystock.widget.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyInfoActivity extends BaseActivity {

    @BindView(R.id.my_head_layout)
    LinearLayout myHeadLayout;
    @BindView(R.id.my_photo)
    CircleImageView myPhoto;
    @BindView(R.id.tv_change_photo)
    TextView tvChangePhoto;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.my_name_layout)
    LinearLayout myNameLayout;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.btn_logout)
    Button btnLogout;
    @BindView(R.id.tv_change_net_photo)
    TextView tvChangeNetPhoto;

    private String phone;
    private String name;
    private String url;
    private AccStock accStock;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        sharedPreferences = getSharedPreferences("MY_STOCK_PREF", MODE_PRIVATE);
        phone = getIntent().getStringExtra("phone");
        accStock = StockBuisnessManager.getInstance(this).getStockAccountByPhone(EncryptUtil.md5WithSalt(phone));
        if (accStock != null) {
            name = accStock.getName();
            url = accStock.getPhotoUrl();
            displayImage();
            etName.setText(name);
            etPhone.setText(phone);
            etName.setSelection(name.length());
            etPhone.setSelection(phone.length());
        }
    }

    @OnClick({R.id.my_head_layout, R.id.my_photo, R.id.tv_change_photo,R.id.tv_change_net_photo, R.id.btn_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.my_head_layout:
//                startActivityForResult(new Intent(MyInfoActivity.this, PhotoSelectActivity.class), 3000);
                break;
            case R.id.my_photo:
                ImageShowActivity.startImageActivity(MyInfoActivity.this, myPhoto, url);
                break;
            case R.id.tv_change_photo:
                startActivityForResult(new Intent(MyInfoActivity.this, ImagePickActivity.class), 3000);
                break;
            case R.id.tv_change_net_photo:
                startActivityForResult(new Intent(this, SearchImagesActivity.class), 5000);
                break;
            case R.id.btn_logout:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("is_login", false);
                editor.commit();
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String selectUrl = data.getStringExtra("url");
            if (!TextUtils.isEmpty(selectUrl)) {
                url = selectUrl;
                displayImage();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!TextUtils.isEmpty(etName.getText().toString())) {
            name = etName.getText().toString();
        }
        if (!TextUtils.isEmpty(etPhone.getText().toString()) && NumberUtil.isPhone(etPhone.getText().toString())) {
            phone = etPhone.getText().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("phone", phone);
            editor.commit();
        }
        if (accStock != null) {
            accStock.setName(name);
            accStock.setPhone(EncryptUtil.md5WithSalt(phone));
            accStock.setPhotoUrl(url);
            StockBuisnessManager.getInstance(this).replaceAccount(accStock);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void displayImage() {
        RequestOptions options = new RequestOptions().centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.mipmap.pictures_no)
                .error(R.mipmap.pictures_no);
        Glide.with(this).load(url).apply(options).into(myPhoto);
    }
}
