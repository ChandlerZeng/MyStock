package com.test.dragclosedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //图片随便找的，加载不出来可以换别的
    public static final String[] images = new String[]{
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1503684240091&di=a9d641f8424c561d6b5b6051c3b164f5&imgtype=0&src=http%3A%2F%2Ff.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2Fd009b3de9c82d158d14de70c800a19d8bd3e42bb.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1503684322968&di=adad2ba7aaeb3c70cf8233a99b5c07ad&imgtype=jpg&src=http%3A%2F%2Fimg2.imgtn.bdimg.com%2Fit%2Fu%3D1312683452%2C3087431303%26fm%3D214%26gp%3D0.jpg"
    };
    private Bundle mReenterState;
    ViewGroup parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ArrayList<String> pictureList = new ArrayList<>();
        pictureList.addAll(Arrays.asList(images));

        parent = (ViewGroup) findViewById(R.id.container);
        int cnt = Math.min(images.length, parent.getChildCount());
        for (int i = 0; i < cnt; i++) {
            final ImageView imageView = (ImageView) parent.getChildAt(i);
            Glide.with(this).load(images[i]).into(imageView);
            final int index = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PhotoBrowseActivity.startWithElement(MainActivity.this, pictureList, index, imageView);
                }
            });

        }

        setSharedElementCallback(this);
    }



    /**
     * 接管Activity的setExitSharedElementCallback
     * @param activity
     */
    public void setSharedElementCallback(Activity activity){
        ActivityCompat.setExitSharedElementCallback(activity, new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if (mReenterState!=null){
                    int index = mReenterState.getInt("index",0);
                    sharedElements.clear();
                    sharedElements.put("tansition_view",parent.getChildAt(index));
                    mReenterState = null;
                }
            }
        });

    }
    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        mReenterState = new Bundle(data.getExtras());
    }
}
